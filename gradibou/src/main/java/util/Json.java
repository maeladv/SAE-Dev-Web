package util;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

public class Json {
    
    public static void envoyerJsonSuccess(HttpServletResponse response, String message, String redirect) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        String escapedMessage = escapeJsonString(message);
        String escapedRedirect = escapeJsonString(redirect);
        
        String json = "{\"success\": true, \"message\": \"" + escapedMessage + "\", \"redirect\": \"" + escapedRedirect + "\"}";
        response.getWriter().print(json);
        response.getWriter().flush();
    }

    public static void envoyerJsonError(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        
        String escapedMessage = escapeJsonString(message);
        String json = "{\"success\": false, \"message\": \"" + escapedMessage + "\"}";
        response.getWriter().print(json);
        response.getWriter().flush();
    }
    
    /**
     * Échappe les caractères spéciaux pour JSON
     */
    private static String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}