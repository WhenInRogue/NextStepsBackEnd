package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.GroupMembershipDTO;
import com.WhenInRogue.NextSteps.dtos.Response;

public interface GroupMembershipService {

    Response addMember(Long groupId, GroupMembershipDTO membershipDTO);

    Response getMembersByGroup(Long groupId);

    Response getMembershipsByUser(Long userId);

    Response updateMembership(Long membershipId, GroupMembershipDTO membershipDTO);

    Response removeMember(Long membershipId);
}
