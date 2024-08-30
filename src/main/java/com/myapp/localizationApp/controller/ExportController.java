package com.myapp.localizationApp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.ExportDto;
import com.myapp.localizationApp.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/export")
public class ExportController {
    @Autowired
    private ExportService exportService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/csv")
    public ResponseEntity<String> exportTranslationsToCsv(@RequestParam Long projectId, @RequestParam Long languageId) {
        try {
            Map<String, String> exportData = exportService.exportTranslationsByLanguage(projectId, languageId);

            String languageName = exportService.getLanguageCode(languageId);

            StringWriter writer = new StringWriter();
            writer.write("Term,Translation\n");

            for (Map.Entry<String, String> entry : exportData.entrySet()) {
                writer.write(String.format("\"%s\",\"%s\"\n",
                        escapeCsv(entry.getKey()),
                        escapeCsv(entry.getValue())));
            }

            String fileName = languageName + ".csv";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            return new ResponseEntity<>(writer.toString(), headers, HttpStatus.OK);

        } catch (Exception e) {
            throw new ResourceNotFoundException("Downloading Translations CSV Failed, Due to: " + e.getMessage());
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @GetMapping("/json")
    public ResponseEntity<ByteArrayResource> exportTranslationsToJson(@RequestParam Long projectId, @RequestParam Long languageId) {
        try {
            Map<String, String> exportData = exportService.exportTranslationsByLanguage(projectId, languageId);

//            String jsonData = objectMapper.writeValueAsString(exportData);
            String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData);

            ByteArrayResource resource = new ByteArrayResource(jsonData.getBytes());

            String fileName = exportService.getLanguageCode(languageId) + ".json";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resource);

        } catch (JsonProcessingException e) {
            throw new ResourceNotFoundException("Error processing JSON for export");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Downloading Translations JSON Failed, Due to: " + e.getMessage());
        }
    }

}
