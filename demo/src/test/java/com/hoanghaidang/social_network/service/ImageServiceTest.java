package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.service.impl.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;
    @Mock
    private Path path;
    @Mock
    private UrlResource urlResource;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFiles_success() throws IOException {
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
        assertEquals("One of the files is not an image",response.getBody());
    }

    @Test
    void uploadFiles_fileIsEmpty(){
        MockMultipartFile file = new MockMultipartFile("file","text.txt","text/plain","".getBytes());
        List<MultipartFile> files = List.of(file);

        ResponseEntity<?> response = imageService.uploadFiles(files);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals("One of the files is empty",response.getBody());
    }

//    @Test
//    void downloadImage_fileExists() throws Exception {
//        String filename = "test.jpg";
//        Path filePath = mock(Path.class);  // Mock đối tượng Path
//        UrlResource resourceMock = mock(UrlResource.class);  // Mock đối tượng UrlResource
//
//        // Giả lập đường dẫn tới file
//        try (var mockedFiles = mockStatic(Files.class);
//             var mockedPath = mockStatic(Path.class)) {
//
//            // Mock Path.of()
//            mockedPath.when(() -> Path.of("/uploads")).thenReturn(filePath);
//            mockedPath.when(() -> filePath.resolve(filename)).thenReturn(filePath);
//            mockedPath.when(filePath::normalize).thenReturn(filePath);
//
//            // Mock Files.probeContentType()
//            mockedFiles.when(() -> Files.probeContentType(filePath)).thenReturn("image/jpeg");
//
//            // Giả lập file tồn tại và có thể đọc được
//            when(resourceMock.exists()).thenReturn(true);
//            when(resourceMock.isReadable()).thenReturn(true);
//            when(resourceMock.getURI()).thenReturn(filePath.toUri()); // Giả lập URL từ filePath
//
//            // Test service
//            ResponseEntity<?> response = imageService.downloadImage(filename);
//
//            // Kiểm tra kết quả
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertNotNull(response.getBody());
//            assertEquals("attachment; filename=\"" + filename + "\"", response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));
//        }
//    }


    @Test
    void downloadImage_fileNotFound() throws Exception{
        String filename = "nonexistent.jpg";

        ResponseEntity<?> response = imageService.downloadImage(filename);

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertEquals("File not found",response.getBody());
    }

    @Test
    void testDownloadImage_FileNotReadable() {
        String filename = "a.jpg";

        when(urlResource.exists()).thenReturn(true);
        when(urlResource.isReadable()).thenReturn(false);

        ResponseEntity<?> response = imageService.downloadImage(filename);

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertEquals("File not found",response.getBody());
    }

//    @Test
//    void testDownloadImage_Success() throws Exception {
//        String filename = "a.jpg";
//        Path filePath = Paths.get("uploads/").resolve(filename).normalize();
//        when(urlResource.exists()).thenReturn(true);
//        when(urlResource.isReadable()).thenReturn(true);
//        when(urlResource.getURL()).thenReturn(filePath.toUri().toURL());
//        when(Files.probeContentType(filePath)).thenReturn("image/jpeg");
//
//        ResponseEntity<?> response = imageService.downloadImage(filename);
//
//        assertEquals(HttpStatus.OK,response.getStatusCode());
//        assertEquals("image/jpeg",response.getHeaders().getContentType().toString());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody() instanceof UrlResource);
//    }

//    @Test
//    void testDownloadImage_MalformedURLException() throws IOException {
//        String filename ="a.jpg";
//        when(urlResource.exists()).thenReturn(true);
//        when(urlResource.isReadable()).thenReturn(true);
//        Path filePath = Paths.get("uploads/").resolve(filename).normalize();
//        when(Files.probeContentType(filePath)).thenThrow(new IOException("Malformed URL"));
//
//        ResponseEntity<?> response = imageService.downloadImage(filename);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
//        assertEquals("Malformed URL",response.getBody());
//    }

//    @Test
//    void testDownloadImage_IOException(){
//        String filename = "error.jpg";
//        // Giả lập lỗi
//        doThrow(new IOException()).when(resourceMock).exists();
//    }
}
