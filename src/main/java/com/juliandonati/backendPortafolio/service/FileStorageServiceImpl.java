package com.juliandonati.backendPortafolio.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Component
public class FileStorageServiceImpl implements FileStorageService {
    private final Cloudinary cloudinary;

    private final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    public FileStorageServiceImpl(Cloudinary c) {
        cloudinary = c;
    }

    @Value("${ACTIVE_PROFILE}")
    private String activeProfile;

    @Transactional
    public String uploadImage(MultipartFile imageMPFile, String username) throws IOException {
        Map<String, Object> uploaderConfig = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", true,
                "metadata", ObjectUtils.asMap(
                        "ownerusername", username
                )
        );

        Map uploadResult = cloudinary.uploader().upload(imageMPFile.getBytes(), uploaderConfig);

        return uploadResult.get("url").toString();
    }

    @Transactional
    @PreDestroy
    public void deleteAllFiles() throws Exception {
        /*if (activeProfile.equals("dev"))
            cloudinary.api().deleteAllResources(ObjectUtils.emptyMap());

            No habilitar con dirección de prod de Cloudinary */
    }

    @Transactional
    public void deleteImageByUrl(String imageUrl) throws Exception {
        if (imageUrl != null) {
            int lastSlashIndex = imageUrl.lastIndexOf('/');
            int extensionDotIndex = imageUrl.lastIndexOf('.');
            String imagePublicId = imageUrl.substring(lastSlashIndex + 1, extensionDotIndex);


            logger.debug("Eliminando imagen de publicId = {}", imagePublicId);

            cloudinary.uploader().destroy(imagePublicId, ObjectUtils.emptyMap());
            logger.debug("¡Imagen eliminada con éxito!");
        } else
            throw new IllegalArgumentException("imageUrl es null");
    }
}
