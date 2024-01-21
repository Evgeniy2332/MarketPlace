package com.example.Sale.service;

import com.example.Sale.models.Role;
import com.example.Sale.models.User;
import com.example.Sale.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User createUsers(User user) {
        log.info("Создание нового пользователя с email: {}", user.getEmail());
        user.init(); // явный вызов, если необходим, иначе JPA вызовет при сохранении
        User savedUser = userRepository.save(user);
        log.info("Создан новый пользователь с id: {}", savedUser.getId());
        return savedUser;
    }

    public boolean createUser(User user) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email) != null) {
            log.error("Пользователь с почтой {} уже существует.", email);
            return false;
        }
        user.setActive(true);
        user.setLoginCount(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        log.info("Сохранение нового пользователя с email: {}", email);
        userRepository.save(user);
        return true;
    }


    // добавление роли админа
    public void addAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь: " + userId + " не найден"));
        if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.getRoles().add(Role.ROLE_ADMIN);
            userRepository.save(user);
            log.info("Роль администратора добавлена пользователю с id: {}", userId);
        }
    }

    // снятие роли админа с пользователя
    public void removeAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь: " + userId + " не найден"));
        if (user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.getRoles().remove(Role.ROLE_ADMIN);
            userRepository.save(user);
            log.info("Роль администратора удалена у пользователя с id: {}", userId);
        }
    }

    // Переключение роли пользователя
    public void toggleAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь: " + userId + " не найден"));

        if (user.getRoles().contains(Role.ROLE_ADMIN)) {
            user.getRoles().remove(Role.ROLE_ADMIN);
            log.info("Роль администратора удалена у пользователя с id: {}", userId);
        } else {
            user.getRoles().add(Role.ROLE_ADMIN);
            log.info("Роль администратора добавлена пользователю с id: {}", userId);
        }
        userRepository.save(user);
    }
    //активация/деактивация пользователя

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь: " + userId + " не найден"));
        user.setActive(false);
        userRepository.save(user);
        log.info("Пользователь с id: {} деактивирован.", userId);
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь: " + userId + " не найден"));
        user.setActive(true);
        userRepository.save(user);
        log.info("Пользователь с id: {} активирован.", userId);
    }

    // обновление пользователя
    public User updateUser(Long id, User user) {
        log.info("Обновление пользователя с id: {}", id);
        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с id: " + id + " не найден"));

        updateProperties(existingUser, user); // Должен быть реализован этот метод

        try {
            User updatedUser = userRepository.save(existingUser);
            log.info("Пользователь с id: {} успешно обновлен.", id);
            return updatedUser;
        } catch (Exception e) {
            log.error("Произошла ошибка при обновлении пользователя с id: {}", id, e);
            throw new PersistenceException("Не удалось обновить пользователя с id: " + id, e);
        }
    }

    // настройки обновления
    private void updateProperties(User existingUser, User newUser) {
        if (newUser.getEmail() != null) existingUser.setEmail(newUser.getEmail());
        if (newUser.getPhoneNumber() != null) existingUser.setPhoneNumber(newUser.getPhoneNumber());
        if (newUser.getName() != null) existingUser.setName(newUser.getName());
        if (newUser.isActive() != existingUser.isActive()) existingUser.setActive(newUser.isActive());
        // Update other fields as necessary
        if (newUser.getRoles() != null) existingUser.setRoles(newUser.getRoles());
        // Add logic to update loginCount, if necessary
    }

    // удаление пользователя
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        userRepository.deleteById(id);
        log.info("Пользователь с id: {} удален.", id);
    }

    //получение всех пользователей
    public List<User> getAllUsers() {
        try {
            log.info("Запрос всех пользователей из базы данных.");
            List<User> users = userRepository.findAll();
            log.info("Из базы данных получено {} пользователей.", users.size());
            return users;
        } catch (Exception e) {
            log.error("Ошибка при получении всех пользователей из базы данных.", e);
            throw e;
        }
    }


}
