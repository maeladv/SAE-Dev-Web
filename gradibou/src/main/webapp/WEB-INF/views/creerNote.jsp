<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html>
<head>
    <title>Saisir Note - Espace Admin</title>
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
        <h2>Saisir une note</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="success"><%= request.getAttribute("success") %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/app/admin/creer-note">
            <label for="examen">Examen</label>
            <select id="examen" name="examenId" required>
                <option value="">-- Choisir un examen --</option>
                <% 
                String selectedExamenId = request.getParameter("idExamen");
                List<Examen> examens = (List<Examen>) request.getAttribute("examens");
                if (examens != null) {
                    for (Examen e : examens) {
                %>
                    <option value="<%= e.getId() %>" <%= (selectedExamenId != null && selectedExamenId.equals(String.valueOf(e.getId()))) ? "selected" : "" %>><%= e.getNom() %> (Coeff: <%= e.getCoefficient() %>)</option>
                <% 
                    }
                }
                %>
            </select>

            <label for="etudiant">Étudiant</label>
            <select id="etudiant" name="etudiantId" required>
                <option value="">-- Choisir un étudiant --</option>
                <% 
                List<Utilisateur> etudiants = (List<Utilisateur>) request.getAttribute("etudiants");
                if (etudiants != null) {
                    for (Utilisateur s : etudiants) {
                %>
                    <option value="<%= s.getId() %>"><%= s.getNom() %> <%= s.getPrenom() %></option>
                <% 
                    }
                }
                %>
            </select>

            <label for="note">Note (/20)</label>
            <input type="number" id="note" name="note" min="0" max="20" placeholder="Note" required>
            
            <button type="submit">Enregistrer la note</button>
        </form>
    </div>

    <a href="<%= request.getContextPath() %>/app/admin" class="button">Retour au tableau de bord</a>
</body>
</html>
