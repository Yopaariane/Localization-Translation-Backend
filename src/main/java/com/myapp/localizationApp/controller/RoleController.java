package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.RoleDto;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto roleDto = roleService.getRoleById(id);
        return  ResponseEntity.ok(roleDto);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roleDto = roleService.getAllRoles();
        return ResponseEntity.ok(roleDto);
    }
}
