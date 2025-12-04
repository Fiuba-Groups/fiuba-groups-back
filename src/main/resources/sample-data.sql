-- =====================================================
-- FIUBA Groups - Sample Data
-- =====================================================
-- This file contains sample data for testing the FIUBA Groups application
-- Execute this after the application has created the tables via Hibernate

-- =====================================================
-- 1. SUBJECTS (Materias)
-- =====================================================
INSERT INTO subject (code, name, department) VALUES
('75.01', 'Algoritmos y Programación I', 'Computación'),
('75.02', 'Algoritmos y Programación II', 'Computación'),
('75.03', 'Algoritmos y Programación III', 'Computación'),
('75.40', 'Algoritmos y Programación Avanzada', 'Computación'),
('75.41', 'Taller de Programación I', 'Computación'),
('75.42', 'Taller de Programación II', 'Computación'),
('75.07', 'Algoritmos y Estructuras de Datos', 'Computación'),
('75.08', 'Organización de Datos', 'Computación'),
('75.09', 'Análisis Numérico', 'Computación'),
('93.59', 'Gestión de Datos', 'Computación'),
('61.03', 'Análisis Matemático II', 'Matemática'),
('61.08', 'Análisis Numérico I', 'Matemática'),
('62.01', 'Física I', 'Física'),
('62.03', 'Física II', 'Física'),
('66.09', 'Probabilidad y Estadística A', 'Matemática'),
('71.14', 'Modelos y Optimización I', 'Industrial'),
('86.01', 'Economía', 'Economía'),
('95.12', 'Legislación y Ejercicio Profesional', 'Humanidades');

-- =====================================================
-- 2. COURSES (Cursos/Comisiones)
-- =====================================================
INSERT INTO course (id, commission, active, subject_code) VALUES
-- Algoritmos y Programación I (75.01) - 3 courses
(1, 'Curso 1 - Méndez', true, '75.01'),
(2, 'Curso 2 - Buchwald', true, '75.01'),
(3, 'Curso 3 - Wachenchauzer', true, '75.01'),

-- Algoritmos y Programación II (75.02) - 2 courses
(4, 'Curso 1 - Essaya', true, '75.02'),
(5, 'Curso 2 - Buchwald', true, '75.02'),

-- Algoritmos y Programación III (75.03) - 2 courses
(6, 'Curso 1 - Martínez López', true, '75.03'),
(7, 'Curso 2 - Omeliansky', true, '75.03'),

-- Gestión de Datos (93.59) - 2 courses
(8, 'Curso 1 - Servetto', true, '93.59'),
(9, 'Curso 2 - Kogan', true, '93.59'),

-- Análisis Matemático II (61.03) - 2 courses
(10, 'Curso 1 - Gálvez', true, '61.03'),
(11, 'Curso 2 - Cáliz', true, '61.03'),

-- Física II (62.03) - 2 courses
(12, 'Curso 1 - Torres', true, '62.03'),
(13, 'Curso 2 - Ranea Sandoval', true, '62.03'),

-- Probabilidad y Estadística (66.09)
(14, 'Curso 1 - García', true, '66.09'),

-- Taller de Programación I (75.41)
(15, 'Curso 1 - Veiga', true, '75.41');

-- =====================================================
-- 3. COURSE OFFERINGS (Cursadas por cuatrimestre)
-- =====================================================
INSERT INTO course_offering (id, quarter, year, course_id) VALUES
-- 2024 - 2C (Current semester)
(1, '2C', '2024', 1),  -- Algo I - Méndez
(2, '2C', '2024', 2),  -- Algo I - Buchwald
(3, '2C', '2024', 4),  -- Algo II - Essaya
(4, '2C', '2024', 5),  -- Algo II - Buchwald
(5, '2C', '2024', 6),  -- Algo III - Martínez López
(6, '2C', '2024', 8),  -- Gestión de Datos - Servetto
(7, '2C', '2024', 10), -- Análisis Matemático II - Gálvez
(8, '2C', '2024', 12), -- Física II - Torres
(9, '2C', '2024', 14), -- Probabilidad - García
(10, '2C', '2024', 15), -- Taller I - Veiga

-- 2025 - 1C (Next semester)
(11, '1C', '2025', 1),  -- Algo I - Méndez
(12, '1C', '2025', 3),  -- Algo I - Wachenchauzer
(13, '1C', '2025', 4),  -- Algo II - Essaya
(14, '1C', '2025', 7),  -- Algo III - Omeliansky
(15, '1C', '2025', 9),  -- Gestión de Datos - Kogan
(16, '1C', '2025', 11), -- Análisis Matemático II - Cáliz
(17, '1C', '2025', 13); -- Física II - Ranea Sandoval

