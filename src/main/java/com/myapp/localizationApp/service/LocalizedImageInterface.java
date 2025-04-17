package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.LocalizedImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface LocalizedImageInterface {
    LocalizedImageDto saveImage(MultipartFile file, Long languageId, Long projectId, String imageKey) throws IOException;
    Map<String, String> exportImages(Long projectId, Long languageId);
    LocalizedImageDto getImageById(Long id);
    List<LocalizedImageDto> getImagesByProjectId(Long projectId);
    List<LocalizedImageDto> getImagesByLanguageId(Long languageId);
    List<LocalizedImageDto> getImagesByProjectAndLanguage(Long projectId, Long languageId);
    void deleteImage(Long id) throws IOException;
    LocalizedImageDto updateImage(Long id, MultipartFile file, String imageKey) throws IOException;
}
