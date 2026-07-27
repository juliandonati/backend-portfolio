package com.juliandonati.backendPortafolio.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    public String uploadImage(MultipartFile imageMPFile, String username) throws IOException;
    public void deleteAllFiles() throws Exception;
    public void deleteImageByUrl(String imageUrl) throws Exception;
}
