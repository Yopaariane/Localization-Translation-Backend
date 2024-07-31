package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TranslationDto {
    private Long id;
    private String key;
    private String value;
    private String language;
}
