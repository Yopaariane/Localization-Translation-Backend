package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
