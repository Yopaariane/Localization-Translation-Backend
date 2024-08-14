package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class TranslationsDto {
    private Long id;
    private String translationText;
    private Long termId;
    private Long languageId;
    private Long creatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
