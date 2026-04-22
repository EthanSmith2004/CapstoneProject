package com.jel.spys.facade;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.FeedbackDTO;
import com.jel.spys.model.FeedbackRequest;
import com.jel.spys.service.FeedbackService;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFeedbackFacade {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    /**
     * Place feedback for current user
     * Facade method that automatically injects current user for
     * FeedbackService.placeUserFeedback(UserEntity user, FeedbackRequest feedback)
     */
    public FeedbackDTO placeFeedback(@Valid FeedbackRequest feedback) {
        UserEntity currentUser = userService.getCurrentUser();
        FeedbackDTO result = feedbackService.placeUserFeedback(currentUser, feedback);
        
        // Log feedback submitted event
        userEventService.logEvent(currentUser, UserEventType.FEEDBACK_SUBMITTED);
        
        return result;
    }
}
