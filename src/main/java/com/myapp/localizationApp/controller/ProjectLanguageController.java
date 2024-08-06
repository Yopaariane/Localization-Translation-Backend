package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.ProjectLanguageDto;
import com.myapp.localizationApp.service.ProjectLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projectLanguages")
public class ProjectLanguageController {
    @Autowired
    private ProjectLanguageService projectLanguageService;

    @PostMapping
    public ResponseEntity<ProjectLanguageDto> assignLanguageToProject(@RequestBody ProjectLanguageDto projectLanguageDto){
        ProjectLanguageDto createProjectLanguage = projectLanguageService.assignLanguageToProject(projectLanguageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createProjectLanguage);
    }

    @GetMapping ("/project/{projectId}")
    public ResponseEntity<List<ProjectLanguageDto>> getLanguageByProjectId(@PathVariable Long projectId){
        List<ProjectLanguageDto> projectLanguage = projectLanguageService.getLanguageByProjectId(projectId);
        return ResponseEntity.ok(projectLanguage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectLanguage(@PathVariable Long id){
        projectLanguageService.deleteProjectLanguage(id);
        return ResponseEntity.noContent().build();
    }
}
