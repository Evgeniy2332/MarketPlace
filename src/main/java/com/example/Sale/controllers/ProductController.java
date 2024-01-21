package com.example.Sale.controllers;

<<<<<<< HEAD
=======
import com.example.Sale.models.Image;
>>>>>>> 5271124 (Initial commit)
import com.example.Sale.models.Product;
import com.example.Sale.models.User;
import com.example.Sale.repositories.ProductRepository;
import com.example.Sale.repositories.UserRepository;
import com.example.Sale.service.ProductService;
<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
=======
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
>>>>>>> 5271124 (Initial commit)
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/product/{id}")
    public String productInfo(@PathVariable Long id, Model model) {
        log.info("Запрос информации о продукте с ID: {}", id);
        model.addAttribute("product", productService.getProductById(id));
        return "product-info";
    }

    @GetMapping("/")
    public String products(@RequestParam(name = "title", required = false) String title, Model model) {
        log.info("Получение списка продуктов. Название продукта: {}", title != null ? title : "не указано");
        model.addAttribute("products", productService.listProducts(title));
        return "products";
    }

<<<<<<< HEAD
    //    @PostMapping("/product/create")
//    public String createProduct(Product product, MultipartFile file, MultipartFile file2, MultipartFile file3) throws IOException {
//        productService.saveProducts(product, file, file2,file3);
//        return "redirect:/";
//    }
//@PostMapping("/product/create")
//public String createProduct(Product product, MultipartFile file, MultipartFile file2, MultipartFile file3, Principal principal) throws IOException {
//    if (principal != null) {
//        // Получаем имя пользователя из principal и находим User в базе данных
//        String username = principal.getName();
//        User user = userRepository.findByEmailOpt(username).orElse(null);
//        product.setUser(user);
//        productService.saveProducts(product, file, file2, file3);
//        return "redirect:/";
//    } else {
//        return "redirect:/registration"; // Перенаправляем на страницу регистрации если пользователь не вошел в систему
//    }
//}
    @PostMapping("/product/create")
    public String createProduct(Product product, MultipartFile file, MultipartFile file2, MultipartFile file3, Principal principal) throws IOException {
=======
    // создание товара
    @PostMapping("/product/create")
    public String createProduct(@ModelAttribute Product product,
                                @RequestParam("files[0]") MultipartFile file1,
                                @RequestParam("files[1]") MultipartFile file2,
                                @RequestParam("files[2]") MultipartFile file3,
                                Principal principal) throws IOException {
>>>>>>> 5271124 (Initial commit)
        log.info("Попытка создания нового продукта с названием: {}", product.getTitle());
        if (principal != null) {
            String name = principal.getName();
            User user = userRepository.findByEmail(name);
            if (user != null) {
                product.setUser(user);
<<<<<<< HEAD
                productService.saveProducts(product, file, file2, file3);
=======
                productService.saveProducts(product, file1, file2, file3);
>>>>>>> 5271124 (Initial commit)
                log.info("Продукт '{}' сохранен успешно пользователем с email: {}", product.getTitle(), name);
                return "redirect:/lk";
            } else {
                log.warn("Попытка создания продукта пользователем, который не найден в системе");
                return "error_page";
            }
        } else {
            log.warn("Попытка создания продукта неавторизованным пользователем");
            return "redirect:/registration";
        }
    }

<<<<<<< HEAD
//    @PostMapping("/product/delete/{id}")
//    public String deleteProduct(@PathVariable Long id) {
//        productService.deleteProduct(id);
//        return "redirect:/";
//    }
@PostMapping("/product/delete/{id}")
public String deleteProduct(@PathVariable Long id, Principal principal) {
    log.info("Запрос на удаление продукта с ID: {}", id);
    if (principal != null) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && user.equals(product.getUser())) {
            productService.deleteProduct(id);
            log.info("Продукт с ID: {} был удален пользователем с email: {}", id, username);
        } else {
            log.warn("Пользователь с email: {} пытался удалить продукт, к которому не имеет права доступа", username);
        }
    } else {
        log.warn("Попытка удаления продукта неавторизованным пользователем");
    }
    return "redirect:/";
}

=======

    //обновление данных о товаре

    //    @PostMapping("/product/update")
