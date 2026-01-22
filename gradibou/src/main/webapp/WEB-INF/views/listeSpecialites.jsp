<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Specialite" %>
<%
    String pageRole = (String) request.getAttribute("userRole");
    boolean isAdmin = "admin".equals(pageRole);
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spécialités - <%= isAdmin ? "Admin" : "Gestion" %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/modals.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/specialites.css">
</head>
<body>
    <script>
        const contextPath = '<%= request.getContextPath() %>';
        const isAdmin = <%= isAdmin %>;
    </script>
    <div class="page-container">
        <%@ include file="includes/header.jsp" %>
        
        <main class="specialites-main">
            <!-- Toolbar -->
            <div class="specialites-toolbar">
                <h1>Liste des spécialités de l'INSA</h1>
                <% if (isAdmin) { %>
                <button class="btn btn-primary btn-with-icon" onclick="openModal('createSpecialiteModal')">
                    Ajouter une spécialité
                    <img src="<%= request.getContextPath() %>/static/icons/white/user-plus.svg" alt="Ajouter">
                </button>
                <% } %>
            </div>

            <!-- Table -->
            <div class="specialites-table-container">
                <table class="specialites-table">
                    <thead>
                        <tr>
                            <th>TAG</th>
                            <th>Année</th>
                            <th>Nom</th>
                            <th>Nombre d'étudiants</th>
                            <th>Nombre de professeurs</th>
                            <th>Plus d'options</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Specialite> specialites = (List<Specialite>) request.getAttribute("specialites");
                            if (specialites != null && !specialites.isEmpty()) {
                                for (Specialite s : specialites) {
                                    String tagClass = "tag-" + s.getTag().toLowerCase();
                                    // Récupération des compteurs
                                    int nbEtudiants = 0;
                                    int nbProfs = 0;
                                    try {
                                        nbEtudiants = s.compterEtudiants();
                                        nbProfs = s.compterProfesseurs();
                                    } catch (Exception e) {
                                        // Silently ignore counting errors
                                    }
                        %>
                        <tr>
                            <td>
                                <span class="specialite-tag-badge <%= tagClass %>"><%= s.getTag().toUpperCase() %></span>
                            </td>
                            <td>
                                <span class="annee-badge"><%= s.getAnnee() %>A</span>
                            </td>
                            <td><%= s.getNom() %></td>
                            <td><%= nbEtudiants %></td>
                            <td><%= nbProfs %></td>
                            <td>
                                <div class="specialites-table-actions">
                                    <% if (isAdmin) { %>
                                    <button type="button" title="Modifier" onclick="openEditSpecialiteModal(<%= s.getId() %>, '<%= s.getNom().replace("'", "\\'") %>', '<%= s.getTag().replace("'", "\\'") %>', <%= s.getAnnee() %>)">
                                        <img src="<%= request.getContextPath() %>/static/icons/black/pen.svg" alt="Modifier">
                                    </button>
                                    <button type="button" title="Exporter PDF">
                                        <img src="<%= request.getContextPath() %>/static/icons/black/file-export.svg" alt="PDF">
                                    </button>
                                    <button type="button" title="Supprimer" onclick="confirmDeleteSpecialite(<%= s.getId() %>, '<%= s.getTag() %>', <%= s.getAnnee() %>)">
                                        <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg" alt="Supprimer">
                                    </button>
                                    <% } %>
                                    <a href="<%= request.getContextPath() %>/app/gestion/specialite/details?specId=<%= s.getId() %>" class="btn-primary" title="Voir les matières">
                                        <img src="<%= request.getContextPath() %>/static/icons/white/eye.svg" alt="Voir">
                                    </a>
                                </div>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="6" class="specialites-empty">Aucune spécialité trouvée.</td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </main>
    </div>

    <!-- Modal: Créer une spécialité -->
    <div id="createSpecialiteModal" class="modal-overlay">
        <div class="modal">
            <h2 class="modal-title">Créer une spécialité</h2>

            <form onsubmit="return submitCreateSpecialite(event)">
                <div class="modal-form-grid">
                    <div class="form-group">
                        <label for="nom">Nom de la spécialité</label>
                        <input type="text" id="nom" name="nom" placeholder="Informatique et Cybersécurité" required>
                        <span class="error-message">Ce champ est invalide</span>
                    </div>

                    <div class="form-group">
                        <label for="tag">Tag de la spécialité (ICY, IIA, ME...) - Ne pourra pas être modifié ultérieurement</label>
                        <input type="text" id="tag" name="tag" placeholder="ICY" required maxlength="10">
                        <span class="error-message">Ce champ est invalide</span>
                    </div>

                    <div class="form-group">
                        <label for="annee">Année scolaire des étudiants</label>
                        <input type="number" id="annee" name="annee" placeholder="1, 2, 3, 4, 5..." required min="1" max="5">
                        <span class="error-message">Ce champ est invalide</span>
                    </div>
                </div>

                <div class="modal-actions">
                    <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                    <button type="submit" class="modal-btn modal-btn-primary">Créer la spécialité</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal: Modifier une spécialité -->
    <div id="editSpecialiteModal" class="modal-overlay">
        <div class="modal">
            <h2 class="modal-title">Modifier la spécialité</h2>

            <form onsubmit="return submitEditSpecialite(event)">
                <input type="hidden" id="edit-id" name="id">
                <div class="modal-form-grid">
                    <div class="form-group">
                        <label for="edit-nom">Nom de la spécialité</label>
                        <input type="text" id="edit-nom" name="nom" required>
                        <span class="error-message">Ce champ est invalide</span>
                    </div>
                </div>

                <div class="modal-actions">
                    <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                    <button type="submit" class="modal-btn modal-btn-primary">Mettre à jour la spécialité</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal: Confirmation de suppression -->
    <div id="deleteSpecialiteModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title" id="delete-title">Supprimer la spécialité ICY 3A ?</h2>

            <p class="modal-description">Êtes-vous sûr.e de vouloir effectuer cette opération ?</p>

            <div class="modal-actions">
                <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                <button type="button" class="modal-btn modal-btn-primary" onclick="executeDeleteSpecialite()">Supprimer définitivement</button>
            </div>
        </div>
    </div>

    <script src="<%= request.getContextPath() %>/static/js/modals.js"></script>
    <script src="<%= request.getContextPath() %>/static/js/specialites.js"></script>
</body>
</html>