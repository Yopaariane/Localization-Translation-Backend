package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.ProjectDto;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.entity.*;
import com.myapp.localizationApp.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslationsService {

    private final ModelMapper modelMapper;
    private final  TranslationsRepository translationsRepository;
    private  final  TermsRepository termsRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final ProjectLanguageRepository projectLanguageRepository;
    private  final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final OrganizationService organizationService;

    public TranslationsService(ModelMapper modelMapper, TranslationsRepository translationsRepository, TermsRepository termsRepository, ProjectRepository projectRepository, ProjectService projectService, ProjectLanguageRepository projectLanguageRepository, LanguageRepository languageRepository, UserRepository userRepository, OrganizationService organizationService) {
        this.modelMapper = modelMapper;
        this.translationsRepository = translationsRepository;
        this.termsRepository = termsRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.projectLanguageRepository = projectLanguageRepository;
        this.languageRepository = languageRepository;
        this.userRepository = userRepository;
        this.organizationService = organizationService;
    }

    @CacheEvict(value = {"translationByTerm", "translationsByLanguage",  "averageTranslationForUser",
            "overallTranslationForProject", "translationByTermLanguageAndCreator", "stringTranslationProgress",
            "translationProgressFromTerm", "translationProgressForLanguage"},
            allEntries = true)
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
        User creator = userRepository.findById(translationsDto.getCreatorId().longValue())
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

    @CachePut(value = "translationByTerm", key = "#id")
    @CacheEvict(value = {"translationsByLanguage", "translationProgressFromTerm",  "averageTranslationForUser", "stringTranslationProgress",
            "overallTranslationForProject", "translationByTermLanguageAndCreator", "translationProgressForLanguage"},
            allEntries = true)
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
            User creator = userRepository.findById(translationsDto.getCreatorId().longValue())
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

    @CacheEvict(value = {"translationByTerm", "translationsByLanguage", "averageTranslationForUser",
            "overallTranslationForProject", "translationByTermLanguageAndCreator",
            "translationProgressFromTerm", "stringTranslationProgress", "translationProgressForLanguage"},
            allEntries = true)
    public void deleteTranslation(Long id) {
        Translations existingTranslation = translationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with id: " + id));

        translationsRepository.delete(existingTranslation);
    }

    @Cacheable(value = "translationByTerm", key = "#termId")
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

    @Cacheable(value = "translationsByLanguage", key = "#languageId")
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

    @Cacheable(value = "translationByTermLanguageAndCreator", key = "#termId + '-' + #languageId + '-' + #creatorId", unless="#result == null")
    public TranslationsDto findByTermIdAndLanguageIdAndCreatorId(Long termId, Long languageId, BigInteger creatorId) {
        Translations translationEntity = translationsRepository.findByTermIdAndLanguageIdAndCreatorId(termId, languageId, creatorId);
        return translationEntity == null ? null : modelMapper.map(translationEntity, TranslationsDto.class);
    }


    public Long countTranslationsByProjectId(Long projectId) {
        return translationsRepository.countTranslationsByProjectId(projectId);
    }

    @Cacheable(value = "translationProgressFromTerm", key = "#termId")
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

    @Cacheable(value = "translationProgressForLanguage", key = "#languageId + '-' + #projectId")
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

    @Cacheable(value = "overallTranslationForProject", key = "#projectId")
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

    @Cacheable(value = "averageTranslationForUser", key = "#userId")
    public double getAverageTranslationProgressForUser(Long userId) {
        List<ProjectDto> userProjects = projectService.getUserProjects(userId);
        return calculateAverageTranslationProgress(userProjects);
    }

    // @Cacheable(value = "getAverageTranslationProgressForOrganization", key = "#organizationId")
    public double getAverageTranslationProgressForOrganization(Long organizationId) {
        List<ProjectDto> organizationProjects = organizationService.getProjectsByOrganizationId(organizationId);
        return calculateAverageTranslationProgress(organizationProjects);
    }

    private double calculateAverageTranslationProgress(List<ProjectDto> projects) {
        if (projects.isEmpty()) {
            return 0.0;
        }

        double totalProgress = projects.stream()
                .mapToDouble(project -> getOverallTranslationProgressForProject(project.getId()))
                .sum();

        double averageProgress = totalProgress / projects.size();
        return roundToOneDecimalPlace(averageProgress);
    }


    private double roundToOneDecimalPlace(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    @Cacheable(value = "totalStringNumber", key = "#ownerId")
    public Integer getTotalStringNumberByOwnerId(BigInteger ownerId) {
        Integer totalStringNumber = translationsRepository.sumStringNumbersByOwnerId(ownerId);
        return totalStringNumber != null ? totalStringNumber : 0;
    }

    @Cacheable(value = "stringTranslationProgress", key = "#userId")
    public int calculateStringsTranslationProgress(BigInteger userId) {
        Integer totalStringNumber = translationsRepository.sumStringNumbersByOwnerId(userId);
        if (totalStringNumber == null) {
            totalStringNumber = 0;
        }

        return Math.min((totalStringNumber * 100) / 10000, 100);
    }
}
