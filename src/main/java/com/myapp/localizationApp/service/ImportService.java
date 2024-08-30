package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.ImportDto;
import com.myapp.localizationApp.dto.ProjectLanguageDto;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.repository.TermsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

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

    public void processFileUpload(Long projectId, Long languageId, BigInteger creatorId, List<ImportDto> importData) {
        // Ensure the project-language association exists
        if (!projectLanguageService.existsByProjectIdAndLanguageId(projectId, languageId)) {
            ProjectLanguageDto projectLanguageDto = new ProjectLanguageDto();
            projectLanguageDto.setProjectId(projectId);
            projectLanguageDto.setLanguageId(languageId);
            projectLanguageService.assignLanguageToProject(projectLanguageDto);
        }

        for (ImportDto importDto : importData) {
            // Check if the term already exists for the given project
            TermsDto existingTerm = termsService.findByTermAndProjectId(importDto.getTerm(), projectId);
            TermsDto termsDto;

            if (existingTerm != null) {
                termsDto = existingTerm;
            } else {
                termsDto = new TermsDto();
                termsDto.setTerm(importDto.getTerm());
                termsDto.setContext(importDto.getContext());
                termsDto.setProjectId(projectId);
                termsDto = termsService.createTerm(termsDto);
            }

            // Check if the translation already exists for the term, language, and creator
            TranslationsDto existingTranslation = translationsService.findByTermIdAndLanguageIdAndCreatorId(
                    termsDto.getId(), languageId, creatorId);

            if (existingTranslation != null) {
                existingTranslation.setTranslationText(importDto.getTranslation());
                translationsService.updateTranslation(existingTranslation.getId(), existingTranslation);
            } else {
                TranslationsDto translationsDto = new TranslationsDto();
                translationsDto.setTranslationText(importDto.getTranslation());
                translationsDto.setTermId(termsDto.getId());
                translationsDto.setLanguageId(languageId);
                translationsDto.setCreatorId(creatorId);
                translationsService.createTranslation(translationsDto);
            }
        }
    }
}
