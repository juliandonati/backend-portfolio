package com.juliandonati.backendPortafolio.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageModerationServiceImplTest {

    @Mock
    RestTemplate restTemplate;

    private ImageModerationServiceImpl imageModerationService;

    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // No usamos @InjectMocks: el campo "restTemplate" de la clase ya viene
        // inicializado en su declaración (private final RestTemplate restTemplate = new RestTemplate()),
        // y la inyección por campo de Mockito solo pisa campos que estén en null.
        // Como resultado, @InjectMocks dejaría pasar el RestTemplate real sin avisar.
        // Por eso instanciamos manualmente y forzamos el reemplazo por reflection.
        imageModerationService = new ImageModerationServiceImpl();
        ReflectionTestUtils.setField(imageModerationService, "restTemplate", restTemplate);

        mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        // Los campos @Value no se resuelven en un test unitario, los seteamos a mano
        ReflectionTestUtils.setField(imageModerationService, "apiUser", "fake-user");
        ReflectionTestUtils.setField(imageModerationService, "apiSecret", "fake-secret");
    }

    private String buildJson(String status, double safeNudityScore, double goreProbability) {
        return String.format(
                "{\"status\":\"%s\",\"nudity\":{\"none\":%s},\"gore\":{\"prob\":%s}}",
                status, safeNudityScore, goreProbability
        );
    }

    // ---------- Tests de isImageSafe ----------

    @Test
    void testIsImageSafeReturnsTrueWhenSightengineReportsSafeImage() throws IOException {
        // Arrange
        String json = buildJson("success", 0.95, 0.01);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertTrue(result);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testIsImageSafeReturnsFalseWhenSightengineReportsNudity() throws IOException {
        // Arrange (safeNudityScore < 0.8 => se considera desnudez)
        String json = buildJson("success", 0.5, 0.01);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsImageSafeReturnsFalseWhenSightengineReportsGore() throws IOException {
        // Arrange (probabilityGore > 0.2 => se considera violento)
        String json = buildJson("success", 0.95, 0.5);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsImageSafeReturnsFalseWhenRestTemplateThrowsException() throws IOException {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Timeout de conexión"));

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert (el catch general del método rechaza la imagen ante cualquier falla)
        assertFalse(result);
    }

    @Test
    void testIsImageSafeReturnsFalseWhenApiRespondsWithErrorStatus() throws IOException {
        // Arrange
        String json = "{\"status\":\"failure\",\"error\":{\"message\":\"Créditos insuficientes\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act (isPossiblyUnsafe lanza RuntimeException, que el catch general de isImageSafe absorbe)
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsImageSafeReturnsFalseWhenFileThrowsIOException() throws IOException {
        // Arrange
        MultipartFile brokenFile = mock(MultipartFile.class);
        when(brokenFile.getBytes()).thenThrow(new IOException("No se pudo leer el archivo"));

        // Act
        boolean result = imageModerationService.isImageSafe(brokenFile);

        // Assert
        assertFalse(result);
        verifyNoInteractions(restTemplate);
    }

    // ---------- Tests de isPossiblyUnsafe ----------

    @Test
    void testIsPossiblyUnsafeReturnsTrueWhenImageIsSafe() throws Exception {
        // Arrange
        String json = buildJson("success", 0.9, 0.05);

        // Act
        boolean result = imageModerationService.isPossiblyUnsafe(json);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsPossiblyUnsafeReturnsFalseWhenNudityScoreIsBelowThreshold() throws Exception {
        // Arrange
        String json = buildJson("success", 0.79, 0.0);

        // Act
        boolean result = imageModerationService.isPossiblyUnsafe(json);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsPossiblyUnsafeReturnsFalseWhenGoreProbabilityExceedsThreshold() throws Exception {
        // Arrange
        String json = buildJson("success", 1.0, 0.21);

        // Act
        boolean result = imageModerationService.isPossiblyUnsafe(json);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsPossiblyUnsafeThrowsExceptionWhenStatusIsNotSuccess() {
        // Arrange
        String json = "{\"status\":\"failure\",\"error\":{\"message\":\"API key inválida\"}}";

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> imageModerationService.isPossiblyUnsafe(json));
        assertTrue(exception.getMessage().contains("Fallo en la API de Sightengine"));
    }
}