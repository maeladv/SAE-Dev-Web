<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Utilisateur" %>
<%
    // Déterminer le statut de l'utilisateur et son rôle
    Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
    String userRole = "non-connecte";
    String displayRole = "Non connecté";
    
    if (currentUser != null && currentUser.getRole() != null) {
        String role = currentUser.getRole().toLowerCase();
        switch(role) {
            case "etudiant":
                userRole = "etudiant";
                displayRole = "Étudiant";
                break;
            case "professeur":
                userRole = "professeur";
                displayRole = "Professeur";
                break;
            case "admin":
                userRole = "admin";
                displayRole = "Administrateur";
                break;
        }
    }
%>
<header class="site-header" data-role="<%= userRole %>">
    <a class="brand" href="<%= request.getContextPath() %>/">
        <img src="https://www.figma.com/api/mcp/asset/254d3b6e-e5ea-46c1-ae6d-55e9947e4239" alt="Logo Gradibou">
    </a>
    
    <div class="nav-actions">
        <% if ("non-connecte".equals(userRole)) { %>
            <!-- Header pour utilisateurs non connectés -->
            <nav class="nav-links" aria-label="Navigation principale">
                <a href="#">Emploi du temps</a>
                <a href="#">Moodle</a>
            </nav>
            <div class="header-cta">
                <a class="btn btn-primary btn-with-icon" href="<%= request.getContextPath() %>/app/login">
                    Connexion →
                </a>
            </div>
        <% } else { %>
            <!-- Header pour utilisateurs connectés -->
            <nav class="nav-links" aria-label="Navigation principale">
                <% if ("etudiant".equals(userRole)) { %>
                    <a href="#">Evaluation des enseignements</a>
                    <a href="#">Mes notes</a>
                    <a href="#">Mon compte</a>
                <% } else if ("professeur".equals(userRole)) { %>
                    <a href="#">Evaluations de mes enseignements</a>
                    <a href="#">Mes matières</a>
                    <a href="#">Mon compte</a>
                <% } else if ("admin".equals(userRole)) { %>
                    <a href="#">Évaluation des enseignements</a>
                    <a href="#">Spécialités</a>
                    <a href="<%= request.getContextPath() %>/app/admin">Comptes</a>
                    <a href="#">Mon compte</a>
                <% } %>
            </nav>
            <div class="header-cta">
                <a class="btn btn-primary btn-with-icon" href="<%= request.getContextPath() %>/app/logout">
                    Déconnexion →
                </a>
            </div>
        <% } %>
    </div>
</header>
