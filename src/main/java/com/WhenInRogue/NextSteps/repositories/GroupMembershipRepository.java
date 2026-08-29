package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Group;
import com.WhenInRogue.NextSteps.models.GroupMembership;
import com.WhenInRogue.NextSteps.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {

    List<GroupMembership> findByUser(User user);

    List<GroupMembership> findByGroup(Group group);

    List<GroupMembership> findByUserAndIsActiveTrue(User user);

    Optional<GroupMembership> findByGroupAndUser(Group group, User user);

    boolean existsByGroupAndUser(Group group, User user);
}
