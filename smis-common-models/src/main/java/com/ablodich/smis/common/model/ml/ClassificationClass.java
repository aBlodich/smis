package com.ablodich.smis.common.model.ml;

import lombok.Data;

@Data
public class ClassificationClass {
    private String className;
    private Integer order;
    private Boolean malignant;
}
