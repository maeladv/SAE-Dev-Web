<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Compléter votre profil - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <style>
        .card { border: 1px solid #ddd; padding: 30px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); max-width: 500px; margin: 50px auto; background: white; }
        h2 { margin-top: 0; text-align: center; }
        form { display: flex; flex-direction: column; }
        .error { color: #dc3545; padding: 10px; background: #f8d7da; border-radius: 4px; margin-bottom: 15px; }
    </style>
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>
    
    <main>
        <div class="card">
            <h2>Définissez votre mot de passe</h2>
            
            <% if (request.getAttribute("error") != null) { %>
                <p class="error"><%= request.getAttribute("error") %></p>
            <% } %>

            <p>Bienvenue ! Veuillez choisir un mot de passe sécurisé pour finaliser la création de votre compte.</p>

            <form method="post" action="<%= request.getContextPath() %>/app/complete-profil">
                <input type="hidden" name="token" value="<%= request.getParameter("token") %>">
                
                <input class="input-field" type="password" name="motDePasse" placeholder="Mot de passe" required minlength="6">
                <input class="input-field" type="password" name="confirmPassword" placeholder="Confirmer le mot de passe" required minlength="6" style="margin-top: 10px;">
                
                <button class="btn btn-primary" type="submit" style="margin-top: 20px;">Activer mon compte</button>
            </form>
        </div>
    </main>
</div>
</body>
</html>
