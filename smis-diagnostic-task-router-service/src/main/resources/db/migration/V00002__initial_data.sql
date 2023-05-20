insert into diagnosis_types(name, code)
values ('Классификация рака на МРТ головного мозга', 'BRAIN_MRI_TUMOR_CLASSIFICATION');

insert into checking_services(service_name, diagnosis_type_code)
values ('smis-ml-brain-mri-tumor-classification-service', 'BRAIN_MRI_TUMOR_CLASSIFICATION');