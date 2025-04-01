package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.UserRoleDto;
import com.myapp.localizationApp.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userRoles")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    @PostMapping
    public ResponseEntity<UserRoleDto> assignRoleToUser(@RequestBody UserRoleDto userRoleDto) {
        UserRoleDto createdUserRole = userRoleService.assignRoleToUser(userRoleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserRole);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserRoleDto>> getRolesByUserId(@PathVariable Long userId) {
        List<UserRoleDto> userRoles = userRoleService.getRolesByUserId(userId);
        return ResponseEntity.ok(userRoles);
    }

    @GetMapping("/user/{userId}/role/{roleId}")
    public  ResponseEntity<List<UserRoleDto>> getByUserIdAndRoleId(@PathVariable Long userId, @PathVariable Long roleId) {
        List<UserRoleDto> userRoleDto = userRoleService.getByUserIdAndRoleId(userId, roleId);
        return ResponseEntity.ok(userRoleDto);
    }

    @GetMapping("/user/{userId}/project/{projectId}")
    public  ResponseEntity<List<UserRoleDto>> getByUserIdAndProjectId(@PathVariable Long userId, @PathVariable Long projectId) {
        List<UserRoleDto> userRoleDto = userRoleService.getByUserIdAndProjectId(userId, projectId);
        return ResponseEntity.ok(userRoleDto);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<UserRoleDto>> getRolesByProjectId(@PathVariable Long projectId) {
        List<UserRoleDto> userRoles = userRoleService.getRolesByProjectId(projectId);
        return ResponseEntity.ok(userRoles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable Long id) {
        userRoleService.deleteUserRole(id);
        return ResponseEntity.noContent().build();
    }
}
