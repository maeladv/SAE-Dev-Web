package controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DatabaseManager;

@WebServlet("/app/*")
public class Controller extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        try {
            DatabaseManager.init();
            // DatabaseManager.createTables();
            System.out.println("Base de données initialisée");
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Erreur d'initialisation BD", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String path = request.getPathInfo();
        
        if (path == null || path.isEmpty()) {
            path = "/";
        }

        String view = null;
        
        switch (path) {
            case "/":
                view = "/WEB-INF/views/index.jsp";
                break;
            case "/test":
                view = "/WEB-INF/views/test.jsp";
                break;
                
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }
        
        if (view != null) {
            request.getRequestDispatcher(view).forward(request, response);
        }
    }
}