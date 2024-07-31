package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.service.UserService;
import com.myapp.localizationApp.dto.Response;
import com.myapp.localizationApp.dto.UserDto;
import com.myapp.localizationApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


//@RequestMapping("/users")
@RequestMapping("/api")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody UserDto user) {
        Optional<User> existingUser = userService.findByUsername(user.getName());
        if (existingUser.isPresent()) {
            Response response = new Response();
            response.setStatus("NOTOK");
            response.setData("Username already exists");
            return ResponseEntity.status(401).body(response);
        }
        userService.saveUser(user);
        Response response = new Response();
        response.setStatus("OK");
        response.setData("SignUp successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> foundUser = userService.findByUsername(user.getUsername());
        if (foundUser.isPresent() && userService.validatePassword(user.getPasswordHash(), foundUser.get().getPasswordHash())) {
            Response response = new Response();
            response.setStatus("OK");
            response.setData("Login successful");
            return ResponseEntity.ok(response);
        }
        Response response = new Response();
        response.setStatus("NOTOK");
        response.setData("Login failed");
        return ResponseEntity.status(401).body(response);
    }
}
