package org.example.studentmanagement.persistance.impl;

import org.example.studentmanagement.dto.StudentDTO;

import java.sql.Connection;
import java.sql.SQLException;

public interface StudentDAO {
    StudentDTO getStudent(String studentId, Connection connection) throws SQLException;
    String saveStudent(StudentDTO studentDTO,Connection connection);
    boolean deleteStudent(String studentId,Connection connection);
    boolean updateStudent(String studentId,StudentDTO student,Connection connection);
}
