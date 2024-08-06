package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.service.UserService;
import com.myapp.localizationApp.dto.Response;
import com.myapp.localizationApp.dto.UserDto;
import com.myapp.localizationApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            Response response = new Response(
                    "NOTOK",
                    "Login failed",
                    null,
                    null,
                    null
            );
            return ResponseEntity.status(401).body(response);
        }
        userService.saveUser(user);
        Response response = new Response(
                "OK",
                "SignUp successful",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody User user) {
//        Optional<User> foundUser = userService.findByUsername(user.getUsername());
//        if (foundUser.isPresent() && userService.validatePassword(user.getPasswordHash(), foundUser.get().getPasswordHash())) {
//            Response response = new Response();
//            response.setStatus("OK");
//            response.setData("Login successful");
//
//            return ResponseEntity.ok(response);
//        }
//        Response response = new Response();
//        response.setStatus("NOTOK");
//        response.setData("Login failed");
//        return ResponseEntity.status(401).body(response);
//    }
@PostMapping("/login")
public ResponseEntity<Response> login(@RequestBody UserDto userDto) {
    Optional<User> foundUser = userService.findByUsername(userDto.getName());

    if (foundUser.isPresent() && userService.validatePassword(userDto.getPassword(), foundUser.get().getPasswordHash())) {
        User user = foundUser.get();
        Response response = new Response(
                "OK",
                "Login successful",
                user.getId().longValue(),
                user.getUsername(),
                user.getEmail()
        );
        return ResponseEntity.ok(response);
    } else {
        Response response = new Response(
                "NOTOK",
                "Login failed",
                null,
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}

}
