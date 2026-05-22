package auto.trace.entity;

import auto.trace.enums.FeedbackEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 255, nullable = false)
    private String description;

    @Column(name="feedback_type", nullable = false)
    private FeedbackEnum feedbackType;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
