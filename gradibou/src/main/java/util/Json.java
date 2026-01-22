package util;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

public class Json {
    
    public static void envoyerJsonSuccess(HttpServletResponse response, String message, String lien) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(String.format(
            "{\"success\": true, \"message\": \"%s\", \"lien\": \"%s\"}", 
            message, lien
        ));
    }

    public static void envoyerJsonError(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write(String.format(
            "{\"success\": false, \"message\": \"%s\"}", 
            message
        ));
    }
}