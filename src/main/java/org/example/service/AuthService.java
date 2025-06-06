package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.DTO.LoginRequest;
import org.example.DTO.RegisterRequest;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Phone already registered");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already registered");
        }


        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository
                .findByEmailOrPhone(request.identifier(), request.identifier())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        return user;
    }

    public User getUserByIdentifier(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
