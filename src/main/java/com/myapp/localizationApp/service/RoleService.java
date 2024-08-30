package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.LanguageDto;
import com.myapp.localizationApp.dto.RoleDto;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.entity.Role;
import com.myapp.localizationApp.entity.Terms;
import com.myapp.localizationApp.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;

    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        return modelMapper.map(role, RoleDto.class);
    }

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> modelMapper.map(role, RoleDto.class))
                .collect(Collectors.toList());
    }
}
