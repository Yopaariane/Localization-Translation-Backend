package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByUserId(Long userId);
}
