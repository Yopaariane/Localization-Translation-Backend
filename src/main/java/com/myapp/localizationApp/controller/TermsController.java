package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.service.TermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terms")
public class TermsController {
    @Autowired
    private TermsService termsService;

    @PostMapping
    public ResponseEntity<TermsDto> createTerm(@RequestBody TermsDto termsDto) {
        TermsDto createdTerm = termsService.createTerm(termsDto);
        return new ResponseEntity<>(createdTerm, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TermsDto> updateTerm(@PathVariable Long id, @RequestBody TermsDto termsDto) {
        TermsDto updatedTerm = termsService.updateTerm(id, termsDto);
        return ResponseEntity.ok(updatedTerm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerm(@PathVariable Long id) {
        termsService.deleteTerm(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TermsDto> getTermById(@PathVariable Long id) {
        TermsDto term = termsService.findById(id);
        return ResponseEntity.ok(term);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TermsDto>> getTermsByProjectId(@PathVariable Long projectId) {
        List<TermsDto> termsList = termsService.findTermsByProjectId(projectId);
        return ResponseEntity.ok(termsList);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTermsCountByProjectId(@RequestParam Long projectId) {
        long count = termsService.countTermsByProjectId(projectId);
        return ResponseEntity.ok(count);
    }
}
