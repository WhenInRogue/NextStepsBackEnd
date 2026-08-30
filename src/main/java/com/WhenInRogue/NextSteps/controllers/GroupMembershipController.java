package com.WhenInRogue.NextSteps.controllers;

import com.WhenInRogue.NextSteps.dtos.GroupMembershipDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.services.GroupMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GroupMembershipController {

    private final GroupMembershipService groupMembershipService;

    @PostMapping("/api/groups/{groupId}/addmember")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DREAM_TEAM_LEADER')")
    public ResponseEntity<Response> addMember(@PathVariable Long groupId,
                                              @RequestBody @Valid GroupMembershipDTO membershipDTO) {
        return ResponseEntity.ok(groupMembershipService.addMember(groupId, membershipDTO));
    }

    @GetMapping("/api/groups/{groupId}/members")
    public ResponseEntity<Response> getMembersByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupMembershipService.getMembersByGroup(groupId));
    }

    @GetMapping("/api/users/{userId}/memberships")
    public ResponseEntity<Response> getMembershipsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(groupMembershipService.getMembershipsByUser(userId));
    }

    @PutMapping("/api/memberships/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DREAM_TEAM_LEADER')")
    public ResponseEntity<Response> updateMembership(@PathVariable Long id,
                                                     @RequestBody @Valid GroupMembershipDTO membershipDTO) {
        return ResponseEntity.ok(groupMembershipService.updateMembership(id, membershipDTO));
    }

    @PutMapping("/api/memberships/{id}/remove")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DREAM_TEAM_LEADER')")
    public ResponseEntity<Response> removeMember(@PathVariable Long id) {
        return ResponseEntity.ok(groupMembershipService.removeMember(id));
    }
}
