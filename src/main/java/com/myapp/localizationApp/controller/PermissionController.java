package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.PermissionDto;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionDto> createPermission(@RequestBody PermissionDto permissionDto) {
        PermissionDto createdPermission = permissionService.createPermission(permissionDto);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }
}
