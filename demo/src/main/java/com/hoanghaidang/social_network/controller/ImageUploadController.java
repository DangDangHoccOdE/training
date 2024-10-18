package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.service.impl.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "Image Management", description = "APIs for managing Image")
public class ImageUploadController {
   ImageService imageService;

    @Operation(summary = "Upload", description = "Upload")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("files") List<MultipartFile> file) throws IOException {
        return imageService.uploadFiles(file);
    }

    @Operation(summary = "Download", description = "Download")
    @GetMapping(value = "/download")
    public ResponseEntity<?> getImage(@RequestParam("filename") String filename) {
        return imageService.downloadImage(filename);
    }
}
