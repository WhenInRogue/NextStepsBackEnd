package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.GroupMembershipDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.enums.GroupPosition;
import com.WhenInRogue.NextSteps.enums.UserRole;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.Group;
import com.WhenInRogue.NextSteps.models.GroupMembership;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.GroupMembershipRepository;
import com.WhenInRogue.NextSteps.repositories.GroupRepository;
import com.WhenInRogue.NextSteps.repositories.UserRepository;
import com.WhenInRogue.NextSteps.services.GroupMembershipService;
import com.WhenInRogue.NextSteps.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMembershipServiceImpl implements GroupMembershipService {

    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response addMember(Long groupId, GroupMembershipDTO membershipDTO) {
        Group group = findGroup(groupId);
        User currentUser = userService.getCurrentLoggedInUser();
        requireCanManageMembers(currentUser, group);

        if (!group.isActive()) {
            throw new IllegalArgumentException("Cannot add members to an inactive group");
        }

        Long userId = resolveUserId(membershipDTO);
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        GroupPosition position = membershipDTO.getPosition() != null
                ? membershipDTO.getPosition()
                : GroupPosition.MEMBER;

        GroupMembership membership = groupMembershipRepository.findByGroupAndUser(group, member)
                .orElse(null);

        if (membership != null && membership.isActive()) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        if (membership != null) {
            membership.setActive(true);
            membership.setPosition(position);
        } else {
            membership = GroupMembership.builder()
                    .group(group)
                    .user(member)
                    .position(position)
                    .isActive(true)
                    .build();
        }

        groupMembershipRepository.save(membership);

        return Response.builder()
                .status(200)
                .message("Member added successfully")
                .groupMembership(modelMapper.map(membership, GroupMembershipDTO.class))
                .build();
    }

    @Override
    public Response getMembersByGroup(Long groupId) {
        Group group = findGroup(groupId);
        User currentUser = userService.getCurrentLoggedInUser();

        if (!canViewGroup(currentUser, group)) {
            throw new ForbiddenException("You do not have access to this group");
        }

        List<GroupMembership> memberships = currentUser.getRole() == UserRole.ADMIN
                ? groupMembershipRepository.findByGroup(group)
                : groupMembershipRepository.findByGroupAndIsActiveTrue(group);

        List<GroupMembershipDTO> membershipDTOs = modelMapper.map(memberships, new TypeToken<List<GroupMembershipDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .groupMemberships(membershipDTOs)
                .build();
    }

    @Override
    public Response getMembershipsByUser(Long userId) {
        User currentUser = userService.getCurrentLoggedInUser();
        if (currentUser.getRole() != UserRole.ADMIN && !currentUser.getId().equals(userId)) {
            throw new ForbiddenException("You can only view your own group memberships");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        List<GroupMembership> memberships = currentUser.getRole() == UserRole.ADMIN
                ? groupMembershipRepository.findByUser(user)
                : groupMembershipRepository.findByUserAndIsActiveTrue(user);

        List<GroupMembershipDTO> membershipDTOs = modelMapper.map(memberships, new TypeToken<List<GroupMembershipDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .groupMemberships(membershipDTOs)
                .build();
    }

    @Override
    @Transactional
    public Response updateMembership(Long membershipId, GroupMembershipDTO membershipDTO) {
        GroupMembership membership = findMembership(membershipId);
        User currentUser = userService.getCurrentLoggedInUser();
        requireCanManageMembers(currentUser, membership.getGroup());

        if (membershipDTO.getPosition() != null) {
            membership.setPosition(membershipDTO.getPosition());
        }
        if (membershipDTO.getIsActive() != null) {
            membership.setActive(membershipDTO.getIsActive());
        }

        groupMembershipRepository.save(membership);

        return Response.builder()
                .status(200)
                .message("Membership updated successfully")
                .groupMembership(modelMapper.map(membership, GroupMembershipDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response removeMember(Long membershipId) {
        GroupMembership membership = findMembership(membershipId);
        User currentUser = userService.getCurrentLoggedInUser();
        requireCanManageMembers(currentUser, membership.getGroup());

        membership.setActive(false);
        groupMembershipRepository.save(membership);

        return Response.builder()
                .status(200)
                .message("Member removed successfully")
                .build();
    }

    private Group findGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group Not Found"));
    }

    private GroupMembership findMembership(Long membershipId) {
        return groupMembershipRepository.findById(membershipId)
                .orElseThrow(() -> new NotFoundException("Group Membership Not Found"));
    }

    private Long resolveUserId(GroupMembershipDTO membershipDTO) {
        if (membershipDTO.getUserId() != null) {
            return membershipDTO.getUserId();
        }
        if (membershipDTO.getUser() != null && membershipDTO.getUser().getId() != null) {
            return membershipDTO.getUser().getId();
        }
        throw new IllegalArgumentException("User id is required");
    }

    private void requireCanManageMembers(User currentUser, Group group) {
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }
        if (currentUser.getRole() == UserRole.DREAM_TEAM_LEADER
                && groupMembershipRepository.existsByGroupAndUserAndIsActiveTrueAndPosition(
                group, currentUser, GroupPosition.LEADER)) {
            return;
        }
        throw new ForbiddenException("You do not have permission to manage members of this group");
    }

    private boolean canViewGroup(User currentUser, Group group) {
        if (currentUser.getRole() == UserRole.ADMIN) {
            return true;
        }
        return group.isActive()
                && groupMembershipRepository.existsByGroupAndUserAndIsActiveTrue(group, currentUser);
    }
}
