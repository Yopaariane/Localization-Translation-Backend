package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.UserDto;
import com.myapp.localizationApp.entity.User;
import com.myapp.localizationApp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @CachePut(value = "userById", key = "#result.id")
    @CacheEvict(value = {"userByEmail", "userByUsername"}, key = "#user.email + '-' + #user.username")
    public UserDto saveUser(UserDto user) {
        User user1= new User();
        user1.setUsername(user.getName());
        user1.setEmail(user.getEmail());
        user1.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user1);
        user.setId(user1.getId().longValue());
        return user;
    }

    @Cacheable(value = "userByUsername", key = "#username")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    // Role management
    @Cacheable(value = "userById", key = "#id")
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

    @Cacheable(value = "allUsers", key = "'all'")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CachePut(value = {"userByEmail", "userById", "userByUsername"}, key = "#updatedUserDto.email + '-' + #id + '-' + #updatedUserDto.username")
    @CacheEvict(value = {"userByEmail", "userByUsername"}, key = "#existingUser.email + '-' + #existingUser.username")
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

    @CacheEvict(value = {"userByEmail", "userById", "userByUsername"}, key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Cacheable(value = "userByEmail", key = "#email")
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
