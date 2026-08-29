package com.WhenInRogue.NextSteps.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "category_scores",
        uniqueConstraints = @UniqueConstraint(columnNames = {"test_result_id", "category_id"})
)
@Data
@Builder
public class CategoryScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryScoreId;

    @NotNull
    @Column(name = "total_raw_points", nullable = false)
    @Min(value = 0, message = "Value cannot be negative")
    private Integer totalRawPoints;

    @NotNull
    @Column(name = "max_points", nullable = false)
    @Min(value = 0, message = "Value cannot be negative")
    private Integer maxPoints;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_result_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TestResult testResult;


    @Override
    public String toString() {
        return "CategoryScore{" +
                "categoryScoreId=" + categoryScoreId +
                ", totalRawPoints=" + totalRawPoints +
                ", maxPoints=" + maxPoints +
                '}';
    }
}
