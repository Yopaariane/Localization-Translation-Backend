package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.OrganizationDto;
import com.myapp.localizationApp.dto.ProjectDto;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService){
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(@RequestBody OrganizationDto organizationDto) {
        OrganizationDto createdOrganization = organizationService.createOrganization(organizationDto);
        return new ResponseEntity<>(createdOrganization, HttpStatus.CREATED);
    }

    @PostMapping("/{organizationId}/assign-projects")
    public ResponseEntity<String> assignProjectsToOrganization(@PathVariable Long organizationId, @RequestBody List<Long> projectIds) {
        organizationService.assignProjectsToOrganization(organizationId, projectIds);
        return ResponseEntity.ok("Projects assigned successfully!");
    }

    @GetMapping("/{organizationId}/projects")
    public ResponseEntity<List<ProjectDto>> getProjectsByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(organizationService.getProjectsByOrganizationId(organizationId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrganizationDto>> getOrganizationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(organizationService.getOrganizationsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> getOrganizationsById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }
}
