package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.repository.CourseOfferingRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseOfferingCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseOfferingService {
    @Autowired CourseOfferingRepository courseOfferingRepository;

    public CourseOffering addCourseOffering(CourseOfferingCreateRequest request) {
        if (courseOfferingRepository.existsByQuarterAndSubjectAndCourse(
                request.getQuarter(), request.getSubject(), request.getCourse())) {
            throw new BadRequestException("CourseOffering already exists");
        }

        CourseOffering newCourseOffering = new CourseOffering();
        newCourseOffering.setQuarter(request.getQuarter());
        newCourseOffering.setYear(request.getYear());
        newCourseOffering.setSubject(request.getSubject());
        newCourseOffering.setCourse(request.getCourse());
        return courseOfferingRepository.save(newCourseOffering);
    }
}
