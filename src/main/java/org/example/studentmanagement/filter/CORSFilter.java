package org.example.studentmanagement.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class CORSFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {

        var origin  = req.getHeader("origin");
        var configOrigin = getServletContext().getInitParameter("origin");
        if(origin.contains(configOrigin)){
            resp.setHeader("Access-Control-Allow-Origin",origin);
            resp.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers","Content-Type");
            resp.setHeader("Access-Control-Expose-Headers","Content-Type");
            resp.setHeader("Access-Control-Allow-Credentials","true");
            chain.doFilter(req,resp);
        } else{
          chain.doFilter(req,resp);
      }

    }

}
