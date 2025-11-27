package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.StudentUpdateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

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
}
