package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Glossary;
import com.myapp.localizationApp.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GlossaryRepository extends JpaRepository<Glossary, Long> {
    List<Glossary> findGlossaryByOrganizationId(Long organizationId);
}
