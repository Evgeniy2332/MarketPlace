package com.example.Sale.service;

import com.example.Sale.models.Image;
import com.example.Sale.models.Product;
import com.example.Sale.repositories.ImageRepository;
import com.example.Sale.repositories.ProductRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final ImageRepository imageRepository;


    // метод для получения списка продуктов по названию или полного списка при его отсутствии
    public List<Product> listProducts(String title) {
        log.debug("Fetching product list, title filter: {}", title);

        if (title != null) {
            log.info("Finding products by title: {}", title);
            return productRepository.findByTitle(title);
        }

        log.info("Title not provided, fetching all products.");
        return productRepository.findAll();
    }

    // сохранение товара/услуги
    @Transactional
    public void saveProducts(Product product, MultipartFile... files) throws IOException {
        log.info("Сохранение товара с заголовком: {}", product.getTitle());
        if (files != null) { // Проверка, что массив файлов не null
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) { // Проверка, что файл не null и не пустой
                    Image image = toImageEntity(file);
                    if (file == files[0]) {
                        image.setPreviewImage(true);
                        log.debug("Установка изображения в качестве превью: {}", file.getOriginalFilename());
                    }
                    product.addImageToProduct(image);
                    log.debug("Добавлено изображение к товару: {}", file.getOriginalFilename());
                }
            }
        }
        log.info("Сохранение нового товара. Заголовок: {}; Автор: {}", product.getTitle(), product.getUser());
        productRepository.save(product);
    }

    // метод для наполнения продукта
    public Image toImageEntity(MultipartFile file) throws IOException {
        log.debug("Преобразование MultipartFile в сущность Image: {}", file.getOriginalFilename());

        Image image = new Image();
        image.setName(file.getName());
        image.setOriginFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());

        log.debug("Сущность Image создана для файла: {}", file.getOriginalFilename());
        return image;
    }

    public void updateProduct(Product product, MultipartFile... files) throws IOException {
        // Удаляем старые изображения
        product.getImages().clear();

        // Добавляем новые изображения (если они загружены)
        if (files != null) {
            for (int i=0; i< files.length && i<3; i++ ) {
                MultipartFile file = files[i];
                if (file != null && !file.isEmpty()) {
                    Image updateImage = toImageEntity(file);
                    if (i < product.getImages().size()) {
                        // Если изображение уже существует на указанной позиции, заменяем его
                        Image existingImage = product.getImages().get(i);
                        existingImage.setPreviewImage(true);
                        existingImage.setBytes(file.getBytes());
                        log.debug("Заменено изображение на позиции {}: {}", i, file.getOriginalFilename());
                    } else {
                        // Иначе, добавляем новое изображение
                        updateImage.setPreviewImage(i == 0);
                        product.addImageToProduct(updateImage);
                        log.debug("Добавлено изображение к товару: {}", file.getOriginalFilename());
                    }
                }
            }
        }
        log.info("Сохранение нового товара. Заголовок: {}; Автор: {}", product.getTitle(), product.getUser());
        productRepository.save(product);

    }
    public void updateImages(Product product, MultipartFile file, int index) throws IOException {
        if (file != null && !file.isEmpty()) {
            Image newImage = toImageEntity(file);
            if (index < product.getImages().size()) {
                // Заменяем существующее фото
                Image existingImage = product.getImages().get(index);
                existingImage.setPreviewImage(true);
                existingImage.setBytes(file.getBytes());
            } else if (index == product.getImages().size()) {
                // Добавляем новое фото, если есть свободное место
                newImage.setPreviewImage(index == 0);
                product.addImageToProduct(newImage);
            }
        }
    }


    // удаление продукта
    public void deleteProduct(Long id) {
        log.info("Удаление товара с id: {}", id);
        productRepository.deleteById(id);
        log.info("Товар успешно удален.");
    }

    // получение продукта по Id
    @Transactional(readOnly = true) // Добавляем управление транзакциями
    public Product getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            Hibernate.initialize(product.getImages()); // Инициализируем коллекцию
        }
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String title) {
        return productRepository.findByTitleIgnoreCase(title);
    }
}
