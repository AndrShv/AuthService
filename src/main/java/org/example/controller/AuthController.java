package org.example.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.DTO.JwtResponse;
import org.example.DTO.LoginRequest;
import org.example.DTO.RegisterRequest;
import org.example.entity.User;
import org.example.event.UserRegisteredEvent;
import org.example.service.AuthService;
import org.example.service.JwtService;
import org.example.service.MessageSending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final MessageSending messageSending;
    private final JwtService jwtService;




    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password())
        );

        User user = authService.getUserByIdentifier(loginRequest.identifier());
        String token = jwtService.generateToken(user);

        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        messageSending.sendUserRegisteredEvent(event);

        return ResponseEntity.ok(new JwtResponse(token));
    }


}


