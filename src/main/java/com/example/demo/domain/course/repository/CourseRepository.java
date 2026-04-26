package com.example.demo.domain.course.repository;

import com.example.demo.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

	boolean existsByExternalId(String externalId);
}
