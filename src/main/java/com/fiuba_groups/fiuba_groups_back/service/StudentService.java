package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;
import com.fiuba_groups.fiuba_groups_back.model.ShowcasedGroup;
import com.fiuba_groups.fiuba_groups_back.service.dto.ShowcasedGroupRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.StudentUpdateRequest;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public Student upsertStudentForUser(User user, StudentUpdateRequest request) {
        Student student;

        if (user.getStudent() != null) {
            // Update existing student
            student = user.getStudent();
            student.setRegister(request.getRegister());
            student.setName(request.getName());
        } else {
            // Create new student
            student = new Student();
            student.setRegister(request.getRegister());
            student.setName(request.getName());

            // Link student to user
            user.setStudent(student);
        }

        // Save user (cascade will save student)
        userRepository.save(user);

        return student;
    }

    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateShowcasedGroups(Long studentId, List<ShowcasedGroupRequest> requests) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Clear existing (orphanRemoval will delete them)
        student.getShowcasedGroups().clear();

        for (ShowcasedGroupRequest req : requests) {
            Group group = groupRepository.findById(req.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found: " + req.getGroupId()));

            // Verify membership
            boolean isMember = group.getMembers().stream()
                    .anyMatch(m -> m.getId().equals(studentId));
            if (!isMember) {
                throw new RuntimeException("Student is not a member of group: " + group.getId());
            }

            ShowcasedGroup showcasedGroup = new ShowcasedGroup();
            showcasedGroup.setStudent(student);
            showcasedGroup.setGroup(group);
            showcasedGroup.setDescription(req.getDescription());
            
            student.getShowcasedGroups().add(showcasedGroup);
        }

        return studentRepository.save(student);
    }
}
