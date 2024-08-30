package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.ProjectDto;
import com.myapp.localizationApp.dto.ProjectLanguageDto;
import com.myapp.localizationApp.entity.ProjectLanguage;
import com.myapp.localizationApp.service.ProjectLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProjectLanguageDto> getProjectLanguageById(@PathVariable Long id){
        Optional<ProjectLanguageDto> projectLanguageDto = projectLanguageService.getProjectLanguageById(id);
        return projectLanguageDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectLanguage(@PathVariable Long id){
        projectLanguageService.deleteProjectLanguage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}/language/{languageId}")
    public ResponseEntity<ProjectLanguageDto> getProjectLanguage(@PathVariable Long projectId, @PathVariable Long languageId) {
        ProjectLanguageDto projectLanguageDto = projectLanguageService.getByLanguageIdAndProjectId(languageId, projectId);
        return ResponseEntity.ok(projectLanguageDto);
    }
}
