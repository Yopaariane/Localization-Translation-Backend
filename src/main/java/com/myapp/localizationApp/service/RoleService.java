package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.RoleDto;
import com.myapp.localizationApp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

}
