package com.juliandonati.backendPortafolio.service;

import com.google.cloud.vision.v1.Likelihood;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageModerationService {
    boolean isImageSafe(MultipartFile file) throws IOException;
    boolean isPossiblyUnsafe(Likelihood likelihood);
}
