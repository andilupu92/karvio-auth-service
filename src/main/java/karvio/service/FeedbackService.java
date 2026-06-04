package karvio.service;

import karvio.dto.FeedbackRequest;
import karvio.entity.Feedback;
import karvio.enums.FeedbackEnum;
import karvio.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;


    public String bugReports(Long userId, String type, FeedbackRequest feedbackRequest) {

        feedbackRepository.save(Feedback.builder()
                                        .userId(userId)
                                        .feedbackType(FeedbackEnum.valueOf(type))
                                        .description(feedbackRequest.description())
                                        .build());

        return "The user " + userId + " was added a bug report";
    }
}
