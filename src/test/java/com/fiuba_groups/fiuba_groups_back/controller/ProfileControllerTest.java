package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.service.StudentService;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private StudentService studentService;

    @Test
    @WithMockUser
    public void getUserByStudentId_ShouldReturnUser_WhenStudentExists() throws Exception {
        // Arrange
        Long studentId = 123L;
        Long userId = 456L;
        String email = "test@example.com";
        String name = "Test Student";
        int register = 12345;

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        Student student = new Student();
        student.setId(studentId);
        student.setName(name);
        student.setRegister(register);
        
        user.setStudent(student);

        given(userService.getUserByStudentId(studentId)).willReturn(user);

        // Act & Assert
        mockMvc.perform(get("/users/student/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.student.id").value(studentId))
                .andExpect(jsonPath("$.student.name").value(name))
                .andExpect(jsonPath("$.student.register").value(register));
    }
}
