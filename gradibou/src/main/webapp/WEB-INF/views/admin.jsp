<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="fr">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Comptes Gradibou - Admin</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/admin.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/modals.css">
    </head>

    <body>
        <script>
            const contextPath = '<%= request.getContextPath() %>';
        </script>
        <div class="page-container admin-page">
            <%@ include file="includes/header.jsp" %>

                <main class="admin-main">
                    <!-- Barre d'actions -->
                    <div class="admin-toolbar">
                        <h1>Comptes Gradibou</h1>
                        <div class="admin-actions">
                            <div class="dropdown" data-dropdown="role">
                                <button type="button" class="dropdown-toggle" aria-haspopup="listbox" aria-expanded="false">
                                    <span class="dropdown-label">Choisir un rôle</span>
                                    <span class="dropdown-icon">▾</span>
                                </button>
                                <ul class="dropdown-menu" role="listbox">
                                    <li class="dropdown-option" role="option" data-value="admin" data-label="Administrateur" data-role="admin">Administrateur</li>
                                    <li class="dropdown-option" role="option" data-value="professeur" data-label="Professeur" data-role="professeur">Professeur</li>
                                    <li class="dropdown-option" role="option" data-value="etudiant" data-label="Étudiant" data-role="etudiant">Étudiant</li>
                                </ul>
                            </div>
                            <div class="dropdown" data-dropdown="specialite">
                                <button type="button" class="dropdown-toggle" aria-haspopup="listbox" aria-expanded="false">
                                    <span class="dropdown-label">Choisir une spécialité</span>
                                    <span class="dropdown-icon">▾</span>
                                </button>
                                <ul class="dropdown-menu" role="listbox">
                                    <li class="dropdown-option" role="option" data-value="icy" data-label="ICY">ICY</li>
                                    <li class="dropdown-option" role="option" data-value="iia" data-label="IIA">IIA</li>
                                    <li class="dropdown-option" role="option" data-value="gcb" data-label="GCB">GCB</li>
                                    <li class="dropdown-option" role="option" data-value="me" data-label="ME">ME</li>
                                </ul>
                            </div>
                            <button class="btn btn-secondary btn-with-icon" type="button" onclick="resetFilters()">
                                <img src="<%= request.getContextPath() %>/static/icons/black/xmark.svg" alt="Réinitialiser les filtres">
                            </button>
                            <button class="btn btn-tertiary btn-with-icon" onclick="openModal('csvUploadModal')">
                                Importer depuis un .csv
                                <img src="/gradibou/static/icons/black/file-csv.svg" alt="Importer icone">
                            </button>
                            <button class="btn btn-primary btn-with-icon" onclick="openModal('createAccountModal')">
                                Ajouter un utilisateur
                                <img src="/gradibou/static/icons/white/user-plus.svg"
                                    alt="AAjouter un utilisateur icone">
                            </button>
                        </div>
                    </div>

                    <!-- Tableau des utilisateurs -->
                    <div class="users-table-container">
                        <table class="users-table">
                            <thead>
                                <tr>
                                    <th class="sortable" onclick="sortTable(0, 'number')">
                                        Numéro
                                        <span class="sort-indicator">▲</span>
                                    </th>
                                    <th class="sortable" onclick="sortTable(1, 'text')">
                                        Nom
                                        <span class="sort-indicator">▲</span>
                                    </th>
                                    <th class="sortable" onclick="sortTable(2, 'text')">
                                        Prénom
                                        <span class="sort-indicator">▲</span>
                                    </th>
                                    <th class="sortable" onclick="sortTable(3, 'text')">
                                        Rôle
                                        <span class="sort-indicator">▲</span>
                                    </th>
                                    <th class="sortable" onclick="sortTable(4, 'text')">
                                        Email
                                        <span class="sort-indicator">▲</span>
                                    </th>
                                    <th class="sortable" onclick="sortTable(5, 'text')">
                                        INE
                                        <span class="sort-indicator">▲</span>
                                    </th>
                                    <th class="sortable" onclick="sortTable(6, 'text')">
                                        Spécialité
                                        <span class="sort-indicator">▲</span>
                                    </th>
                                    <th>Plus d'options</th>
                                </tr>
                            </thead>
                            <tbody id="usersTableBody">
                                <%
                                    java.util.List<model.Utilisateur> utilisateurs = (java.util.List<model.Utilisateur>) request.getAttribute("utilisateurs");
                                    if (utilisateurs != null && !utilisateurs.isEmpty()) {
                                        int index = 0;
                                        for (model.Utilisateur utilisateur : utilisateurs) {
                                            String rowClass = (index % 2 == 1) ? "table-row-alt" : "";
                                            String roleClass = "role-" + utilisateur.getRole().toLowerCase();
                                            String roleLabel = utilisateur.getRole().substring(0, 1).toUpperCase() + utilisateur.getRole().substring(1);
                                            String ine = "-";
                                            String specialiteTag = "-";
                                            String specialiteClass = "";
                                            try {
                                                String valIne = utilisateur.getIne();
                                                ine = (valIne != null && !valIne.isEmpty()) ? valIne : "-";
                                                String tag = utilisateur.getSpecialiteTag();
                                                if (tag != null && !tag.isEmpty()) {
                                                    specialiteTag = tag.toUpperCase();
                                                    specialiteClass = "specialite-badge spe-" + tag.toLowerCase();
                                                } else {
                                                    specialiteTag = "-";
                                                    specialiteClass = "";
                                                }
                                            } catch (Exception ignored) {
                                                ine = "-";
                                                specialiteTag = "-";
                                                specialiteClass = "";
                                            }
                                %>
                                <tr <%= !rowClass.isEmpty() ? "class=\"" + rowClass + "\"" : "" %>>
                                    <td><%= utilisateur.getId() %></td>
                                    <td><%= utilisateur.getNom() %></td>
                                    <td><%= utilisateur.getPrenom() %></td>
                                    <td><span class="role-badge <%= roleClass %>"><%= roleLabel.toUpperCase() %></span></td>
                                    <td><%= utilisateur.getemail() %></td>
                                    <td><%= ine %></td>
                                    <td>
                                        <%
                                            if (specialiteClass.isEmpty()) {
                                        %>
                                            -
                                        <%
                                            } else {
                                        %>
                                            <span class="<%= specialiteClass %>"><%= specialiteTag %></span>
                                        <%
                                            }
                                        %>
                                    </td>
                                    <td>
                                        <div class="table-actions">
                                            <button class="btn-tertiary" title="Éditer"
                                                onclick="editUser(<%= utilisateur.getId() %>, '<%= utilisateur.getNom() %>', '<%= utilisateur.getPrenom() %>', '<%= utilisateur.getemail() %>', '<%= utilisateur.getRole() %>')">
                                                <img src="<%= request.getContextPath() %>/static/icons/black/pen.svg"
                                                    alt="Éditer un utilisateur icone">
                                            </button>
                                            <button class="btn-tertiary" title="Supprimer"
                                                onclick="confirmDelete(<%= utilisateur.getId() %>, '<%= utilisateur.getPrenom() %> <%= utilisateur.getNom() %>')">
                                                <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg"
                                                    alt="Supprimer un utilisateur icone">
                                            </button>
                                            <button class="btn-tertiary" title="Copier le lien de réinitialisation"
                                                onclick="copyResetLink(<%= utilisateur.getId() %>)">
                                                <img src="<%= request.getContextPath() %>/static/icons/black/link.svg"
                                                    alt="Copier le lien de réinitialisation icone">
                                            </button>
                                            <button class="btn-primary" title="Voir le profil" onclick="VoirProfilUtilisateur(<%= utilisateur.getId() %>)">
                                                <img src="<%= request.getContextPath() %>/static/icons/white/eye.svg" alt="Voir le profil icone">
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                            index++;
                                        }
                                    } else {
                                %>
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 20px; color: #999;">Aucun utilisateur trouvé</td>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </main>
        </div>

        <!-- Modal: Créer un compte -->
        <div id="createAccountModal" class="modal-overlay">
            <div class="modal">
                <h2 class="modal-title">Créer un compte</h2>

                <form onsubmit="return submitCreateAccount(event)">
                    <div class="modal-form-grid">
                        <!-- Colonne gauche -->
                        <div class="form-group">
                            <label for="prenom">Prénom</label>
                            <input type="text" id="prenom" name="prenom" placeholder="Rentrez le prénom..." required>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <div class="form-group">
                            <label for="nom">Nom</label>
                            <input type="text" id="nom" name="nom" placeholder="Rentrez le nom..." required>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <div class="form-group">
                            <label for="email">Adresse mail</label>
                            <input type="email" id="email" name="email" placeholder="Adresse mail INSA" required>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <div class="form-group">
                            <label for="dateNaissance">Date de naissance</label>
                            <input type="date" id="dateNaissance" name="dateNaissance" required>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <!-- Colonne droite -->
                        <div class="form-group">
                            <label>Choix du rôle</label>
                            <div class="dropdown" data-dropdown="modal-role">
                                <button type="button" class="dropdown-toggle" aria-haspopup="listbox" aria-expanded="false">
                                    <span class="dropdown-label">Choisir un rôle</span>
                                    <span class="dropdown-icon">▾</span>
                                </button>
                                <ul class="dropdown-menu" role="listbox">
                                    <li class="dropdown-option" role="option" data-value="admin" data-label="Administrateur" data-role="admin">Administrateur</li>
                                    <li class="dropdown-option" role="option" data-value="professeur" data-label="Professeur" data-role="professeur">Professeur</li>
                                    <li class="dropdown-option" role="option" data-value="etudiant" data-label="Étudiant" data-role="etudiant">Étudiant</li>
                                </ul>
                            </div>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <div class="form-group">
                            <label for="specialite">Spécialité</label>
                            <input type="text" id="specialite" name="specialite" placeholder="Spécialité (optionnel)">
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <div class="form-group">
                            <label for="ine">INE</label>
                            <input type="text" id="ine" name="ine" placeholder="Numéro INE (optionnel)">
                            <span class="error-message">Ce champ est invalide</span>
                        </div>
                    </div>

                    <div class="modal-actions">
                        <button type="button" class="modal-btn modal-btn-secondary"
                            onclick="closeModal()">Annuler</button>
                        <button type="submit" class="modal-btn modal-btn-primary">Créer le compte</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal: Éditer un utilisateur -->
        <div id="editAccountModal" class="modal-overlay">
            <div class="modal">
                <h2 class="modal-title">Modifier l'utilisateur</h2>

                <form onsubmit="return submitEditAccount(event)">
                    <input type="hidden" id="editidUtilisateur" name="id">
                    <div class="modal-form-grid">
                        <!-- Colonne gauche -->
                        <div class="form-group">
                            <label for="editPrenom">Prénom</label>
                            <input type="text" id="editPrenom" name="prenom" placeholder="Rentrez le prénom..." required>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <div class="form-group">
                            <label for="editNom">Nom</label>
                            <input type="text" id="editNom" name="nom" placeholder="Rentrez le nom..." required>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <div class="form-group">
                            <label for="editEmail">Adresse mail</label>
                            <input type="email" id="editEmail" name="email" placeholder="Adresse mail INSA" required>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>

                        <!-- Colonne droite -->
                        <div class="form-group">
                            <label>Choix du rôle</label>
                            <div class="dropdown" data-dropdown="modal-edit-role">
                                <button type="button" class="dropdown-toggle" aria-haspopup="listbox" aria-expanded="false">
                                    <span class="dropdown-label">Choisir un rôle</span>
                                    <span class="dropdown-icon">▾</span>
                                </button>
                                <ul class="dropdown-menu" role="listbox">
                                    <li class="dropdown-option" role="option" data-value="admin" data-label="Administrateur" data-role="admin">Administrateur</li>
                                    <li class="dropdown-option" role="option" data-value="professeur" data-label="Professeur" data-role="professeur">Professeur</li>
                                    <li class="dropdown-option" role="option" data-value="etudiant" data-label="Étudiant" data-role="etudiant">Étudiant</li>
                                </ul>
                            </div>
                            <span class="error-message">Ce champ est invalide</span>
                        </div>
                    </div>

                    <div class="modal-actions">
                        <button type="button" class="modal-btn modal-btn-secondary"
                            onclick="closeModal()">Annuler</button>
                        <button type="submit" class="modal-btn modal-btn-primary">Modifier l'utilisateur</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal: Upload CSV -->
        <div id="csvUploadModal" class="modal-overlay">
            <div class="modal modal-small">
                <h2 class="modal-title">Uploader une liste de comptes via un CSV</h2>

                <form action="<%= request.getContextPath() %>/app/admin/upload-csv" method="post"
                    enctype="multipart/form-data" onsubmit="return submitCSVUpload(event)">
                    <div class="form-group">
                        <label for="csvFile">Fichier</label>
                        <div class="file-input-wrapper">
                            <input type="file" id="csvFile" name="csvFile" accept=".csv" required>
                            <div class="file-input-display">Déposez un fichier .csv dans le bon format</div>
                        </div>
                        <span class="error-message">Ce champ est invalide</span>
                    </div>

                    <div class="modal-actions">
                        <button type="button" class="modal-btn modal-btn-secondary"
                            onclick="closeModal()">Annuler</button>
                        <button type="submit" class="modal-btn modal-btn-primary">Upload du fichier</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal: Upload CSV - Erreur -->
        <div id="csvErrorModal" class="modal-overlay">
            <div class="modal modal-small">
                <h2 class="modal-title">Uploader une liste de comptes via un CSV</h2>

                <form action="<%= request.getContextPath() %>/app/admin/upload-csv" method="post"
                    enctype="multipart/form-data">
                    <div class="form-group">
                        <label for="csvFileError">Fichier</label>
                        <div class="file-input-wrapper">
                            <input type="file" id="csvFileError" name="csvFile" accept=".csv" required>
                            <div class="file-input-display error">Déposez un fichier .csv dans le bon format</div>
                        </div>
                        <span class="error-message" style="display: block; color: #fe3232;">Le format de fichier est
                            invalide</span>
                    </div>

                    <div class="modal-actions">
                        <button type="button" class="modal-btn modal-btn-secondary"
                            onclick="closeModal()">Annuler</button>
                        <button type="submit" class="modal-btn modal-btn-primary">Upload du fichier</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal: Confirmation de suppression -->
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal modal-small">
                <h2 class="modal-title">Supprimer l'utilisateur</h2>

                <p class="modal-description">Êtes-vous sûr.e de vouloir effectuer cette opération ?</p>

                <div class="modal-actions">
                    <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                    <button type="button" class="modal-btn modal-btn-primary" onclick="executeDelete()">Supprimer
                        définitivement</button>
                </div>
            </div>
        </div>

        <script src="<%= request.getContextPath() %>/static/js/modals.js"></script>
        <script src="<%= request.getContextPath() %>/static/js/admin.js"></script>
    </body>

    </html>