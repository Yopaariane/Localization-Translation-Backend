package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TermsDto {
    private Long id;
    private String term;
    private String context;
    private Date createAt;
    private Long projectId;
    private String stringNumber;
}
