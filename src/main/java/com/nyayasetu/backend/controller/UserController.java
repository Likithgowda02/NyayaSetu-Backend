package com.nyayasetu.backend.controller;

import java.util.HashMap;
import java.util.Map;

import com.nyayasetu.backend.entity.User;
import com.nyayasetu.backend.service.UserService;
import com.nyayasetu.backend.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {

        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User user) {

        User loggedInUser = userService.loginUser(
                user.getEmail(),
                user.getPassword()
        );

        if (loggedInUser != null) {

            // Generate JWT token
            String token = jwtService.generateToken(
                    loggedInUser.getEmail(),
                    loggedInUser.getRole()
            );

            // Create response
            Map<String, Object> response = new HashMap<>();

            response.put("token", token);
            response.put("id", loggedInUser.getId());
            response.put("name", loggedInUser.getName());
            response.put("email", loggedInUser.getEmail());
            response.put("phone", loggedInUser.getPhone());
            response.put("role", loggedInUser.getRole());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .status(401)
                .body("Invalid email or password");
    }

    // Protected endpoint - JWT required
    @GetMapping("/profile")
    public ResponseEntity<?> profile() {

        return ResponseEntity.ok(
                "JWT authentication is working!"
        );
    }
}