-- =====================================================
-- 4. STUDENTS (Alumnos)
-- =====================================================
INSERT INTO student (id, register, name) VALUES
(1, 108234, 'Juan Pérez García'),
(2, 108567, 'María González López'),
(3, 109123, 'Carlos Rodríguez Martínez'),
(4, 109456, 'Ana Silva Torres'),
(5, 109789, 'Diego Fernández Ruiz'),
(6, 110012, 'Laura Martínez Sánchez'),
(7, 110345, 'Pablo Gómez Díaz'),
(8, 110678, 'Sofía Romero Castro'),
(9, 110901, 'Martín López Herrera'),
(10, 111234, 'Valentina Díaz Moreno'),
(11, 111567, 'Lucas Ruiz Jiménez'),
(12, 111890, 'Camila Castro Vargas'),
(13, 112123, 'Tomás Herrera Medina'),
(14, 112456, 'Lucía Moreno Rojas'),
(15, 112789, 'Santiago Jiménez Ortiz'),
(16, 113012, 'Catalina Vargas Suárez'),
(17, 113345, 'Mateo Medina Gil'),
(18, 113678, 'Florencia Rojas Luna'),
(19, 113901, 'Nicolás Ortiz Ramos'),
(20, 114234, 'Isabella Suárez Vega');

-- =====================================================
-- 5. GROUPS (Grupos de estudio)
-- =====================================================
-- NOTE: creator_student_register now uses student ID (not register/padrón number)
INSERT INTO groups (id, title, description, member_count, max_members, creator_student_register, course_offering_id) VALUES
-- Groups for Algo I - Méndez (2C 2024)
(1, 'Grupo TP1 - Listas Enlazadas', 'Buscamos gente para resolver el TP1. Tenemos experiencia en Python.', 3, 4, 1, 1),
(2, 'Equipo Recursividad', 'Grupo para practicar ejercicios de recursividad y preparar el parcial.', 2, 5, 2, 1),
(3, 'Los Algorítmicos', 'Grupo de estudio para TPs y parciales. Nos juntamos los martes y jueves.', 4, 5, 3, 1),

-- Groups for Algo II - Essaya (2C 2024)
(4, 'Aventureros de C', 'Primer grupo de C! Buscamos compañeros para el TP de ABB.', 2, 4, 4, 3),
(5, 'Punteros y Estructuras', 'Grupo avanzado para resolver TPs complejos. Nivel intermedio.', 3, 4, 5, 3),

-- Groups for Algo III - Martínez López (2C 2024)
(6, 'Java Masters', 'Grupo para TPs de Java. Conocimientos de OOP requeridos.', 2, 3, 6, 5),
(7, 'Diseño y Testing', 'Enfocados en buenas prácticas, TDD y diseño de software.', 3, 4, 7, 5),

-- Groups for Gestión de Datos - Servetto (2C 2024)
(8, 'SQL Warriors', 'Grupo para TPs de bases de datos. Buscamos 1 persona más.', 3, 4, 8, 6),
(9, 'Data Wizards', 'Especialistas en normalización y diseño de BDs.', 2, 4, 9, 6),

-- Groups for Análisis Matemático II (2C 2024)
(10, 'Integrales Dobles', 'Estudiamos juntos para los parciales. Nivel: principiante.', 4, 6, 10, 7),
(11, 'Matemáticos Pro', 'Grupo avanzado. Resolvemos la guía completa semanalmente.', 3, 5, 11, 7),

-- Groups for Física II (2C 2024)
(12, 'Campo Eléctrico Team', 'Grupo para resolver problemas de electromagnetismo.', 2, 4, 12, 8),

-- Groups for Taller I (2C 2024)
(13, 'Proyecto Conway', 'Implementando el Juego de la Vida. Buscamos 1 más!', 3, 4, 13, 10),

-- Groups for next semester (1C 2025)
(14, 'Algoritmos desde Cero', 'Grupo para arrancar bien Algo I. Principiantes bienvenidos!', 1, 5, 20, 11),
(15, 'Python Gang 2025', 'Nuevo grupo para dominar Python desde el inicio.', 2, 4, 19, 11);

-- =====================================================
-- 6. GROUP MEMBERS (Miembros de grupos)
-- =====================================================
INSERT INTO group_members (group_id, student_id) VALUES
-- Group 1: Grupo TP1 - Listas Enlazadas (3 members)
(1, 1),  -- Juan Pérez (creator)
(1, 2),  -- María González
(1, 3),  -- Carlos Rodríguez

-- Group 2: Equipo Recursividad (2 members)
(2, 2),  -- María González (creator)
(2, 4),  -- Ana Silva

