package com.fiuba_groups.fiuba_groups_back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.model.Subject;
import com.fiuba_groups.fiuba_groups_back.model.Course;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;
import com.fiuba_groups.fiuba_groups_back.repository.SubjectRepository;
import com.fiuba_groups.fiuba_groups_back.repository.CourseRepository;
import com.fiuba_groups.fiuba_groups_back.repository.CourseOfferingRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private SubjectRepository subjectRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private CourseOfferingRepository courseOfferingRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        // Crear usuario admin
        try {
            userRepo.save(new User("admin@fi.uba.ar", encoder.encode("1234")));
        } catch (Exception e) {
            // Usuario ya existe
        }

        // Crear materias, cursos y course offerings para el frontend
        if (courseOfferingRepo.count() == 0) {
            initializeCourseData();
        }
    }

    private void initializeCourseData() {
        // Definición de materias con sus cátedras
        String[][] subjectsData = {
            // {code, name, department, cátedras separadas por coma}
            {"AM2", "Análisis Matemático II", "Matemática", "García,Pérez"},
            {"AL", "Álgebra Lineal", "Matemática", "Rodríguez,Gómez"},
            {"F1", "Física I", "Física", "López,Martínez"},
            {"QO", "Química Orgánica", "Química", "Fernández,Silva"},
            {"P1", "Programación I", "Computación", "Silva,López"},
            {"EP", "Estadística y Probabilidades", "Matemática", "Mendoza,Vega"},
            {"CN", "Cálculo Numérico", "Matemática", "Ramírez"},
            {"EC", "Economía Política", "Gestión", "Gutiérrez,Moreno"},
        };

        for (String[] subjectData : subjectsData) {
            String code = subjectData[0];
            String name = subjectData[1];
            String department = subjectData[2];
            String[] cathedras = subjectData[3].split(",");

            // Crear materia si no existe
            if (!subjectRepo.existsById(code)) {
                Subject subject = new Subject();
                subject.setCode(code);
                subject.setName(name);
                subject.setDepartment(department);
                subjectRepo.save(subject);
            }

            // Crear cursos y course offerings para cada cátedra
            for (String cathedra : cathedras) {
                // Crear curso (cátedra)
                Course course = new Course();
                course.setCommission(cathedra.trim());
                course.setActive(true);
                course.setSubjectCode(code);
                Course savedCourse = courseRepo.save(course);

                // Crear course offering
                CourseOffering offering = new CourseOffering();
                offering.setQuarter("2C");
                offering.setYear("2025");
                offering.setCourseId(savedCourse.getId());
                courseOfferingRepo.save(offering);
            }
        }

        System.out.println("Datos de materias y cursos inicializados correctamente.");
    }
}
