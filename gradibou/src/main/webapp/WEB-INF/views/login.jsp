<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/login.css">
</head>
<body>
<div class="page-container login-page">
    <header class="site-header">
        <a class="brand" href="<%= request.getContextPath() %>/">
            <img src="https://www.figma.com/api/mcp/asset/fd9ab368-a25a-4ba0-8893-eeb203a759c4" alt="Logo Gradibou">
        </a>
        <div class="nav-actions">
            <nav class="nav-links" aria-label="Navigation principale">
                <a href="#">Emploi du temps</a>
                <a href="#">Moodle</a>
            </nav>
            <div class="header-cta">
                <a class="btn btn-primary" href="<%= request.getContextPath() %>/app/login">Connexion &rarr;</a>
            </div>
        </div>
    </header>

    <main class="login-main">
        <div class="login-logo-large">
            <img src="https://www.figma.com/api/mcp/asset/fd9ab368-a25a-4ba0-8893-eeb203a759c4" alt="Gradibou">
        </div>

        <section class="login-card" aria-labelledby="login-title">
            <h1 id="login-title">Connexion</h1>

            <% if (request.getAttribute("error") != null) { %>
                <p class="status-message error"><%= request.getAttribute("error") %></p>
            <% } %>
            <% if (request.getAttribute("success") != null) { %>
                <p class="status-message success"><%= request.getAttribute("success") %></p>
            <% } %>

            <form class="form-fields" method="post" action="<%= request.getContextPath() %>/app/login">
                <div class="field-group">
                    <input class="input-field" type="email" name="email" placeholder="Votre Gradimail ou GridiPseudo" required>
                    <p class="form-feedback" aria-live="polite">&nbsp;</p>
                </div>
                <div class="field-group">
                    <input class="input-field" type="password" name="motDePasse" placeholder="Votre GradiMotdepasse" required>
                    <p class="form-feedback" aria-live="polite">&nbsp;</p>
                </div>
                <div class="actions">
                    <a class="btn btn-ghost" href="#">Mot de passe oublié</a>
                    <button class="btn btn-primary btn-with-icon" type="submit">
                        Se connecter
                        <img src="https://www.figma.com/api/mcp/asset/51cfe758-9834-4608-9a3d-2b1e4de9b7be" alt="">
                    </button>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>