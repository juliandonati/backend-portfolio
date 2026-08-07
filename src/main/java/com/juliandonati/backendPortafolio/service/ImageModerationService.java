package com.juliandonati.backendPortafolio.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageModerationService {
    boolean isImageSafe(MultipartFile file) throws IOException;
    boolean isPossiblyUnsafe(String jsonResponse) throws Exception;
}
