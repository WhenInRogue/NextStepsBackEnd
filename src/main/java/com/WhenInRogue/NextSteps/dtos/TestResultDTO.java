package com.WhenInRogue.NextSteps.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestResultDTO {

    private Long testResultId;

    private LocalDateTime completedAt;

    private Boolean complete;

    private UserDTO user;

    private TestDTO test;

    private List<AnswerDTO> answers;

    private List<CategoryScoreDTO> categoryScores;
}
