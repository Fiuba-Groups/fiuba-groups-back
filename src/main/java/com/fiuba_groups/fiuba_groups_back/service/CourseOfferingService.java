package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.repository.CourseOfferingRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseOfferingCreateRequest;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseOfferingService {
    @Autowired CourseOfferingRepository courseOfferingRepository;

    public CourseOffering addCourseOffering(CourseOfferingCreateRequest request) {
        try {
            CourseOffering newCourseOffering = new CourseOffering();
            newCourseOffering.setQuarter(request.getQuarter());
            newCourseOffering.setYear(request.getYear());
            newCourseOffering.setCourseId(request.getCourseId());
            return courseOfferingRepository.save(newCourseOffering);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(e.getMostSpecificCause().getMessage());
        }
    }

    public List<CourseOffering> getAllCourseOfferings() {
        return courseOfferingRepository.findAll();
    }

    public CourseOffering getCourseOfferingById(Long id) {
        return courseOfferingRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course offering with id " + id + " not found"));
    }
}
