package com.ablodich.smis.common.model.dto;

import java.io.Serializable;

public record IdResultDto <T>(T id) implements Serializable {}
