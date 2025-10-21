package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.service.CourseOfferingService;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseOfferingCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("courseOfferings")
public class CourseOfferingController {
    @Autowired CourseOfferingService courseOfferingService;

    @PostMapping
    public ResponseEntity<CourseOffering> addCourseOffering(
            @RequestBody CourseOfferingCreateRequest request) {
        return ResponseEntity.ok(courseOfferingService.addCourseOffering(request));
    }
}
