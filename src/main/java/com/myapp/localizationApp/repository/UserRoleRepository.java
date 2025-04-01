package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByProjectId(Long projectId);

    List<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

    List<UserRole> findByUserIdAndProjectId(Long userId, Long projectId);

    //List<UserRole> findByOrganizationId(Long organizationId);
}
