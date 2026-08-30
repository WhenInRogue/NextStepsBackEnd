package com.WhenInRogue.NextSteps.dtos;

import com.WhenInRogue.NextSteps.enums.GroupPosition;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMembershipDTO {

    private Long groupMembershipId;

    private GroupPosition position;

    private Boolean isActive;

    private GroupDTO group;

    private UserDTO user;

    private Long userId;
}