-- Group 3: Los Algorítmicos (4 members)
(3, 3),  -- Carlos Rodríguez (creator)
(3, 5),  -- Diego Fernández
(3, 6),  -- Laura Martínez
(3, 7),  -- Pablo Gómez

-- Group 4: Aventureros de C (2 members)
(4, 4),  -- Ana Silva (creator)
(4, 8),  -- Sofía Romero

-- Group 5: Punteros y Estructuras (3 members)
(5, 5),  -- Diego Fernández (creator)
(5, 9),  -- Martín López
(5, 10), -- Valentina Díaz

-- Group 6: Java Masters (2 members)
(6, 6),  -- Laura Martínez (creator)
(6, 11), -- Lucas Ruiz

-- Group 7: Diseño y Testing (3 members)
(7, 7),  -- Pablo Gómez (creator)
(7, 12), -- Camila Castro
(7, 13), -- Tomás Herrera

-- Group 8: SQL Warriors (3 members)
(8, 8),  -- Sofía Romero (creator)
(8, 14), -- Lucía Moreno
(8, 15), -- Santiago Jiménez

-- Group 9: Data Wizards (2 members)
(9, 9),  -- Martín López (creator)
(9, 16), -- Catalina Vargas

-- Group 10: Integrales Dobles (4 members)
(10, 10), -- Valentina Díaz (creator)
(10, 11), -- Lucas Ruiz
(10, 17), -- Mateo Medina
(10, 18), -- Florencia Rojas

-- Group 11: Matemáticos Pro (3 members)
(11, 11), -- Lucas Ruiz (creator)
(11, 12), -- Camila Castro
(11, 19), -- Nicolás Ortiz

-- Group 12: Campo Eléctrico Team (2 members)
(12, 12), -- Camila Castro (creator)
(12, 13), -- Tomás Herrera

-- Group 13: Proyecto Conway (3 members)
(13, 13), -- Tomás Herrera (creator)
(13, 14), -- Lucía Moreno
(13, 15), -- Santiago Jiménez

-- Group 14: Algoritmos desde Cero (1 member - creator only)
(14, 20), -- Isabella Suárez (creator)

-- Group 15: Python Gang 2025 (2 members)
(15, 19), -- Nicolás Ortiz (creator)
(15, 20); -- Isabella Suárez

-- =====================================================
-- 7. GROUP JOIN REQUESTS (Solicitudes pendientes)
-- =====================================================
INSERT INTO group_join_request (id, group_id, student_id) VALUES
-- Requests for Group 1 (has 3/4 members)
(1, 1, 16), -- Catalina wants to join "Grupo TP1"
(2, 1, 17), -- Mateo also wants to join

-- Requests for Group 2 (has 2/5 members)
(3, 2, 5),  -- Diego wants to join "Equipo Recursividad"
(4, 2, 18), -- Florencia wants to join

-- Requests for Group 4 (has 2/4 members)
(5, 4, 6),  -- Laura wants to join "Aventureros de C"

-- Requests for Group 6 (has 2/3 members)
(6, 6, 7),  -- Pablo wants to join "Java Masters"

-- Requests for Group 8 (has 3/4 members)
(7, 8, 1),  -- Juan wants to join "SQL Warriors"

-- Requests for Group 14 (has 1/5 members - needs more people!)
(8, 14, 1), -- Juan wants to join
(9, 14, 2), -- María wants to join
(10, 14, 3); -- Carlos wants to join

-- =====================================================
-- SEQUENCES UPDATE (PostgreSQL)
-- =====================================================
-- Update sequences to avoid conflicts with manually inserted IDs
SELECT setval('course_id_seq', (SELECT MAX(id) FROM course));
SELECT setval('course_offering_id_seq', (SELECT MAX(id) FROM course_offering));
SELECT setval('student_id_seq', (SELECT MAX(id) FROM student));
SELECT setval('groups_id_seq', (SELECT MAX(id) FROM groups));
SELECT setval('group_join_request_id_seq', (SELECT MAX(id) FROM group_join_request));

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
-- Uncomment these to verify the data was inserted correctly:

-- SELECT COUNT(*) as total_subjects FROM subject;
-- SELECT COUNT(*) as total_courses FROM course;
-- SELECT COUNT(*) as total_course_offerings FROM course_offering;
-- SELECT COUNT(*) as total_students FROM student;
-- SELECT COUNT(*) as total_groups FROM groups;
-- SELECT COUNT(*) as total_group_members FROM group_members;
-- SELECT COUNT(*) as total_join_requests FROM group_join_request;

-- Show groups with available slots:
-- SELECT g.id, g.title, g.member_count, g.max_members,
--        (g.max_members - g.member_count) as available_slots
-- FROM groups g
-- WHERE g.member_count < g.max_members
-- ORDER BY available_slots DESC;
