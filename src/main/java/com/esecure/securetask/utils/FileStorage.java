package com.esecure.securetask.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileStorage {
    private final Path baseDir;

    public FileStorage(Path baseDir) throws IOException {
        this.baseDir = baseDir;
        Files.createDirectories(baseDir);
    }

    public String save(String id, MultipartFile file) throws IOException {
        String safeName = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
        Path dest = baseDir.resolve(id + "_" + safeName);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        return dest.toString();
    }
}
