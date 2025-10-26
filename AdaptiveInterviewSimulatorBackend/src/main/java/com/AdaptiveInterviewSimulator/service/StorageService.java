// com/AdaptiveInterviewSimulator/service/StorageService.java
package com.AdaptiveInterviewSimulator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    @Value("${app.storage.base-path:./storage/audio}")
    private String basePath;

    public String saveAudio(MultipartFile file, Long sessionId, Integer sequenceIndex) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : ".webm";
        Path sessionDir = Paths.get(basePath, String.valueOf(sessionId));
        Files.createDirectories(sessionDir);
        String filename = "answer_" + System.currentTimeMillis() + ext;
        Path target = sessionDir.resolve(filename);
        file.transferTo(target.toFile());
        return target.toAbsolutePath().toString();
    }
}
