package org.example.service;

import org.example.DTO.LoginRequest;
import org.example.DTO.RegisterRequest;
import org.example.entity.User;
import org.example.event.UserRegisteredEvent;
import org.example.event.VideoCreatingEvent;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("register")
    class Register {

        @BeforeEach
        void setUp() {
            lenient().when(userRepository.existsByEmail(anyString())).thenReturn(false);
            lenient().when(userRepository.existsByPhone(anyString())).thenReturn(false);
            lenient().when(userRepository.existsByUsername(anyString())).thenReturn(false);
            lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            lenient().when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(UUID.randomUUID());
                return user;
            });
        }


        @org.junit.jupiter.api.Test
        void registersUserAndSendsEvents() {
            RegisterRequest request = new RegisterRequest(
                    "user@example.com",
                    "+380123456789",
                    "user",
                    "password",
                    "password"
            );

            authService.register(request);

            verify(userRepository).save(any(User.class));
            verify(rabbitTemplate).convertAndSend(eq("user.exchange"), eq("user.registered"), any(UserRegisteredEvent.class));
            verify(rabbitTemplate).convertAndSend(eq("video.exchange"), eq("video.create"), any(VideoCreatingEvent.class));
        }

        @org.junit.jupiter.api.Test
        void throwsWhenPasswordsDoNotMatch() {
            RegisterRequest request = new RegisterRequest(
                    "user@example.com",
                    "+380123456789",
                    "user",
                    "password",
                    "different"
            );

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Passwords do not match");
        }

        @org.junit.jupiter.api.Test
        void throwsWhenEmailAlreadyRegistered() {
            when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

            RegisterRequest request = new RegisterRequest(
                    "user@example.com",
                    "+380123456789",
                    "user",
                    "password",
                    "password"
            );

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already registered");
        }

        @org.junit.jupiter.api.Test
        void throwsWhenPhoneAlreadyRegistered() {
            when(userRepository.existsByPhone("+380123456789")).thenReturn(true);

            RegisterRequest request = new RegisterRequest(
                    "user@example.com",
                    "+380123456789",
                    "user",
                    "password",
                    "password"
            );

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Phone already registered");
        }

        @org.junit.jupiter.api.Test
        void throwsWhenUsernameAlreadyRegistered() {
            when(userRepository.existsByUsername("user")).thenReturn(true);

            RegisterRequest request = new RegisterRequest(
                    "user@example.com",
                    "+380123456789",
                    "user",
                    "password",
                    "password"
            );

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Username already registered");
        }
    }


    @Nested
    @DisplayName("login")
    class Login {

        @org.junit.jupiter.api.Test
        void returnsUserWhenCredentialsAreCorrect() {
            User user = new User();
            user.setPassword("encodedPassword");
            when(userRepository.findByEmailOrPhone("user@example.com", "user@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

            User result = authService.login(new LoginRequest("user@example.com", "password"));

            assertThat(result).isEqualTo(user);
        }

        @org.junit.jupiter.api.Test
        void throwsWhenUserNotFound() {
            when(userRepository.findByEmailOrPhone("notfound", "notfound")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(new LoginRequest("notfound", "password")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }

        @org.junit.jupiter.api.Test
        void throwsWhenPasswordIsWrong() {
            User user = new User();
            user.setPassword("encodedPassword");
            when(userRepository.findByEmailOrPhone("user@example.com", "user@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "wrong")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wrong password");
        }
    }

    @Nested
    @DisplayName("getUserByIdentifier")
    class GetUserByIdentifier {

        @org.junit.jupiter.api.Test
        void returnsUserWhenFoundByUsername() {
            User user = new User();
            when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.of(user));

            User result = authService.getUserByIdentifier("user");

            assertThat(result).isEqualTo(user);
        }

        @org.junit.jupiter.api.Test
        void throwsWhenUserNotFound() {
            when(userRepository.findByUsernameOrEmail("unknown", "unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.getUserByIdentifier("unknown"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }
}