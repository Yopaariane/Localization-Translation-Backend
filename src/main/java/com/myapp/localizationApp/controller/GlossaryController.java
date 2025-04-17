package com.myapp.localizationApp.controller;

import com.myapp.localizationApp.dto.GlossaryDto;
import com.myapp.localizationApp.service.GlossaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/glossary")
public class GlossaryController {
    private final GlossaryService glossaryService;

    public GlossaryController(GlossaryService glossaryService){
        this.glossaryService = glossaryService;
    }

    @PostMapping
    public ResponseEntity<GlossaryDto> createGlossary(@RequestBody GlossaryDto glossaryDto) {
        GlossaryDto createdGlossary = glossaryService.createGlossary(glossaryDto);
        return new ResponseEntity<>(createdGlossary, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlossaryDto> updateGlossary(@PathVariable Long id, @RequestBody GlossaryDto glossaryDto) {
        GlossaryDto updatedGlossary = glossaryService.updateGlossary(id, glossaryDto);
        return ResponseEntity.ok(updatedGlossary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGlossary(@PathVariable Long id) {
        glossaryService.deleteGlossary(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<GlossaryDto>> getGlossaryByOrganizationId(@PathVariable Long organizationId) {
        List<GlossaryDto> glossaryList = glossaryService.findGlossaryByOrganizationId(organizationId);
        return ResponseEntity.ok(glossaryList);
    }
}
