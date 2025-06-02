package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
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

