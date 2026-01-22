<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Specialite" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Matières</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        ul { list-style-type: none; padding: 0; }
        li { padding: 10px; border-bottom: 1px solid #ddd; display: flex; justify-content: space-between; align-items: center; }
        li:last-child { border-bottom: none; }
        a { text-decoration: none; color: #0d6efd; }
        a:hover { text-decoration: underline; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 20px; margin-bottom: 20px; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; }
        input[type="text"] { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        button { padding: 8px 12px; background-color: #0d6efd; color: white; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #0b5ed7; }
        
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
    <% Specialite spec = (Specialite) request.getAttribute("specialite"); %>
    <h2>Matières de la spécialité : <%= spec != null ? spec.getNom() : "" %></h2>
    
    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>
    <% String success = (String) request.getAttribute("success");
       if (success != null) { %>
        <p style="color:green;"><%= success %></p>
    <% } %>

    <!-- Liste des matières -->
    <div class="card" style="padding: 0 10px;">
        <ul>
        <% 
            List<Matiere> matieres = (List<Matiere>) request.getAttribute("matieres");
            if (matieres != null && !matieres.isEmpty()) {
                for (Matiere m : matieres) {
        %>
            <li>
                <span><%= m.getNom() %> (Semestre <%= m.getSemestre() %>)</span>
                <div style="display: flex; gap: 10px; align-items: center;">
                    <a href="<%= request.getContextPath() %>/app/admin/examens?matId=<%= m.getId() %>">Voir les examens &rarr;</a>
                    <button type="button" onclick="openEditModal('<%= m.getId() %>', '<%= m.getNom().replace("'", "\\'") %>', '<%= m.getSemestre() %>', '<%= m.getProfId() %>')" style="background-color: #ffc107; color: black; border: 1px solid #ffca2c; padding: 5px 10px; font-size: 0.8em; cursor: pointer; border-radius: 4px;">Modifier</button>
                    <form action="<%= request.getContextPath() %>/app/admin/supprimer-matiere" method="post" style="margin: 0;" onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cette matière ?');">
                        <input type="hidden" name="id" value="<%= m.getId() %>">
                        <input type="hidden" name="specId" value="<%= spec != null ? spec.getId() : "" %>">
                        <button type="submit" style="background-color: #dc3545; padding: 5px 10px; font-size: 0.8em;">Supprimer</button>
                    </form>
                </div>
            </li>
        <%      }
            } else {
        %>
            <li style="text-align: center; color: #777; padding: 20px;">Aucune matière trouvée.</li>
        <%  } %>
        </ul>
    </div>

    <!-- Formulaire d'ajout de matière -->
    <div class="card">
        <h3>Ajouter une matière</h3>
        <form action="<%= request.getContextPath() %>/app/admin/creer-matiere" method="post" style="display: flex; gap: 10px; align-items: center;">
            <input type="hidden" name="specialiteId" value="<%= spec != null ? spec.getId() : "" %>">
            <label for="nom">Nom :</label>
            <input type="text" id="nom" name="nom" required placeholder="Ex: Mathématiques">
            <button type="submit">Ajouter</button>
        </form>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/admin/specialites" class="back-link">&larr; Retour aux spécialités</a>

    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditModal()">&times;</span>
            <h3 style="margin-top: 0;">Modifier Matière</h3>
            <form id="editForm" action="<%= request.getContextPath() %>/app/admin/modifier-matiere" method="post">
                <input type="hidden" id="edit-id" name="id">
                <input type="hidden" name="specId" value="<%= spec != null ? spec.getId() : "" %>">
                <div class="form-group">
                    <label for="edit-nom">Nom:</label>
                    <input type="text" id="edit-nom" name="nom" required>
                </div>
                <div class="form-group">
                    <label for="edit-semestre">Semestre:</label>
                    <input type="number" id="edit-semestre" name="semestre" required>
                </div>
                <div class="form-group">
                    <label for="edit-profId">Professeur:</label>
                    <select id="edit-profId" name="profId" required style="width: 100%; padding: 8px;">
                        <% 
                        List<Utilisateur> profs = (List<Utilisateur>) request.getAttribute("professeurs");
                        if (profs != null) {
                            for (Utilisateur p : profs) {
                        %>
                            <option value="<%= p.getId() %>"><%= p.getNom() %> <%= p.getPrenom() %></option>
                        <% 
                            }
                        } 
                        %>
                    </select>
                </div>
                <button type="submit" style="background-color: #0d6efd; color: white; width: 100%;">Enregistrer</button>
            </form>
        </div>
    </div>

    <script>
        var modal = document.getElementById("editModal");
        function openEditModal(id, nom, semestre, coeff, profId) {
            document.getElementById("edit-id").value = id;
            document.getElementById("edit-nom").value = nom;
            document.getElementById("edit-semestre").value = semestre;
            document.getElementById("edit-profId").value = profId;
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