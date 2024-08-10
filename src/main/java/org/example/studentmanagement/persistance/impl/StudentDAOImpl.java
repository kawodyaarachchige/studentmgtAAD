package org.example.studentmanagement.persistance.impl;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.example.studentmanagement.dto.StudentDTO;
import org.example.studentmanagement.utill.GenerateId;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class StudentDAOImpl implements StudentDAO {
    static String SAVE_STUDENT = "INSERT INTO student (id,name,city,email,level) VALUES (?,?,?,?,?)";
    static String GET_STUDENT = "SELECT * FROM student WHERE id=?";
    static String UPDATE_STUDENT = "UPDATE student SET name=?,city=?,email=?,level=? WHERE id=?";
    static String DELETE_STUDENT = "DELETE FROM student WHERE id=?";

    @Override
    public StudentDTO getStudent(String studentId, Connection connection) throws SQLException {
        var studentDTO = new StudentDTO();
        try {
            var ps = connection.prepareStatement(GET_STUDENT);
            ps.setString(1, studentId);
            var resultSet = ps.executeQuery();
            while (resultSet.next()) {
                studentDTO.setId(resultSet.getString("id"));
                studentDTO.setName(resultSet.getString("name"));
                studentDTO.setCity(resultSet.getString("city"));
                studentDTO.setEmail(resultSet.getString("email"));
                studentDTO.setLevel(resultSet.getString("level"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return studentDTO;
    }

    @Override
    public boolean saveStudent(StudentDTO studentDTO, Connection connection) {
        try {
            var pstm = connection.prepareStatement(SAVE_STUDENT);
            pstm.setString(1, GenerateId.generateId());
            pstm.setString(2,studentDTO.getName());
            pstm.setString(3,studentDTO.getEmail());
            pstm.setString(4,studentDTO.getCity());
            pstm.setString(5,studentDTO.getLevel());
            if(pstm.executeUpdate() > 0){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            throw new RuntimeException();
        }

    }

    @Override
    public boolean deleteStudent(String studentId, Connection connection) {
        try {
            var ps = connection.prepareStatement(DELETE_STUDENT);
            ps.setString(1, studentId);
            return ps.executeUpdate() != 0;
        }catch (SQLException e){
            throw new RuntimeException();
        }

    }

    @Override
    public boolean updateStudent(String studentId, StudentDTO student, Connection connection) {
        try{
            var pstm = connection.prepareStatement(UPDATE_STUDENT);
            pstm.setString(1,student.getName());
            pstm.setString(2,student.getCity());
            pstm.setString(3,student.getEmail());
            pstm.setString(4,student.getLevel());
            pstm.setString(5,studentId);
            if(pstm.executeUpdate() != 0){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
