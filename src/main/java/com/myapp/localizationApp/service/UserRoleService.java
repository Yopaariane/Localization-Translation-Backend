package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.UserRoleDto;
import com.myapp.localizationApp.entity.*;
import com.myapp.localizationApp.repository.ProjectRepository;
import com.myapp.localizationApp.repository.RoleRepository;
import com.myapp.localizationApp.repository.UserRepository;
import com.myapp.localizationApp.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserRoleDto assignRoleToUser(UserRoleDto userRoleDto) {
        UserRole userRole = convertToEntity(userRoleDto);
        UserRole savedUserRole = userRoleRepository.save(userRole);
        return convertToDto(savedUserRole);
    }

public List<UserRoleDto> getRolesByUserId(Long userId) {
    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    return userRoles.stream().map(this::convertToDto).collect(Collectors.toList());
}


//    public List<UserRoleDto> getRolesByUserId(Long userId) {
//        return userRoleRepository.findByUserId(userId).stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

    public List<UserRoleDto> getRolesByProjectId(Long projectId) {
        List<UserRole> userRoles = userRoleRepository.findByProjectId(projectId);
        return userRoles.stream().map(this::convertToDto).collect(Collectors.toList());
    }

//    public List<UserRoleDto> getRolesByProjectId(Long projectId) {
//        return userRoleRepository.findByProjectId(projectId).stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

//    public List<UserRoleDto> getRolesByOrganizationId(Long organizationId) {
//        return userRoleRepository.findByOrganizationId(organizationId).stream()
//                .map(this::converterDto)
//                .collect(Collectors.toList());
//    }
private UserRole convertToEntity(UserRoleDto userRoleDto) {
    UserRole userRole = new UserRole();
    userRole.setUser(userRepository.findById(userRoleDto.getUserId()).orElse(null));
    userRole.setProject(projectRepository.findById(userRoleDto.getProjectId()).orElse(null));
    //userRole.setOrganization(organizationRepository.findById(userRoleDto.getOrganizationId()).orElse(null));
    userRole.setRole(roleRepository.findById(userRoleDto.getRoleId()).orElse(null));
    return userRole;
}

//    private UserRole convertToEntity(UserRoleDto userRoleDto) {
//        UserRole userRole = new UserRole();
//        userRole.setId(userRoleDto.getId());
//        User user = new User();
//        user.setId(BigInteger.valueOf(userRoleDto.getUserId()));
//        userRole.setUser(user);
//        Project project = new Project();
//        project.setId(BigInteger.valueOf(userRoleDto.getProjectId()));
//        userRole.setProject(project);
//        Role role = new Role();
//        role.setId(userRoleDto.getRoleId());
//        userRole.setRole(role);
//        return userRole;
//    }

    private UserRoleDto convertToDto(UserRole userRole) {
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setId(userRole.getId());
        userRoleDto.setUserId(userRole.getUser().getId().longValue());
        userRoleDto.setProjectId(userRole.getProject().getId().longValue());
        //userRoleDto.setOrganizationId(userRole.getOrganization().getId());
        userRoleDto.setRoleId(userRole.getRole().getId());
        return userRoleDto;
    }

    public void deleteUserRole(Long id) {
        userRoleRepository.deleteById(id);
    }
}