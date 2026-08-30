package com.WhenInRogue.NextSteps.dtos;

import com.WhenInRogue.NextSteps.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //Generic
    private int status;
    private String message;

    //for login
    private String token;
    private UserRole role;
    private String expirationTime;

    //for pagination
    private Integer totalPages;
    private Long totalElements;

    //data output optionals
    private UserDTO user;
    private List<UserDTO> users;

    private TestDTO test;
    private List<TestDTO> tests;

    private GroupDTO group;
    private List<GroupDTO> groups;

    private GroupMembershipDTO groupMembership;
    private List<GroupMembershipDTO> groupMemberships;

    private CategoryDTO category;
    private List<CategoryDTO> categories;

    private QuestionDTO question;
    private List<QuestionDTO> questions;

    private TestResultDTO testResult;
    private List<TestResultDTO> testResults;

    private AnswerDTO answer;
    private List<AnswerDTO> answers;

    private CategoryScoreDTO categoryScore;
    private List<CategoryScoreDTO> categoryScores;
}
