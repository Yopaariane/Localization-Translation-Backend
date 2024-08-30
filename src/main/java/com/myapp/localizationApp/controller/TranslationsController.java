package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.service.TranslationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
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

    @GetMapping("/{termId}/progress")
    public ResponseEntity<Double> getTranslationProgressForTerm(@PathVariable Long termId) {
        double progress = translationsService.getTranslationProgressForTerm(termId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/progress/{languageId}/{projectId}")
    public ResponseEntity<Double> getTranslationProgressForLanguage(@PathVariable Long languageId, @PathVariable Long projectId) {
        double progress = translationsService.getTranslationProgressForLanguage(languageId, projectId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/progress/{projectId}")
    public  ResponseEntity<Double> getOverallTranslationProgressForProject(@PathVariable Long projectId) {
        double progress = translationsService.getOverallTranslationProgressForProject(projectId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/translation-progress/users/{userId}")
    public ResponseEntity<Double> getAverageTranslationProgressForUser(@PathVariable Long userId) {
        double averageProgress = translationsService.getAverageTranslationProgressForUser(userId);
        return ResponseEntity.ok(averageProgress);
    }

    @GetMapping("/total-strings/{ownerId}")
    public ResponseEntity<Integer> getTotalStringNumber(@PathVariable BigInteger ownerId) {
        Integer totalStringNumber = translationsService.getTotalStringNumberByOwnerId(ownerId);
        return ResponseEntity.ok(totalStringNumber);
    }

    @GetMapping("/string-progress/users/{userId}")
    public ResponseEntity<Integer> getStringsTranslationProgress(@PathVariable BigInteger userId) {
        int progress = translationsService.calculateStringsTranslationProgress(userId);
        return ResponseEntity.ok(progress);
    }
}
