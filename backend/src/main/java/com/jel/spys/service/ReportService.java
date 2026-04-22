package com.jel.spys.service;

import com.jel.spys.entity.OrderItemStatus;
import com.jel.spys.model.AdminCreateReportRequest;
import com.jel.spys.model.ReportStatusDTO;
import com.jel.spys.model.report.DeliveryReportData;
import com.jel.spys.model.report.DeliveryReportItem;
import com.jel.spys.model.report.KitchenReportData;
import com.jel.spys.model.report.KitchenReportItem;
import com.jel.spys.repository.OrderItemRepository;

import jakarta.persistence.Tuple;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReportService {

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ClockService clockService;

    public ReportStatusDTO getReportStatus(Long reportId) {
        return null;
    }

    public ReportStatusDTO createReport(@Valid AdminCreateReportRequest request) {
        return null;
    }

    public KitchenReportData getKitchenReport(Instant date) {
        log.info("Generating kitchen report data for: {} ", date);
        
        try {
            // Get raw data from repository
            List<Tuple> rawData = orderItemRepository.getKitchenReportData(date);
            log.debug("Retrieved {} kitchen report records", rawData.size());
            
            // Transform raw data into report items
            List<KitchenReportItem> items = rawData.stream()
                .map(tuple -> KitchenReportItem.builder()
                    .name(tuple.get(0, String.class))
                    .quantity(tuple.get(1, Long.class))
                    .totalSales(tuple.get(2, BigDecimal.class))
                    .build())
                .collect(Collectors.toList());
            
            // Calculate summary statistics
            long totalQuantity = items.stream()
                .mapToLong(KitchenReportItem::getQuantity)
                .sum();
            
            BigDecimal totalRevenue = items.stream()
                .map(KitchenReportItem::getTotalSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Create report data
            return KitchenReportData.builder()
                .startDate(date)
                .generatedAt(clockService.now())
                .items(items)
                .totalQuantity(totalQuantity)
                .totalRevenue(totalRevenue)
                .uniqueItems(items.size())
                .build();
            
        } catch (Exception e) {
            log.error("Error generating kitchen report data", e);
            throw new RuntimeException("Failed to generate kitchen report data", e);
        }
    }

    public DeliveryReportData getDeliveryReport(Instant date) {
        log.info("Generating delivery report data for date {}", date);
        
        try {
            // Get raw data from repository
            List<Tuple> rawData = orderItemRepository.getDeliveryReportData(
                date, List.of(OrderItemStatus.IN_PROGRESS, OrderItemStatus.PAID)
            );
            log.debug("Retrieved {} delivery report records", rawData.size());
            
            // Transform raw data into report items
            List<DeliveryReportItem> deliveries = rawData.stream()
                .map(tuple -> DeliveryReportItem.builder()
                    .deliveryDate(tuple.get(0, Instant.class))
                    .itemName(tuple.get(1, String.class))
                    .quantity(tuple.get(2, Long.class))
                    .firstName(tuple.get(3, String.class))
                    .lastName(tuple.get(4, String.class))
                    .credentialNumber(tuple.get(5, String.class))
                    .residenceName(tuple.get(6, String.class))
                    .campusName(tuple.get(7, String.class))
                    .build())
                .collect(Collectors.toList());
            
            // Calculate summary statistics
            int uniqueCampuses = (int) deliveries.stream()
                .map(DeliveryReportItem::getCampusName)
                .distinct()
                .count();
            
            int uniqueResidences = (int) deliveries.stream()
                .map(DeliveryReportItem::getResidenceName)
                .distinct()
                .count();
            
            return DeliveryReportData.builder()
                .startDate(date)
                .generatedAt(clockService.now())
                .deliveries(deliveries)
                .totalDeliveries(deliveries.size())
                .uniqueCampuses(uniqueCampuses)
                .uniqueResidences(uniqueResidences)
                .build();
            
        } catch (Exception e) {
            log.error("Error generating delivery report data", e);
            throw new RuntimeException("Failed to generate delivery report data", e);
        }
    }

    public KitchenReportData getKitchenReportPeriod(Instant start, Instant end, Long campusId, Long residenceId) {
        log.info("Generating kitchen report data for period: {} to {}, campusId: {}, residenceId: {}", start, end, campusId, residenceId);
        
        try {
            // Get raw data from repository
            List<Tuple> rawData = orderItemRepository.getKitchenReportPeriodData(start, end, campusId, residenceId);
            log.debug("Retrieved {} kitchen report records for period", rawData.size());
            
            // Transform raw data into report items
            List<KitchenReportItem> items = rawData.stream()
                .map(tuple -> KitchenReportItem.builder()
                    .name(tuple.get(0, String.class))
                    .quantity(tuple.get(1, Long.class))
                    .totalSales(tuple.get(2, BigDecimal.class))
                    .build())
                .collect(Collectors.toList());
            
            // Calculate summary statistics
            long totalQuantity = items.stream()
                .mapToLong(KitchenReportItem::getQuantity)
                .sum();
            
            BigDecimal totalRevenue = items.stream()
                .map(KitchenReportItem::getTotalSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Create report data
            return KitchenReportData.builder()
                .startDate(start)
                .endDate(end)
                .generatedAt(clockService.now())
                .items(items)
                .totalQuantity(totalQuantity)
                .totalRevenue(totalRevenue)
                .uniqueItems(items.size())
                .build();
            
        } catch (Exception e) {
            log.error("Error generating kitchen report data for period", e);
            throw new RuntimeException("Failed to generate kitchen report data for period", e);
        }
    }

    public DeliveryReportData getDeliveryReportPeriod(Instant start, Instant end, Long campusId, Long residenceId) {
        log.info("Generating delivery report data for period: {} to {}, campusId: {}, residenceId: {}", start, end, campusId, residenceId);
        
        try {
            // Get raw data from repository
            List<Tuple> rawData = orderItemRepository.getDeliveryReportPeriodData(
                start, end, List.of(OrderItemStatus.IN_PROGRESS, OrderItemStatus.PAID), campusId, residenceId
            );
            log.debug("Retrieved {} delivery report records for period", rawData.size());
            
            // Transform raw data into report items
            List<DeliveryReportItem> deliveries = rawData.stream()
                .map(tuple -> DeliveryReportItem.builder()
                    .deliveryDate(tuple.get(0, Instant.class))
                    .itemName(tuple.get(1, String.class))
                    .quantity(tuple.get(2, Long.class))
                    .firstName(tuple.get(3, String.class))
                    .lastName(tuple.get(4, String.class))
                    .credentialNumber(tuple.get(5, String.class))
                    .residenceName(tuple.get(6, String.class))
                    .campusName(tuple.get(7, String.class))
                    .build())
                .collect(Collectors.toList());
            
            // Calculate summary statistics
            int uniqueCampuses = (int) deliveries.stream()
                .map(DeliveryReportItem::getCampusName)
                .distinct()
                .count();
            
            int uniqueResidences = (int) deliveries.stream()
                .map(DeliveryReportItem::getResidenceName)
                .distinct()
                .count();
            
            return DeliveryReportData.builder()
                .startDate(start)
                .endDate(end)
                .generatedAt(clockService.now())
                .deliveries(deliveries)
                .totalDeliveries(deliveries.size())
                .uniqueCampuses(uniqueCampuses)
                .uniqueResidences(uniqueResidences)
                .build();
            
        } catch (Exception e) {
            log.error("Error generating delivery report data for period", e);
            throw new RuntimeException("Failed to generate delivery report data for period", e);
        }
    }
}
