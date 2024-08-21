package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.ExportDto;
import com.myapp.localizationApp.entity.Language;
import com.myapp.localizationApp.entity.Project;
import com.myapp.localizationApp.entity.ProjectLanguage;
import com.myapp.localizationApp.repository.LanguageRepository;
import com.myapp.localizationApp.repository.ProjectLanguageRepository;
import com.myapp.localizationApp.repository.ProjectRepository;
import com.myapp.localizationApp.repository.TranslationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectLanguageRepository projectLanguageRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private TranslationsRepository translationRepository;

    public List<ExportDto> exportTranslationsByLanguage(Long projectId, Long languageId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        ProjectLanguage projectLanguage = projectLanguageRepository.findByLanguageIdAndProjectId(languageId, projectId);

        return translationRepository.findByTerm_ProjectAndLanguage(project, projectLanguage.getLanguage())
                .stream()
                .map(translation -> {
                    ExportDto dto = new ExportDto();
                    dto.setTerm(translation.getTerm().getTerm());
                    dto.setTranslation(translation.getTranslation_text());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public String getProjectName(Long projectId) {
        return projectRepository.findById(projectId)
                .map(Project::getName)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public String getLanguageName(Long languageId) {
        return languageRepository.findById(languageId)
                .map(Language::getName)
                .orElseThrow(() -> new ResourceNotFoundException("Language not found"));
    }
}
