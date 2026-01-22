<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Specialite" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Matières</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        ul { list-style-type: none; padding: 0; }
        li { padding: 10px; border-bottom: 1px solid #ddd; display: flex; justify-content: space-between; align-items: center; }
        li:last-child { border-bottom: none; }
        a { text-decoration: none; color: #0d6efd; }
        a:hover { text-decoration: underline; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 20px; margin-bottom: 20px; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; }
        input[type="text"] { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        button { padding: 8px 12px; background-color: #0d6efd; color: white; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #0b5ed7; }
    </style>
</head>
<body>
    <% Specialite spec = (Specialite) request.getAttribute("specialite"); %>
    <h2>Matières de la spécialité : <%= spec != null ? spec.getNom() : "" %></h2>
    
    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>
    <% String success = (String) request.getAttribute("success");
       if (success != null) { %>
        <p style="color:green;"><%= success %></p>
    <% } %>

    <!-- Liste des matières -->
    <div class="card" style="padding: 0 10px;">
        <ul>
        <% 
            List<Matiere> matieres = (List<Matiere>) request.getAttribute("matieres");
            if (matieres != null && !matieres.isEmpty()) {
                for (Matiere m : matieres) {
        %>
            <li>
                <span><%= m.getNom() %></span>
                <a href="<%= request.getContextPath() %>/app/admin/examens?matId=<%= m.getId() %>">Voir les examens &rarr;</a>
            </li>
        <%      }
            } else {
        %>
            <li style="text-align: center; color: #777; padding: 20px;">Aucune matière trouvée.</li>
        <%  } %>
        </ul>
    </div>

    <!-- Formulaire d'ajout de matière -->
    <div class="card">
        <h3>Ajouter une matière</h3>
        <form action="<%= request.getContextPath() %>/app/admin/creer-matiere" method="post" style="display: flex; gap: 10px; align-items: center;">
            <input type="hidden" name="specialiteId" value="<%= spec != null ? spec.getId() : "" %>">
            <label for="nom">Nom :</label>
            <input type="text" id="nom" name="nom" required placeholder="Ex: Mathématiques">
            <button type="submit">Ajouter</button>
        </form>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/admin/specialites" class="back-link">&larr; Retour aux spécialités</a>
</body>
</html>