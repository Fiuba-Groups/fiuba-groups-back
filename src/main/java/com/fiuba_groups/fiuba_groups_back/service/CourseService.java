package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Course;
import com.fiuba_groups.fiuba_groups_back.repository.CourseRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired CourseRepository courseRepository;

    public Course addCourse(CourseCreateRequest request) {
        try {
            Course newCourse = new Course();
            newCourse.setCommission(request.getCommission());
            newCourse.setActive(request.getActive());
            newCourse.setSubjectCode(request.getSubjectCode());
            return courseRepository.save(newCourse);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(e.getMostSpecificCause().getMessage());
        }
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}