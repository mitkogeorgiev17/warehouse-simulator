package com.example.warehouse.simulator.warehouse.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter
public class GridConverter implements AttributeConverter<List<List<String>>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<List<String>> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting grid to JSON", e);
        }
    }

    @Override
    public List<List<String>> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading grid from JSON", e);
        }
    }
}
