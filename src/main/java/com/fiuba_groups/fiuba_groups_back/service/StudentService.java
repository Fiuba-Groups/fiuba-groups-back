package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;
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
    public Student updateShowcasedGroups(Long studentId, List<Long> groupIds) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Group> groups = groupRepository.findAllById(groupIds);
        
        // Verify that the student is actually a member of these groups
        // This is a security check to prevent users from showcasing groups they are not part of
        for (Group group : groups) {
            boolean isMember = group.getMembers().stream()
                    .anyMatch(m -> m.getId().equals(studentId));
            if (!isMember) {
                throw new RuntimeException("Student is not a member of group: " + group.getId());
            }
        }

        student.setShowcasedGroups(groups);
        return studentRepository.save(student);
    }
}
