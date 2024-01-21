package com.example.Sale.service;

import com.example.Sale.models.User;
import com.example.Sale.repositories.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        if (!user.isActive()) { // Проверяем, активен ли пользователь
            throw new DisabledException("Учетная запись пользователя деактивирована");
        }

        // Увеличиваем счетчик входов, если пользователь активен
        user.setLoginCount(user.getLoginCount() + 1);
        userRepo.save(user);

        return user;
    }


}


