insert into ml_models(file_id, name, options, active)
values ('vgg19_93_acc.pt.zip', 'Нейросеть VGG19 для классификации рака головного мозга', '{
  "transforms": {
    "resize": 256,
    "centerCrop": 224,
    "normalizeOptions": {
      "mean": [0.2444, 0.2445, 0.2445],
      "std": [0.2103, 0.2103, 0.2103]
    }
  },
  "applySoftmax": true,
  "classes": [
    {
      "className": "Glioma",
      "order": 1,
      "malignant": true
    },
    {
      "className": "Meningioma",
      "order": 2,
      "malignant": true
    },
    {
      "className": "Notumor",
      "order": 3,
      "malignant": false
    },
    {
      "className": "Pituitary",
      "order": 4,
      "malignant": true
    }
  ]
}
' , true);