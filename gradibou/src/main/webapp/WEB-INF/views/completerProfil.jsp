<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Finalisez votre inscription - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/complete-profil.css">
</head>
<body>
<div class="page-container complete-profil-page">
    <%@ include file="includes/header.jsp" %>
    
    <main class="complete-profil-main">
        <div class="complete-profil-logo">
            <img src="<%= request.getContextPath() %>/static/images/logo/logo-gradibou.svg" alt="Gradibou">
        </div>

        <section class="complete-profil-card" aria-labelledby="complete-profil-title">
            <h1 id="complete-profil-title">Finalisez votre inscription !</h1>
            
            <% if (request.getAttribute("error") != null) { %>
                <p class="status-message error"><%= request.getAttribute("error") %></p>
            <% } %>

            <form class="form-fields" method="post" action="<%= request.getContextPath() %>/app/complete-profil">
                <input type="hidden" name="token" value="<%= request.getParameter("token") %>">
                
                <div class="field-group">
                    <label class="field-label" for="motDePasse">Choisissez votre GradiMotdepasse</label>
                    <input class="input-field" id="motDePasse" type="password" name="motDePasse" placeholder="Votre GradiMotdepasse" required minlength="6">
                    <p class="form-feedback" aria-live="polite">&nbsp;</p>
                </div>
                
                <div class="field-group">
                    <label class="field-label" for="confirmPassword">Confirmez votre GradiMotdepasse</label>
                    <input class="input-field" id="confirmPassword" type="password" name="confirmPassword" placeholder="Votre GradiMotdepasse" required minlength="6">
                    <p class="form-feedback" aria-live="polite">&nbsp;</p>
                </div>
                
                <button class="btn btn-primary btn-with-icon btn-full-width" type="submit">
                    Accéder à la plateforme !
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M7 10H13M13 10L10 7M13 10L10 13" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
            </form>
        </section>
    </main>
</div>
</body>
</html>
