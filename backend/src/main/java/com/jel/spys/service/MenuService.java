package com.jel.spys.service;

import com.jel.spys.entity.AllergyEntity;
import com.jel.spys.entity.MenuItemEntity;
import com.jel.spys.model.AdminMenuItemCreateRequest;
import com.jel.spys.model.MenuItemDTO;
import com.jel.spys.model.MenuItemQueueRequest;
import com.jel.spys.model.MenuItemStatisticsDTO;
import com.jel.spys.model.AdminMenuItemUpdateRequest;
import com.jel.spys.repository.AllergyRepository;
import com.jel.spys.repository.MenuItemRepository;
import com.jel.spys.repository.OrderItemRepository;
import io.micrometer.core.instrument.Clock;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private AllergyRepository allergyRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private Clock clock;
    @Autowired
    private ClockService clockService;

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getAllUpcomingMenuItems() {
        // For now, return all menu items - can be filtered by client
        List<MenuItemEntity> allItems = menuItemRepository.findAll();
        return allItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getMenuItemsAdminPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        Page<MenuItemEntity> menuItemPage = menuItemRepository.findAll(pageable);
        return menuItemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> searchMenuItemsPaginated(String search, int page, int size) {
        // For now, return all menu items - search can be implemented later
        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        Page<MenuItemEntity> menuItemPage = menuItemRepository.findAll(pageable);
        return menuItemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MenuItemDTO createMenuItem(@Valid AdminMenuItemCreateRequest request) {
        // Get allergies
        List<AllergyEntity> allergies = List.of();
        if (request.getAllergyIds() != null && !request.getAllergyIds().isEmpty()) {
            allergies = allergyRepository.findAllById(request.getAllergyIds());
            if (allergies.size() != request.getAllergyIds().size()) {
                throw new IllegalArgumentException("One or more allergy IDs are invalid");
            }
        }

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setKcal(request.getKcal());
        menuItem.setDeliveryDate(request.getDeliveryDate());
        menuItem.setReleaseDate(request.getReleaseDate());
        menuItem.setEditBy(request.getEditBy());
        menuItem.setImageHero(request.getImageHero());
        menuItem.setImageDetail(request.getImageDetail());
        menuItem.setOrderBy(request.getOrderBy());
        menuItem.setAllergies(allergies);

        menuItem = menuItemRepository.save(menuItem);
        log.info("Created menu item: {}", menuItem.getName());

        return convertToDTO(menuItem);
    }

    @Transactional
    public MenuItemDTO updateMenuItem(Long id, @Valid AdminMenuItemUpdateRequest request) {
        MenuItemEntity menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + id));

        // Update fields if provided
        if (request.getName() != null) {
            menuItem.setName(request.getName());
        }
        if (request.getDescription() != null) {
            menuItem.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            menuItem.setPrice(request.getPrice());
        }
        if (request.getKcal() != null) {
            menuItem.setKcal(request.getKcal());
        }
        if (request.getDeliveryDate() != null) {
            menuItem.setDeliveryDate(request.getDeliveryDate());
        }
        if (request.getReleaseDate() != null) {
            menuItem.setReleaseDate(request.getReleaseDate());
        }
        if (request.getEditBy() != null) {
            menuItem.setEditBy(request.getEditBy());
        }
        if (request.getImageHero() != null) {
            menuItem.setImageHero(request.getImageHero());
        }
        if (request.getImageDetail() != null) {
            menuItem.setImageDetail(request.getImageDetail());
        }
        if (request.getOrderBy() != null) {
            menuItem.setOrderBy(request.getOrderBy());
        }

        // Update allergies if provided
        if (request.getAllergyIds() != null) {
            List<AllergyEntity> allergies = new ArrayList<>();
            if (!request.getAllergyIds().isEmpty()) {
                allergies = allergyRepository.findAllById(request.getAllergyIds());
                if (allergies.size() != request.getAllergyIds().size()) {
                    throw new IllegalArgumentException("One or more allergy IDs are invalid");
                }
            }
            menuItem.setAllergies(allergies);
        }

        menuItem = menuItemRepository.save(menuItem);
        log.info("Updated menu item: {}", menuItem.getName());

        return convertToDTO(menuItem);
    }

    /**
     * Delete a menu item. Only draft and current (non-historic) items can be deleted.
     * Historic items (where orderBy is in the past) cannot be deleted as they may be referenced in orders.
     */
    @Transactional
    public void deleteMenuItem(Long id) {
        log.info("Attempting to delete menu item with ID: {}", id);
        
        MenuItemEntity menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + id));
        
        // Check if the item is historic (orderBy date is in the past)
        if (menuItem.getOrderBy() != null && menuItem.getOrderBy().isBefore(clockService.now())) {
            throw new IllegalStateException("Cannot delete historic menu items. This item has already been ordered or delivered.");
        }
        
        menuItemRepository.deleteById(id);
        log.info("Successfully deleted menu item with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getMenu() {
        // For now, return all menu items - filtering can be added later
        List<MenuItemEntity> availableItems = menuItemRepository.findAll();
        return availableItems.stream()
                .filter(item -> item.isReleased(clockService))
                .filter(item -> item.isActive(clockService))
                .sorted(Comparator.comparing(MenuItemEntity::getDeliveryDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuItemDTO getMenuItemDetail(Long itemId) {
        MenuItemEntity menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + itemId));
        return convertToDTO(menuItem);
    }

    /**
     * Convert MenuItemEntity to MenuItemDTO
     */
    private MenuItemDTO convertToDTO(MenuItemEntity menuItem) {
        return MenuItemDTO.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .kcal(menuItem.getKcal())
                .deliveryDate(menuItem.getDeliveryDate())
                .releaseDate(menuItem.getReleaseDate())
                .editBy(menuItem.getEditBy())
                .imageHero(menuItem.getImageHero())
                .imageDetail(menuItem.getImageDetail())
                .orderBy(menuItem.getOrderBy())
                .allergies(menuItem.getAllergies() != null ? menuItem.getAllergies().stream()
                        .map(AllergyEntity::getAllergy)
                        .collect(Collectors.toList()) : List.of())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .isReleased(menuItem.isReleased(clockService))
                .build();
    }
    
    // New methods for refactored menu endpoints
    
    /**
     * Get all draft menu items (items with null dates)
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getDraftMenuItems() {
        List<MenuItemEntity> draftItems = menuItemRepository.findDraftItems();
        return draftItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get current menu items (released items where orderBy is not past)
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getCurrentMenuItems() {
        Instant now = clockService.now();
        List<MenuItemEntity> currentItems = menuItemRepository.findCurrentMenuItems(now);
        return currentItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get historic menu items (items where orderBy is past)
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getHistoricMenuItems() {
        Instant now = clockService.now();
        List<MenuItemEntity> historicItems = menuItemRepository.findHistoricMenuItems(now);
        return historicItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Queue a new menu item by copying an existing one with new dates
     */
    @Transactional
    public MenuItemDTO queueMenuItem(@Valid MenuItemQueueRequest request) {
        // Find the source menu item
        MenuItemEntity sourceItem = menuItemRepository.findById(request.getSourceMenuItemId())
                .orElseThrow(() -> new IllegalArgumentException("Source menu item not found with id: " + request.getSourceMenuItemId()));
        
        // Create a new menu item by copying the source
        MenuItemEntity newMenuItem = new MenuItemEntity();
        newMenuItem.setName(sourceItem.getName());
        newMenuItem.setDescription(sourceItem.getDescription());
        newMenuItem.setPrice(sourceItem.getPrice());
        newMenuItem.setKcal(sourceItem.getKcal());
        newMenuItem.setImageHero(sourceItem.getImageHero());
        newMenuItem.setImageDetail(sourceItem.getImageDetail());
        
        // Set the new dates from the request
        newMenuItem.setDeliveryDate(request.getDeliveryDate());
        newMenuItem.setReleaseDate(request.getReleaseDate());
        newMenuItem.setEditBy(request.getEditBy());
        newMenuItem.setOrderBy(request.getOrderBy());
        
        // Copy allergies
        newMenuItem.setAllergies(new ArrayList<>(sourceItem.getAllergies()));
        
        newMenuItem = menuItemRepository.save(newMenuItem);
        log.info("Queued menu item '{}' from source item id: {}", newMenuItem.getName(), sourceItem.getId());
        
        return convertToDTO(newMenuItem);
    }
    
    /**
     * Get statistics about popular menu items from order history
     */
    @Transactional(readOnly = true)
    public List<MenuItemStatisticsDTO> getMenuItemStatistics() {
        List<Object[]> results = orderItemRepository.findMenuItemStatistics();
        
        return results.stream()
                .map(row -> MenuItemStatisticsDTO.builder()
                        .menuItemName((String) row[0])
                        .totalOrders((Long) row[1])
                        .totalQuantity((Long) row[2])
                        .totalRevenue((BigDecimal) row[3])
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Get statistics about popular menu items within a date range
     */
    @Transactional(readOnly = true)
    public List<MenuItemStatisticsDTO> getMenuItemStatisticsByDateRange(Instant startDate, Instant endDate) {
        List<Object[]> results = orderItemRepository.findMenuItemStatisticsByDateRange(startDate, endDate);
        
        return results.stream()
                .map(row -> MenuItemStatisticsDTO.builder()
                        .menuItemName((String) row[0])
                        .totalOrders((Long) row[1])
                        .totalQuantity((Long) row[2])
                        .totalRevenue((BigDecimal) row[3])
                        .build())
                .collect(Collectors.toList());
    }
}
