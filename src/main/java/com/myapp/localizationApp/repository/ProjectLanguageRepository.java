package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.ProjectLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectLanguageRepository extends JpaRepository<ProjectLanguage, Long> {
    List<ProjectLanguage>  findByProjectId(Long projectId);
    List<ProjectLanguage> findByLanguageId(Long languageId);
}
