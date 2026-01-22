<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Note" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Matiere" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Notes - ${examen.nom} - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/admin.css">
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>
    
    <main class="admin-main">
        <div class="admin-toolbar">
            <h1>Notes : ${examen.nom}</h1>
            <a href="<%= request.getContextPath() %>/app/professeur/matiere/examens?matiereId=${matiere.id}" class="btn">Retour aux examens</a>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Note</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Note> notes = (List<Note>) request.getAttribute("notes");
                    if (notes != null && !notes.isEmpty()) {
                        for (Note n : notes) {
                    %>
                    <tr>
                        <td><%= n.getNomEtudiant() %></td>
                        <td><%= n.getPrenomEtudiant() %></td>
                        <td><%= n.getValeur() %> / 20</td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="3" style="text-align:center;">Aucune note saisie pour cet examen.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
