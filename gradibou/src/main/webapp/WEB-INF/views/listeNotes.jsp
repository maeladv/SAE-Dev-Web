<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Note" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Administration - Notes</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h2 { margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f8f9fa; }
        .card { border: 1px solid #ddd; border-radius: 6px; padding: 20px; margin-bottom: 20px; }
        .back-link { margin-top: 20px; display: inline-block; color: #6c757d; text-decoration: none; }
        .button { display: inline-block; padding: 10px 14px; background: #0d6efd; color: #fff; text-decoration: none; border-radius: 4px; margin-bottom: 20px; }
        .button:hover { background: #0b5ed7; color: #fff; text-decoration: none; }
        
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
    <% Examen examen = (Examen) request.getAttribute("examen"); %>
    <h2>Notes de l'examen : <%= examen != null ? examen.getNom() : "" %></h2>
    
    <a href="<%= request.getContextPath() %>/app/admin/creer-note?idExamen=<%= examen != null ? examen.getId() : "" %>" class="button">Ajouter une note</a>

    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>

    <!-- Liste des notes -->
    <div class="card" style="padding: 0; overflow: hidden;">
        <table>
            <thead>
                <tr>
                    <th>Étudiant</th>
                    <th>Note (/20)</th>
                    <th>Date</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            <% 
                List<Note> notes = (List<Note>) request.getAttribute("notes");
                if (notes != null && !notes.isEmpty()) {
                    for (Note n : notes) {
                        Utilisateur etudiant = Utilisateur.trouverParId(n.getIdEtudiant());
            %>
                <tr>
                    <td><%= etudiant != null ? etudiant.getNom() + " " + etudiant.getPrenom() : "Étudiant #" + n.getIdEtudiant() %></td>
                    <td><strong><%= n.getValeur() %></strong></td>
                    <td><%= n.getDate() %></td>
                    <td>
                        <div style="display: flex; gap: 10px; align-items: center;">
                            <button type="button" onclick="openEditModal('<%= n.getIdEtudiant() %>', '<%= n.getIdExamen() %>', '<%= n.getValeur() %>')" style="background-color: #ffc107; color: black; border: 1px solid #ffca2c; padding: 5px 10px; font-size: 0.8em; cursor: pointer; border-radius: 4px;">Modifier</button>
                            <form action="<%= request.getContextPath() %>/app/admin/supprimer-note" method="post" style="margin: 0;" onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cette note ?');">
                                <input type="hidden" name="etudiantId" value="<%= n.getIdEtudiant() %>">
                                <input type="hidden" name="examenId" value="<%= n.getIdExamen() %>">
                                <button type="submit" style="background-color: #dc3545; padding: 5px 10px; font-size: 0.8em; color: white; border: none; border-radius: 4px; cursor: pointer;">Supprimer</button>
                            </form>
                        </div>
                    </td>
                </tr>
            <%      }
                } else {
            %>
                <tr><td colspan="4" style="text-align: center; color: #777;">Aucune note trouvée pour cet examen.</td></tr>
            <%  } %>
            </tbody>
        </table>
    </div>
    
    <a href="<%= request.getContextPath() %>/app/admin/examens?matId=<%= examen != null ? examen.getId_matiere() : "" %>" class="back-link">
        &larr; Retour aux examens
    </a>

    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditModal()">&times;</span>
            <h3 style="margin-top: 0;">Modifier Note</h3>
            <form id="editForm" action="<%= request.getContextPath() %>/app/admin/modifier-note" method="post">
                <input type="hidden" id="edit-etudiantId" name="etudiantId">
                <input type="hidden" id="edit-examenId" name="examenId">
                <div class="form-group">
                    <label for="edit-note">Note (/20):</label>
                    <input type="number" id="edit-note" name="note" required min="0" max="20" step="0.5">
                </div>
                <button type="submit" class="button" style="width: 100%;">Enregistrer</button>
            </form>
        </div>
    </div>

    <script>
        var modal = document.getElementById("editModal");
        function openEditModal(etudiantId, examenId, note) {
            document.getElementById("edit-etudiantId").value = etudiantId;
            document.getElementById("edit-examenId").value = examenId;
            document.getElementById("edit-note").value = note;
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