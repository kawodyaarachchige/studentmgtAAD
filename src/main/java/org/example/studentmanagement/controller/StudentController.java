package org.example.studentmanagement.controller;

import jakarta.json.JsonException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.Class.forName;

@WebServlet(urlPatterns = "/student",loadOnStartup = 2)/*
initParams =
        {
                @WebInitParam(name = "driver-class",value = "com.mysql.cj.jdbc.Driver"),
                @WebInitParam(name = "dbURL",value = "jdbc:mysql://localhost:3306/AADstmgt"),
                @WebInitParam(name = "dbUserName",value = "root"),
                @WebInitParam(name = "dbPassword",value = "Ijse@1234")
        })*/

public class StudentController  extends HttpServlet {
    static Logger logger = LoggerFactory.getLogger(StudentController.class);
    Connection connection;
    static String SAVE_STUDENT = "insert into student(id,name,email,city,level) values(?,?,?,?,?)";
    static String GET_STUDENTS = "select * from student where id=?";
    static String UPDATE_STUDENT = "UPDATE student SET name=?, city=?, email=?, level=? WHERE id=?";
    static String DELETE_STUDENT = "DELETE FROM student WHERE id=?";

    @Override
    public void init() throws ServletException {
        logger.info("initializing student controller with call init method");
        try {

            /*var driverClass = getServletContext().getInitParameter("driver-class");
            var url = getServletContext().getInitParameter("dbURL");
            var username = getServletContext().getInitParameter("dbUserName");
            var password = getServletContext().getInitParameter("dbPassword");*/

            // configuration context(cookie jar)
           /* var driverClass = getServletConfig().InitParameter("driver-class");
            var url = getServletConfig().getInitParameter("dbURL");
            var username = getServletConfig().getInitParameter("dbUserName");
            var password = getServletConfig().getInitParameter("dbPassword");*/

          /*  Class.forName(driverClass);
            this.connection = DriverManager.getConnection(url,username,password);*/

            var ctx = new InitialContext();
            DataSource pool =(DataSource) ctx.lookup("java:/comp/env/jdbc/AADstmgt");
            this.connection = pool.getConnection();

        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var studentId = req.getParameter("id");
        var studentDao = new StudentDAOImpl();
        try (var writer = resp.getWriter()){
            var student = studentDao.getStudent(studentId, connection);
            System.out.println(student);
            resp.setContentType("application/json");
            var jsonb = JsonbBuilder.create();
            jsonb.toJson(student,writer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!req.getContentType().toLowerCase().startsWith("application/json")|| req.getContentType() == null){
            //send error
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        // try-with-resource used to implicitly close the resource
        try (var writer = resp.getWriter()){
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);//read json from request and convert to StudentDTO
            studentDTO.setId(GenerateId.generateId());
            var studentDAO = new StudentDAOImpl();
            if (studentDAO.saveStudent(studentDTO, connection)){
                writer.write("Student saved successfully");
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }else {
                writer.write("Save student failed");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (JsonException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var studentId= req.getParameter("id");
        try (var writer = resp.getWriter()){ // convert to json(MIME type) type to object (deserialization) / serialization mean to convert object to json or MIME types
            var studentDAO = new StudentDAOImpl();
            if(studentDAO.deleteStudent(studentId, connection)){
                writer.write("Student Deleted Successfully");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("Delete Failed");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!req.getContentType().toLowerCase().startsWith("application/json")|| req.getContentType() == null){
            //send error
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);}
        try(var writer = resp.getWriter()) {
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
            var StudentDAO = new StudentDAOImpl();

            if(StudentDAO.updateStudent(studentDTO.getId(),studentDTO,connection)){
                writer.write("Student Updated Successfully");
               resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                writer.write("Student Not Updated");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
