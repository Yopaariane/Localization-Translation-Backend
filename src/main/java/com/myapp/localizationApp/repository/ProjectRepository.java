package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Project;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner_IdAndOrganizationIsNull(Long ownerId);
    List<Project> findAllByOrganizationIsNull();

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.defaultLanguage WHERE p.organization.id = :organizationId")
    List<Project> findByOrganizationId(Long organizationId);

    @Query("SELECT DISTINCT p, " +
            "(SELECT ur.role.id FROM UserRole ur WHERE ur.project.id = p.id AND ur.user.id = :userId) " +
            "FROM Project p " +
            "LEFT JOIN UserRole ur ON ur.project.id = p.id " +
            "WHERE (p.owner.id = :userId OR ur.user.id = :userId) " +
            "AND p.organization IS NULL")
    List<Object[]> findUserProjectsWithRole(@Param("userId") Long userId);


}
