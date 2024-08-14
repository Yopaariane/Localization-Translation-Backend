package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.entity.Language;
import com.myapp.localizationApp.entity.Terms;
import com.myapp.localizationApp.entity.Translations;
import com.myapp.localizationApp.entity.User;
import com.myapp.localizationApp.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslationsService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TranslationsRepository translationsRepository;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private ProjectLanguageRepository projectLanguageRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private UserRepository userRepository;

    public TranslationsDto createTranslation(TranslationsDto translationsDto) {
        // Manually map simple fields
        Translations translations = new Translations();
        translations.setTranslation_text(translationsDto.getTranslationText());

        // Fetch and set the Term entity
        Terms terms = termsRepository.findById(translationsDto.getTermId())
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + translationsDto.getTermId()));
        translations.setTerm(terms);

        // Fetch and set the Language entity
        Language language = languageRepository.findById(translationsDto.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language not found with id: " + translationsDto.getLanguageId()));
        translations.setLanguage(language);

        // Fetch and set the User entity
        User creator = userRepository.findById(translationsDto.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + translationsDto.getCreatorId()));
        translations.setCreator(creator);

        // mapping to dto
        modelMapper.typeMap(Translations.class, TranslationsDto.class).addMappings(mapper -> {
            mapper.map(Translations::getTranslation_text, TranslationsDto::setTranslationText);
            mapper.map(Translations::getCreated_at, TranslationsDto::setCreatedAt);
            mapper.map(Translations::getUpdated_at, TranslationsDto::setUpdatedAt);
        });

        // Save the Translations entity
        Translations savedTranslation = translationsRepository.save(translations);

        // Convert the saved entity back to DTO
        return modelMapper.map(savedTranslation, TranslationsDto.class);
    }

    public TranslationsDto updateTranslation(Long id, TranslationsDto translationsDto) {
        Translations existingTranslation = translationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with id: " + id));

        // Update fields
        existingTranslation.setTranslation_text(translationsDto.getTranslationText());

        // Update related entities if needed
        if (translationsDto.getTermId() != null) {
            Terms terms = termsRepository.findById(translationsDto.getTermId())
                    .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + translationsDto.getTermId()));
            existingTranslation.setTerm(terms);
        }

        if (translationsDto.getLanguageId() != null) {
            Language language = languageRepository.findById(translationsDto.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language not found with id: " + translationsDto.getLanguageId()));
            existingTranslation.setLanguage(language);
        }

        if (translationsDto.getCreatorId() != null) {
            User creator = userRepository.findById(translationsDto.getCreatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + translationsDto.getCreatorId()));
            existingTranslation.setCreator(creator);
        }
        modelMapper.typeMap(Translations.class, TranslationsDto.class).addMappings(mapper -> {
            mapper.map(Translations::getTranslation_text, TranslationsDto::setTranslationText);
            mapper.map(Translations::getCreated_at, TranslationsDto::setCreatedAt);
            mapper.map(Translations::getUpdated_at, TranslationsDto::setUpdatedAt);
        });

        // Save the updated translation
        Translations updatedTranslation = translationsRepository.save(existingTranslation);

        // Convert to DTO and return
        return modelMapper.map(updatedTranslation, TranslationsDto.class);
    }

    public void deleteTranslation(Long id) {
        Translations existingTranslation = translationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with id: " + id));

        translationsRepository.delete(existingTranslation);
    }

    public List<TranslationsDto> getTranslationsByTermId(Long termId) {
        List<Translations> translations = translationsRepository.findTranslationsByTermId(termId);
        modelMapper.typeMap(Translations.class, TranslationsDto.class).addMappings(mapper -> {
            mapper.map(Translations::getTranslation_text, TranslationsDto::setTranslationText);
            mapper.map(Translations::getCreated_at, TranslationsDto::setCreatedAt);
            mapper.map(Translations::getUpdated_at, TranslationsDto::setUpdatedAt);
        });
        return translations.stream()
                .map(translation -> modelMapper.map(translation, TranslationsDto.class))
                .collect(Collectors.toList());
    }

    public List<TranslationsDto> getTranslationsByLanguageId(Long languageId) {
        List<Translations> translations = translationsRepository.findTranslationsByLanguageId(languageId);
        modelMapper.typeMap(Translations.class, TranslationsDto.class).addMappings(mapper -> {
            mapper.map(Translations::getTranslation_text, TranslationsDto::setTranslationText);
            mapper.map(Translations::getCreated_at, TranslationsDto::setCreatedAt);
            mapper.map(Translations::getUpdated_at, TranslationsDto::setUpdatedAt);
        });
        return translations.stream()
                .map(translation -> modelMapper.map(translation, TranslationsDto.class))
                .collect(Collectors.toList());
    }

    public Long countTranslationsByProjectId(Long projectId) {
        return translationsRepository.countTranslationsByProjectId(projectId);
    }

}
