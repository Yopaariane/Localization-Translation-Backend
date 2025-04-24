package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Role;
import com.myapp.localizationApp.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);
    List<RolePermission> findByRole(Role role);

}
