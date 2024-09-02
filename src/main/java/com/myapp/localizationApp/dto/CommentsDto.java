package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentsDto {
    private Long id;
    private String comment;
    private Long termId;
    private Long userId;
    private LocalDateTime createdAt;
}
