package com.example.Sale.service;

import com.example.Sale.models.Image;
import com.example.Sale.models.Product;
import com.example.Sale.repositories.ImageRepository;
import com.example.Sale.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.LobCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final ImageRepository imageRepository;
    @PersistenceContext
    private EntityManager entityManager;


    // метод для получения списка продуктов по названию или полного списка при его отсутствии
    public List<Product> listProducts(String title) {
        log.debug("Получение списка продуктов, фильтр по названию: {}", title);

        if (title != null) {
            log.info("Поиск продуктов по названию: {}", title);
            return productRepository.findByTitle(title);
        }

        log.info("Название не указано, получение всех продуктов.");
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

        // Создайте Blob из массива байтов файла
        Blob blob = null;
        Session session = entityManager.unwrap(Session.class);
        LobHelper lobHelper = session.getLobHelper();
        blob = lobHelper.createBlob(file.getInputStream(), file.getSize());
        image.setBytes(blob);

        log.debug("Сущность Image создана для файла: {}", file.getOriginalFilename());
        return image;
    }
//    public Image toImageEntity(MultipartFile file) throws IOException {
//        log.debug("Преобразование MultipartFile в сущность Image: {}", file.getOriginalFilename());
//
//        Image image = new Image();
//        image.setName(file.getName());
//        image.setOriginFileName(file.getOriginalFilename());
//        image.setContentType(file.getContentType());
//        image.setSize(file.getSize());
//        image.setBytes(file.getBytes());
////        image.setBytes(file.getBytes());
//
//        log.debug("Сущность Image создана для файла: {}", file.getOriginalFilename());
//        return image;
//    }


    @Transactional
    public void updateProduct(Product product, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        // Load existing product from the database
        Product existingProduct = productRepository.findById(product.getId()).orElse(null);

        if (existingProduct != null) {
            // Update product details
            existingProduct.setTitle(product.getTitle());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCity(product.getCity());

            // Update images if new ones are provided
            if (file1 != null && !file1.isEmpty()) {
                updateImages(existingProduct, file1, 1);
            }
            if (file2 != null && !file2.isEmpty()) {
                updateImages(existingProduct, file2, 2);
            }
            if (file3 != null && !file3.isEmpty()) {
                updateImages(existingProduct, file3, 3);
            }

            // Save the updated product to the database
            productRepository.save(existingProduct);
            log.info("Товар обновлен: {}", existingProduct.getId());
        }
    }

    public void updateImages(Product product, MultipartFile file, int index) throws IOException {
        if (file != null && !file.isEmpty()) {
            Image newImage = toImageEntity(file);
            if (index < product.getImages().size()) {
                // Заменяем существующее фото
                Image existingImage = product.getImages().get(index);
                existingImage.setPreviewImage(true);
//                existingImage.setBytes(file.getBytes());
                existingImage.setBytes(newImage.getBytes());
            } else if (index == product.getImages().size()) {
                // Добавляем новое фото, если есть свободное место
                newImage.setPreviewImage(index == 0);
                product.addImageToProduct(newImage);
            }


        }
    }


    // удаление продукта
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Попытка удаления товара с id: {}", id);
        try {
            productRepository.deleteById(id);
            log.info("Товар успешно удален.");
        } catch (Exception e) {
            log.error("Ошибка при удалении товара с id: {}", id, e);
        }
    }

    // получение продукта по Id
    @Transactional(readOnly = true) // Добавляем управление транзакциями
    public Product getProductById(Long id) {
        log.info("Получение продукта с идентификатором: {}", id);
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            Hibernate.initialize(product.getImages()); // Инициализируем коллекцию
        }
        return product;
    }

//    @Transactional(readOnly = true)
//    public List<Product> searchProducts(String title) {
//        return productRepository.findByTitleIgnoreCase(title);
//    }
//}

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String title, String description, int minPrice, int maxPrice, String city, String author) {
        return productRepository.findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPriceBetweenAndCityContainingIgnoreCaseAndUser_NameContainingIgnoreCase(
                title, description, minPrice, maxPrice, city, author);
    }
}
