package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
