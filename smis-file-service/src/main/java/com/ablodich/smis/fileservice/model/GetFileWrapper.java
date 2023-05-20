package com.ablodich.smis.fileservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetFileWrapper {
    private String contentType;
    private long contentLength;
    private byte[] content;
    private String fileName;
}
