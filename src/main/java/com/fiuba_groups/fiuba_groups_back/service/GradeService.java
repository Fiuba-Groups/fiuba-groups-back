package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Grade;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.GradeRepository;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GradeRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public Grade addGradeForCurrentUser(User user, GradeRequest request) {
        if (user.getStudent() == null) {
            throw new ResourceNotFoundException("User does not have a student profile");
        }

        Student student = user.getStudent();

        Grade grade = new Grade();
        grade.setName(request.getName());
        grade.setGrade(request.getGrade());
        grade.setSemester(request.getSemester());
        grade.setCredits(request.getCredits());
        grade.setStudent(student);

        return gradeRepository.save(grade);
    }

    public List<Grade> getGradesForStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    @Transactional
    public void deleteGrade(User user, Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade with id " + gradeId + " not found"));

        if (user.getStudent() == null) {
            throw new BadRequestException("User does not have a student profile");
        }

        if (!grade.getStudent().getId().equals(user.getStudent().getId())) {
            throw new BadRequestException("You can only delete your own grades");
        }

        gradeRepository.delete(grade);
    }
}
