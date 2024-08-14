package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.LanguageDto;
import com.myapp.localizationApp.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/languages")
public class LanguageController {
    @Autowired
    private LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<LanguageDto>> getAllLanguages() {
        List<LanguageDto> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LanguageDto> getLanguageById(@PathVariable Long id) {
        Optional<LanguageDto> languageDto = languageService.getLanguageById(id);
        return languageDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
