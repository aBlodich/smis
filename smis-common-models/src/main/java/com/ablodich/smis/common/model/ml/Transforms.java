package com.ablodich.smis.common.model.ml;

import lombok.Data;

@Data
public class Transforms {
    private Integer resize;
    private Integer centerCrop;
    private NormalizeOptions normalizeOptions;
}
