package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectLanguageDto {
    private Long id;
    private Long projectId;
    private Long languageId;
}
