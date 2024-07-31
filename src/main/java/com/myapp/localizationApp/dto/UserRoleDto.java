package com.myapp.localizationApp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRoleDto {
    private Long id;
    private Long userId;
    private Long projectId;
    //private Long organizationId;
    private Long roleId;
}
