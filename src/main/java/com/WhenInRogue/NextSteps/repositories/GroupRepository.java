package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(String name);

    List<Group> findByIsActiveTrue();
}
