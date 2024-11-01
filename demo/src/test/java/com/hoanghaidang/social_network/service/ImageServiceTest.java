package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.impl.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
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
    private UrlResource urlResource;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void uploadFiles_success() throws IOException {
//        // Tạo file giả lập MockMultipartFile
//        MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "image/jpeg", "test data".getBytes());
//        List<MultipartFile> files = List.of(file);
//
//        // Mock ImageIO để trả về một đối tượng BufferedImage giả lập
//        BufferedImage bufferedImageMock = mock(BufferedImage.class);
//        mockStatic(ImageIO.class).when(() -> ImageIO.read(file.getInputStream())).thenReturn(bufferedImageMock);
//
//        // Mock Files.exists và Files.write
//        var mockedFiles = mockStatic(Files.class);
//        mockedFiles.when(() -> Files.exists(Paths.get("uploads/"))).thenReturn(false);
//        mockedFiles.when(() -> Files.createDirectories(Paths.get("uploads/"))).thenReturn(null);
//        mockedFiles.when(() -> Files.write(any(Path.class), eq(file.getBytes()))).thenReturn(null);
//
//        // Gọi service
//        ResponseEntity<?> response = imageService.uploadFiles(files);
//
//        // Kiểm tra kết quả
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        UploadImageResponse body = (UploadImageResponse) response.getBody();
//        assertEquals(1, body.getImages().size());
//    }
//
    @Test
    void uploadFiles_fileNotImage()  throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","text.txt","text/plain","test text".getBytes());
        List<MultipartFile> files = List.of(file);

        CustomException exception = assertThrows(CustomException.class, () -> imageService.uploadFiles(files));

        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
        assertEquals("One of the files is not an image",exception.getMessage());
    }

    @Test
    void uploadFiles_fileIsEmpty() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file","text.txt","text/plain","".getBytes());
        List<MultipartFile> files = List.of(file);

        CustomException exception = assertThrows(CustomException.class,()-> imageService.uploadFiles(files));

        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
        assertEquals("One of the files is empty",exception.getMessage());
    }

    @Test
    void downloadImage_fileNotFound(){
        String filename = "nonexistent.jpg";

        CustomException exception = assertThrows(CustomException.class,()-> imageService.downloadImage(filename));

        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
        assertEquals("File not found",exception.getMessage());
    }

    @Test
    void testDownloadImage_FileNotReadable() {
        String filename = "a.jpg";

        when(urlResource.exists()).thenReturn(true);
        when(urlResource.isReadable()).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class,()-> imageService.downloadImage(filename));

        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
        assertEquals("File not found",exception.getMessage());
    }

//    @Test
//    void testDownloadImage_Success() throws Exception {
//        String filename = "sample.jpg";
//        Path filePath = Paths.get("uploads/").resolve(filename).normalize();
//        Resource resource = new UrlResource(filePath.toUri());
//
//        // Mock UrlResource to represent the file to be downloaded
//        when(resource.exists()).thenReturn(true);
//        when(resource.isReadable()).thenReturn(true);
//
//        // Mock static Files.probeContentType to return "image/jpeg"
//        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
//            mockedFiles.when(() -> Files.probeContentType(filePath)).thenReturn("image/jpeg");
//
//            // Call the downloadImage method
//            ResponseEntity<?> response = imageService.downloadImage(filename);
//
//            // Assert response status and content
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertNotNull(response.getBody());
//            assertEquals("image/jpeg", response.getHeaders().getContentType().toString());
//            assertEquals("attachment; filename=\"" + filename + "\"", response.getHeaders().getContentDisposition().toString());
//        }
//    }


    @Test
    void deleteImageFile_success() throws IOException {
        String filename = "test_image.jpg";
        Path filePath = Paths.get("uploads/").resolve(filename).normalize();

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(filePath)).thenReturn(true);

            imageService.deleteImageFile(filename);

            mockedFiles.verify(() -> Files.deleteIfExists(filePath), times(1));
        }
    }

    @Test
    void deleteImageFile_fileNotFound() {
        String filename = "nonexistent_image.jpg";
        Path filePath = Paths.get("uploads/").resolve(filename).normalize();

        // Mocking Files.deleteIfExists to return false (file does not exist)
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(filePath)).thenReturn(false);

            // Call the method
            imageService.deleteImageFile(filename);

            // Verify that Files.deleteIfExists was called
            mockedFiles.verify(() -> Files.deleteIfExists(filePath), times(1));
        }
    }

    @Test
    void deleteImageFile_failure() {
        String filename = "test_image.jpg";
        Path filePath = Paths.get("uploads/").resolve(filename).normalize();

        // Mocking Files.deleteIfExists to throw an IOException
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(filePath)).thenThrow(new IOException("Deletion failed"));

            // Assert that CustomException is thrown
            CustomException exception = assertThrows(CustomException.class, () -> imageService.deleteImageFile(filename));

            // Verify the exception message and status
            assertEquals("Failed to delete image file", exception.getMessage());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        }
    }
}
