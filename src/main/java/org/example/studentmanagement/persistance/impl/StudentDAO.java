package org.example.studentmanagement.persistance.impl;

import org.example.studentmanagement.dto.StudentDTO;

import java.sql.Connection;
import java.sql.SQLException;

public sealed interface StudentDAO permits StudentDAOImpl {
    StudentDTO getStudent(String studentId, Connection connection) throws SQLException;
    boolean saveStudent(StudentDTO studentDTO,Connection connection);
    boolean deleteStudent(String studentId,Connection connection);
    boolean updateStudent(String studentId,StudentDTO student,Connection connection);
}
