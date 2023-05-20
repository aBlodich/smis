package com.ablodich.smis.brainmriclassificationservice.service;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import com.ablodich.smis.brainmriclassificationservice.config.ServiceTopicsProperties;
import com.ablodich.smis.brainmriclassificationservice.entity.MlModel;
import com.ablodich.smis.brainmriclassificationservice.exception.CachingFileException;
import com.ablodich.smis.brainmriclassificationservice.exception.GettingFileException;
import com.ablodich.smis.brainmriclassificationservice.exception.PredictionException;
import com.ablodich.smis.brainmriclassificationservice.repository.MlModelRepository;
import com.ablodich.smis.common.event.DiagnosisTaskEvent;
import com.ablodich.smis.common.event.DiagnosisTaskEventResult;
import com.ablodich.smis.common.event.enumerate.DiagnosisTaskEventResultState;
import com.ablodich.smis.common.event.enumerate.PredictionResult;
import com.ablodich.smis.common.model.ml.ClassificationClass;
import com.ablodich.smis.common.model.ml.Options;
import com.ablodich.smis.common.model.ml.Transforms;
import com.ablodich.smis.starter.outbox.service.OutboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassificationService {
    private final MlModelRepository mlModelRepository;
    private final OutboxService outboxService;
    private final FileService fileService;
    private final LocalCachingFileService localCachingFileService;
    private final ServiceTopicsProperties serviceTopicsProperties;
    private final ObjectMapper objectMapper;


    @Transactional
    public void classify(final DiagnosisTaskEvent diagnosisTaskEvent) {
        try {
            Optional<MlModel> optionalMlModel = mlModelRepository.findActive();
            if (optionalMlModel.isEmpty()) {
                sendErrorResult(diagnosisTaskEvent, "Отсутствует активная модель для классификации");
                return;
            }
            MlModel mlModel = optionalMlModel.get();
            String mlModelFilePath = localCachingFileService.getCachedFilePath(mlModel.getFileId());

            Options modelOptions = objectMapper.readValue(mlModel.getOptions(), Options.class);

            Translator<Image, Classifications> translator = buildTranslator(modelOptions);

            Criteria<Image, Classifications> criteria = buildCriteria(mlModelFilePath, translator);

            try(ZooModel<Image, Classifications> model = criteria.loadModel()) {
                Classifications.Classification prediction = predict(model, diagnosisTaskEvent.getFileId());
                ClassificationClass classificationClass = getClassificationClassByPrediction(modelOptions, prediction);
                sendDiagnosisTaskResult(diagnosisTaskEvent, classificationClass);
            }

        } catch (GettingFileException | CachingFileException | PredictionException e) {
            sendErrorResult(diagnosisTaskEvent, e.getMessage());
        } catch (Exception e) {
            String errorDescription = "Непредвиденная ошибка при обработке задачи классификации: ";
            log.error(errorDescription, e);
            sendErrorResult(diagnosisTaskEvent, errorDescription + e.getMessage());
        }
    }

    private static ClassificationClass getClassificationClassByPrediction(final Options modelOptions, final Classifications.Classification prediction) {
        return modelOptions.getClasses().stream()
                           .filter(c -> c.getClassName().equals(prediction.getClassName()))
                           .findFirst()
                           .orElseThrow(() -> new PredictionException("Не удалось определить класс предсказания"));
    }

    private Classifications.Classification predict(final ZooModel<Image, Classifications> model, final String imageToPredictId)
            throws IOException, TranslateException {
        var imageToPredict = ImageFactory.getInstance().fromInputStream(fileService.getFileInputStream(imageToPredictId));
        try (Predictor<Image, Classifications> predictor = model.newPredictor()) {
            Classifications classifications = predictor.predict(imageToPredict);
            log.debug("Полученные предсказания:\n{}", classifications);
            return classifications.best();
        }
    }

    private Criteria<Image, Classifications> buildCriteria(final String mlModelFilePath, final Translator<Image, Classifications> translator) {
        return Criteria.builder()
                       .setTypes(Image.class, Classifications.class)
                       .optModelPath(Paths.get(mlModelFilePath))
                       .optTranslator(translator)
                       .build();
    }

    private void sendDiagnosisTaskResult(final DiagnosisTaskEvent diagnosisTaskEvent, final ClassificationClass classificationClass) {
        DiagnosisTaskEventResult event = new DiagnosisTaskEventResult();
        event.setTaskId(diagnosisTaskEvent.getId());
        event.setPrediction(getPredictionResult(classificationClass));
        event.setState(DiagnosisTaskEventResultState.COMPLETED);
        outboxService.sendMessage(event.getTaskId().toString(),  serviceTopicsProperties.getDiagnosisTaskEventResultTopic(), event);
    }

    private PredictionResult getPredictionResult(final ClassificationClass classificationClass) {
        return Boolean.TRUE.equals(classificationClass.getMalignant()) ? PredictionResult.DISEASE_FOUND : PredictionResult.DISEASE_NOT_FOUND;
    }

    private Translator<Image, Classifications> buildTranslator(final Options modelOptions) {
        Transforms transforms = modelOptions.getTransforms();
        List<ClassificationClass> classes = modelOptions.getClasses();
        if (modelOptions.getTransforms() == null) {
            throw new IllegalArgumentException("Отсутсвуют трансформации для входных данных в модель");
        }
        if (CollectionUtils.isEmpty(classes)) {
            throw new IllegalArgumentException("Отсутсвуют классы для выходных данных из модели");
        }

        classes.sort(Comparator.comparing(ClassificationClass::getOrder));
        float[] mean = ArrayUtils.toPrimitive(transforms.getNormalizeOptions().getMean().toArray(Float[]::new));
        float[] std = ArrayUtils.toPrimitive(transforms.getNormalizeOptions().getStd().toArray(Float[]::new));

        return ImageClassificationTranslator.builder()
                                            .addTransform(new Resize(transforms.getResize()))
                                            .addTransform(new CenterCrop(transforms.getCenterCrop(), transforms.getCenterCrop()))
                                            .addTransform(new ToTensor())
                                            .addTransform(new Normalize(mean, std))
                                            .optApplySoftmax(modelOptions.getApplySoftmax())
                                            .optSynset(classes.stream().map(ClassificationClass::getClassName).toList())
                                            .build();
    }

    private void sendErrorResult(final DiagnosisTaskEvent diagnosisTaskEvent, final String errorDescription) {
        DiagnosisTaskEventResult event = new DiagnosisTaskEventResult();
        event.setErrorDescription(errorDescription);
        event.setTaskId(diagnosisTaskEvent.getId());
        event.setState(DiagnosisTaskEventResultState.ERROR);
        outboxService.sendMessage(event.getTaskId().toString(),  serviceTopicsProperties.getDiagnosisTaskEventResultTopic(), event);
    }
}