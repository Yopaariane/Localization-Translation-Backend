package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.service.TranslationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/translation")
public class TranslationsController {
    @Autowired
    private TranslationsService translationsService;

    @PostMapping
    public ResponseEntity<TranslationsDto> createTranslation(@RequestBody TranslationsDto translationsDto) {
        TranslationsDto createdTranslation = translationsService.createTranslation(translationsDto);
        return  new ResponseEntity<>(createdTranslation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TranslationsDto> updateTranslation(@PathVariable Long id, @RequestBody TranslationsDto translationsDto) {
        TranslationsDto updatedTranslation = translationsService.updateTranslation(id, translationsDto);
        return ResponseEntity.ok(updatedTranslation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Long id) {
        translationsService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<TranslationsDto>> getTranslationsByTermId(@PathVariable Long itemId) {
        List<TranslationsDto> translantionList = translationsService.getTranslationsByTermId(itemId);
        return ResponseEntity.ok(translantionList);
    }

    @GetMapping("/language/{languageId}")
    public ResponseEntity<List<TranslationsDto>> getTranslationsByLanguageId(@PathVariable Long languageId) {
        List<TranslationsDto> translationsDtoList = translationsService.getTranslationsByLanguageId(languageId);
        return ResponseEntity.ok(translationsDtoList);
    }

    @GetMapping("/count/{projectId}")
    public ResponseEntity<Long> countTranslationsByProjectId(@PathVariable Long projectId) {
        long count = translationsService.countTranslationsByProjectId(projectId);
        return ResponseEntity.ok(count);
    }
}
