<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Espace Professeur - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <style>
        .prof-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 80vh;
            gap: 2rem;
            text-align: center;
        }
        .prof-actions {
            display: flex;
            gap: 2rem;
            justify-content: center;
            flex-wrap: wrap;
        }
        .prof-welcome {
            margin-bottom: 2rem;
        }
    </style>
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>
    
    <main class="prof-container">
        <div class="prof-welcome">
            <h1>Bienvenue, ${sessionScope.user.prenom} ${sessionScope.user.nom}</h1>
            <p>Espace enseignant - Gestion de vos matières et élèves</p>
        </div>
        
        <div class="prof-actions">
            <a href="<%= request.getContextPath() %>/app/professeur/matieres" class="btn btn-primary btn-lg">
                Voir mes matières
            </a>
            <a href="<%= request.getContextPath() %>/app/professeur/eleves" class="btn btn-primary btn-lg">
                Voir mes élèves
            </a>
        </div>
    </main>
</div>
</body>
</html>
