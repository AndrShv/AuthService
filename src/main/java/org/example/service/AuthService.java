package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.DTO.LoginRequest;
import org.example.DTO.RegisterRequest;
import org.example.entity.User;
import org.example.DTO.UserRegisteredEvent;
import org.example.kafka.KafkaMessageProducer;
import org.example.repository.UserRepository;
import org.example.topic.KafkaTopic;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaMessageProducer kafkaMessageProducer;


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

        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId(),
                user.getUsername(),
                generateUserTag(user.getUsername()),
                Instant.now()
        );

        kafkaMessageProducer.send(KafkaTopic.USER_REGISTERED, event);
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

    private String generateUserTag(String username) {
        return username + "#" + UUID.randomUUID().toString().substring(0, 8);
    }


}
