package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    //private Long organizationId;
}
