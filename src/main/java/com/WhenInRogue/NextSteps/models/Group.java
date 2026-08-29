package com.WhenInRogue.NextSteps.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "groups")
@Data
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @NotBlank(message = "Group name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;



    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<GroupMembership> groupMemberships = new ArrayList<>();

    @Override
    public String toString() {
        return "Group{" +
                "isActive=" + isActive +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
