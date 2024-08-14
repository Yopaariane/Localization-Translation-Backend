package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByProjectId(Long projectId);
    long countByProjectId(Long projectId);
}
