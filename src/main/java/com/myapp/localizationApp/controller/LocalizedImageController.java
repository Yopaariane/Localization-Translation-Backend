package com.myapp.localizationApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.localizationApp.dto.LocalizedImageDto;
import com.myapp.localizationApp.service.LocalizedImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/export/project/{projectId}/language/{languageId}")
    public ResponseEntity<Resource> exportImagesAsFile(
            @PathVariable Long projectId,
            @PathVariable Long languageId
    ) throws IOException {
        Map<String, String> export = imageService.exportImages(projectId, languageId);
        String jsonContent = new ObjectMapper().writeValueAsString(export);
        ByteArrayResource resource = new ByteArrayResource(jsonContent.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=image-export-" + projectId + "-" + languageId + ".json")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
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
