package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.integration.util.UUIDConverter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class User {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email format")
    private String email;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^\\+380\\d{9}$", message = "Phone number must start with +380 and contain 9 digits")
    private String phone;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
}

