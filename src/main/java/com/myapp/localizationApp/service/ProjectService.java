package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.ProjectDto;
import com.myapp.localizationApp.dto.UserRoleDto;
import com.myapp.localizationApp.entity.Organization;
import com.myapp.localizationApp.entity.Project;
import com.myapp.localizationApp.entity.User;
import com.myapp.localizationApp.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRoleService userRoleService;

    public ProjectDto createProject(ProjectDto projectDto){
        Project project = convertToEntity(projectDto);
        project = projectRepository.save(project);
        // Assign the creator as the admin of the project
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setUserId(projectDto.getOwnerId());
        userRoleDto.setProjectId(project.getId().longValue());
        userRoleDto.setRoleId(1L); // Assuming 1L is the ID for Admin role
        userRoleService.assignRoleToUser(userRoleDto);

        return convertToDto(project);


    }

    public Optional<ProjectDto> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDto);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProjectDto updateProject(Long id, ProjectDto updatedProjectDto) {
        return projectRepository.findById(id)
                .map(existingProject -> {
                    existingProject.setName(updatedProjectDto.getName());
                    existingProject.setDescription(updatedProjectDto.getDescription());
                    User owner = new User();
                    owner.setId(BigInteger.valueOf(updatedProjectDto.getOwnerId()));
                    existingProject.setOwner(owner);
//                    Organization organization = new Organization();
//                    organization.setId(updatedProjectDto.getOrganizationId());
//                    existingProject.setOrganization(organization);
                    Project updatedProject = projectRepository.save(existingProject);
                    return convertToDto(updatedProject);
                })
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id " + id));
    }

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
//        Organization organization = new Organization();
//        organization.setId(projectDto.getOrganizationId());
//        project.setOrganization(organization);
        return project;
    }

    private ProjectDto convertToDto(Project project) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId().longValue());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        projectDto.setOwnerId(project.getOwner().getId().longValue());
        //projectDto.setOrganizationId(project.getOrganization().getId());
        return projectDto;
    }
}