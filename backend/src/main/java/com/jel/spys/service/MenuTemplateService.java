package com.jel.spys.service;

import com.jel.spys.entity.MenuTemplateEntity;
import com.jel.spys.model.*;
import com.jel.spys.repository.MenuTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class MenuTemplateService {

    @Autowired
    private MenuTemplateRepository menuTemplateRepository;

    @Transactional(readOnly = true)
    public List<PresetNameDTO> getDistinctPresetNames() {
        log.info("Getting all distinct preset names");
        List<Object[]> results = menuTemplateRepository.findPresetNamesWithMetadata();
        
        return results.stream()
                .map(result -> new PresetNameDTO(
                        (String) result[0],     // presetName
                        ((Long) result[1]).intValue(), // count
                        (Instant) result[2]     // lastUpdated
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MenuTemplateDTO> getTemplatesByPresetName(String presetName) {
        log.info("Getting templates for preset: {}", presetName);
        List<MenuTemplateEntity> entities = menuTemplateRepository.findByPresetNameOrderByDeliveryOffset(presetName);
        return entities.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public MenuTemplateDTO createTemplate(MenuTemplateCreateRequest request) {
        log.info("Creating new template for preset: {}", request.getPresetName());
        
        validateOffsets(request.getDeliveryOffsetMinutes(), request.getReleaseOffsetMinutes(), request.getOrderByOffsetMinutes());
        
        MenuTemplateEntity entity = new MenuTemplateEntity();
        entity.setDescription(request.getDescription());
        entity.setDeliveryOffsetMinutes(request.getDeliveryOffsetMinutes());
        entity.setReleaseOffsetMinutes(request.getReleaseOffsetMinutes());
        entity.setOrderByOffsetMinutes(request.getOrderByOffsetMinutes());
        entity.setPresetName(request.getPresetName());
        
        MenuTemplateEntity savedEntity = menuTemplateRepository.save(entity);
        log.info("Created template with ID: {}", savedEntity.getId());
        
        return convertToDTO(savedEntity);
    }

    public MenuTemplateDTO updateTemplate(Long id, MenuTemplateUpdateRequest request) {
        log.info("Updating template with ID: {}", id);
        
        Optional<MenuTemplateEntity> optionalEntity = menuTemplateRepository.findById(id);
        if (optionalEntity.isEmpty()) {
            throw new RuntimeException("Menu template not found with ID: " + id);
        }
        
        MenuTemplateEntity entity = optionalEntity.get();
        
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getDeliveryOffsetMinutes() != null) {
            entity.setDeliveryOffsetMinutes(request.getDeliveryOffsetMinutes());
        }
        if (request.getReleaseOffsetMinutes() != null) {
            entity.setReleaseOffsetMinutes(request.getReleaseOffsetMinutes());
        }
        if (request.getOrderByOffsetMinutes() != null) {
            entity.setOrderByOffsetMinutes(request.getOrderByOffsetMinutes());
        }
        if (request.getPresetName() != null) {
            entity.setPresetName(request.getPresetName());
        }
        
        // Validate offsets after update
        validateOffsets(entity.getDeliveryOffsetMinutes(), entity.getReleaseOffsetMinutes(), entity.getOrderByOffsetMinutes());
        
        MenuTemplateEntity savedEntity = menuTemplateRepository.save(entity);
        log.info("Updated template with ID: {}", savedEntity.getId());
        
        return convertToDTO(savedEntity);
    }

    public void deleteTemplate(Long id) {
        log.info("Deleting template with ID: {}", id);
        
        if (!menuTemplateRepository.existsById(id)) {
            throw new RuntimeException("Menu template not found with ID: " + id);
        }
        
        menuTemplateRepository.deleteById(id);
        log.info("Deleted template with ID: {}", id);
    }

    public void deleteTemplatesByPresetName(String presetName) {
        log.info("Deleting all templates for preset: {}", presetName);
        
        menuTemplateRepository.deleteByPresetName(presetName);
        log.info("Deleted all templates for preset: {}", presetName);
    }

    private void validateOffsets(Integer deliveryOffset, Integer releaseOffset, Integer orderByOffset) {
        if (releaseOffset > deliveryOffset) {
            throw new IllegalArgumentException("Release offset cannot be greater than delivery offset");
        }
        if (orderByOffset > deliveryOffset) {
            throw new IllegalArgumentException("Order by offset cannot be greater than delivery offset");
        }
    }

    private MenuTemplateDTO convertToDTO(MenuTemplateEntity entity) {
        return new MenuTemplateDTO(
                entity.getId(),
                entity.getDescription(),
                entity.getDeliveryOffsetMinutes(),
                entity.getReleaseOffsetMinutes(),
                entity.getOrderByOffsetMinutes(),
                entity.getPresetName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}