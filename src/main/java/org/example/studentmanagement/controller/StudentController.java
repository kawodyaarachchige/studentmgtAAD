package org.example.studentmanagement.controller;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studentmanagement.dto.StudentDTO;
import org.example.studentmanagement.utill.GenerateId;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Class.forName;

@WebServlet(urlPatterns = "/student")
public class StudentController  extends HttpServlet {

    Connection connection;
    static String SAVE_STUDENT = "insert into student(id,name,email,city,level) values(?,?,?,?,?)";
    static String GET_STUDENTS = "select * from student where id=?";
    static String UPDATE_STUDENT = "UPDATE student SET name=?, city=?, email=?, level=? WHERE id=?";
    static String DELETE_STUDENT = "DELETE FROM student WHERE id=?";

    @Override
    public void init() throws ServletException {
        try {

            var driverClass = getServletContext().getInitParameter("driver-class");
            var url = getServletContext().getInitParameter("dbURL");
            var username = getServletContext().getInitParameter("dbUserName");
            var password = getServletContext().getInitParameter("dbPassword");
            Class.forName(driverClass);
            this.connection = DriverManager.getConnection(url,username,password);
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //Todo:get all students
        var studentDTO = new StudentDTO();
        var StudentId = req.getParameter("id");

        try(var writer = resp.getWriter()){
            PreparedStatement pstm = connection.prepareStatement(GET_STUDENTS);
            pstm.setString(1,StudentId);
            var resultSet = pstm.executeQuery();
            while (resultSet.next()){
                studentDTO.setId(resultSet.getString("id"));
                studentDTO.setName(resultSet.getString("name"));
                studentDTO.setEmail(resultSet.getString("email"));
                studentDTO.setCity(resultSet.getString("city"));
                studentDTO.setLevel(resultSet.getString("level"));
                writer.write(studentDTO.toString());
            }
            System.out.println(studentDTO);
            resp.setContentType("application/json");
            var jsonb = JsonbBuilder.create();
            jsonb.toJson(studentDTO,resp.getWriter());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var writer = resp.getWriter()){
            PreparedStatement pstm = connection.prepareStatement(SAVE_STUDENT);

            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
            pstm.setString(1, GenerateId.generateId());
            pstm.setString(2,studentDTO.getName());
            pstm.setString(3,studentDTO.getEmail());
            pstm.setString(4,studentDTO.getCity());
            pstm.setString(5,studentDTO.getLevel());
           if(pstm.executeUpdate() > 0){
               writer.write("Student Saved");
           }else{
               writer.write("Student Not Saved");
           }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      //  Todo:update student

        if (!req.getContentType().toLowerCase().startsWith("application/json") || req.getContentType() == null) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        Jsonb jsonb = JsonbBuilder.create();
        StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);

        var id = req.getParameter("id");
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        try (var writer = resp.getWriter()) {
            var ps = connection.prepareStatement(UPDATE_STUDENT);
            ps.setString(1, studentDTO.getName());
            ps.setString(2, studentDTO.getCity());
            ps.setString(3, studentDTO.getEmail());
            ps.setString(4, studentDTO.getLevel());
            ps.setString(5, id);
            if (ps.executeUpdate() != 0) {
                writer.write("Student Updated");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                writer.write("Student Not Updated");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

     //   Todo:delete student

        try(var writer = resp.getWriter()) {
            PreparedStatement pstm = connection.prepareStatement(DELETE_STUDENT);
            pstm.setString(1,req.getParameter("id"));
            if (pstm.executeUpdate() != 0) {
                resp.getWriter().write("Student Deleted");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                writer.write("Student Not Deleted");
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
