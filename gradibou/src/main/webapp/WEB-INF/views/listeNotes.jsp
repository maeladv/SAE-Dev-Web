<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Note" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Notes</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f8f9fa; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 20px; margin-bottom: 20px; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; text-decoration: none; }
        .button { display: inline-block; padding: 10px 14px; background: #0d6efd; color: #fff; text-decoration: none; border-radius: 4px; margin-bottom: 20px; }
        .button:hover { background: #0b5ed7; color: #fff; text-decoration: none; }
    </style>
</head>
<body>
    <% Examen examen = (Examen) request.getAttribute("examen"); %>
    <h2>Notes de l'examen : <%= examen != null ? examen.getNom() : "" %></h2>
    
    <a href="<%= request.getContextPath() %>/app/admin/creer-note?idExamen=<%= examen != null ? examen.getId() : "" %>" class="button">Ajouter une note</a>

    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>

    <!-- Liste des notes -->
    <div class="card" style="padding: 0; overflow: hidden;">
        <table>
            <thead>
                <tr>
                    <th>Étudiant</th>
                    <th>Note (/20)</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
            <% 
                List<Note> notes = (List<Note>) request.getAttribute("notes");
                if (notes != null && !notes.isEmpty()) {
                    for (Note n : notes) {
                        Utilisateur etudiant = Utilisateur.trouverParId(n.getIdEtudiant());
            %>
                <tr>
                    <td><%= etudiant != null ? etudiant.getNom() + " " + etudiant.getPrenom() : "Étudiant #" + n.getIdEtudiant() %></td>
                    <td><strong><%= n.getValeur() %></strong></td>
                    <td><%= n.getDate() %></td>
                </tr>
            <%      }
                } else {
            %>
                <tr><td colspan="3" style="text-align: center; color: #777;">Aucune note trouvée pour cet examen.</td></tr>
            <%  } %>
            </tbody>
        </table>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/admin/examens?matId=<%= examen != null ? examen.getId_matiere() : "" %>" class="back-link">
        &larr; Retour aux examens
    </a>
</body>
</html>