package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.ProjectLanguageDto;
import com.myapp.localizationApp.entity.Language;
import com.myapp.localizationApp.entity.Project;
import com.myapp.localizationApp.entity.ProjectLanguage;
import com.myapp.localizationApp.repository.LanguageRepository;
import com.myapp.localizationApp.repository.ProjectLanguageRepository;
import com.myapp.localizationApp.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectLanguageService {

    private  final ProjectLanguageRepository projectLanguageRepository;
    private  final  ProjectRepository projectRepository;
    private final LanguageRepository languageRepository;
    private final ModelMapper modelMapper;

    public ProjectLanguageService(ProjectLanguageRepository projectLanguageRepository, ProjectRepository projectRepository, LanguageRepository languageRepository, ModelMapper modelMapper){
        this.projectLanguageRepository = projectLanguageRepository;
        this.projectRepository = projectRepository;
        this.languageRepository = languageRepository;
        this.modelMapper = modelMapper;
    }

    @CachePut(value = "projectLanguageById", key = "#result.id")
    @CacheEvict(value = {"languageByProjectId", "projectLanByLanguageAndProject", "projectByUser"}, key = "#projectLanguageDto.projectId")
    public ProjectLanguageDto assignLanguageToProject(ProjectLanguageDto projectLanguageDto){
        ProjectLanguage projectLanguage = convertToEntity(projectLanguageDto);
        ProjectLanguage saveProjectLanguage = projectLanguageRepository.save(projectLanguage);
        return convertToDto(saveProjectLanguage);
    }

    @Cacheable(value = "languageByProjectId", key = "#projectId")
    public List<ProjectLanguageDto> getLanguageByProjectId(Long projectId){
        List<ProjectLanguage> projectLanguages = projectLanguageRepository.findByProjectId(BigInteger.valueOf(projectId));
        return projectLanguages.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Cacheable(value = "projectLanguageById", key = "#id")
    public Optional<ProjectLanguageDto> getProjectLanguageById(Long id){
        return  projectLanguageRepository.findById(id).map(this::convertToDto);
    }

    @CacheEvict(value = {"projectLanguageById", "languageByProjectId", "projectLanByLanguageAndProject", "projectByUser"}, allEntries = true)
    public void deleteProjectLanguage( Long id){
        projectLanguageRepository.deleteById(id);
    }

    private ProjectLanguageDto convertToDto(ProjectLanguage projectLanguage){
        ProjectLanguageDto projectLanguageDto = new ProjectLanguageDto();
        projectLanguageDto.setId(projectLanguage.getId());
        projectLanguageDto.setProjectId(projectLanguage.getProject().getId().longValue());
        projectLanguageDto.setLanguageId(projectLanguage.getLanguage().getId());
        return projectLanguageDto;
    }

    private ProjectLanguage convertToEntity(ProjectLanguageDto projectLanguageDto){
        ProjectLanguage projectLanguage = new ProjectLanguage();
        projectLanguage.setProject(projectRepository.findById(projectLanguageDto.getProjectId()).orElse(null));
        projectLanguage.setLanguage(languageRepository.findById(projectLanguageDto.getLanguageId()).orElse(null));
        return projectLanguage;
    }

    public boolean existsByProjectIdAndLanguageId(Long projectId, Long languageId) {
        return projectLanguageRepository.existsByProjectIdAndLanguageId(projectId, languageId);
    }

    @Cacheable(value = "projectLanByLanguageAndProject", key = "#languageId + '-' + #projectId", unless = "#result == null")
    public ProjectLanguageDto getByLanguageIdAndProjectId(Long languageId, Long projectId) {
        ProjectLanguage projectLanguage = projectLanguageRepository
                .findByLanguageIdAndProjectId(languageId, projectId);

        return modelMapper.map(projectLanguage, ProjectLanguageDto.class);
    }
}
