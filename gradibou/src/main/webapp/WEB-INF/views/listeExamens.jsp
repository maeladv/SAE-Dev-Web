<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Specialite" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Examens - Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/modals.css">
    <style>
        .examens-main {
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 20px;
            padding: 60px 40px;
            align-items: center;
        }
        
        .examens-content {
            width: 100%;
            max-width: 1400px;
            display: flex;
            flex-direction: column;
            gap: 40px;
        }
        
        /* Header Section */
        .matiere-header {
            display: flex;
            flex-direction: column;
            gap: 0;
        }
        
        .matiere-header-label {
            font-size: 24px;
            font-weight: 400;
            color: #838383;
            text-transform: uppercase;
            margin: 0;
        }
        
        .matiere-header-title {
            font-size: 48px;
            font-weight: 700;
            color: #000;
            margin: 0;
            line-height: 1.2;
        }
        
        .matiere-header-meta {
            display: flex;
            gap: 16px;
            align-items: center;
            margin-top: 8px;
        }
        
        .matiere-header-meta .specialite-tag-badge {
            padding: 4px 8px;
            border-radius: 8px;
            font-size: 24px;
            font-weight: 800;
            text-transform: uppercase;
        }
        
        .matiere-header-meta .tag-icy { background: rgba(139, 57, 190, 0.2); color: #8b39be; }
        .matiere-header-meta .tag-gcb { background: rgba(23, 119, 117, 0.2); color: #177775; }
        .matiere-header-meta .tag-iia { background: rgba(33, 112, 36, 0.2); color: #217024; }
        .matiere-header-meta .tag-me { background: rgba(52, 91, 163, 0.2); color: #345ba3; }
        .matiere-header-meta .tag-mt { background: rgba(155, 32, 32, 0.2); color: #9b2020; }
        
        .matiere-header-meta .annee {
            font-size: 24px;
            font-weight: 800;
            color: #000;
            text-transform: uppercase;
        }
        
        .matiere-header-meta .semestre {
            font-size: 24px;
            font-weight: 800;
            color: #838383;
            text-transform: uppercase;
        }
        
        /* Professor Info Box */
        .prof-info-box {
            background: #fff;
            border: 1px solid #d9d9d9;
            border-radius: 16px;
            padding: 20px 16px;
        }
        
        .prof-info-box p {
            margin: 0;
            font-size: 16px;
            font-weight: 500;
            color: #000;
        }
        
        .prof-info-box a {
            text-decoration: underline;
            color: #000;
        }
        
        /* Examens Card */
        .examens-card {
            background: #fff;
            border-radius: 32px;
            padding: 24px;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        
        .examens-card-header {
            display: flex;
            align-items: center;
            gap: 16px;
            padding-bottom: 40px;
        }
        
        .examens-card-header h2 {
            font-size: 32px;
            font-weight: 700;
            color: #000;
            margin: 0;
        }
        
        .examens-card-header .spacer {
            flex: 1;
        }
        
        /* Table */
        .examens-table-wrapper {
            background: #fff;
            border: 1px solid #d9d9d9;
            border-radius: 16px;
            overflow: hidden;
        }
        
        .examens-table-wrapper thead {
            background: #fafafa;
        }
        
        .examens-table-wrapper thead tr {
            border-bottom: 1px solid #d9d9d9;
        }
        
        .examens-table-wrapper thead th {
            padding: 10px 20px;
            text-align: left;
            font-size: 12px;
            font-weight: 900;
            color: #838383;
            text-transform: uppercase;
        }
        
        .examens-table-wrapper tbody td {
            padding: 16px 20px;
            font-size: 16px;
            font-weight: 500;
            color: #000;
        }
        
        .examens-table-wrapper tbody tr:not(:last-child) {
            border-bottom: 1px solid #d9d9d9;
        }
        
        .examens-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .examens-table th:nth-child(1),
        .examens-table td:nth-child(1) { width: 16.66%; }
        .examens-table th:nth-child(2),
        .examens-table td:nth-child(2) { width: 16.66%; }
        .examens-table th:nth-child(3),
        .examens-table td:nth-child(3) { width: 16.66%; }
        .examens-table th:nth-child(4),
        .examens-table td:nth-child(4) { width: 16.66%; }
        .examens-table th:nth-child(5),
        .examens-table td:nth-child(5) { width: 16.66%; }
        .examens-table th:nth-child(6),
        .examens-table td:nth-child(6) { width: 16.66%; }
        
        .examens-table tbody td:last-child {
            text-align: right;
        }
        
        .table-action-buttons {
            display: flex;
            align-items: center;
            gap: 8px;
            justify-content: flex-end;
        }
        
        .table-action-buttons img {
            width: 20px;
            height: 20px;
            max-width: none;
        }
        
        .examens-card-header img {
            width: 20px;
            height: 20px;
            max-width: none;
        }
        
        .btn img {
            width: 20px;
            height: 20px;
            max-width: none;
        }
        
        .empty-state {
            text-align: center;
            color: #838383;
            padding: 40px;
            font-size: 16px;
        }
    </style>
</head>
<body>
    <script src="<%= request.getContextPath() %>/static/js/modals.js"></script>
    <script>
        const contextPath = '<%= request.getContextPath() %>';
    </script>

    <div class="page-container">
        <%@ include file="includes/header.jsp" %>

        <main class="examens-main">
            <% 
                Matiere mat = (Matiere) request.getAttribute("matiere");
                Specialite spec = null;
                String tagClass = "";
                if (mat != null) {
                    try {
                        spec = Specialite.trouverParId(mat.getSpecialiteId());
                        if (spec != null) {
                            tagClass = "tag-" + spec.getTag().toLowerCase();
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            %>
            <div class="examens-content">
                <!-- Header Section -->
                <div class="matiere-header">
                    <p class="matiere-header-label">Matière</p>
                    <h1 class="matiere-header-title"><%= mat != null ? mat.getNom() : "" %></h1>
                    <div class="matiere-header-meta">
                        <% if (spec != null) { %>
                        <span class="specialite-tag-badge <%= tagClass %>"><%= spec.getTag().toUpperCase() %></span>
                        <span class="annee"><%= spec.getAnnee() %>A</span>
                        <% } %>
                        <% if (mat != null) { %>
                        <span class="semestre">(Semestre <%= mat.getSemestre() %>)</span>
                        <% } %>
                    </div>
                </div>
                
                <!-- Professor Info -->
                <div class="prof-info-box">
                    <% 
                        Utilisateur professeur = (Utilisateur) request.getAttribute("professeur");
                        if (professeur != null) {
                    %>
                        <p>Professeur référent : <a href="mailto:<%= professeur.getemail() %>"><%= professeur.getemail() %></a></p>
                    <% } else { %>
                        <p>Professeur référent : Non défini</p>
                    <% } %>
                </div>
                
                <!-- Examens Card -->
                <div class="examens-card">
                    <div class="examens-card-header">
                        <h2>Examens</h2>
                        <div class="spacer"></div>
                        <button class="btn btn-secondary" onclick="openModal('createExamModal')">
                            <img src="<%= request.getContextPath() %>/static/icons/black/circle-plus.svg" alt="">
                            Ajouter un examen
                        </button>
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
                    
                    <div class="examens-table-wrapper">
                        <table class="examens-table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Nom de l'épreuve</th>
                                    <th>Moyenne</th>
                                    <th>Coeff</th>
                                    <th>Nombre de notes</th>
                                    <th>Plus d'options</th>
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
                                    <td>-/20</td>
                                    <td><%= e.getCoefficient() %></td>
                                    <td>-/35</td>
                                    <td>
                                        <div class="table-action-buttons">
                                            <button class="btn btn-tertiary" title="Modifier" onclick="openEditExamModal('<%= e.getId() %>', '<%= e.getNom().replace("'", "\\'") %>', '<%= e.getCoefficient() %>', '<%= e.getDate() %>')">
                                                <img src="<%= request.getContextPath() %>/static/icons/black/pen.svg" alt="Modifier">
                                            </button>
                                            <form action="<%= request.getContextPath() %>/app/admin/supprimer-examen" method="post" style="margin:0; display:inline;">
                                                <input type="hidden" name="id" value="<%= e.getId() %>">
                                                <button type="submit" class="btn btn-tertiary" title="Supprimer" onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet examen ?');">
                                                    <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg" alt="Supprimer">
                                                </button>
                                            </form>
                                            <a class="btn btn-primary" href="<%= request.getContextPath() %>/app/admin/notes?examId=<%= e.getId() %>">
                                                <img src="<%= request.getContextPath() %>/static/icons/white/pen.svg" alt="">
                                                Saisir les notes
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            <%      }
                                } else {
                            %>
                                <tr><td colspan="6"><div class="empty-state">Aucun examen trouvé.</div></td></tr>
                            <%  } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Modal: Créer un examen -->
    <div id="createExamModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title">Ajout d'un examen</h2>
            <div class="modal-content">
                <form action="<%= request.getContextPath() %>/app/admin/creer-examen" method="post">
                    <input type="hidden" name="matiereId" value="<%= mat != null ? mat.getId() : "" %>">
                    
                    <div class="form-group">
                        <label for="exam-nom">Nom de l'examen</label>
                        <input id="exam-nom" name="nom" class="input-field" placeholder="Partiels, TP, etc." required>
                    </div>
                    
                    <div class="form-group">
                        <label for="exam-coeff">Coefficient</label>
                        <input id="exam-coeff" name="coefficient" type="number" min="1" step="1" value="1" class="input-field" placeholder="Rentrez un coefficient" required>
                    </div>
                    
                    <div class="modal-actions">
                        <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                        <button type="submit" class="modal-btn modal-btn-primary">Ajouter</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal: Modifier un examen -->
    <div id="editExamModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title">Modification d'un examen</h2>
            <div class="modal-content">
                <form action="<%= request.getContextPath() %>/app/admin/modifier-examen" method="post">
                    <input type="hidden" id="edit-exam-id" name="id">
                    
                    <div class="form-group">
                        <label for="edit-exam-nom">Nom de l'examen</label>
                        <input id="edit-exam-nom" name="nom" class="input-field" placeholder="Partiels, TP, etc." required>
                    </div>
                    
                    <div class="form-group">
                        <label for="edit-exam-coeff">Coefficient</label>
                        <input id="edit-exam-coeff" name="coefficient" type="number" min="1" step="1" class="input-field" placeholder="Rentrez un coefficient" required>
                    </div>
                    
                    <div class="modal-actions">
                        <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                        <button type="submit" class="modal-btn modal-btn-primary">Enregistrer</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        function openEditExamModal(id, nom, coeff, date) {
            document.getElementById('edit-exam-id').value = id;
            document.getElementById('edit-exam-nom').value = nom || '';
            document.getElementById('edit-exam-coeff').value = coeff || 1;
            openModal('editExamModal');
        }
    </script>
</body>
</html>