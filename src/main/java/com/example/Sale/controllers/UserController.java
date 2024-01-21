package com.example.Sale.controllers;


import com.example.Sale.models.User;
import com.example.Sale.repositories.UserRepository;
import com.example.Sale.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService; // внедряем UserService
    private final UserRepository userRepository; // внедрение юзер репозитори


    // доступ к базе пользователей (userList)
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAllUsers(Model model) {
        log.info("Запрос на получение списка всех пользователей");
        model.addAttribute("users", userService.getAllUsers());
        return "userList";
    }

    // добавление роли Админ
    @PostMapping("/admin/users/{userId}/addRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addAdminRole(@PathVariable Long userId) {
        log.info("Добавление роли администратора пользователю с ID: {}", userId);
        userService.addAdminRole(userId);
        return "redirect:/admin/users";
    }

    // Метод для удаления роли администратора
    @PostMapping("/admin/users/{userId}/removeRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String removeAdminRole(@PathVariable Long userId) {
        log.info("Удаление роли администратора у пользователя с ID: {}", userId);
        userService.removeAdminRole(userId);
        return "redirect:/admin/users";
    }

    // Эндпоинт для переключения роли админа
    @PostMapping("/admin/users/{userId}/toggleRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String toggleAdminRole(@PathVariable Long userId) {
        log.info("Переключение роли администратора для пользователя с ID: {}", userId);
        userService.toggleAdminRole(userId);
        return "redirect:/admin/users";
    }

    // активация деактивация пользователя
    @PostMapping("/admin/users/{userId}/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deactivateUser(@PathVariable Long userId) {
        log.info("Деактивация пользователя с ID: {}", userId);
        userService.deactivateUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String activateUser(@PathVariable Long userId) {
        log.info("Активация пользователя с ID: {}", userId);
        userService.activateUser(userId);
        return "redirect:/admin/users";
    }

    // удаление пользователя
    @PostMapping("/admin/users/{userId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    // регистрация пользователя
    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        log.info("Попытка регистрации пользователя с email '{}'", user.getEmail());
        if (!userService.createUser(user)) {
            log.error("Ошибка регистрации: пользователь с email '{}' уже существует", user.getEmail());
            model.addAttribute("errorMessage", "Пользователь с email:" + user.getEmail() + "уже существует");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        log.info("Показ формы для регистрации пользователя");
        model.addAttribute("user", new User());
        return "registration";
    }

    @GetMapping("/login")
    public String login(Model model) {
        log.info("Показ формы логина");
        return "login";
    }

    @GetMapping("/edit-user")
    public String editUserForm(Principal principal, Model model) {
        log.info("Показ формы редактирования пользователя для текущего пользователя");
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName());
            model.addAttribute("user", user);
        }
        return "editUser";
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute User user) {
        log.info("Обновление информации пользователя с ID: {}", user.getId());
        userService.updateUser(user.getId(), user);
        return "redirect:/lk";
    }


}
