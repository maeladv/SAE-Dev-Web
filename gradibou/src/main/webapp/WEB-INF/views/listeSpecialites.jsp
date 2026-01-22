<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Specialite" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Spécialités</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        ul { list-style-type: none; padding: 0; }
        li { padding: 10px; border-bottom: 1px solid #ddd; display: flex; justify-content: space-between; align-items: center; }
        li:last-child { border-bottom: none; }
        a { text-decoration: none; color: #0d6efd; }
        a:hover { text-decoration: underline; }
        .button { display: inline-block; padding: 10px 14px; background: #0d6efd; color: #fff; text-decoration: none; border-radius: 4px; margin-bottom: 20px; }
        .button:hover { background: #0b5ed7; color: #fff; text-decoration: none; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 0 10px; }
    </style>
</head>
<body>
    <h2>Liste des Spécialités</h2>
    
    <a href="<%= request.getContextPath() %>/app/admin/creer-specialite" class="button">Créer une spécialité</a>

    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>

    <div class="card">
    <ul>
    <% 
        List<Specialite> specialites = (List<Specialite>) request.getAttribute("specialites");
        if (specialites != null && !specialites.isEmpty()) {
            for (Specialite s : specialites) {
    %>
        <li>
            <span><%= s.getNom() %> (<%= s.getTag() %> - <%= s.getAnnee() %>)</span>
            <a href="<%= request.getContextPath() %>/app/admin/matieres?specId=<%= s.getId() %>">Voir les matières &rarr;</a>
        </li>
    <%      }
        } else {
    %>
        <li style="text-align: center; color: #777;">Aucune spécialité trouvée.</li>
    <%  } %>
    </ul>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/admin" class="back-link">&larr; Retour au tableau de bord</a>
</body>
</html>