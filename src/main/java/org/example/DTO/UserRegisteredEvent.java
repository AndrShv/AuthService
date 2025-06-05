package org.example.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserRegisteredEvent {
    private UUID userId;
    private String email;
    private String username;
    private Instant registeredAt;

    public UserRegisteredEvent(UUID userId, String email, String username, Instant registeredAt) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.registeredAt = registeredAt;
    }

}