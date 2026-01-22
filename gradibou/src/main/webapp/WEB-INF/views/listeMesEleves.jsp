<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mes Élèves - Gradibou</title>
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
            <h1>Mes Élèves</h1>
            <a href="<%= request.getContextPath() %>/app/professeur" class="btn">Retour</a>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Email</th>
                        <th>Tag Spécialité</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Utilisateur> etudiants = (List<Utilisateur>) request.getAttribute("etudiants");
                    if (etudiants != null && !etudiants.isEmpty()) {
                        for (Utilisateur u : etudiants) {
                    %>
                    <tr>
                        <td>
                            <a href="<%= request.getContextPath() %>/app/professeur/eleve/examens?studentId=<%= u.getId() %>" style="text-decoration: underline; color: inherit;">
                                <%= u.getNom() %>
                            </a>
                        </td>
                        <td><%= u.getPrenom() %></td>
                        <td><%= u.getemail() %></td>
                        <td><%= u.getSpecialiteTag() %></td> 
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="4" style="text-align:center;">Aucun élève trouvé dans vos spécialités.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
