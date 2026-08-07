package com.juliandonati.backendPortafolio.service;

import com.google.cloud.vision.v1.*;
import com.google.rpc.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageModerationServiceImplTest {

    @Mock
    ImageAnnotatorClient imageAnnotatorClient;

    @InjectMocks
    private ImageModerationServiceImpl imageModerationService;

    private MockedStatic<ImageAnnotatorClient> mockedStaticClient;

    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        // Mockeamos el método estático ImageAnnotatorClient.create() para que
        // devuelva nuestro mock en lugar de intentar conectarse a Google Vision de verdad
        mockedStaticClient = mockStatic(ImageAnnotatorClient.class);
        mockedStaticClient.when(ImageAnnotatorClient::create).thenReturn(imageAnnotatorClient);
    }

    @AfterEach
    void tearDown() {
        mockedStaticClient.close();
    }

    private BatchAnnotateImagesResponse buildResponse(Likelihood adult, Likelihood violence, Likelihood racy) {
        SafeSearchAnnotation annotation = SafeSearchAnnotation.newBuilder()
                .setAdult(adult)
                .setViolence(violence)
                .setRacy(racy)
                .build();

        AnnotateImageResponse annotateImageResponse = AnnotateImageResponse.newBuilder()
                .setSafeSearchAnnotation(annotation)
                .build();

        return BatchAnnotateImagesResponse.newBuilder()
                .addResponses(annotateImageResponse)
                .build();
    }

    private BatchAnnotateImagesResponse buildErrorResponse() {
        AnnotateImageResponse errorResponse = AnnotateImageResponse.newBuilder()
                .setError(Status.newBuilder().setMessage("Error simulado de la API").build())
                .build();

        return BatchAnnotateImagesResponse.newBuilder()
                .addResponses(errorResponse)
                .build();
    }

    @Test
    void testIsImageSafeReturnsTrueWhenAllLikelihoodsAreSafe() throws IOException {
        // Arrange
        BatchAnnotateImagesResponse response = buildResponse(
                Likelihood.VERY_UNLIKELY, Likelihood.UNLIKELY, Likelihood.UNKNOWN
        );
        when(imageAnnotatorClient.batchAnnotateImages(anyList())).thenReturn(response);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertTrue(result);
        verify(imageAnnotatorClient, times(1)).batchAnnotateImages(anyList());
        verify(imageAnnotatorClient, times(1)).close();
    }

    @Test
    void testIsImageSafeReturnsFalseWhenAdultContentIsPossible() throws IOException {
        // Arrange
        BatchAnnotateImagesResponse response = buildResponse(
                Likelihood.POSSIBLE, Likelihood.VERY_UNLIKELY, Likelihood.VERY_UNLIKELY
        );
        when(imageAnnotatorClient.batchAnnotateImages(anyList())).thenReturn(response);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertFalse(result);
        verify(imageAnnotatorClient, times(1)).batchAnnotateImages(anyList());
    }

    @Test
    void testIsImageSafeReturnsFalseWhenViolenceIsLikely() throws IOException {
        // Arrange
        BatchAnnotateImagesResponse response = buildResponse(
                Likelihood.VERY_UNLIKELY, Likelihood.LIKELY, Likelihood.VERY_UNLIKELY
        );
        when(imageAnnotatorClient.batchAnnotateImages(anyList())).thenReturn(response);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsImageSafeReturnsFalseWhenRacyIsVeryLikely() throws IOException {
        // Arrange
        BatchAnnotateImagesResponse response = buildResponse(
                Likelihood.VERY_UNLIKELY, Likelihood.VERY_UNLIKELY, Likelihood.VERY_LIKELY
        );
        when(imageAnnotatorClient.batchAnnotateImages(anyList())).thenReturn(response);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsImageSafeReturnsFalseWhenApiResponseHasError() throws IOException {
        // Arrange
        BatchAnnotateImagesResponse response = buildErrorResponse();
        when(imageAnnotatorClient.batchAnnotateImages(anyList())).thenReturn(response);

        // Act
        boolean result = imageModerationService.isImageSafe(mockFile);

        // Assert
        assertFalse(result);
        verify(imageAnnotatorClient, times(1)).batchAnnotateImages(anyList());
    }

    @Test
    void testIsImageSafeSendsCorrectRequestToClient() throws IOException {
        // Arrange
        BatchAnnotateImagesResponse response = buildResponse(
                Likelihood.VERY_UNLIKELY, Likelihood.VERY_UNLIKELY, Likelihood.VERY_UNLIKELY
        );
        when(imageAnnotatorClient.batchAnnotateImages(anyList())).thenReturn(response);

        // Act
        imageModerationService.isImageSafe(mockFile);

        // Assert
        ArgumentCaptor<List<AnnotateImageRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(imageAnnotatorClient).batchAnnotateImages(captor.capture());

        List<AnnotateImageRequest> capturedRequests = captor.getValue();
        assertEquals(1, capturedRequests.size());
        assertEquals(Feature.Type.SAFE_SEARCH_DETECTION, capturedRequests.get(0).getFeatures(0).getType());
    }

    @Test
    void testIsPossiblyUnsafeReturnsTrueForPossible() {
        assertTrue(imageModerationService.isPossiblyUnsafe(Likelihood.POSSIBLE));
    }

    @Test
    void testIsPossiblyUnsafeReturnsTrueForLikely() {
        assertTrue(imageModerationService.isPossiblyUnsafe(Likelihood.LIKELY));
    }

    @Test
    void testIsPossiblyUnsafeReturnsTrueForVeryLikely() {
        assertTrue(imageModerationService.isPossiblyUnsafe(Likelihood.VERY_LIKELY));
    }

    @Test
    void testIsPossiblyUnsafeReturnsFalseForUnlikely() {
        assertFalse(imageModerationService.isPossiblyUnsafe(Likelihood.UNLIKELY));
    }

    @Test
    void testIsPossiblyUnsafeReturnsFalseForVeryUnlikely() {
        assertFalse(imageModerationService.isPossiblyUnsafe(Likelihood.VERY_UNLIKELY));
    }

    @Test
    void testIsPossiblyUnsafeReturnsFalseForUnknown() {
        assertFalse(imageModerationService.isPossiblyUnsafe(Likelihood.UNKNOWN));
    }
}