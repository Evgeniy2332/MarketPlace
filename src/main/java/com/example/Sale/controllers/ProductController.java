package com.example.Sale.controllers;

import com.example.Sale.models.Product;
import com.example.Sale.models.User;
import com.example.Sale.repositories.ProductRepository;
import com.example.Sale.repositories.UserRepository;
import com.example.Sale.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


    @PostMapping("/product/create")
    public String createProduct(Product product, @RequestParam("files") MultipartFile[] files, Principal principal) throws IOException {
        log.info("Попытка создания нового продукта с названием: {}", product.getTitle());
        if (principal != null) {
            String name = principal.getName();
            User user = userRepository.findByEmail(name);
            if (user != null) {
                product.setUser(user);
                productService.saveProducts(product, files);
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

    @PostMapping("/product/update")
    public String updateProduct(
            Product product,
            @RequestParam(value = "file1", required = false) MultipartFile file1,
            @RequestParam(value = "file2", required = false) MultipartFile file2,
            @RequestParam(value = "file3", required = false) MultipartFile file3,
            Principal principal) throws IOException {
        log.info("Попытка обновления продукта с названием: {}", product.getTitle());
        if (principal != null) {
            String name = principal.getName();
            User user = userRepository.findByEmail(name);
            if (user != null) {
                product.setUser(user);
                productService.updateProduct(product, file1, file2, file3);
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
}
