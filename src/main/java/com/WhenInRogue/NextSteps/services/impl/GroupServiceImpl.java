package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.GroupDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.enums.UserRole;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NameValueRequiredException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.Group;
import com.WhenInRogue.NextSteps.models.GroupMembership;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.GroupMembershipRepository;
import com.WhenInRogue.NextSteps.repositories.GroupRepository;
import com.WhenInRogue.NextSteps.services.GroupService;
import com.WhenInRogue.NextSteps.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response createGroup(GroupDTO groupDTO) {
        requireAdmin();

        if (groupDTO.getName() == null || groupDTO.getName().isBlank()) {
            throw new NameValueRequiredException("Group name is required");
        }

        groupRepository.findByName(groupDTO.getName()).ifPresent(existing -> {
            throw new IllegalArgumentException("A group with that name already exists");
        });

        Group group = Group.builder()
                .name(groupDTO.getName())
                .description(groupDTO.getDescription())
                .isActive(groupDTO.getIsActive() == null || groupDTO.getIsActive())
                .build();

        groupRepository.save(group);

        return Response.builder()
                .status(200)
                .message("Group Created Successfully")
                .group(modelMapper.map(group, GroupDTO.class))
                .build();
    }

    @Override
    public Response getAllGroups() {
        User currentUser = userService.getCurrentLoggedInUser();
        List<Group> groups;

        if (currentUser.getRole() == UserRole.ADMIN) {
            groups = groupRepository.findAll(Sort.by(Sort.Direction.DESC, "groupId"));
        } else {
            groups = groupMembershipRepository.findByUserAndIsActiveTrue(currentUser).stream()
                    .map(GroupMembership::getGroup)
                    .filter(Group::isActive)
                    .toList();
        }

        List<GroupDTO> groupDTOs = modelMapper.map(groups, new TypeToken<List<GroupDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .groups(groupDTOs)
                .build();
    }

    @Override
    public Response getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group Not Found"));

        User currentUser = userService.getCurrentLoggedInUser();
        if (!canViewGroup(currentUser, group)) {
            throw new ForbiddenException("You do not have access to this group");
        }

        return Response.builder()
                .status(200)
                .message("success")
                .group(modelMapper.map(group, GroupDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response updateGroup(Long id, GroupDTO groupDTO) {
        requireAdmin();

        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group Not Found"));

        if (groupDTO.getName() != null) {
            groupRepository.findByName(groupDTO.getName())
                    .filter(other -> !other.getGroupId().equals(id))
                    .ifPresent(other -> {
                        throw new IllegalArgumentException("A group with that name already exists");
                    });
            existingGroup.setName(groupDTO.getName());
        }
        if (groupDTO.getDescription() != null) {
            existingGroup.setDescription(groupDTO.getDescription());
        }
        if (groupDTO.getIsActive() != null) {
            existingGroup.setActive(groupDTO.getIsActive());
        }

        groupRepository.save(existingGroup);

        return Response.builder()
                .status(200)
                .message("Group Updated Successfully")
                .group(modelMapper.map(existingGroup, GroupDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response deleteGroup(Long id) {
        requireAdmin();

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group Not Found"));

        groupRepository.delete(group);

        return Response.builder()
                .status(200)
                .message("Group Deleted Successfully")
                .build();
    }

    private void requireAdmin() {
        User currentUser = userService.getCurrentLoggedInUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only an admin can manage groups");
        }
    }

    private boolean canViewGroup(User currentUser, Group group) {
        if (currentUser.getRole() == UserRole.ADMIN) {
            return true;
        }
        return group.isActive()
                && groupMembershipRepository.existsByGroupAndUserAndIsActiveTrue(group, currentUser);
    }
}
