package com.WhenInRogue.NextSteps.models;

import com.WhenInRogue.NextSteps.enums.GroupPosition;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
@Data
@Builder
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupMembershipId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupPosition position;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Override
    public String toString() {
        return "GroupMembership{" +
                "groupMembershipId=" + groupMembershipId +
                ", position=" + position +
                ", isActive=" + isActive +
                '}';
    }
}
