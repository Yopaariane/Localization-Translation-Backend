package com.myapp.localizationApp.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.myapp.localizationApp.dto.ImportDto;
import com.myapp.localizationApp.dto.ProjectLanguageDto;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.repository.TermsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportService {
    @Autowired
    private TermsService termsService;

    @Autowired
    private TranslationsService translationsService;

    @Autowired
    private ProjectLanguageService projectLanguageService;

    @Autowired
    private TermsRepository termsRepository;

    @Transactional
    public void processFileUpload(Long projectId, Long languageId, BigInteger creatorId, MultipartFile file) throws IOException {
        // Ensure the project-language association exists
//        if (!projectLanguageService.existsByProjectIdAndLanguageId(projectId, languageId)) {
//            ProjectLanguageDto projectLanguageDto = new ProjectLanguageDto();
//            projectLanguageDto.setProjectId(projectId);
//            projectLanguageDto.setLanguageId(languageId);
//            projectLanguageService.assignLanguageToProject(projectLanguageDto);
//        }
//
//        for (ImportDto importDto : importData) {
//            // Check if the term already exists for the given project
//            TermsDto existingTerm = termsService.findByTermAndProjectId(importDto.getTerm(), projectId);
//            TermsDto termsDto;
//
//            if (existingTerm != null) {
//                termsDto = existingTerm;
//            } else {
//                termsDto = new TermsDto();
//                termsDto.setTerm(importDto.getTerm());
//                termsDto.setContext(importDto.getContext());
//                termsDto.setProjectId(projectId);
//                termsDto = termsService.createTerm(termsDto);
//            }
//
//            // Check if the translation already exists for the term, language, and creator
//            TranslationsDto existingTranslation = translationsService.findByTermIdAndLanguageIdAndCreatorId(
//                    termsDto.getId(), languageId, creatorId);
//
//            if (existingTranslation != null) {
//                existingTranslation.setTranslationText(importDto.getTranslation());
//                translationsService.updateTranslation(existingTranslation.getId(), existingTranslation);
//            } else {
//                TranslationsDto translationsDto = new TranslationsDto();
//                translationsDto.setTranslationText(importDto.getTranslation());
//                translationsDto.setTermId(termsDto.getId());
//                translationsDto.setLanguageId(languageId);
//                translationsDto.setCreatorId(creatorId);
//                translationsService.createTranslation(translationsDto);
//            }
//        }

        if (!projectLanguageService.existsByProjectIdAndLanguageId(projectId, languageId)) {
            ProjectLanguageDto dto = new ProjectLanguageDto();
            dto.setProjectId(projectId);
            dto.setLanguageId(languageId);
            projectLanguageService.assignLanguageToProject(dto);
        }

        List<ImportDto> importData = parseFileToImportDto(file);

        List<TermsDto> existingTerms = termsService.findTermsByProjectId(projectId);
        Map<String, TermsDto> termMap = existingTerms.stream()
                .collect(Collectors.toMap(TermsDto::getTerm, Function.identity()));

        List<Long> termIds = existingTerms.stream()
                .map(TermsDto::getId)
                .collect(Collectors.toList());

        List<TranslationsDto> existingTranslations = translationsService.findByTermIdsAndLanguageIdAndCreatorId(termIds, languageId, creatorId);

        Map<Long, TranslationsDto> translationMap = existingTranslations.stream()
                .collect(Collectors.toMap(TranslationsDto::getTermId, Function.identity()));

        List<TermsDto> newTerms = new ArrayList<>();
        List<TranslationsDto> newTranslations = new ArrayList<>();
        List<TranslationsDto> translationsToUpdate = new ArrayList<>();

        for (ImportDto importDto : importData) {
            TermsDto termsDto = termMap.get(importDto.getTerm());

            if (termsDto == null) {
                // New term
                termsDto = new TermsDto();
                termsDto.setTerm(importDto.getTerm());
                termsDto.setContext(importDto.getContext());
                termsDto.setProjectId(projectId);
                newTerms.add(termsDto);
            }

            // Check for existing translation
            TranslationsDto existingTranslation = (termsDto.getId() != null)
                    ? translationMap.get(termsDto.getId())
                    : null;

            if (existingTranslation != null) {
                existingTranslation.setTranslationText(importDto.getTranslation());
                translationsToUpdate.add(existingTranslation);
            } else {
                TranslationsDto newTranslation = new TranslationsDto();
                newTranslation.setTranslationText(importDto.getTranslation());
                newTranslation.setLanguageId(languageId);
                newTranslation.setCreatorId(creatorId);
                // Handle case where new term not yet saved (will map id later)
                newTranslation.setTermId(termsDto.getId());
                newTranslations.add(newTranslation);
            }

            // Put new term into map (so future loops can find it)
            if (termsDto.getId() == null) {
                termMap.put(termsDto.getTerm(), termsDto);
            }
        }

        if (!newTerms.isEmpty()) {
            termsService.saveAll(newTerms);

            // Update termMap with newly saved ids
            newTerms.forEach(t -> termMap.put(t.getTerm(), t));

            // Set term ids for corresponding new translations
            newTranslations.forEach(translation -> {
                if (translation.getTermId() == null) {
                    TermsDto correspondingTerm = termMap.get(importData.stream()
                            .filter(d -> d.getTranslation().equals(translation.getTranslationText()))
                            .map(ImportDto::getTerm)
                            .findFirst().orElse(null));
                    if (correspondingTerm != null) {
                        translation.setTermId(correspondingTerm.getId());
                    }
                }
            });
        }

        if (!newTranslations.isEmpty()) {
            translationsService.saveAll(newTranslations);
        }

        if (!translationsToUpdate.isEmpty()) {
            translationsService.updateAll(translationsToUpdate);
        }

    }

    private List<ImportDto> parseFileToImportDto(MultipartFile file) throws IOException {
        List<ImportDto> importData = new ArrayList<>();
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(file.getInputStream())) {
            parser.nextToken();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String term = parser.getCurrentName();
                parser.nextToken();
                String translation = parser.getValueAsString();
                ImportDto dto = new ImportDto();
                dto.setTerm(term);
                dto.setTranslation(translation);
                importData.add(dto);
            }
        }
        return importData;
    }

}
