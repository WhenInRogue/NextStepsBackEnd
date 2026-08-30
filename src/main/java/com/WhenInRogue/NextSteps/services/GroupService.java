package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.GroupDTO;
import com.WhenInRogue.NextSteps.dtos.Response;

public interface GroupService {

    Response createGroup(GroupDTO groupDTO);

    Response getAllGroups();

    Response getGroupById(Long id);

    Response updateGroup(Long id, GroupDTO groupDTO);

    Response deleteGroup(Long id);
}
