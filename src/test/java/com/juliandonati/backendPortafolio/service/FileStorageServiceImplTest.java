package com.juliandonati.backendPortafolio.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

import com.juliandonati.backendPortafolio.exception.UnsafeFileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;



import java.io.IOException;
import java.util.Map;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.TEST_THROWS_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {
    @Mock
    Cloudinary cloudinary;

    @Mock
    ImageModerationService imageModerationService;

    @InjectMocks
    FileStorageServiceImpl fileStorageService;

    @Test
    void testUploadImageUploadsImageSuccessfully() throws IOException {
        // Arrange
        Uploader mockUploader = mock(Uploader.class);

        MockMultipartFile mockImageMPFile = new MockMultipartFile(
                "mock-name",
                "mock-og-name",
                "image/jpeg",
                "mock content".getBytes());
        String mockImageUrl = "http://imagenprueba.com";
        when(cloudinary.uploader()).thenReturn(mockUploader);
        // Puede ser cualquier arreglo de bytes, porque quizás se comprime el archivo o se cambia su contenido de alguna manera.
        when(mockUploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("url",mockImageUrl));
        when(imageModerationService.isImageSafe(mockImageMPFile)).thenReturn(true);

        // Act
        String result = fileStorageService.uploadImage(mockImageMPFile,"pedrito");

        // Assert
        assertNotNull(result);
        assertEquals(mockImageUrl,result);
        verify(cloudinary,times(1)).uploader();
        verify(imageModerationService,times(1)).isImageSafe(mockImageMPFile);
        verify(mockUploader,times(1)).upload(any(byte[].class),anyMap());
    }

    @Test
    void testUploadImageThrowsUnsafeFileException() throws IOException {
        // Arrange
        Uploader mockUploader = mock(Uploader.class);

        MockMultipartFile mockImageMPFile = new MockMultipartFile(
                "mock-name",
                "mock-og-name",
                "image/jpeg",
                "mock content".getBytes());
        String mockImageUrl = "http://imagenprueba.com";
        // Puede ser cualquier arreglo de bytes, porque quizás se comprime el archivo o se cambia su contenido de alguna manera.
        when(imageModerationService.isImageSafe(mockImageMPFile)).thenReturn(false);

        // Act + Assert
        assertThrows(UnsafeFileException.class,()->fileStorageService.uploadImage(mockImageMPFile,"pedrito"));
        verify(cloudinary,never()).uploader();
        verify(imageModerationService,times(1)).isImageSafe(mockImageMPFile);
        verify(mockUploader,never()).upload(any(byte[].class),anyMap());
    }

    /*@Test
    void testDeleteAllFilesDeletesAllFilesSuccessfully() throws Exception{
        // Arrange
        Api mockApi = mock(Api.class);
        ReflectionTestUtils.setField(fileStorageService,"activeProfile","dev");

        when(cloudinary.api()).thenReturn(mockApi);
        when(mockApi.deleteAllResources(anyMap())).thenReturn(mock(ApiResponse.class));

        // Act + Assert
        assertDoesNotThrow(()->fileStorageService.deleteAllFiles(), TEST_THROWS_MESSAGE);
        verify(cloudinary,times(1)).api();
        verify(mockApi,times(1)).deleteAllResources(anyMap());
    }*/

    @Test
    void testDeleteAllFilesDoesNotProceed() throws Exception{
        // Arrange
        ReflectionTestUtils.setField(fileStorageService,"activeProfile","prod");

        // Act
        fileStorageService.deleteAllFiles();

        // Assert
        verify(cloudinary,never()).api();
    }

    @Test
    void testDeleteImageByUrlDeletesImageSuccessfully() throws Exception {
        // Arrange
        String mockUrl = "https://res.cloudinary.com/portfolio-propio/image/upload/v1775673455/file_iuofdx.jpg";
        Uploader mockUploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(mockUploader);
        when(mockUploader.destroy(eq("file_iuofdx"),anyMap())).thenReturn(mock(Map.class));

        // Act + Assert
        assertDoesNotThrow(()->fileStorageService.deleteImageByUrl(mockUrl),TEST_THROWS_MESSAGE);
        verify(cloudinary,times(1)).uploader();
        verify(mockUploader,times(1)).destroy(eq("file_iuofdx"),anyMap());
    }
}