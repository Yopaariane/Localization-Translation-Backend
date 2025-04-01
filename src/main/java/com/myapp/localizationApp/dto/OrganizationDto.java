package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class OrganizationDto {
    private Long id;
    private String name;
    private Timestamp createAt;
    private Long userId;
    private Long defaultLanguageId;
}
