package com.ablodich.smis.common.model.ml;

import lombok.Data;

import java.util.List;

@Data
public class Options {
    private Transforms transforms;
    private Boolean applySoftmax;
    private List<ClassificationClass> classes;
}
