package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByNameContainingOrDescriptionContaining(String name, String description);
}
