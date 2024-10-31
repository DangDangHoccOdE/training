package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dto.request.UploadImageResponse;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.exception.CustomException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    private static final String UPLOAD_DIR = "uploads/";

    public ResponseEntity<ApiResponse<UploadImageResponse>> uploadFiles(List<MultipartFile> files) throws IOException {
        // Kiểm tra nếu danh sách file trống hoặc không có file nào
        if (files == null || files.isEmpty()) {
            throw new CustomException("No file to upload", HttpStatus.BAD_REQUEST);
        }

        // Dùng StringBuilder để lưu các đường dẫn ảnh sau khi upload thành công
        List<String>imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            // Kiểm tra nếu file trống
            if (file.isEmpty()) {
                throw new CustomException("One of the files is empty", HttpStatus.BAD_REQUEST);
            }

            long maxSize = 5 * 1024 * 1024;
            if(file.getSize() > maxSize){
                throw new CustomException("One of the files exceeds the maximum allowed size (5MB)",HttpStatus.BAD_REQUEST);
            }

            BufferedImage image = ImageIO.read(file.getInputStream());
            String contentType = file.getContentType();
            if (contentType == null || !isImage(contentType) || image == null) {
                throw new CustomException("One of the files is not an image", HttpStatus.BAD_REQUEST);
            }

            try {
                // Đặt tên file với UUID để tránh trùng lặp
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = Path.of(UPLOAD_DIR + fileName);

                // Kiểm tra xem thư mục upload đã tồn tại chưa, nếu chưa thì tạo
                if (!Files.exists(Paths.get(UPLOAD_DIR))) {
                    Files.createDirectories(Paths.get(UPLOAD_DIR));
                }

                // Ghi file vào thư mục
                Files.write(filePath, file.getBytes());
                String imageUrl = "/" + fileName;

                // Thêm đường dẫn vào StringBuilder
                imageUrls.add(imageUrl);

            } catch (IOException e) {
                throw new CustomException("An error occurred when uploading one of the files",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // Trả về danh sách đường dẫn các ảnh đã upload thành công

        ApiResponse<UploadImageResponse> apiResponse = ApiResponse.<UploadImageResponse>builder()
                .data(new UploadImageResponse(imageUrls))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    private boolean isImage(String contentType) {
        // Kiểm tra content type có phải là ảnh không
        return contentType.equals(MediaType.IMAGE_JPEG_VALUE) ||
                contentType.equals(MediaType.IMAGE_PNG_VALUE) ||
                contentType.equals(MediaType.IMAGE_GIF_VALUE);
    }

    public ResponseEntity<?> downloadImage(String filename) {
        try {
            String fileExtension = "";
            int lastIndex = filename.lastIndexOf(".");
            if(lastIndex > 0){
                fileExtension = filename.substring(lastIndex);
            }

            String newFileName = UUID.randomUUID().toString() + fileExtension;
            // Xác định đường dẫn file
            Path filePath = Path.of(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Kiểm tra xem file có tồn tại và có thể đọc
            if (resource.exists() && resource.isReadable()) {
                // Lấy kiểu nội dung của file
                String contentType = Files.probeContentType(filePath);

                // Nếu không lấy được contentType, sử dụng APPLICATION_OCTET_STREAM làm mặc định
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }

                // Đặt "Content-Disposition" để hiển thị hoặc download
                String headerValue = "attachment; filename=\"" + newFileName + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue) // Đặt header để download
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Notice("File not found"));
            }
        } catch (MalformedURLException e) {
            throw new CustomException("Malformed URL",HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CustomException("An error occurred when reading the file",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteImageFile(String filename) {
        Path filePath = Paths.get("uploads/").resolve(filename).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new CustomException("Failed to delete image file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
