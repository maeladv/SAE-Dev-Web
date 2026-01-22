<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Matiere" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Examens</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f8f9fa; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 20px; margin-bottom: 20px; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; text-decoration: none; }
        input[type="number"] { padding: 8px; border: 1px solid #ccc; border-radius: 4px; width: 60px; }
        button { padding: 8px 12px; background-color: #0d6efd; color: white; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #0b5ed7; }
        a.action-link { color: #0d6efd; text-decoration: none; }
        a.action-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <% Matiere mat = (Matiere) request.getAttribute("matiere"); %>
    <h2>Examens de la matière : <%= mat != null ? mat.getNom() : "" %></h2>
    
    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>
    <% String success = (String) request.getAttribute("success");
       if (success != null) { %>
        <p style="color:green;"><%= success %></p>
    <% } %>

    <!-- Liste des examens -->
    <div class="card" style="padding: 0; overflow: hidden;">
        <table>
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Nom</th>
                    <th>Coefficient</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            <% 
                List<Examen> examens = (List<Examen>) request.getAttribute("examens");
                if (examens != null && !examens.isEmpty()) {
                    for (Examen e : examens) {
            %>
                <tr>
                    <td><%= e.getDate() %></td>
                    <td><%= e.getNom() %></td>
                    <td><%= e.getCoefficient() %></td>
                    <td>
                        <a href="<%= request.getContextPath() %>/app/admin/notes?examId=<%= e.getId() %>" class="action-link">
                            Voir les notes &rarr;
                        </a>
                    </td>
                </tr>
            <%      }
                } else {
            %>
                <tr><td colspan="4" style="text-align: center; color: #777;">Aucun examen trouvé.</td></tr>
            <%  } %>
            </tbody>
        </table>
    </div>

    <!-- Formulaire d'ajout d'examen -->
    <div class="card">
        <h3>Créer un examen</h3>
        <form action="<%= request.getContextPath() %>/app/admin/creer-examen" method="post" style="display: flex; gap: 10px; align-items: center;">
            <input type="hidden" name="matiereId" value="<%= mat != null ? mat.getId() : "" %>">
            
            <label for="coefficient">Coefficient:</label>
            <input type="number" id="coefficient" name="coefficient" required step="1" min="1" value="1">
            
            <button type="submit">Créer (Date auto)</button>
        </form>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/admin/matieres?specId=<%= mat != null ? mat.getSpecialiteId() : "" %>" class="back-link">
        &larr; Retour aux matières
    </a>
</body>
</html>