package com.ablodich.smis.common.model.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultipartFileImpl implements MultipartFile {
    private final String originalFileName;
    private final String contentType;
    private final byte[] content;

    public MultipartFileImpl(final String originalFileName, final String contentType, final byte[] content) {
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public String getName() {
        return originalFileName;
    }

    @Override
    public String getOriginalFilename() {
        return originalFileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(final File dest) throws IOException, IllegalStateException {
        Path path = Paths.get(dest.getPath());
        Files.write(path, getBytes());
    }
}
