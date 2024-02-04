package com.example.Sale.repositories;

import com.example.Sale.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Blob;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    Optional<Image> findById(Long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO imageSales (name, origin_file_name, size, content_type, is_preview_image, bytes, product_id) " +
            "VALUES (:name, :originFileName, :size, :contentType, :isPreviewImage, :bytes, :productId)", nativeQuery = true)
    void saveImage(@Param("name") String name, @Param("originFileName") String originFileName, @Param("size") Long size,
                   @Param("contentType") String contentType, @Param("isPreviewImage") boolean isPreviewImage,
                   @Param("bytes") Blob bytes, @Param("productId") Long productId);

    // Загрузка LOB-объекта
    @Query(value = "SELECT bytes FROM imageSales WHERE id = :id", nativeQuery = true)
    Blob loadImage(@Param("id") Long id);
}
