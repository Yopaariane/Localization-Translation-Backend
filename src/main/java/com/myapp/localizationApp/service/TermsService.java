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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TermsService {

    private  final TermsRepository termsRepository;
    private  final ProjectRepository projectRepository;
    private  final  ModelMapper modelMapper;
    private final StringRedisTemplate redisTemplate;


    public TermsService(TermsRepository termsRepository, ProjectRepository projectRepository, ModelMapper modelMapper, StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.modelMapper = modelMapper;
        this.termsRepository = termsRepository;
        this.projectRepository = projectRepository;
    }

    @CacheEvict(value = {"totalStringNumber", "termsByProject", "totalStrings"}, key = "#termsDto.projectId", beforeInvocation = true)
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

    @CachePut(value = "terms", key = "#termsDto.id")
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

    @Cacheable(value = "termsByTermAndProject", unless = "#result == null")
    public TermsDto findByTermAndProjectId(String term, Long projectId) {
        Terms termEntity = termsRepository.findByTermAndProjectId(term, projectId);
        return termEntity == null ? null : modelMapper.map(termEntity, TermsDto.class);
    }


    @CacheEvict(value = {"totalStringNumber", "terms", "termsByProject", "totalStrings"}, allEntries = true)
    public void deleteTerm(Long id) {
        termsRepository.deleteById(id);
    }

    @Cacheable(value =  "terms", key = "#id")
    public TermsDto findById(Long id) {
        Terms term = termsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + id));
        return modelMapper.map(term, TermsDto.class);
    }

    @Cacheable(value =  "termsByProject", key = "#p0")
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
        String cacheKey = "totalStrings:" + projectId;
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            return Integer.parseInt(cachedValue);
        }

        // Fetch from DB if not cached
        int total = termsRepository.findByProjectId(projectId)
                .stream()
                .mapToInt(Terms::getStringNumber)
                .sum();

        redisTemplate.opsForValue().set(cacheKey, String.valueOf(total), 10, TimeUnit.MINUTES);
        return total;
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
