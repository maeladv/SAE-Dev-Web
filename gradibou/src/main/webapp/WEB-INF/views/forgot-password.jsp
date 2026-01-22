<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réinitialiser votre mot de passe - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/forgot-password.css">
</head>
<body>
<div class="page-container forgot-password-page">
    <%@ include file="includes/header.jsp" %>

    <main class="forgot-password-main">
        <div class="forgot-password-logo">
            <img src="https://www.figma.com/api/mcp/asset/2b35421e-3f8e-46b3-ac0e-dcfb7845a774" alt="Gradibou">
        </div>

        <section class="forgot-password-card" aria-labelledby="forgot-password-title">
            <h1 id="forgot-password-title">Réinitialisez votre gradiMotdepasse</h1>

            <% if (request.getAttribute("error") != null) { %>
                <p class="status-message error"><%= request.getAttribute("error") %></p>
            <% } %>
            <% if (request.getAttribute("success") != null) { %>
                <p class="status-message success"><%= request.getAttribute("success") %></p>
            <% } %>

            <form class="form-fields" method="post" action="<%= request.getContextPath() %>/app/admin/maj-mdp">
                <div class="field-group">
                    <input class="input-field" type="email" name="email" placeholder="Rentrez votre gradiPseudo ou votre gradiMail" required>
                    <p class="form-feedback" aria-live="polite">&nbsp;</p>
                </div>
                <div class="form-actions">
                    <a class="btn " href="<%= request.getContextPath() %>/app/login">
                        Retourner à la connexion
                    </a>
                    <button class="btn btn-primary btn-with-icon" type="submit">
                        Envoyer le mail de réinitialisation
                        <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M7 10H13M13 10L10 7M13 10L10 13" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </button>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
