package com.example.demo.domain.course.repository;

import com.example.demo.domain.course.entity.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

	Optional<Course> findByIdAndIsValidTrue(Long id);
}
