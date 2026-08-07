package com.juliandonati.backendPortafolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class ImageModerationServiceImpl implements ImageModerationService{
    private final Logger logger = LoggerFactory.getLogger(ImageModerationServiceImpl.class);

    @Value("${SIGHTENGINE_API_USER}")
    private String apiUser;
    @Value("${SIGHTENGINE_API_SECRET}")
    private String apiSecret;

    // Cliente HTTP nativo de Spring y Jackson para parsear el JSON
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isImageSafe(MultipartFile file) throws IOException {
        try {
            // URL
            String url = "https://api.sightengine.com/1.0/check.json" +
                    "?models=nudity-2.0,gore" +
                    "&api_user=" + apiUser +
                    "&api_secret=" + apiSecret;

            // Preparar el archivo para enviarlo por HTTP
            // RestTemplate requiere que el ByteArrayResource tenga un nombre de archivo
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
                }
            };

            // Configurar los Headers (multipart/form-data)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 4. Armar el cuerpo de la petición
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("media", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 5. Enviar la petición a Sightengine
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            // 6. Analizar la respuesta JSON
            boolean isPossiblyUnsafe = isPossiblyUnsafe(response.getBody());

            logger.debug("El archivo subido es {}",isPossiblyUnsafe ? "SFW :D" : "NSFW :(");
            return isPossiblyUnsafe;

        } catch (Exception e) {
            logger.error("Error al comunicarse con Sightengine: {}", e.getMessage());
            // Si la API falla por alguna razón, rechazamos la imagen por seguridad
            return false;
        }
    }

    @Override
    public boolean isPossiblyUnsafe(String jsonResponse) throws Exception {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Verificamos si la API devolvió un estado de éxito
            if (!"success".equals(rootNode.path("status").asText())) {
                throw new RuntimeException("Fallo en la API de Sightengine: " + rootNode.path("error").path("message").asText());
            }

            // --- Evaluación de Desnudez (nudity-2.0) ---
            // 'none' es la probabilidad de que la imagen sea totalmente segura (va de 0.0 a 1.0)
            double safeNudityScore = rootNode.path("nudity").path("none").asDouble();

            // --- Evaluación de Violencia (gore) ---
            double probabilityGore = rootNode.path("gore").path("prob").asDouble();

            // Consideramos la imagen insegura si el score de "seguro" es menor al 80% (0.8)
            // o si la probabilidad de violencia es mayor al 20% (0.2).
            // Puedes ajustar estos umbrales según lo estricto que quieras ser.
            boolean containsNudity = safeNudityScore < 0.8;
            boolean containsGore = probabilityGore > 0.2;

            // Si contiene alguna de las dos, la imagen NO es segura (devuelve false)
            return !(containsNudity || containsGore);
        }
}
