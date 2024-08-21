package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.ProjectLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ProjectLanguageRepository extends JpaRepository<ProjectLanguage, Long> {
    List<ProjectLanguage>  findByProjectId(BigInteger projectId);
    List<ProjectLanguage> findByLanguageId(Long languageId);
    ProjectLanguage findByLanguageIdAndProjectId(Long languageId, Long projectId);

    @Query("SELECT COUNT(pl) FROM ProjectLanguage pl WHERE pl.project.id = :projectId")
    Long countLanguagesByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pl.project.id FROM ProjectLanguage pl WHERE pl.language.id = :languageId")
    Optional<Long> findProjectIdByLanguageId(@Param("languageId") Long languageId);
}
