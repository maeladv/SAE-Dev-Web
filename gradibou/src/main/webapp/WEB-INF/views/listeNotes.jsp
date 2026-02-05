<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Specialite" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Saisir les notes - Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/modals.css">
    <style>
        .notes-main {
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 20px;
            padding: 60px 40px;
            align-items: center;
        }
        
        .notes-content {
            width: 100%;
            max-width: 1400px;
            display: flex;
            flex-direction: column;
            gap: 40px;
        }
        
        /* Examen Header */
        .examen-header {
            display: flex;
            flex-direction: column;
            gap: 0;
            padding: 20px 0;
        }
        
        .examen-header-label {
            font-size: 24px;
            font-weight: 400;
            color: #838383;
            text-transform: uppercase;
            margin: 0;
        }
        
        .examen-header-title {
            font-size: 48px;
            font-weight: 700;
            color: #000;
            margin: 0;
            line-height: 1.2;
        }
        
        .examen-header-meta {
            display: flex;
            gap: 16px;
            align-items: center;
            margin-top: 8px;
        }
        
        .examen-header-meta .specialite-tag-badge {
            padding: 4px 8px;
            border-radius: 8px;
            font-size: 24px;
            font-weight: 800;
            text-transform: uppercase;
        }
        
        .examen-header-meta .tag-icy { background: rgba(139, 57, 190, 0.2); color: #8b39be; }
        .examen-header-meta .tag-gcb { background: rgba(23, 119, 117, 0.2); color: #177775; }
        .examen-header-meta .tag-iia { background: rgba(33, 112, 36, 0.2); color: #217024; }
        .examen-header-meta .tag-me { background: rgba(52, 91, 163, 0.2); color: #345ba3; }
        .examen-header-meta .tag-mt { background: rgba(155, 32, 32, 0.2); color: #9b2020; }
        
        .examen-header-meta .annee {
            font-size: 24px;
            font-weight: 800;
            color: #000;
            text-transform: uppercase;
        }
        
        .examen-header-meta .matiere-name {
            font-size: 24px;
            font-weight: 800;
            color: #838383;
            text-transform: uppercase;
            flex: 1;
        }
        
        /* Actions Bar */
        .actions-bar {
            background: #fff;
            border: 1px solid #d9d9d9;
            border-radius: 16px;
            padding: 8px 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        
        .coeff-field {
            display: flex;
            flex-direction: column;
            width: 240px;
        }
        
        .coeff-label {
            font-size: 14px;
            font-weight: 700;
            color: #1e1e1e;
            margin-bottom: 4px;
        }
        
        .coeff-input {
            background: #fff;
            border: 0.5px solid #d9d9d9;
            border-radius: 12px;
            padding: 12px 16px;
            font-size: 16px;
            color: #878787;
        }
        
        .action-buttons {
            display: flex;
            gap: 16px;
            align-items: center;
        }
        
        /* Notes Card */
        .notes-card {
            background: #fff;
            border-radius: 32px;
            padding: 24px;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        
        .notes-card-header {
            display: flex;
            align-items: center;
            gap: 16px;
            padding-bottom: 40px;
        }
        
        .notes-card-header h2 {
            font-size: 32px;
            font-weight: 700;
            color: #000;
            margin: 0;
        }
        
        .notes-card-header .spacer {
            flex: 1;
        }
        
        /* Table */
        .notes-table-wrapper {
            background: #fff;
            border: 1px solid #d9d9d9;
            border-radius: 16px;
            overflow: hidden;
        }
        
        .notes-table-wrapper thead {
            background: #fafafa;
        }
        
        .notes-table-wrapper thead tr {
            padding-top: 10px;
            border-bottom: 1px solid #d9d9d9;
        }
        
        .notes-table-wrapper thead th {
            padding: 10px 20px;
            text-align: left;
            font-size: 12px;
            font-weight: 900;
            color: #838383;
            text-transform: uppercase;
        }
        
        .notes-table-wrapper tbody td {
            padding: 16px 20px;
            font-size: 16px;
            font-weight: 500;
            color: #000;
        }
        
        .notes-table-wrapper tbody tr:not(:last-child) {
            border-bottom: 1px solid #d9d9d9;
        }
        
        .notes-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .notes-table th:nth-child(1),
        .notes-table td:nth-child(1) { width: 25%; }
        .notes-table th:nth-child(2),
        .notes-table td:nth-child(2) { width: 25%; }
        .notes-table th:nth-child(3),
        .notes-table td:nth-child(3) { width: 25%; }
        .notes-table th:nth-child(4),
        .notes-table td:nth-child(4) { width: 25%; }
        
        .note-input-wrapper {
            display: flex;
            flex-direction: column;
        }
        
        .note-input {
            background: #fff;
            border: 0.5px solid #d9d9d9;
            border-radius: 12px;
            padding: 12px 16px;
            font-size: 16px;
            color: #000;
            width: 100%;
        }
        
        .note-input::placeholder {
            color: #878787;
        }
        
        .note-input-validation {
            font-size: 10px;
            font-weight: 600;
            margin-top: 2px;
            min-height: 14px;
        }
        
        .empty-state {
            text-align: center;
            color: #838383;
            padding: 40px;
            font-size: 16px;
        }
        
        .btn img {
            width: 20px;
            height: 20px;
            max-width: none;
        }
    </style>
</head>
<body>
    <div class="page-container">
        <%@ include file="includes/header.jsp" %>

        <main class="notes-main">
            <% 
                Examen examen = (Examen) request.getAttribute("examen");
                Matiere matiere = null;
                Specialite spec = null;
                String tagClass = "";
                
                if (examen != null) {
                    try {
                        matiere = Matiere.trouverParId(examen.getId_matiere());
                        if (matiere != null) {
                            spec = Specialite.trouverParId(matiere.getSpecialiteId());
                            if (spec != null) {
                                tagClass = "tag-" + spec.getTag().toLowerCase();
                            }
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            %>
            <div class="notes-content">
                <!-- Examen Header -->
                <div class="examen-header">
                    <p class="examen-header-label">EXAMEN</p>
                    <h1 class="examen-header-title"><%= examen != null ? examen.getNom() : "" %></h1>
                    <div class="examen-header-meta">
                        <% if (spec != null) { %>
                        <span class="specialite-tag-badge <%= tagClass %>"><%= spec.getTag().toUpperCase() %></span>
                        <span class="annee"><%= spec.getAnnee() %>A</span>
                        <% } %>
                        <% if (matiere != null) { %>
                        <span class="matiere-name"><%= matiere.getNom() %></span>
                        <% } %>
                    </div>
                </div>
                
                <!-- Actions Bar -->
                <div class="actions-bar">
                    <div class="coeff-field">
                        <label class="coeff-label">Coefficient</label>
                        <input type="text" class="coeff-input" placeholder="Coefficient de l'épreuve" value="<%= examen != null ? examen.getCoefficient() : "" %>" readonly>
                    </div>
                    
                    <div class="action-buttons">
                        <button type="button" class="btn" style="background: #fff; color: #1e1e1e; box-shadow: 0px 0px 6px 0px rgba(0,0,0,0.25);">
                            <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg" alt="">
                            Supprimer l'examen
                        </button>
                        <button type="button" class="btn" style="background: #fff; color: #1e1e1e; box-shadow: 0px 0px 6px 0px rgba(0,0,0,0.25);">
                            <img src="<%= request.getContextPath() %>/static/icons/black/file-export.svg" alt="">
                            Exporter les notes
                        </button>
                        <button type="submit" form="notesForm" class="btn btn-primary">
                            <img src="<%= request.getContextPath() %>/static/icons/white/floppy-disk.svg" alt="">
                            Enregistrer les données
                        </button>
                    </div>
                </div>
                
                <!-- Notes Card -->
                <div class="notes-card">
                    <div class="notes-card-header">
                        <h2>Examens</h2>
                        <div class="spacer"></div>
                    </div>
                    
                    <% 
                        String error = request.getParameter("error");
                        if (error == null) {
                            error = (String) request.getAttribute("error");
                        }
                        if (error != null) { 
                    %>
                        <p style="color:#fe3232; margin:0 0 12px;"><%= error %></p>
                    <% } %>
                    <% 
                        String success = request.getParameter("success");
                        if (success == null) {
                            success = (String) request.getAttribute("success");
                        }
                        if (success != null) { 
                    %>
                        <p style="color:#7fce60; margin:0 0 12px;"><%= success %></p>
                    <% } %>
                    
                    <form id="notesForm" method="post" action="<%= request.getContextPath() %>/app/admin/sauvegarder-notes">
                        <input type="hidden" name="examId" value="<%= examen != null ? examen.getId() : "" %>">
                        
                        <div class="notes-table-wrapper">
                            <table class="notes-table">
                                <thead>
                                    <tr>
                                        <th>INE</th>
                                        <th>NOM</th>
                                        <th>Prenom</th>
                                        <th>Note</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <% 
                                    List<Utilisateur> etudiants = (List<Utilisateur>) request.getAttribute("etudiants");
                                    if (etudiants != null && !etudiants.isEmpty()) {
                                        for (Utilisateur etudiant : etudiants) {
                                %>
                                    <tr>
                                        <td><%= etudiant.getId() %></td>
                                        <td><%= etudiant.getNom() %></td>
                                        <td><%= etudiant.getPrenom() %></td>
                                        <td>
                                            <div class="note-input-wrapper">
                                                <input 
                                                    type="number" 
                                                    name="note_<%= etudiant.getId() %>" 
                                                    class="note-input" 
                                                    placeholder="Note..." 
                                                    min="0" 
                                                    max="20" 
                                                    step="0.01">
                                                <span class="note-input-validation"></span>
                                            </div>
                                        </td>
                                    </tr>
                                <%      }
                                    } else {
                                %>
                                    <tr><td colspan="4"><div class="empty-state">Aucun étudiant trouvé.</div></td></tr>
                                <%  } %>
                                </tbody>
                            </table>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>

    <script>
        const contextPath = '<%= request.getContextPath() %>';
        
        // Gérer la soumission du formulaire des notes
        document.getElementById('notesForm').addEventListener('submit', function(event) {
            event.preventDefault();
            
            const examId = '<%= examen != null ? examen.getId() : "" %>';
            const params = new URLSearchParams();
            params.append('examId', examId);
            
            // Récupérer toutes les notes saisies
            const noteInputs = document.querySelectorAll('input[name^="note_"]');
            let hasNotes = false;
            
            noteInputs.forEach(input => {
                const value = input.value.trim();
                if (value !== '') {
                    const etudiantId = input.name.replace('note_', '');
                    params.append('note_' + etudiantId, value);
                    hasNotes = true;
                }
            });
            
            if (!hasNotes) {
                alert('Veuillez saisir au moins une note avant d\'enregistrer.');
                return;
            }
            
            // Envoyer les données au serveur
            fetch(contextPath + '/app/admin/sauvegarder-notes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: params
            }).then(response => {
                if (response.ok) {
                    // Afficher un message de succès
                    alert('Les notes ont été enregistrées avec succès !');
                    location.reload();
                } else {
                    return response.text().then(text => {
                        const message = text && text.trim() ? text : 'Erreur lors de l\'enregistrement des notes';
                        alert(message);
                    });
                }
            }).catch(error => {
                console.error('Erreur:', error);
                alert('Erreur réseau : impossible de contacter le serveur');
            });
        });
        
        // Validation des notes en temps réel
        document.querySelectorAll('.note-input').forEach(input => {
            input.addEventListener('input', function() {
                const validation = this.parentElement.querySelector('.note-input-validation');
                const value = parseFloat(this.value);
                
                if (this.value === '') {
                    validation.textContent = '';
                    validation.style.color = '';
                } else if (isNaN(value) || value < 0 || value > 20) {
                    validation.textContent = 'Ce champ est invalide';
                    validation.style.color = '#fe3232';
                } else {
                    validation.textContent = 'Ce champ est valide';
                    validation.style.color = '#7fce60';
                }
            });
        });
    </script>
</body>
</html>
