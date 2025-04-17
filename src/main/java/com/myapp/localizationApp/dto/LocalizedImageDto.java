package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class LocalizedImageDto {
    private Long id;
    private Long languageId;
    private String imageKey;
    private String imagePath;
    private Long projectId;
    private Timestamp createdAt;
}
