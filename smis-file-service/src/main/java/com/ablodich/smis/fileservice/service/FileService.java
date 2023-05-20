package com.ablodich.smis.fileservice.service;

import com.ablodich.smis.fileservice.model.GetFileWrapper;

public interface FileService {
    String uploadFile(final byte[] bytes, final String fileExtension, final String extension);

    GetFileWrapper getFile(String fileId);

    boolean fileExists(String fileId);

    String deleteFile(String fileId);

}
