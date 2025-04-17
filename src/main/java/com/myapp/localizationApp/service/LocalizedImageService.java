package com.myapp.localizationApp.service;

import com.myapp.localizationApp.dto.GlossaryDto;
import com.myapp.localizationApp.dto.LocalizedImageDto;
import com.myapp.localizationApp.entity.Glossary;
import com.myapp.localizationApp.entity.Language;
import com.myapp.localizationApp.entity.LocalizedImage;
import com.myapp.localizationApp.entity.Project;
import com.myapp.localizationApp.repository.LanguageRepository;
import com.myapp.localizationApp.repository.LocalizedImageRepository;
import com.myapp.localizationApp.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalizedImageService implements LocalizedImageInterface {
    private final LocalizedImageRepository imageRepository;
    private final LanguageRepository languageRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    @Value("${image.storage.path}")
    private String imageStoragePath;

    @Override
    public LocalizedImageDto saveImage(MultipartFile file, Long languageId, Long projectId, String imageKey) throws IOException {
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = imageKey + "-" + language.getCode() + "." + extension;

        Path projectDir = Paths.get(imageStoragePath, String.valueOf(projectId));
        Files.createDirectories(projectDir);
        Path filePath = projectDir.resolve(filename);
        Files.write(filePath, file.getBytes());

        LocalizedImage image = new LocalizedImage();
        image.setImage_key(imageKey);
        image.setImage_path(filePath.toString());
        image.setLanguage(language);
        image.setProject(project);

        LocalizedImage saved = imageRepository.save(image);
        return modelMapper.map(saved, LocalizedImageDto.class);
    }

    @Override
    public Map<String, String> exportImages(Long projectId, Long languageId) {
        List<LocalizedImage> images = imageRepository.findByProjectIdAndLanguageId(projectId, languageId);
        return images.stream().collect(Collectors.toMap(
                LocalizedImage::getImage_key,
                img -> "/image-storage/" + projectId + "/" + img.getImage_key() + "-" + img.getLanguage().getCode() + "." + FilenameUtils.getExtension(img.getImage_path())
        ));
    }

    @Override
    public LocalizedImageDto getImageById(Long id) {
        LocalizedImage image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        modelMapper.typeMap(LocalizedImage.class, LocalizedImageDto.class).addMappings(mapper ->{
            mapper.map(LocalizedImage::getCreated_at, LocalizedImageDto::setCreatedAt);
            mapper.map(LocalizedImage::getImage_key, LocalizedImageDto::setImageKey);
            mapper.map(LocalizedImage::getImage_path, LocalizedImageDto::setImagePath);
        });
        return modelMapper.map(image, LocalizedImageDto.class);
    }

    @Override
    public List<LocalizedImageDto> getImagesByProjectId(Long projectId) {
        modelMapper.typeMap(LocalizedImage.class, LocalizedImageDto.class).addMappings(mapper ->{
            mapper.map(LocalizedImage::getCreated_at, LocalizedImageDto::setCreatedAt);
            mapper.map(LocalizedImage::getImage_key, LocalizedImageDto::setImageKey);
            mapper.map(LocalizedImage::getImage_path, LocalizedImageDto::setImagePath);
        });
        return imageRepository.findByProject_Id(projectId)
                .stream()
                .map(img -> modelMapper.map(img, LocalizedImageDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalizedImageDto> getImagesByLanguageId(Long languageId) {
        return imageRepository.findByLanguage_Id(languageId)
                .stream()
                .map(img -> modelMapper.map(img, LocalizedImageDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalizedImageDto> getImagesByProjectAndLanguage(Long projectId, Long languageId) {
        return imageRepository.findByProject_IdAndLanguage_Id(projectId, languageId)
                .stream()
                .map(img -> modelMapper.map(img, LocalizedImageDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(Long id) throws IOException {
        LocalizedImage image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        Path path = Paths.get(image.getImage_path());
        Files.deleteIfExists(path);
        imageRepository.deleteById(id);
    }

    @Override
    public LocalizedImageDto updateImage(Long id, MultipartFile file, String imageKey) throws IOException {
        LocalizedImage image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = imageKey + "-" + image.getLanguage().getCode() + "." + extension;

        Path projectDir = Paths.get(imageStoragePath, String.valueOf(image.getProject().getId()));
        Files.createDirectories(projectDir);
        Path newPath = projectDir.resolve(filename);
        Files.write(newPath, file.getBytes());

        // Delete old file if different
        Path oldPath = Paths.get(image.getImage_path());
        if (!oldPath.equals(newPath)) {
            Files.deleteIfExists(oldPath);
        }

        image.setImage_key(imageKey);
        image.setImage_path(newPath.toString());
        return modelMapper.map(imageRepository.save(image), LocalizedImageDto.class);
    }
}
