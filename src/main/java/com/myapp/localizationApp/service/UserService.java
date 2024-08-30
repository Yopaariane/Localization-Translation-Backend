package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.UserDto;
import com.myapp.localizationApp.entity.User;
import com.myapp.localizationApp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;

    // Authentication management
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public UserDto saveUser(UserDto user) {
        User user1= new User();
        user1.setUsername(user.getName());
        user1.setEmail(user.getEmail());
        user1.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user1);
        user.setId(user1.getId().longValue());
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    // Role management
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDto userDTO = new UserDto();
                    userDTO.setId(user.getId().longValue());
                    userDTO.setName(user.getUsername());
                    userDTO.setEmail(user.getEmail());
                    return userDTO;
                });
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDto updateUser(Long id, UserDto updatedUserDto) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUserDto.getName());
                    user.setEmail(updatedUserDto.getEmail());
                    user.setPasswordHash(updatedUserDto.getPassword());
                    user = userRepository.save(user);
                    return updatedUserDto;
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return modelMapper.map(user, UserDto.class);
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }
}
