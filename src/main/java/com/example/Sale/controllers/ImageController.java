package com.example.Sale.controllers;


import com.example.Sale.repositories.ImageRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageRepository imageRepository; // предположим, что есть imageRepository

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType(image.getContentType()))
                        .body(image.getBytes()))
                .orElse(ResponseEntity.notFound().build());
    }
}