package com.jel.spys.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CanidateFeedbackTest {

    @Test
    public void testGetRandomFeedback() {
        // Test with specific item
        String feedback = CanidateFeedback.getRandomFeedback("Spaghetti Bolognese");
        assertNotNull(feedback);
        assertFalse(feedback.isEmpty());
        
        // Test with non-existent item (should still return feedback)
        String fallbackFeedback = CanidateFeedback.getRandomFeedback("Non-existent Item");
        assertNotNull(fallbackFeedback);
        assertFalse(fallbackFeedback.isEmpty());
        
        System.out.println("Sample feedback for Spaghetti Bolognese: " + feedback);
        System.out.println("Fallback feedback: " + fallbackFeedback);
    }
    
    @Test
    public void testMultipleCalls() {
        // Test that multiple calls work (randomness test)
        String feedback1 = CanidateFeedback.getRandomFeedback("Spaghetti Bolognese");
        String feedback2 = CanidateFeedback.getRandomFeedback("Spaghetti Bolognese");
        String feedback3 = CanidateFeedback.getRandomFeedback("Spaghetti Bolognese");
        
        assertNotNull(feedback1);
        assertNotNull(feedback2);
        assertNotNull(feedback3);
        
        System.out.println("Feedback 1: " + feedback1);
        System.out.println("Feedback 2: " + feedback2);
        System.out.println("Feedback 3: " + feedback3);
    }
}