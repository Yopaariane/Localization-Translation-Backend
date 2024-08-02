package com.myapp.localizationApp.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;

    public UserDto() {
    }

    public UserDto(Long id, String name, String password, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
