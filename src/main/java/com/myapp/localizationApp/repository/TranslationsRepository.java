package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Language;
import com.myapp.localizationApp.entity.Project;
import com.myapp.localizationApp.entity.Translations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranslationsRepository extends JpaRepository<Translations, Long> {
    List<Translations> findTranslationsByTermId(Long termId);
    List<Translations> findByTerm_ProjectAndLanguage(Project project, Language language);

    @Query("SELECT t FROM Translations t WHERE t.language.id = :languageId")
    List<Translations> findTranslationsByLanguageId(@Param("languageId") Long languageId);

    @Query("SELECT COUNT(t) FROM Translations t WHERE t.term.project.id = :projectId")
    Long countTranslationsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(t) FROM Translations t WHERE t.term.id = :termId")
    Long countTranslationsByTermId(@Param("termId") Long termId);

    @Query("SELECT COUNT(t) FROM Translations t WHERE t.language.id = :languageId")
    Long countTranslationsByLanguageId(@Param("languageId") Long languageId);

    @Query("SELECT COUNT(t.id) FROM Translations t WHERE t.language.id = :languageId AND t.term.project.id = :projectId")
    Long countTranslationsByLanguageAndProject(@Param("languageId") Long languageId, @Param("projectId") Long projectId);

//    @Query("SELECT SUM(t.stringNumber) FROM Translations tr JOIN tr.term t WHERE tr.user.id = :userId")
//    Long sumTranslatedStringsForUser(@Param("userId") Long userId);

}
