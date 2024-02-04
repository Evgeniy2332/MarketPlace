package com.example.Sale.controllers;



import com.example.Sale.models.Image;
import com.example.Sale.repositories.ImageRepository;


import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;


import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;


@RestController
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private final ImageRepository imageRepository; // предположим, что есть imageRepository

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
//        return imageRepository.findById(id)
//                .map(image -> ResponseEntity
//                        .ok()
//                        .contentType(MediaType.parseMediaType(image.getContentType()))
//                        .body(image.getBytes()))
//                .orElse(ResponseEntity.notFound().build());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            byte[] imageBytes = null;
            try {
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                imageBytes = transactionTemplate.execute(status -> {
                    Blob blob = image.getBytes();
                    byte[] bytes = null;
                    try {
                        bytes = blob.getBinaryStream().readAllBytes();
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                    return bytes;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (imageBytes != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(image.getContentType()));
                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            }
        }
        return ResponseEntity.notFound().build();
    }



}