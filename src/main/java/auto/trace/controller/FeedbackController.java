package auto.trace.controller;

import auto.trace.dto.FeedbackRequest;
import auto.trace.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedback/{type}")
    public ResponseEntity<String> addFeedback(@RequestHeader("X-User-Id") Long userId,
                                            @Valid @RequestBody FeedbackRequest feedbackRequest,
                                            @PathVariable String type) {
        return new ResponseEntity<>(feedbackService.bugReports(userId, type, feedbackRequest), HttpStatus.CREATED);
    }
}
