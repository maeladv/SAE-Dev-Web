<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Matiere" %>
<!DOCTYPE html>
<html>
<head>
    <title>Créer Examen - Espace Admin</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        .card { border: 1px solid #ddd; padding: 20px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px; }
        h2 { margin-top: 0; }
        form { display: flex; flex-direction: column; }
        input, select { margin: 10px 0; padding: 8px; }
        button { padding: 10px; background-color: #0d6efd; color: #fff; border: none; cursor: pointer; border-radius: 4px; }
        button:hover { background-color: #0b5ed7; }
        .error { color: #dc3545; padding: 10px; background: #f8d7da; border-radius: 4px; margin-bottom: 10px; }
        .success { color: #155724; padding: 10px; background: #d4edda; border-radius: 4px; margin-bottom: 10px; }
        a.button { display: inline-block; margin-top: 15px; padding: 10px 14px; background: #6c757d; color: #fff; text-decoration: none; border-radius: 4px; }
        a.button:hover { background: #5a6268; }
    </style>
</head>
<body>
    <h1>Espace Administrateur</h1>
    
    <div class="card">
        <h2>Créer un nouvel examen</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="success"><%= request.getAttribute("success") %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/app/admin/creer-examen">
            <label for="nom">Nom de l'examen</label>
            <input type="text" id="nom" name="nom" placeholder="Ex: Partiel 1" required>

            <label for="coefficient">Coefficient</label>
            <input type="number" id="coefficient" name="coefficient" min="1" placeholder="Coefficient" required>
            
            <label for="matiere">Matière</label>
            <select id="matiere" name="matiereId" required>
                <option value="">-- Choisir une matière --</option>
                <% 
                List<Matiere> matieres = (List<Matiere>) request.getAttribute("matieres");
                if (matieres != null) {
                    for (Matiere m : matieres) {
                %>
                    <option value="<%= m.getId() %>"><%= m.getNom() %></option>
                <% 
                    }
                }
                %>
            </select>
            
            <button type="submit">Créer l'examen</button>
        </form>
    </div>

    <a href="<%= request.getContextPath() %>/app/admin" class="button">Retour au tableau de bord</a>
</body>
</html>
