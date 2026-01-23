<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Specialite" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html>
<head>
    <title>Créer Matière - Espace Admin</title>
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
        <h2>Créer une nouvelle matière</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="success"><%= request.getAttribute("success") %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/app/admin/creer-matiere">
            <label for="nom">Nom de la matière</label>
            <input type="text" id="nom" name="nom" placeholder="Ex: Mathématiques" required>

            <label for="semestre">Semestre</label>
            <input type="number" id="semestre" name="semestre" min="1" max="10" placeholder="Semestre" required>
            
            <label for="specialite">Spécialité</label>
            <select id="specialite" name="idspecialite" required>
                <option value="">-- Choisir une spécialité --</option>
                <% 
                List<Specialite> specialites = (List<Specialite>) request.getAttribute("specialites");
                if (specialites != null) {
                    for (Specialite s : specialites) {
                %>
                    <option value="<%= s.getId() %>"><%= s.getNom() %> (<%= s.getAnnee() %>)</option>
                <% 
                    }
                }
                %>
            </select>

            <label for="professeur">Professeur responsable</label>
            <select id="professeur" name="profId" required>
                <option value="">-- Choisir un professeur --</option>
                <% 
                List<Utilisateur> professeurs = (List<Utilisateur>) request.getAttribute("professeurs");
                if (professeurs != null) {
                    for (Utilisateur p : professeurs) {
                %>
                    <option value="<%= p.getId() %>"><%= p.getNom() %> <%= p.getPrenom() %></option>
                <% 
                    }
                }
                %>
            </select>
            
            <button type="submit">Créer la matière</button>
        </form>
    </div>

    <a href="<%= request.getContextPath() %>/app/admin" class="button">Retour au tableau de bord</a>
</body>
</html>
