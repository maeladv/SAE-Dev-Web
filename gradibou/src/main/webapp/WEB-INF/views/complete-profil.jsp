<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Compléter votre profil</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 500px; margin: 50px auto; padding: 20px; }
        .card { border: 1px solid #ddd; padding: 30px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        h2 { margin-top: 0; text-align: center; }
        form { display: flex; flex-direction: column; }
        input { margin: 10px 0; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
        button { padding: 12px; margin-top: 10px; background-color: #0d6efd; color: #fff; border: none; cursor: pointer; border-radius: 4px; font-size: 16px; }
        button:hover { background-color: #0b5ed7; }
        .error { color: #dc3545; padding: 10px; background: #f8d7da; border-radius: 4px; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="card">
        <h2>Définissez votre mot de passe</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <p class="error"><%= request.getAttribute("error") %></p>
        <% } %>

        <p>Bienvenue ! Veuillez choisir un mot de passe sécurisé pour finaliser la création de votre compte.</p>

        <form method="post" action="<%= request.getContextPath() %>/app/complete-profil">
            <input type="hidden" name="token" value="<%= request.getParameter("token") %>">
            
            <input type="password" name="motDePasse" placeholder="Mot de passe" required minlength="6">
            <input type="password" name="confirmPassword" placeholder="Confirmer le mot de passe" required minlength="6">
            
            <button type="submit">Activer mon compte</button>
        </form>
    </div>
</body>
</html>
