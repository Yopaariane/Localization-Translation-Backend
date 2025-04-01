package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.LanguageDto;
import com.myapp.localizationApp.dto.OrganizationDto;
import com.myapp.localizationApp.dto.ProjectDto;
import com.myapp.localizationApp.dto.ProjectLanguageDto;
import com.myapp.localizationApp.entity.*;
import com.myapp.localizationApp.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final LanguageRepository languageRepository;
    private final ProjectLanguageRepository projectLanguageRepository;

    public OrganizationService (OrganizationRepository organizationRepository, ModelMapper modelMapper, UserRepository userRepository, ProjectRepository projectRepository, LanguageRepository languageRepository, ProjectLanguageRepository projectLanguageRepository) {
        this.organizationRepository = organizationRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.languageRepository = languageRepository;
        this.projectLanguageRepository = projectLanguageRepository;
    }


    public OrganizationDto createOrganization(OrganizationDto organizationDto) {
        Organization organization = modelMapper.map(organizationDto, Organization.class);

        User user = userRepository.findById(organizationDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + organizationDto.getUserId()));
        organization.setUser(user);

        Language language = languageRepository.findById(organizationDto.getDefaultLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Language ID"));
        organization.setDefaultLanguage(language);

        Organization savedorganization = organizationRepository.save(organization);

        return modelMapper.map(savedorganization, OrganizationDto.class);
    }

    @Transactional
    public void assignProjectsToOrganization(Long organizationId, List<Long> projectIds) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        List<Project> projects = projectRepository.findAllById(projectIds);
        if (projects.isEmpty()) {
            throw new RuntimeException("No valid projects found");
        }

        projects.forEach(project -> project.setOrganization(organization));
        projectRepository.saveAll(projects);
    }

    @Transactional
    public List<ProjectDto> getProjectsByOrganizationId(Long organizationId) {
        List<Project> projects = projectRepository.findByOrganizationId(organizationId);

        return projects.stream()
                .map(this::mapProjectToDto)
                .collect(Collectors.toList());
    }

    private ProjectDto mapProjectToDto(Project project) {
        ModelMapper modelMapper = new ModelMapper();

        // Map defaultLangId manually
        modelMapper.typeMap(Project.class, ProjectDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getDefaultLanguage().getId(), ProjectDto::setDefaultLangId);
            mapper.map(Project::getCreatedAt, ProjectDto::setCreateAt);
        });

        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        // Manually map languages
        List<Language> languages = projectLanguageRepository.findByProjectId(project.getId())
                .stream()
                .map(ProjectLanguage::getLanguage)
                .toList();

        // Convert Language objects to LanguageDto objects
        List<LanguageDto> languageDto = languages.stream()
                .map(language -> {
                    LanguageDto dto = new LanguageDto();
                    dto.setId(language.getId());
                    dto.setCode(language.getCode());
                    dto.setName(language.getName());
                    return dto;
                })
                .toList();
        projectDto.setLanguages(languageDto);

        return projectDto;
    }


    //    @Cacheable(value = "organizationByUser", key = "#userId")
    public List<OrganizationDto> getOrganizationsByUserId(Long userId) {
        List<Organization> organizations = organizationRepository.findByUserId(userId);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Organization.class, OrganizationDto.class).addMappings(mapper -> {
            mapper.map(Organization::getCreatedAt, OrganizationDto::setCreateAt);
            mapper.map(org -> org.getDefaultLanguage().getId(), OrganizationDto::setDefaultLanguageId);
        });

        return organizations.stream()
                .map(org -> modelMapper.map(org, OrganizationDto.class))
                .collect(Collectors.toList());
    }
}
