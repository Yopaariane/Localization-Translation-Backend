package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner_Id(Long ownerId);
}
