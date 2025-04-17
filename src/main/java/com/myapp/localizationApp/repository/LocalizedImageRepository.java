package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.LocalizedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalizedImageRepository extends JpaRepository<LocalizedImage, Long> {
    List<LocalizedImage> findByProjectIdAndLanguageId(Long projectId, Long languageId);
    List<LocalizedImage> findByProject_Id(Long projectId);
    List<LocalizedImage> findByLanguage_Id(Long languageId);
    List<LocalizedImage> findByProject_IdAndLanguage_Id(Long projectId, Long languageId);
}
