package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.service.CourseOfferingService;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseOfferingCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("courseOfferings")
public class CourseOfferingController {
    @Autowired CourseOfferingService courseOfferingService;

    @GetMapping
    public ResponseEntity<List<CourseOffering>> getAllCourseOfferings() {
        return ResponseEntity.ok(courseOfferingService.getAllCourseOfferings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseOffering(@PathVariable Long id) {
        try {
            CourseOffering courseOffering = courseOfferingService.getCourseOfferingById(id);
            return ResponseEntity.ok(courseOffering);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<CourseOffering> addCourseOffering(
            @RequestBody CourseOfferingCreateRequest request) {
        return ResponseEntity.ok(courseOfferingService.addCourseOffering(request));
    }
}
