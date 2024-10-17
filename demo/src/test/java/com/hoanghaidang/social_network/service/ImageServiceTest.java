package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.impl.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;
    @Mock
    private UrlResource urlResource;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFiles_success() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        List<MultipartFile> files = List.of(file);

        try (var mocked = mockStatic(Files.class)) {
            mocked.when(() -> Files.exists(Paths.get("uploads/"))).thenReturn(false);
            mocked.when(() -> Files.createDirectories(Paths.get("uploads/"))).thenReturn(null);
            mocked.when(() -> Files.write(Path.of("uploads/test.jpg"), file.getBytes())).thenReturn(null);

            ResponseEntity<?> response = imageService.uploadFiles(files);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void uploadFiles_fileNotImage(){
        MockMultipartFile file = new MockMultipartFile("file","text.txt","text/plain","test text".getBytes());
        List<MultipartFile> files = List.of(file);

        ResponseEntity<?> response = imageService.uploadFiles(files);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(new Notice("One of the files is not an image"),response.getBody());
    }

    @Test
    void uploadFiles_fileIsEmpty(){
        MockMultipartFile file = new MockMultipartFile("file","text.txt","text/plain","".getBytes());
        List<MultipartFile> files = List.of(file);

        ResponseEntity<?> response = imageService.uploadFiles(files);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(new Notice("One of the files is empty"),response.getBody());
    }

    @Test
    void downloadImage_fileNotFound(){
        String filename = "nonexistent.jpg";

        ResponseEntity<?> response = imageService.downloadImage(filename);

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertEquals(new Notice("File not found"),response.getBody());
    }

    @Test
    void testDownloadImage_FileNotReadable() {
        String filename = "a.jpg";

        when(urlResource.exists()).thenReturn(true);
        when(urlResource.isReadable()).thenReturn(false);

        ResponseEntity<?> response = imageService.downloadImage(filename);

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertEquals(new Notice("File not found"),response.getBody());
    }

    @Test
    void testDownloadImage_Success() throws Exception {
        String filename = "4fd0f963-fbf8-4570-8db9-16d91c23b4ca_anh3.jpg";
        Path filePath = Paths.get("uploads/").resolve(filename).normalize();

        // Mock UrlResource
        when(urlResource.exists()).thenReturn(true);
        when(urlResource.isReadable()).thenReturn(true);
        when(urlResource.getURL()).thenReturn(filePath.toUri().toURL());

        // Mock phương thức tĩnh Files.probeContentType
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.probeContentType(filePath)).thenReturn("image/jpeg");

            ResponseEntity<?> response = imageService.downloadImage(filename);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("image/jpeg", Objects.requireNonNull(response.getHeaders().getContentType()).toString());
            assertNotNull(response.getBody());
            assertInstanceOf(UrlResource.class, response.getBody());
        }
    }
}
