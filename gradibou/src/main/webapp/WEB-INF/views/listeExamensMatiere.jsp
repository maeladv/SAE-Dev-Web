<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Matiere" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Examens - ${matiere.nom} - Gradibou</title>
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
            <h1>Examens de ${matiere.nom}</h1>
            <a href="<%= request.getContextPath() %>/app/professeur/matieres" class="btn">Retour aux matières</a>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Nom de l'examen</th>
                        <th>Coefficient</th>
                        <th>Moyenne</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Examen> examens = (List<Examen>) request.getAttribute("examens");
                    if (examens != null && !examens.isEmpty()) {
                        for (Examen e : examens) {
                    %>
                    <tr>
                        <td>
                            <a href="<%= request.getContextPath() %>/app/professeur/examen/notes?examenId=<%= e.getId() %>" style="text-decoration: underline; color: inherit;">
                                <%= e.getNom() %>
                            </a>
                        </td>
                        <td><%= e.getCoefficient() %></td>
                        <td>
                            <% 
                                if (e.getMoyenne() >= 0) { 
                            %>
                                <%= String.format("%.2f", e.getMoyenne()) %> / 20
                            <% 
                                } else { 
                            %>
                                -
                            <% 
                                } 
                            %>
                        </td>
                        <td><%= e.getDate() %></td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="3" style="text-align:center;">Aucun examen trouvé pour cette matière.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
