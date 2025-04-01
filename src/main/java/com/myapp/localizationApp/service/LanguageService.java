package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.LanguageDto;
import com.myapp.localizationApp.dto.ProjectDto;
import com.myapp.localizationApp.entity.Language;
import com.myapp.localizationApp.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {
    @Autowired
    private LanguageRepository languageRepository;

//    @Cacheable(value = "allLanguages")
    public List<LanguageDto> getAllLanguages() {
        return languageRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

//    @Cacheable(value = "languageById", key = "#id")
    public Optional<LanguageDto> getLanguageById(Long id) {
        return languageRepository.findById(id)
                .map(this::toDto);
    }

//    public  Language getLanguageByCode(String code) {
//        return  languageRepository.findByCode(code);
//    }

//    public LanguageDto getDefaultLanguage() {
//        Language defaultLanguage = languageRepository.findByIsDefaultTrue();
//        return defaultLanguage != null ? toDto(defaultLanguage) : null;
//    }

    private LanguageDto toDto(Language language) {
        LanguageDto dto = new LanguageDto();
        dto.setId(language.getId());
        dto.setCode(language.getCode());
        dto.setName(language.getName());
//        dto.setDefault(language.isDefault());
        return dto;
    }
}
