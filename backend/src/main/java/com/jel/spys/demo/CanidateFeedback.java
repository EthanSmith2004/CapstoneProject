package com.jel.spys.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CanidateFeedback {
    
    private static final Random random = new Random();
    private static List<FeedbackEntry> feedbackData = null;
    
    static {
        loadFeedbackData();
    }
    
    public static FeedbackEntry getRandomFeedback(String item) {
        if (feedbackData == null || feedbackData.isEmpty()) {
            return new FeedbackEntry("Geen terugvoer beskikbaar nie.", 0, "");
        }
        
        // Filter feedback for the specific item
        List<FeedbackEntry> itemFeedback = feedbackData.stream()
            .filter(entry -> entry.getItem().trim().equalsIgnoreCase(item.trim()))
            .toList();
        
        // If no specific feedback for this item, return a random one from all items
        if (itemFeedback.isEmpty()) {
            itemFeedback = feedbackData;
        }
        
        // Return random feedback
        FeedbackEntry randomEntry = itemFeedback.get(random.nextInt(itemFeedback.size()));
        return randomEntry;
    }
    
    private static void loadFeedbackData() {
        feedbackData = new ArrayList<>();
        
        try {
            Resource resource = new ClassPathResource("NeoReviews.csv");
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                boolean isFirstLine = true;
                
                while ((line = reader.readLine()) != null) {
                    // Skip header line
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    
                    FeedbackEntry entry = parseCsvLine(line);
                    if (entry != null) {
                        feedbackData.add(entry);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading feedback data: " + e.getMessage());
            feedbackData = new ArrayList<>(); // Initialize as empty list on error
        }
    }
    
    private static FeedbackEntry parseCsvLine(String line) {
        try {
            // Handle CSV parsing with quoted fields
            List<String> fields = new ArrayList<>();
            boolean inQuotes = false;
            StringBuilder currentField = new StringBuilder();
            
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                
                if (c == '"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    fields.add(currentField.toString().trim());
                    currentField = new StringBuilder();
                } else {
                    currentField.append(c);
                }
            }
            
            // Add the last field
            fields.add(currentField.toString().trim());
            
            if (fields.size() >= 3) {
                String item = fields.get(0).trim();
                int rating = Integer.parseInt(fields.get(1).trim().split("/")[0]);
                String feedback = fields.get(2).trim();
                
                // Remove quotes from feedback if present
                if (feedback.startsWith("\"") && feedback.endsWith("\"")) {
                    feedback = feedback.substring(1, feedback.length() - 1);
                }
                
                return new FeedbackEntry(item, rating, feedback);
            }
        } catch (Exception e) {
            System.err.println("Error parsing CSV line: " + line + " - " + e.getMessage());
        }
        
        return null;
    }
    
    public static class FeedbackEntry {
        private final String item;
        private final int rating;
        private final String feedback;
        
        public FeedbackEntry(String item, int rating, String feedback) {
            this.item = item;
            this.rating = rating;
            this.feedback = feedback;
        }
        
        public String getItem() {
            return item;
        }
        
        public int getRating() {
            return rating;
        }
        
        public String getFeedback() {
            return feedback;
        }
    }
}
