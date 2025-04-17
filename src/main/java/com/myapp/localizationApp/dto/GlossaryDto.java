package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GlossaryDto {
    private Long id;
    private String term;
    private String initial_translation;
    private String context;
    private String translatable;
    private String comment;
    private String status;
    private LocalDateTime createdAt;
    private Long organizationId;
}
