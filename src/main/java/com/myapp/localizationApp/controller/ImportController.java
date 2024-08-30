package com.myapp.localizationApp.controller;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.ImportDto;
import com.myapp.localizationApp.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/upload")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("projectId") Long projectId,
                                        @RequestParam("languageId") Long languageId,
                                        @RequestParam("creatorId") Long creatorId) {
        try {
            List<ImportDto> importData = parseFileToImportDto(file);

            importService.processFileUpload(projectId, languageId, BigInteger.valueOf(creatorId), importData);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "File processed successfully.");
            responseBody.put("status", "success");

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error processing file: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private List<ImportDto> parseFileToImportDto(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        Map<String, String> jsonMap = objectMapper.readValue(bytes, Map.class);

        List<ImportDto> importData = new ArrayList<>();
        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            ImportDto importDto = new ImportDto();
            importDto.setTerm(entry.getKey());
            importDto.setTranslation(entry.getValue());
            importData.add(importDto);
        }

        return importData;
    }
}

