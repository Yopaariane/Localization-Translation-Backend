package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.entity.*;
import com.myapp.localizationApp.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
    private ProjectRepository projectRepository;

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

    public double getTranslationProgressForTerm(Long termId) {
        Terms term = termsRepository.findById(termId)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found"));

        Long totalLanguages = projectLanguageRepository.countLanguagesByProjectId(term.getProject().getId().longValue());
        Long translatedLanguages = translationsRepository.countTranslationsByTermId(termId);

        if (totalLanguages == 0) {
            return 0.0;
        }

        double progress = (double) translatedLanguages / totalLanguages * 100;

        return roundToOneDecimalPlace(progress);
    }

    public double getTranslationProgressForLanguage(Long languageId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));

        ProjectLanguage projectLanguage = projectLanguageRepository.findByLanguageIdAndProjectId(languageId, projectId);

        // Count total terms in the project
        long totalTerms = termsRepository.countByProjectId(projectId);

        // Count translated terms for the language within the project
        Long translatedTerms = translationsRepository.countTranslationsByLanguageAndProject(languageId, projectId);

        if (totalTerms == 0 || translatedTerms == null) {
            return 0.0;
        }

        double progress = (double) translatedTerms / totalTerms * 100;

        return roundToOneDecimalPlace(progress);
    }

    public double getOverallTranslationProgressForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));

        // Retrieve all ProjectLanguages for the given project
        List<ProjectLanguage> projectLanguages = projectLanguageRepository.findByProjectId(BigInteger.valueOf(projectId));

        if (projectLanguages.isEmpty()) {
            return 0.0;
        }

        double totalProgress = 0.0;

        for (ProjectLanguage projectLanguage : projectLanguages) {
            Long languageId = projectLanguage.getLanguage().getId();
            double languageProgress = getTranslationProgressForLanguage(languageId, projectId);
            totalProgress += languageProgress;
        }

        double overallProgress = totalProgress / projectLanguages.size();

        return roundToOneDecimalPlace(overallProgress);
    }

    public double getAverageTranslationProgressForUser(Long userId) {
        List<Project> projects = projectRepository.findByOwner_Id(userId);

        if (projects.isEmpty()) {
            throw new ResourceNotFoundException("No projects found for user with id " + userId);
        }

        double totalProgress = 0.0;
        int projectCount = 0;

        for (Project project : projects) {
            double projectProgress = getOverallTranslationProgressForProject(project.getId().longValue());

            // Sum up the progress
            totalProgress += projectProgress;
            projectCount++;
        }

        //average progress
        double averageProgress = projectCount > 0 ? totalProgress / projectCount : 0.0;

        return Math.round(averageProgress * 10) / 10.0;
    }


    private double roundToOneDecimalPlace(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Total number of translated strings
//    public double calculateStringProgressForUser(Long userId) {
//        Long totalTranslatedStrings = translationsRepository.sumTranslatedStringsForUser(userId);
//
//        // Default to 0 if no translations are found
//        if (totalTranslatedStrings == null) {
//            totalTranslatedStrings = 0L;
//        }
//
//        // Calculate the progress percentage
//        double progress = ((double) totalTranslatedStrings / 10000) * 100;
//
//        // Round to one decimal place
//        return Math.round(progress * 10.0) / 10.0;
//    }
}
