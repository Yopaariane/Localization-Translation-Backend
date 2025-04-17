package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.CommentsDto;
import com.myapp.localizationApp.dto.GlossaryDto;
import com.myapp.localizationApp.entity.*;
import com.myapp.localizationApp.repository.GlossaryRepository;
import com.myapp.localizationApp.repository.OrganizationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GlossaryService {
    private final ModelMapper modelMapper;
    private final GlossaryRepository glossaryRepository;
    private final OrganizationRepository organizationRepository;

    public GlossaryService(ModelMapper modelMapper, GlossaryRepository glossaryRepository, OrganizationRepository organizationRepository){
        this.modelMapper = modelMapper;
        this.glossaryRepository = glossaryRepository;
        this.organizationRepository = organizationRepository;
    }

    public GlossaryDto createGlossary(GlossaryDto glossaryDto){
        Glossary glossary = modelMapper.map(glossaryDto, Glossary.class);
        Organization organization = organizationRepository.findById(glossaryDto.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id:" + glossaryDto.getOrganizationId()));
        glossary.setOrganization(organization);

        Glossary saveGlossary = glossaryRepository.save(glossary);
        return modelMapper.map(saveGlossary, GlossaryDto.class);
    }

    public GlossaryDto updateGlossary(Long id, GlossaryDto glossaryDto){
        Glossary glossary = glossaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("glossary not found with id: " + id));
        glossary.setComment(glossaryDto.getComment());

        Organization organization = organizationRepository.findById(glossaryDto.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id:" + glossaryDto.getOrganizationId()));

        glossary.setOrganization(organization);

        Glossary updatedGlossay = glossaryRepository.save(glossary);
        return  modelMapper.map(updatedGlossay, GlossaryDto.class);
    }

    public void deleteGlossary(Long id){
        glossaryRepository.deleteById(id);
    }

    public List<GlossaryDto> findGlossaryByOrganizationId(Long organizationId) {
        List<Glossary> glossaries = glossaryRepository.findGlossaryByOrganizationId(organizationId);
        modelMapper.typeMap(Glossary.class, GlossaryDto.class).addMappings(mapper ->{
            mapper.map(Glossary::getCreate_at, GlossaryDto::setCreatedAt);
        });
        return  glossaries.stream().map(glossary -> modelMapper.map(glossary, GlossaryDto.class))
                .collect(Collectors.toList());
    }

}
