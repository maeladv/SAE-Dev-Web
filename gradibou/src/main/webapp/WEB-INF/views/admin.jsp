<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Espace Admin</title>
</head>
<body>
    <h1>Espace Administrateur</h1>

    <p>
        <a href="<%= request.getContextPath() %>/app/admin/creer-compte">Créer un compte utilisateur</a><br>
        <a href="<%= request.getContextPath() %>/app/admin/creer-specialite">Créer une spécialité</a>
    </p>

    <a class="button" href="<%= request.getContextPath() %>/app/logout">Se déconnecter</a>
</body>
</html>
