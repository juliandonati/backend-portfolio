package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.exception.UnsafeFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface FileStorageService {
    String uploadImage(MultipartFile imageMPFile, String username) throws IOException, UnsafeFileException;
    void deleteAllFiles() throws Exception;
    void deleteImageByUrl(String imageUrl) throws Exception;
}
