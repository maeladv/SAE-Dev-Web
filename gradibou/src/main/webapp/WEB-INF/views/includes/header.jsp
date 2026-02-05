<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Utilisateur" %>
<%
    // Déterminer le statut de l'utilisateur et son rôle
    Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");
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
    <a class="brand" href="<%= request.getContextPath() %>/app/">
        <img src="/gradibou/static/images/logo/logo-gradibou.svg" alt="Logo Gradibou">
    </a>
    
    <div class="nav-actions">
        <% if ("non-connecte".equals(userRole)) { %>
            <!-- Header pour utilisateurs non connectés -->
            <nav class="nav-links" aria-label="Navigation principale">
                <a href="https://vtmob.uphf.fr">Emploi du temps</a>
                <a href="https://moodle.uphf.fr">Moodle</a>
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
                    <a href="<%= request.getContextPath() %>/app/etudiant/evaluations">Evaluation des enseignements</a>

                    <a href="<%= request.getContextPath() %>/app/etudiant">Mes notes</a>
                    <a href="<%= request.getContextPath() %>/app/moncompte">Mon compte</a>
                <% } else if ("professeur".equals(userRole)) { %>
                    <a href="#">Evaluations de mes enseignements</a>
                    <a href="<%= request.getContextPath() %>/app/gestion/specialites">Mes matières</a>
                    <a href="<%= request.getContextPath() %>/app/moncompte">Mon compte</a>
                <% } else if ("admin".equals(userRole)) { %>
                    <a href="<%= request.getContextPath() %>/app/admin/resultats-evaluations">Évaluation des enseignements</a>
                    <a href="<%= request.getContextPath() %>/app/gestion/specialites">Spécialités</a>
                    <a href="<%= request.getContextPath() %>/app/admin">Comptes</a>
                    <a href="<%= request.getContextPath() %>/app/moncompte">Mon compte</a>
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
<% if (!"non-connecte".equals(userRole)) {
     String guidePdf = "guide_etudiant.pdf";
     if ("professeur".equals(userRole)) {
         guidePdf = "guide_professeur.pdf";
     } else if ("admin".equals(userRole)) {
         guidePdf = "guide_admin.pdf";
     }
%>
<style>
/* Styles injectés directement pour éviter les problèmes de cache navigateur */
.help-button-fixed {
    position: fixed !important;
    bottom: 30px !important;
    right: 30px !important;
    width: 60px !important;
    height: 60px !important;
    background-color: #FA467E !important; /* var(--gradibou-rose) */
    border-radius: 50% !important;
    display: flex !important;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
    z-index: 99999 !important;
    transition: transform 0.2s;
    cursor: pointer;
    text-decoration: none !important;
}

.help-button-fixed:hover {
    transform: scale(1.1);
}

.help-button-fixed svg {
    width: 36px !important;
    height: 36px !important;
    color: white !important;
}
</style>

<a href="<%= request.getContextPath() %>/static/guides/<%= guidePdf %>" target="_blank" class="help-button-fixed" title="Guide Utilisateur" id="gradibouHelpButton">
    <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <circle cx="12" cy="12" r="4"></circle>
        <line x1="4.93" y1="4.93" x2="9.17" y2="9.17"></line>
        <line x1="14.83" y1="14.83" x2="19.07" y2="19.07"></line>
        <line x1="14.83" y1="9.17" x2="19.07" y2="4.93"></line>
        <line x1="14.83" y1="9.17" x2="18.36" y2="5.64"></line>
        <line x1="4.93" y1="19.07" x2="9.17" y2="14.83"></line>
    </svg>
</a>

<script>
    // Déplacer le bouton dans le body pour éviter les conflits de positionnement (transform CSS, etc)
    document.addEventListener("DOMContentLoaded", function() {
        var btn = document.getElementById('gradibouHelpButton');
        if (btn && document.body) {
            document.body.appendChild(btn);
        }
    });
</script>
<% } %>
