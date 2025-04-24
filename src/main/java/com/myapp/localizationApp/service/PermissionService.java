package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.PermissionDto;
import com.myapp.localizationApp.entity.Permission;
import com.myapp.localizationApp.entity.Role;
import com.myapp.localizationApp.entity.RolePermission;
import com.myapp.localizationApp.entity.UserRole;
import com.myapp.localizationApp.repository.PermissionRepository;
import com.myapp.localizationApp.repository.RolePermissionRepository;
import com.myapp.localizationApp.repository.UserRoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public PermissionService(PermissionRepository permissionRepository, ModelMapper modelMapper,
                             UserRoleRepository userRoleRepository, RolePermissionRepository rolePermissionRepository){
        this.permissionRepository = permissionRepository;
        this.modelMapper = modelMapper;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public PermissionDto createPermission(PermissionDto permissionDto) {
        Permission permission = modelMapper.map(permissionDto, Permission.class);
        Permission savedPermission = permissionRepository.save(permission);
        return modelMapper.map(savedPermission, PermissionDto.class);
    }

    public boolean hasPermission(Long userId, Long projectId, String permissionName) {
        Optional<Permission> permissionOpt = permissionRepository.findByName(permissionName);
        if (permissionOpt.isEmpty()) {
            return false;
        }

        Long permissionId = permissionOpt.get().getId();

        List<UserRole> roles = userRoleRepository.findByUserIdAndProjectId(userId, projectId);
        for (UserRole userRole : roles) {
            List<RolePermission> permissions = rolePermissionRepository.findByRoleId(userRole.getRole().getId());
            for (RolePermission rp : permissions) {
                if (rp.getPermission().getId().equals(permissionId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
