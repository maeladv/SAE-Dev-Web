<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Créer Évaluation - Espace Admin</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        .card { border: 1px solid #ddd; padding: 20px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px; }
        h2 { margin-top: 0; }
        form { display: flex; flex-direction: column; }
        input, select { margin: 10px 0; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
        button { padding: 10px; background-color: #0d6efd; color: #fff; border: none; cursor: pointer; border-radius: 4px; }
        button:hover { background-color: #0b5ed7; }
        .error { color: #dc3545; padding: 10px; background: #f8d7da; border-radius: 4px; margin-bottom: 10px; }
        .success { color: #155724; padding: 10px; background: #d4edda; border-radius: 4px; margin-bottom: 10px; }
        a.button { display: inline-block; margin-top: 15px; padding: 10px 14px; background: #6c757d; color: #fff; text-decoration: none; border-radius: 4px; }
        a.button:hover { background: #5a6268; }
        label { font-weight: bold; margin-top: 10px; }
    </style>
</head>
<body>
    <h1>Espace Administrateur</h1>
    
    <div class="card">
        <h2>Créer une nouvelle évaluation</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="success"><%= request.getAttribute("success") %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/app/admin/creer-evaluation">
            <label for="date_debut">Date de début</label>
            <input type="datetime-local" id="date_debut" name="date_debut" required>

            <label for="date_fin">Date de fin</label>
            <input type="datetime-local" id="date_fin" name="date_fin" required>
            
            <label for="semestre">Semestre</label>
            <select id="semestre" name="semestre" required>
                <option value="">-- Choisir un semestre --</option>
                <option value="1">Semestre 1</option>
                <option value="2">Semestre 2</option>
                <option value="3">Semestre 3</option>
                <option value="4">Semestre 4</option>
                <option value="5">Semestre 5</option>
                <option value="6">Semestre 6</option>
            </select>
            
            <button type="submit">Créer l'évaluation</button>
        </form>
    </div>

    <a href="<%= request.getContextPath() %>/app/admin" class="button">Retour au tableau de bord</a>
</body>
</html>
