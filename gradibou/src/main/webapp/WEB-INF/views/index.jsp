<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gradibou - L'application de scolarité de l'INSA HdF</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/index.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=REM:wght@300;400;700&family=Roboto:wght@700&display=swap" rel="stylesheet">
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>
    
    <main class="home-main">
        <div class="home-hero">
            <div class="hero-logo-container">
                <img class="hero-logo-image" src="https://www.figma.com/api/mcp/asset/c8d056af-03ce-4c10-a3aa-e08820dc5d06" alt="Logo Gradibou">
                <h1 class="hero-logo-text">
                    <span class="hero-logo-grad">Grad</span><span class="hero-logo-ibou">ibou</span>
                </h1>
            </div>
            <p class="hero-subtitle">L'application de scolarité de l'INSA HdF</p>
        </div>
        
        <div class="home-cta-buttons">
            <a href="<%= request.getContextPath() %>/app/login" class="btn btn-ghost">
                Accéder à l'espace enseignant
            </a>
            <a href="<%= request.getContextPath() %>/app/login" class="btn btn-primary btn-dark btn-with-icon">
                Connexion →
            </a>
        </div>
    </main>
</div>
</body>
</html>
