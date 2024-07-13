package org.example.studentmanagement.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studentmanagement.dto.StudentDTO;
import org.example.studentmanagement.persistance.impl.StudentDAOImpl;
import org.example.studentmanagement.utill.GenerateId;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.Class.forName;

@WebServlet(urlPatterns = "/student")/*
initParams =
        {
                @WebInitParam(name = "driver-class",value = "com.mysql.cj.jdbc.Driver"),
                @WebInitParam(name = "dbURL",value = "jdbc:mysql://localhost:3306/AADstmgt"),
                @WebInitParam(name = "dbUserName",value = "root"),
                @WebInitParam(name = "dbPassword",value = "Ijse@1234")
        })*/

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

            // configuration context(cookie jar)
           /* var driverClass = getServletConfig().InitParameter("driver-class");
            var url = getServletConfig().getInitParameter("dbURL");
            var username = getServletConfig().getInitParameter("dbUserName");
            var password = getServletConfig().getInitParameter("dbPassword");*/

            Class.forName(driverClass);
            this.connection = DriverManager.getConnection(url,username,password);
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var studentId = req.getParameter("id");
        var studentDAO = new StudentDAOImpl();
        try{
            var writer = resp.getWriter();
            var student = studentDAO.getStudent(studentId,connection);
            //writer.write(student.toString());
            System.out.println(student);
            resp.setContentType("application/json");
            var jsonb = JsonbBuilder.create();
            jsonb.toJson(student,writer);

        }catch (SQLException e){
            throw new RuntimeException(e);

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var writer = resp.getWriter()) {
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
            studentDTO.setId(GenerateId.generateId());
            var StudentDAO = new StudentDAOImpl();
            writer.write(StudentDAO.saveStudent(studentDTO,connection));
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try(var writer = resp.getWriter()) {
            var id = req.getParameter("id");
            var StudentDAO = new StudentDAOImpl();
            writer.write(String.valueOf(StudentDAO.deleteStudent(id,connection)));

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try(var writer = resp.getWriter()) {
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
            var StudentDAO = new StudentDAOImpl();

            if(StudentDAO.updateStudent(studentDTO.getId(),studentDTO,connection)){
                writer.write("Student Updated");
            }else {
                writer.write("Student Not Updated");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
