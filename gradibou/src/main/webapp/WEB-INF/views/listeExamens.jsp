<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Matiere" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Examens</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f8f9fa; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 20px; margin-bottom: 20px; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; text-decoration: none; }
        input[type="number"] { padding: 8px; border: 1px solid #ccc; border-radius: 4px; width: 60px; }
        button { padding: 8px 12px; background-color: #0d6efd; color: white; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #0b5ed7; }
        a.action-link { color: #0d6efd; text-decoration: none; }
        a.action-link:hover { text-decoration: underline; }
        
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
    <% Matiere mat = (Matiere) request.getAttribute("matiere"); %>
    <h2>Examens de la matière : <%= mat != null ? mat.getNom() : "" %></h2>
    
    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>
    <% String success = (String) request.getAttribute("success");
       if (success != null) { %>
        <p style="color:green;"><%= success %></p>
    <% } %>

    <!-- Liste des examens -->
    <div class="card" style="padding: 0; overflow: hidden;">
        <table>
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Nom</th>
                    <th>Coefficient</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            <% 
                List<Examen> examens = (List<Examen>) request.getAttribute("examens");
                if (examens != null && !examens.isEmpty()) {
                    for (Examen e : examens) {
            %>
                <tr>
                    <td><%= e.getDate() %></td>
                    <td><%= e.getNom() %></td>
                    <td><%= e.getCoefficient() %></td>
                    <td>
                        <div style="display: flex; gap: 10px; align-items: center;">
                            <a href="<%= request.getContextPath() %>/app/admin/notes?examId=<%= e.getId() %>" class="action-link">
                                Voir les notes &rarr;
                            </a>
                            <button type="button" onclick="openEditModal('<%= e.getId() %>', '<%= e.getNom().replace("'", "\\'") %>', '<%= e.getCoefficient() %>')" style="background-color: #ffc107; color: black; border: 1px solid #ffca2c; padding: 5px 10px; font-size: 0.8em; cursor: pointer; border-radius: 4px;">Modifier</button>
                            <form action="<%= request.getContextPath() %>/app/admin/supprimer-examen" method="post" style="margin: 0;" onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cet examen ?');">
                                <input type="hidden" name="id" value="<%= e.getId() %>">
                                <button type="submit" style="background-color: #dc3545; padding: 5px 10px; font-size: 0.8em;">Supprimer</button>
                            </form>
                        </div>
                    </td>
                </tr>
            <%      }
                } else {
            %>
                <tr><td colspan="4" style="text-align: center; color: #777;">Aucun examen trouvé.</td></tr>
            <%  } %>
            </tbody>
        </table>
    </div>

    <!-- Formulaire d'ajout d'examen -->
    <div class="card">
        <h3>Créer un examen</h3>
        <form action="<%= request.getContextPath() %>/app/admin/creer-examen" method="post" style="display: flex; gap: 10px; align-items: center;">
            <input type="hidden" name="matiereId" value="<%= mat != null ? mat.getId() : "" %>">
            
            <label for="coefficient">Coefficient:</label>
            <input type="number" id="coefficient" name="coefficient" required step="1" min="1" value="1">
            
            <button type="submit">Créer (Date auto)</button>
        </form>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/gestion/specialite/details?specId=<%= mat != null ? mat.getSpecialiteId() : "" %>" class="back-link">
        &larr; Retour aux matières
    </a>

    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditModal()">&times;</span>
            <h3 style="margin-top: 0;">Modifier Examen</h3>
            <form id="editForm" action="<%= request.getContextPath() %>/app/admin/modifier-examen" method="post">
                <input type="hidden" id="edit-id" name="id">
                <div class="form-group">
                    <label for="edit-nom">Nom:</label>
                    <input type="text" id="edit-nom" name="nom" required>
                </div>
                <div class="form-group">
                    <label for="edit-coefficient">Coefficient:</label>
                    <input type="number" id="edit-coefficient" name="coefficient" required>
                </div>
                <button type="submit" style="background-color: #0d6efd; color: white; width: 100%;">Enregistrer</button>
            </form>
        </div>
    </div>

    <script>
        var modal = document.getElementById("editModal");
        function openEditModal(id, nom, coeff) {
            document.getElementById("edit-id").value = id;
            document.getElementById("edit-nom").value = nom;
            document.getElementById("edit-coefficient").value = coeff;
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