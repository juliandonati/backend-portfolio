package com.juliandonati.backendPortafolio.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageModerationServiceImpl implements ImageModerationService{
    private final Logger logger = LoggerFactory.getLogger(ImageModerationServiceImpl.class);

    @Override
    public boolean isImageSafe(MultipartFile file) throws IOException {
        // Convertir el MultipartFile a Image (com.google.cloud.vision.v1.Image)
        ByteString imgBytes = ByteString.readFrom(file.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();

        // Indicar el uso de la detección de SafeSearch
        Feature feature = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(img).build();

        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        // Inicializar el cliente
        try(ImageAnnotatorClient client = ImageAnnotatorClient.create()){
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for(AnnotateImageResponse res : responses){
                if(res.hasError()) {
                    logger.warn("Error de la API: {}", res.getError().getMessage());
                    return false; // (Rechazamos ante la duda)
                }

                SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();

                // Evaluamos si contiene contenido para adultos / violento
                boolean isPossiblyAdult = isPossiblyUnsafe(annotation.getAdult());
                boolean isPossiblyViolence = isPossiblyUnsafe(annotation.getViolence());
                boolean isPossiblyRacy = isPossiblyUnsafe(annotation.getRacy());

                if(isPossiblyAdult || isPossiblyRacy || isPossiblyViolence)
                    return false;
            }
        }

        return true; // Pasó por todos los filtros
    }

    @Override
    public boolean isPossiblyUnsafe(Likelihood likelihood) {
        return likelihood == Likelihood.POSSIBLE || likelihood == Likelihood.LIKELY || likelihood == Likelihood.VERY_LIKELY;
    }
}