//    public String updateProduct(@ModelAttribute Product product,
//                                @RequestParam(name = "deleteImage", required = false) List<String> deleteImages,
//                                @RequestParam(name = "file", required = false) MultipartFile file,
//                                @RequestParam(name = "file2", required = false) MultipartFile file2,
//                                @RequestParam(name = "file3", required = false) MultipartFile file3,
//                                Principal principal, Model model) throws IOException {
//        if (principal == null) {
//            return "redirect:/login"; // Пользователь должен быть авторизован, чтобы редактировать товар
//        }
//
//        Product oldProduct = productRepository.findById(product.getId()).orElse(null);
//        if (oldProduct == null) {
//            return "error_page"; // товар не найден
//        }
//
//        String email = principal.getName();
//        User user = userRepository.findByEmail(email);
//        if (!oldProduct.getUser().equals(user)) {
//            return "error_page"; // пользователь не является владельцем, не может редактировать
//        }
//
//        // Обновляем поля товара, кроме автора
//        oldProduct.setTitle(product.getTitle());
//        oldProduct.setDescription(product.getDescription());
//        oldProduct.setPrice(product.getPrice());
//        oldProduct.setCity(product.getCity());
//        oldProduct.setDateOfCreated(product.getDateOfCreated());
//
////        // Удаляем старые изображения
////        oldProduct.getImages().clear();
//        // Удаляем выбранные изображения
//        if (deleteImages != null) {
//            for (String deleteImageIndex : deleteImages) {
//                int index = Integer.parseInt(deleteImageIndex);
//                if (index >= 0 && index < oldProduct.getImages().size()) {
//                    oldProduct.getImages().remove(index);
//                }
//            }
//        }
//
//        // Добавляем новые изображения (если они загружены)
//        if (file != null && !file.isEmpty()) {
//            Image newImage = productService.toImageEntity(file);
//            oldProduct.addImageToProduct(newImage);
//        }
//        if (file2 != null && !file2.isEmpty()) {
//            Image newImage2 = productService.toImageEntity(file2);
//            oldProduct.addImageToProduct(newImage2);
//        }
//        if (file3 != null && !file3.isEmpty()) {
//            Image newImage3 = productService.toImageEntity(file3);
//            oldProduct.addImageToProduct(newImage3);
//        }
//
//        //productService.updateProduct(oldProduct); // Добавьте метод обновления продукта в ProductService
//        productService.updateProduct(oldProduct,file,file2,file3);
//
//        return "redirect:/lk"; // Перенаправление в личный кабинет после обновления товара
//    }
    @PostMapping("/product/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam(name = "deleteImage", required = false) List<String> deleteImages,
                                @RequestParam(name = "file", required = false) MultipartFile file,
                                @RequestParam(name = "file2", required = false) MultipartFile file2,
                                @RequestParam(name = "file3", required = false) MultipartFile file3,
                                Principal principal, Model model) throws IOException {
        if (principal == null) {
            return "redirect:/login"; // Пользователь должен быть авторизован, чтобы редактировать товар
        }

        Product oldProduct = productRepository.findById(product.getId()).orElse(null);
        if (oldProduct == null) {
            return "error_page"; // Товар не найден
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        if (!oldProduct.getUser().equals(user)) {
            return "error_page"; // Пользователь не является владельцем, не может редактировать
        }

        // Удаляем выбранные изображения
        if (deleteImages != null) {
            for (String deleteImageIndex : deleteImages) {
                int index = Integer.parseInt(deleteImageIndex);
                if (index >= 0 && index < oldProduct.getImages().size()) {
                    oldProduct.getImages().remove(index);
                }
            }
        }

        // Добавляем или заменяем фотографии
        productService.updateImages(oldProduct, file, 0);
        productService.updateImages(oldProduct, file2, 1);
        productService.updateImages(oldProduct, file3, 2);

        // Обновляем остальные поля товара
        oldProduct.setTitle(product.getTitle());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setCity(product.getCity());
        oldProduct.setDateOfCreated(product.getDateOfCreated());

        productService.updateProduct(oldProduct);

        return "redirect:/lk"; // Перенаправление в личный кабинет после обновления товара
    }


    @GetMapping("/product/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Пользователь должен быть авторизован, чтобы редактировать товар
        }

        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "error_page"; // товар не найден
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        if (!product.getUser().equals(user)) {
            return "error_page"; // пользователь не является владельцем, не может редактировать
        }

        model.addAttribute("product", product);
        return "edit-product";
    }

    //   УДАЛЕНИЕ ТОВАРА
    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Principal principal) {
        log.info("Запрос на удаление продукта с ID: {}", id);
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.findByEmail(username);
            Product product = productRepository.findById(id).orElse(null);
            if (product != null && user.equals(product.getUser())) {
                productService.deleteProduct(id);
                log.info("Продукт с ID: {} был удален пользователем с email: {}", id, username);
            } else {
                log.warn("Пользователь с email: {} пытался удалить продукт, к которому не имеет права доступа", username);
            }
        } else {
            log.warn("Попытка удаления продукта неавторизованным пользователем");
        }
        return "redirect:/lk";
    }

    // поиск
>>>>>>> 5271124 (Initial commit)
    @GetMapping("/search")
    public String search(@RequestParam("search") String search, Model model) {
        log.info("Выполнение поиска по запросу: {}", search);
        List<Product> searchResults = productService.searchProducts(search);
        model.addAttribute("products", searchResults);
        return "products";
    }

    // просмотр товаров пользователя в ЛК


    @GetMapping("/lk")
    public String lk(Principal principal, Model model) {
        log.info("Запрос на отображение личного кабинета пользователя");
        if (principal != null) {
            String email = principal.getName();
            User user = userRepository.findByEmail(email);
            if (user != null) {
                model.addAttribute("user", user);
                List<Product> products = productRepository.findAllByUser(user);
                model.addAttribute("products", products);
                log.info("Личный кабинет пользователя с email: {} загружен успешно", email);
            } else {
                log.warn("Пользователь с email: {} не найден при запросе личного кабинета", email);
                return "redirect:/login";
            }
        } else {
            log.warn("Доступ к личному кабинету неавторизованным пользователем");
            return "redirect:/login";
        }
        return "lk";
    }
<<<<<<< HEAD
=======


>>>>>>> 5271124 (Initial commit)
}
