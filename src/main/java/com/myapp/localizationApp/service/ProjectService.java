package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.LanguageDto;
import com.myapp.localizationApp.dto.ProjectDto;
import com.myapp.localizationApp.dto.UserRoleDto;
import com.myapp.localizationApp.entity.*;
import com.myapp.localizationApp.repository.LanguageRepository;
import com.myapp.localizationApp.repository.ProjectLanguageRepository;
import com.myapp.localizationApp.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRoleService userRoleService;
    private final ProjectLanguageRepository projectLanguageRepository;
    private final LanguageRepository languageRepository;

    public ProjectService(ProjectRepository projectRepository, UserRoleService userRoleService, ProjectLanguageRepository projectLanguageRepository, LanguageRepository languageRepository){
        this.projectRepository = projectRepository;
        this.userRoleService = userRoleService;
        this.projectLanguageRepository = projectLanguageRepository;
        this.languageRepository = languageRepository;
    }


    @CachePut(value = "projectById", key = "#result.id")
    @CacheEvict(value = "projectByUser", key = "#projectDto.ownerId")
    public ProjectDto createProject(ProjectDto projectDto){
        Project project = convertToEntity(projectDto);
        project = projectRepository.save(project);
        // Assign the creator as the admin of the project
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setUserId(projectDto.getOwnerId());
        userRoleDto.setProjectId(project.getId().longValue());
        userRoleDto.setRoleId(1L);
        userRoleService.assignRoleToUser(userRoleDto);

        assignDefaultLanguageToProject(project);

        return convertToDto(project);
    }

    private void assignDefaultLanguageToProject(Project project) {
        Language defaultLanguage = project.getDefaultLanguage();
        if (defaultLanguage == null) {
            defaultLanguage = languageRepository.findById(1L)
                    .orElseThrow(() -> new EntityNotFoundException("Default Language not found with id 1"));
            project.setDefaultLanguage(defaultLanguage);
            projectRepository.save(project);
        }

        // Assign the default language in project_languages table
        ProjectLanguage projectLanguage = new ProjectLanguage();
        projectLanguage.setProject(project);
        projectLanguage.setLanguage(defaultLanguage);
        projectLanguageRepository.save(projectLanguage);
    }


    @Cacheable(value = "projectById", key = "#id")
    public Optional<ProjectDto> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDto);
    }

    @Cacheable(value = "allProject")
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAllByOrganizationIsNull().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"projectById", "projectByUser"}, key = "#updatedProjectDto.projectId")
    public ProjectDto updateProject(Long id, ProjectDto updatedProjectDto) {
        return projectRepository.findById(id)
                .map(existingProject -> {
                    existingProject.setName(updatedProjectDto.getName());
                    existingProject.setDescription(updatedProjectDto.getDescription());
                    User owner = new User();
                    owner.setId(BigInteger.valueOf(updatedProjectDto.getOwnerId()));
                    existingProject.setOwner(owner);
                    Organization organization = new Organization();
                    organization.setId(updatedProjectDto.getOrganizationId());
                    existingProject.setOrganization(organization);
                    if (updatedProjectDto.getDefaultLangId() != null) {
                        Language language = languageRepository.findById(updatedProjectDto.getDefaultLangId())
                                .orElseThrow(() -> new EntityNotFoundException("Language not found with id " + updatedProjectDto.getDefaultLangId()));
                        existingProject.setDefaultLanguage(language);
                    }

                    Project updatedProject = projectRepository.save(existingProject);
                    return convertToDto(updatedProject);
                })
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id " + id));
    }

    @CacheEvict(value = {"projectByUser", "projectById"}, key = "#id")
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private Project convertToEntity(ProjectDto projectDto) {
        Project project = new Project();
        if (projectDto.getId() != null) {
            project.setId(BigInteger.valueOf(projectDto.getId()));
        }
        //project.setId(BigInteger.valueOf(projectDto.getId()));
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());

        User owner = new User();
        owner.setId(BigInteger.valueOf(projectDto.getOwnerId()));
        project.setOwner(owner);


        if (projectDto.getOrganizationId() != null) {
            Organization organization = new Organization();
            organization.setId(projectDto.getOrganizationId());
            project.setOrganization(organization);
        } else {
            project.setOrganization(null);
        }

        if (projectDto.getDefaultLangId() != null) {
            Language language = languageRepository.findById(projectDto.getDefaultLangId())
                    .orElseThrow(() -> new EntityNotFoundException("Language not found with id " + projectDto.getDefaultLangId()));
            project.setDefaultLanguage(language);
        } else {
            Language defaultLanguage = languageRepository.findById(1L)
                    .orElseThrow(() -> new EntityNotFoundException("Default Language not found with id 1"));
            project.setDefaultLanguage(defaultLanguage);
        }

        return project;
    }

    private ProjectDto convertToDto(Project project) {
        // Convert ProjectLanguage entities to Language objects
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
        System.out.println(languageDto);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId().longValue());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        projectDto.setCreateAt(project.getCreatedAt());
        projectDto.setOwnerId(project.getOwner().getId().longValue());
        projectDto.setDefaultLangId(project.getDefaultLanguage().getId());
        if (project.getOrganization() != null) {
            projectDto.setOrganizationId(project.getOrganization().getId());
        } else {
            projectDto.setOrganizationId(null);
        }
        projectDto.setLanguages(languageDto);
        return projectDto;
    }

//    @Cacheable(value = "projectByUser", key = "#userId")
//    public List<ProjectDto> getUserProjects(Long userId) {
//        return projectRepository.findByOwner_IdAndOrganizationIsNull(userId).stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

    @Cacheable(value = "projectByUser", key = "#userId")
    public List<ProjectDto> getUserProjects(Long userId) {
        List<Object[]> results = projectRepository.findUserProjectsWithRole(userId);

        return results.stream().map(result -> {
            Project project = (Project) result[0];
            Long roleId = (Long) result[1];

            ProjectDto dto = convertToDto(project);
            dto.setRoleId(roleId != null ? roleId :
                    (project.getOwner().getId().equals(userId) ? 1 : null));

            return dto;
        }).collect(Collectors.toList());
    }


}
