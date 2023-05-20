package com.ablodich.smis.brainmriclassificationservice.service;

import com.ablodich.smis.brainmriclassificationservice.exception.CachingFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalCachingFileService {
    private final FileService fileService;
    private final ConcurrentHashMap<String, File> cache = new ConcurrentHashMap<>();


    public String getCachedFilePath(final String fileId) {
        File cachedFile = cache.get(fileId);
        if (cachedFile != null) {
            return cachedFile.getAbsolutePath();
        }
        File tmpFile = null;
        try {
            tmpFile = saveFileToTempDirectory(fileId, fileService.getFileBytes(fileId));
            cache.put(fileId, tmpFile);
            return tmpFile.getAbsolutePath();
        }
        catch (IOException e) {
            String errorDescription = "Ошибка при локальном кэшировании файла: ";
            log.error(errorDescription, e);
            throw new CachingFileException(errorDescription + e.getMessage());
        }

    }

    public File saveFileToTempDirectory(final String fileId, byte[] content) throws IOException {
        File tmpDir = Files.createTempDirectory("ml_tmp_dir").toFile();
        File tmpFile = Files.createFile(Path.of(tmpDir.getAbsolutePath(), fileId)).toFile();
        FileUtils.writeByteArrayToFile(tmpFile, content);
        return tmpFile;
    }

    public void removeTempFile(final  String fileId) throws IOException {
        File tmpFile = cache.get(fileId);
        if (tmpFile == null) {
            return;
        }
        FileUtils.deleteDirectory(tmpFile.getParentFile());
    }
}
