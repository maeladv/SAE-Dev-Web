<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Matiere" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mes Matières - Gradibou</title>
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
            <h1>Mes Matières</h1>
            <a href="<%= request.getContextPath() %>/app/professeur" class="btn">Retour</a>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Semestre</th>
                        <th>Tag Spécialité</th>
                        <th>Moyenne</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Matiere> matieres = (List<Matiere>) request.getAttribute("matieres");
                    if (matieres != null && !matieres.isEmpty()) {
                        for (Matiere m : matieres) {
                    %>
                    <tr>
                        <td>
                            <a href="<%= request.getContextPath() %>/app/professeur/matiere/examens?matiereId=<%= m.getId() %>" style="text-decoration: underline; color: inherit;">
                                <%= m.getNom() %>
                            </a>
                        </td>
                        <td><%= m.getSemestre() %></td>
                        <td><%= m.getSpecialiteTag() != null ? m.getSpecialiteTag() : m.getSpecialiteId() %></td>
                        <td>
                            <% 
                                if (m.getMoyenne() >= 0) { 
                            %>
                                <%= String.format("%.2f", m.getMoyenne()) %> / 20
                            <% 
                                } else { 
                            %>
                                -
                            <% 
                                } 
                            %>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="3" style="text-align:center;">Aucune matière trouvée.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
