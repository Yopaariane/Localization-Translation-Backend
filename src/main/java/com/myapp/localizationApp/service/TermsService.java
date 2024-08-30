package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.dto.TranslationsDto;
import com.myapp.localizationApp.entity.Project;
import com.myapp.localizationApp.entity.Terms;
import com.myapp.localizationApp.entity.Translations;
import com.myapp.localizationApp.repository.ProjectRepository;
import com.myapp.localizationApp.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermsService {
    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    
    public TermsDto createTerm(TermsDto termsDto) {
        Terms term = modelMapper.map(termsDto, Terms.class);

        Project project = projectRepository.findById(termsDto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + termsDto.getProjectId()));

        term.setProject(project);

        int stringNumber = countStrings(term.getTerm());
        term.setStringNumber(stringNumber);
        if (term.getContext() == null) {
            term.setContext("");
        }

        Terms savedTerm = termsRepository.save(term);
        return modelMapper.map(savedTerm, TermsDto.class);
    }

    
    public TermsDto updateTerm(Long id, TermsDto termsDto) {
        Terms term = termsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + id));

        term.setTerm(termsDto.getTerm());
        term.setContext(termsDto.getContext());

        Project project = projectRepository.findById(termsDto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + termsDto.getProjectId()));

        term.setProject(project);

        int stringNumber = countStrings(term.getTerm());
        term.setStringNumber(stringNumber);

        Terms updatedTerm = termsRepository.save(term);
        return modelMapper.map(updatedTerm, TermsDto.class);
    }

    public TermsDto findByTermAndProjectId(String term, Long projectId) {
        Terms termEntity = termsRepository.findByTermAndProjectId(term, projectId);
        return termEntity == null ? null : modelMapper.map(termEntity, TermsDto.class);
    }



    public void deleteTerm(Long id) {
        termsRepository.deleteById(id);
    }

    public TermsDto findById(Long id) {
        Terms term = termsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + id));
        return modelMapper.map(term, TermsDto.class);
    }

    public List<TermsDto> findTermsByProjectId(Long projectId) {
        List<Terms> termsList = termsRepository.findByProjectId(projectId);
        modelMapper.typeMap(Terms.class, TermsDto.class).addMappings(mapper ->{
            mapper.map(Terms::getCreatedAt, TermsDto::setCreateAt);
        });
        return termsList.stream()
                .map(term -> modelMapper.map(term, TermsDto.class))
                .collect(Collectors.toList());
    }

    public int getTotalStringNumberByProjectId(Long projectId) {
        List<Terms> termsList = termsRepository.findByProjectId(projectId);
        return termsList.stream()
                .mapToInt(Terms::getStringNumber)
                .sum();
    }
    
    public long countTermsByProjectId(Long projectId) {
        return termsRepository.countByProjectId(projectId);
    }

    private int countStrings(String term) {
        if (term == null || term.trim().isEmpty()) {
            return 0;
        }
        // Assuming strings are separated by spaces
        return term.trim().split("\\s+").length;
    }
}
