<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Note" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Notes de ${etudiant.prenom} ${etudiant.nom} - Gradibou</title>
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
            <h1>Notes : ${etudiant.nom} ${etudiant.prenom}</h1>
            <a href="<%= request.getContextPath() %>/app/professeur/eleves" class="btn">Retour aux élèves</a>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Matière</th>
                        <th>Examen</th>
                        <th>Note</th>
                        <th>Score</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Note> notes = (List<Note>) request.getAttribute("notes");
                    if (notes != null && !notes.isEmpty()) {
                        for (Note n : notes) {
                    %>
                    <tr>
                        <td><%= n.getNomMatiere() %></td>
                        <td>
                            <%= n.getNomExamen() %>
                            <span style="font-size: 0.8em; color: #666;">(Coeff. <%= n.getCoefficientExamen() %>)</span>
                        </td>
                        <td>
                            <% if (n.getValeur() < 10) { %>
                                <span style="color: #e74c3c; font-weight: bold;"><%= n.getValeur() %></span>
                            <% } else if (n.getValeur() >= 14) { %>
                                <span style="color: #27ae60; font-weight: bold;"><%= n.getValeur() %></span>
                            <% } else { %>
                                <%= n.getValeur() %>
                            <% } %>
                             / 20
                        </td>
                        <td>
                            <%-- Affichage optionnel du score pondéré ou autre info --%>
                            <%= n.getValeur() * n.getCoefficientExamen() %> points
                        </td>
                        <td><%= n.getDate() %></td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="5" style="text-align:center;">Aucune note trouvée pour cet étudiant.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
