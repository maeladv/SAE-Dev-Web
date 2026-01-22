<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Specialite" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Spécialités</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        ul { list-style-type: none; padding: 0; }
        li { padding: 10px; border-bottom: 1px solid #ddd; display: flex; justify-content: space-between; align-items: center; }
        li:last-child { border-bottom: none; }
        a { text-decoration: none; color: #0d6efd; }
        a:hover { text-decoration: underline; }
        .button { display: inline-block; padding: 10px 14px; background: #0d6efd; color: #fff; text-decoration: none; border-radius: 4px; margin-bottom: 20px; }
        .button:hover { background: #0b5ed7; color: #fff; text-decoration: none; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 0 10px; }
        
        /* Modal styles */
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.4); }
        .modal-content { background-color: #fefefe; margin: 15% auto; padding: 20px; border: 1px solid #888; width: 80%; max-width: 500px; border-radius: 5px; }
        .close { color: #aaa; float: right; font-size: 28px; font-weight: bold; cursor: pointer; }
        .close:hover, .close:focus { color: black; text-decoration: none; cursor: pointer; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input { width: 100%; padding: 8px; box-sizing: border-box; }
    </style>
</head>
<body>
    <h2>Liste des Spécialités</h2>
    
    <a href="<%= request.getContextPath() %>/app/admin/creer-specialite" class="button">Créer une spécialité</a>

    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>

    <div class="card">
    <ul>
    <% 
        List<Specialite> specialites = (List<Specialite>) request.getAttribute("specialites");
        if (specialites != null && !specialites.isEmpty()) {
            for (Specialite s : specialites) {
    %>
        <li>
            <span><%= s.getNom() %> (<%= s.getTag() %> - <%= s.getAnnee() %>)</span>
            <div style="display: flex; gap: 10px; align-items: center;">
                <a href="<%= request.getContextPath() %>/app/admin/matieres?specId=<%= s.getId() %>">Voir les matières &rarr;</a>
                <button type="button" onclick="openEditModal('<%= s.getId() %>', '<%= s.getNom().replace("'", "\\'") %>', '<%= s.getTag().replace("'", "\\'") %>', '<%= s.getAnnee() %>')" style="background-color: #ffc107; color: black; border: 1px solid #ffca2c; padding: 5px 10px; font-size: 0.8em; cursor: pointer; border-radius: 4px;">Modifier</button>
                <form action="<%= request.getContextPath() %>/app/admin/supprimer-specialite" method="post" style="margin: 0;" onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cette spécialité ? Tout le contenu associé sera également supprimé permanently.');">
                    <input type="hidden" name="id" value="<%= s.getId() %>">
                    <button type="submit" style="background-color: #dc3545; padding: 5px 10px; font-size: 0.8em;">Supprimer</button>
                </form>
            </div>
        </li>
    <%      }
        } else {
    %>
        <li style="text-align: center; color: #777;">Aucune spécialité trouvée.</li>
    <%  } %>
    </ul>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/admin" class="back-link">&larr; Retour au tableau de bord</a>

    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditModal()">&times;</span>
            <h3 style="margin-top: 0;">Modifier Spécialité</h3>
            <form id="editForm" action="<%= request.getContextPath() %>/app/admin/modifier-specialite" method="post">
                <input type="hidden" id="edit-id" name="id">
                <div class="form-group">
                    <label for="edit-nom">Nom:</label>
                    <input type="text" id="edit-nom" name="nom" required>
                </div>
                <div class="form-group">
                    <label for="edit-tag">Tag (ex: INFO):</label>
                    <input type="text" id="edit-tag" name="tag" required>
                </div>
                <div class="form-group">
                    <label for="edit-annee">Année:</label>
                    <input type="number" id="edit-annee" name="annee" required>
                </div>
                <button type="submit" class="button">Enregistrer</button>
            </form>
        </div>
    </div>

    <script>
        var modal = document.getElementById("editModal");
        function openEditModal(id, nom, tag, annee) {
            document.getElementById("edit-id").value = id;
            document.getElementById("edit-nom").value = nom;
            document.getElementById("edit-tag").value = tag;
            document.getElementById("edit-annee").value = annee;
            modal.style.display = "block";
        }
        function closeEditModal() {
            modal.style.display = "none";
        }
        window.onclick = function(event) {
            if (event.target == modal) {
                closeEditModal();
            }
        }
    </script>
</body>
</html>