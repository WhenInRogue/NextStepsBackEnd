package com.WhenInRogue.NextSteps.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "answers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"test_result_id", "question_id"})
)
@Data
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @NotNull(message = "Response value is required")
    @Min(value = 0, message = "Response value must be at least 0")
    @Max(value = 4, message = "Response value must be at most 4")
    @Column(name = "response_value", nullable = false)
    private Integer responseValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_result_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TestResult testResult;


    @Override
    public String toString() {
        return "Answer{" +
                "answerId=" + answerId +
                ", responseValue=" + responseValue +
                '}';
    }
}
