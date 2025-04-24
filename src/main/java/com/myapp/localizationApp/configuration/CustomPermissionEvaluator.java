package com.myapp.localizationApp.configuration;

import com.myapp.localizationApp.entity.Permission;
import com.myapp.localizationApp.entity.RolePermission;
import com.myapp.localizationApp.entity.User;
import com.myapp.localizationApp.entity.UserRole;
import com.myapp.localizationApp.repository.PermissionRepository;
import com.myapp.localizationApp.repository.RolePermissionRepository;
import com.myapp.localizationApp.repository.UserRepository;
import com.myapp.localizationApp.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component("customPermissionEvaluator")
public class CustomPermissionEvaluator {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public boolean hasPermission(Authentication authentication, Long projectId, String permissionName) {
        System.out.println("Authentication principal: " + authentication.getPrincipal());
        System.out.println("Authentication name: " + authentication.getName());


        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return false;
        }

        List<UserRole> userRoles = userRoleRepository.findByUserIdAndProjectId(user.getId().longValue(), projectId);

        for (UserRole userRole : userRoles) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(userRole.getRole().getId());

            for (RolePermission rolePermission : rolePermissions) {
                Permission permission = permissionRepository.findById(rolePermission.getPermission().getId())
                        .orElse(null);

                if (permission != null && permission.getName().equals(permissionName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
