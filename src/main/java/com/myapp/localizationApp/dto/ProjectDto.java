package com.myapp.localizationApp.dto;

import com.myapp.localizationApp.entity.Language;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Timestamp createAt;
    private Long ownerId;
    private List<LanguageDto> languages;
}
