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
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectLanguageService {
    @Autowired
    private ProjectLanguageRepository projectLanguageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ProjectLanguageDto assignLanguageToProject(ProjectLanguageDto projectLanguageDto){
        ProjectLanguage projectLanguage = convertToEntity(projectLanguageDto);
        ProjectLanguage saveProjectLanguage = projectLanguageRepository.save(projectLanguage);
        return convertToDto(saveProjectLanguage);
    }

    public List<ProjectLanguageDto> getLanguageByProjectId(Long projectId){
        List<ProjectLanguage> projectLanguages = projectLanguageRepository.findByProjectId(BigInteger.valueOf(projectId));
        return projectLanguages.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public Optional<ProjectLanguageDto> getProjectLanguageById(Long id){
        return  projectLanguageRepository.findById(id).map(this::convertToDto);
    }

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

    public ProjectLanguageDto getByLanguageIdAndProjectId(Long languageId, Long projectId) {
        ProjectLanguage projectLanguage = projectLanguageRepository
                .findByLanguageIdAndProjectId(languageId, projectId);

        return modelMapper.map(projectLanguage, ProjectLanguageDto.class);
    }

//    public ProjectLanguageDto createProjectLanguage(Long projectId, Long languageId) {
//        if (existsByProjectIdAndLanguageId(projectId, languageId)) {
//            Project project = projectRepository.findById(projectId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
//
//            Language language = languageRepository.findById(languageId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Language not found with id: " + languageId));
//
//            ProjectLanguage projectLanguage = new ProjectLanguage();
//            projectLanguage.setProject(project);
//            projectLanguage.setLanguage(language);
//
//            ProjectLanguage savedProjectLanguage = projectLanguageRepository.save(projectLanguage);
//
//            return convertToDto(savedProjectLanguage);
//        } else {
//            throw new ResourceNotFoundException("ProjectLanguage already exists for projectId: " + projectId + " and languageId: " + languageId);
//        }
//    }

}
