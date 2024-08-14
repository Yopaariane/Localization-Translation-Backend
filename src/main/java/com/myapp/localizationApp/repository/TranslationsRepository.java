package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Translations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranslationsRepository extends JpaRepository<Translations, Long> {
    List<Translations> findTranslationsByTermId(Long termId);

    @Query("SELECT t FROM Translations t WHERE t.language.id = :languageId")
    List<Translations> findTranslationsByLanguageId(@Param("languageId") Long languageId);

    @Query("SELECT COUNT(t) FROM Translations t WHERE t.term.project.id = :projectId")
    Long countTranslationsByProjectId(@Param("projectId") Long projectId);
}
