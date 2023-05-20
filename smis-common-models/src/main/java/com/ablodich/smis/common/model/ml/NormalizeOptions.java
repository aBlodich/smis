package com.ablodich.smis.common.model.ml;

import lombok.Data;

import java.util.List;

@Data
public class NormalizeOptions {
    private List<Float> mean;
    private List<Float> std;
}
