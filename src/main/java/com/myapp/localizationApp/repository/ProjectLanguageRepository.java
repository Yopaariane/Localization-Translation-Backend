package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.ProjectLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface ProjectLanguageRepository extends JpaRepository<ProjectLanguage, Long> {
    List<ProjectLanguage>  findByProjectId(BigInteger projectId);
    List<ProjectLanguage> findByLanguageId(Long languageId);
}
