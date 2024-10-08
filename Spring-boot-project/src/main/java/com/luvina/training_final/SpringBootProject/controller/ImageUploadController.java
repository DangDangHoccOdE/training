package com.luvina.training_final.SpringBootProject.controller;

import com.luvina.training_final.SpringBootProject.service.impl.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
public class ImageUploadController {
    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("files") List<MultipartFile> file) {
        return imageService.uploadFiles(file);
    }

    @GetMapping("/upload")
    public ResponseEntity<?> getImage(@RequestParam("filename") String filename) {
        return imageService.getImage(filename);
    }
}
