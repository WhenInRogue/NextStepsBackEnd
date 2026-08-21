package com.WhenInRogue.NextSteps.dtos;

import com.WhenInRogue.NextSteps.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(
        value = {},
        allowSetters = true
)
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    @JsonIgnore
    private String password;

    private UserRole role;
}
