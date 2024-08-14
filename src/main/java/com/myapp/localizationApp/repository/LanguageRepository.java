package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    Language findByCode(String code);


}
