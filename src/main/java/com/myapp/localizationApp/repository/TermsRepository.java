package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByProjectId(Long projectId);

    long countByProjectId(Long projectId);

    Terms findByTermAndProjectId(String term, Long projectId);

    @Query("SELECT t.project.id FROM Terms t WHERE t.id = :termId")
    Long findProjectIdByTermId(@Param("termId") Long termId);

}
