package com.WhenInRogue.NextSteps.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "questions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"test_id", "question_number"})
)
@Data
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @NotNull(message = "Question number is required")
    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @NotBlank(message = "Question text is required")
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;



    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", questionNumber=" + questionNumber +
                ", questionText='" + questionText + '\'' +
                '}';
    }
}
