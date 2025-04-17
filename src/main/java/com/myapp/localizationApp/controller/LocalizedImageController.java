package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.LocalizedImageDto;
import com.myapp.localizationApp.service.LocalizedImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class LocalizedImageController {
    private final LocalizedImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<LocalizedImageDto> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("languageId") Long languageId,
            @RequestParam("projectId") Long projectId,
            @RequestParam("imageKey") String imageKey
    ) throws IOException {
        return ResponseEntity.ok(imageService.saveImage(file, languageId, projectId, imageKey));
    }

    @GetMapping("/export")
    public ResponseEntity<Map<String, String>> exportImages(
            @RequestParam Long projectId,
            @RequestParam Long languageId
    ) {
        return ResponseEntity.ok(imageService.exportImages(projectId, languageId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalizedImageDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<LocalizedImageDto>> getByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(imageService.getImagesByProjectId(projectId));
    }

    @GetMapping("/language/{languageId}")
    public ResponseEntity<List<LocalizedImageDto>> getByLanguageId(@PathVariable Long languageId) {
        return ResponseEntity.ok(imageService.getImagesByLanguageId(languageId));
    }

    @GetMapping("/project/{projectId}/language/{languageId}")
    public ResponseEntity<List<LocalizedImageDto>> getByProjectAndLanguage(@PathVariable Long projectId, @PathVariable Long languageId) {
        return ResponseEntity.ok(imageService.getImagesByProjectAndLanguage(projectId, languageId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalizedImageDto> update(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam String imageKey
    ) throws IOException {
        return ResponseEntity.ok(imageService.updateImage(id, file, imageKey));
    }
}
