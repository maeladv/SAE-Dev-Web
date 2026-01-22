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
                                    <li class="dropdown-option" role="option" data-value="professeur" data-label="Professeur" data-role="prof">Professeur</li>
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
                            <button class="btn  btn-with-icon" onclick="openModal('csvUploadModal')">
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
                                <!-- Les données seront chargées via JavaScript depuis le backend -->
                                <tr>
                                    <td>200</td>
                                    <td>Doe</td>
                                    <td>John</td>
                                    <td><span class="role-badge role-admin">Administrateur</span></td>
                                    <td>john.doe@insa-hdf.fr</td>
                                    <td>-</td>
                                    <td><span class="specialite-badge spe-icy">ICY</span></td>
                                    <td>
                                        <div class="table-actions">
                                            <button class="btn-tertiary" title="Éditer"
                                                onclick="openModal('createAccountModal')">
                                                <img src="/gradibou/static/icons/black/pen.svg"
                                                    alt="Éditer un utilisateur icone">
                                            </button>
                                            <button class="btn-tertiary" title="Supprimer"
                                                onclick="confirmDelete(200, 'John Doe')">
                                                <img src="/gradibou/static/icons/black/trash.svg"
                                                    alt="Supprimer un utilisateur icone">
                                            </button>
                                            <button class="btn-primary" title="Voir le profil">
                                                <img src="/gradibou/static/icons/white/eye.svg" alt="Voir le profil icone">
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <tr class="table-row-alt">
                                    <td>201</td>
                                    <td>Smith</td>
                                    <td>Jane</td>
                                    <td><span class="role-badge role-prof">Professeur</span></td>
                                    <td>jane.smith@insa-hdf.fr</td>
                                    <td>-</td>
                                    <td>-</td>
                                    <td>
                                        <div class="table-actions">
                                            <button class="btn-tertiary" title="Éditer"
                                                onclick="openModal('createAccountModal')">
                                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none"
                                                    xmlns="http://www.w3.org/2000/svg">
                                                    <path
                                                        d="M13.0283 2.6625L11.6252 4.06563L15.9345 8.375L17.3377 6.97188C17.7627 6.55 18.0002 5.975 18.0002 5.375C18.0002 4.775 17.7627 4.2 17.3377 3.77813L16.222 2.6625C15.8002 2.2375 15.2252 2 14.6252 2C14.0252 2 13.4502 2.2375 13.0283 2.6625ZM10.5658 5.125L3.84078 11.8469C3.50641 12.1813 3.26266 12.6 3.13453 13.0562L2.02828 17.05C1.95641 17.3094 2.02828 17.5906 2.22203 17.7812C2.41578 17.9719 2.69391 18.0469 2.95328 17.975L6.94703 16.8656C7.40328 16.7375 7.81891 16.4969 8.15641 16.1594L14.8752 9.43437L10.5658 5.125Z"
                                                        fill="black" />
                                                </svg>
                                            </button>
                                            <button class="btn-tertiary" title="Supprimer"
                                                onclick="confirmDelete(201, 'Jane Smith')">
                                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none"
                                                    xmlns="http://www.w3.org/2000/svg">
                                                    <path
                                                        d="M7.27187 2.18438L7 3H4C3.44687 3 3 3.44687 3 4C3 4.55312 3.44687 5 4 5H16C16.5531 5 17 4.55312 17 4C17 3.44687 16.5531 3 16 3H13L12.7281 2.18438C12.5906 1.775 12.2094 1.5 11.7781 1.5H8.22188C7.79063 1.5 7.40937 1.775 7.27187 2.18438ZM16 6.5H4L4.65938 16.5969C4.70938 17.3875 5.36562 18 6.15625 18H13.8438C14.6344 18 15.2906 17.3875 15.3406 16.5969L16 6.5Z"
                                                        fill="black" />
                                                </svg>
                                            </button>
                                            <button class="btn-primary" title="Voir le profil">
                                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none"
                                                    xmlns="http://www.w3.org/2000/svg">
                                                    <path
                                                        d="M9.9999 3C7.4749 3 5.45303 4.15 3.98115 5.51875C2.51865 6.87813 1.54053 8.5 1.0749 9.61563C0.971777 9.8625 0.971777 10.1375 1.0749 10.3844C1.54053 11.5 2.51865 13.125 3.98115 14.4812C5.45303 15.8469 7.4749 17 9.9999 17C12.5249 17 14.5468 15.85 16.0187 14.4812C17.4812 13.1219 18.4593 11.5 18.9249 10.3844C19.028 10.1375 19.028 9.8625 18.9249 9.61563C18.4593 8.5 17.4812 6.875 16.0187 5.51875C14.5468 4.15313 12.5249 3 9.9999 3ZM5.4999 10C5.4999 7.51562 7.51553 5.5 9.9999 5.5C12.4843 5.5 14.4999 7.51562 14.4999 10C14.4999 12.4844 12.4843 14.5 9.9999 14.5C7.51553 14.5 5.4999 12.4844 5.4999 10ZM9.9999 8C9.9999 9.10313 9.10303 10 7.9999 10C7.64053 10 7.30303 9.90625 7.00928 9.7375C6.97803 10.0781 7.00615 10.4281 7.0999 10.775C7.52803 12.375 9.1749 13.325 10.7749 12.8969C12.3749 12.4688 13.3249 10.8219 12.8968 9.22188C12.5155 7.79375 11.1624 6.88438 9.7374 7.00938C9.90303 7.3 9.9999 7.6375 9.9999 8Z"
                                                        fill="white" />
                                                </svg>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td>202</td>
                                    <td>Brown</td>
                                    <td>Emily</td>
                                    <td><span class="role-badge role-etudiant">Étudiant</span></td>
                                    <td>emily.brown@insa-hdf.fr</td>
                                    <td>22300765</td>
                                    <td><span class="specialite-badge spe-iia">IIA</span></td>
                                    <td>
                                        <div class="table-actions">
                                            <button class="btn-tertiary" title="Éditer"
                                                onclick="openModal('createAccountModal')">
                                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none"
                                                    xmlns="http://www.w3.org/2000/svg">
                                                    <path
                                                        d="M13.0283 2.6625L11.6252 4.06563L15.9345 8.375L17.3377 6.97188C17.7627 6.55 18.0002 5.975 18.0002 5.375C18.0002 4.775 17.7627 4.2 17.3377 3.77813L16.222 2.6625C15.8002 2.2375 15.2252 2 14.6252 2C14.0252 2 13.4502 2.2375 13.0283 2.6625ZM10.5658 5.125L3.84078 11.8469C3.50641 12.1813 3.26266 12.6 3.13453 13.0562L2.02828 17.05C1.95641 17.3094 2.02828 17.5906 2.22203 17.7812C2.41578 17.9719 2.69391 18.0469 2.95328 17.975L6.94703 16.8656C7.40328 16.7375 7.81891 16.4969 8.15641 16.1594L14.8752 9.43437L10.5658 5.125Z"
                                                        fill="black" />
                                                </svg>
                                            </button>
                                            <button class="btn-tertiary" title="Supprimer"
                                                onclick="confirmDelete(202, 'Emily Brown')">
                                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none"
                                                    xmlns="http://www.w3.org/2000/svg">
                                                    <path
                                                        d="M7.27187 2.18438L7 3H4C3.44687 3 3 3.44687 3 4C3 4.55312 3.44687 5 4 5H16C16.5531 5 17 4.55312 17 4C17 3.44687 16.5531 3 16 3H13L12.7281 2.18438C12.5906 1.775 12.2094 1.5 11.7781 1.5H8.22188C7.79063 1.5 7.40937 1.775 7.27187 2.18438ZM16 6.5H4L4.65938 16.5969C4.70938 17.3875 5.36562 18 6.15625 18H13.8438C14.6344 18 15.2906 17.3875 15.3406 16.5969L16 6.5Z"
                                                        fill="black" />
                                                </svg>
                                            </button>
                                            <button class="btn-primary" title="Voir le profil">
                                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none"
                                                    xmlns="http://www.w3.org/2000/svg">
                                                    <path
                                                        d="M9.9999 3C7.4749 3 5.45303 4.15 3.98115 5.51875C2.51865 6.87813 1.54053 8.5 1.0749 9.61563C0.971777 9.8625 0.971777 10.1375 1.0749 10.3844C1.54053 11.5 2.51865 13.125 3.98115 14.4812C5.45303 15.8469 7.4749 17 9.9999 17C12.5249 17 14.5468 15.85 16.0187 14.4812C17.4812 13.1219 18.4593 11.5 18.9249 10.3844C19.028 10.1375 19.028 9.8625 18.9249 9.61563C18.4593 8.5 17.4812 6.875 16.0187 5.51875C14.5468 4.15313 12.5249 3 9.9999 3ZM5.4999 10C5.4999 7.51562 7.51553 5.5 9.9999 5.5C12.4843 5.5 14.4999 7.51562 14.4999 10C14.4999 12.4844 12.4843 14.5 9.9999 14.5C7.51553 14.5 5.4999 12.4844 5.4999 10ZM9.9999 8C9.9999 9.10313 9.10303 10 7.9999 10C7.64053 10 7.30303 9.90625 7.00928 9.7375C6.97803 10.0781 7.00615 10.4281 7.0999 10.775C7.52803 12.375 9.1749 13.325 10.7749 12.8969C12.3749 12.4688 13.3249 10.8219 12.8968 9.22188C12.5155 7.79375 11.1624 6.88438 9.7374 7.00938C9.90303 7.3 9.9999 7.6375 9.9999 8Z"
                                                        fill="white" />
                                                </svg>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </main>
        </div>

        <!-- Modal: Créer un compte -->
        <div id="createAccountModal" class="modal-overlay">
            <div class="modal">
                <h2 class="modal-title">Créer un compte</h2>

                <form action="<%= request.getContextPath() %>/app/admin/creer-utilisateur" method="post"
                    onsubmit="return submitCreateAccount(event)">
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
                            <div class="custom-dropdown">
                                <div class="dropdown-toggle">
                                    <div class="dropdown-value">Choisir un rôle</div>
                                    <svg class="dropdown-arrow" width="20" height="20" viewBox="0 0 20 20" fill="none">
                                        <path d="M6 8L10 12L14 8" stroke="currentColor" stroke-width="1.5"
                                            stroke-linecap="round" stroke-linejoin="round" />
                                    </svg>
                                </div>
                                <div class="dropdown-menu">
                                    <div class="dropdown-item role-item role-admin" data-value="admin">Administrateur
                                    </div>
                                    <div class="dropdown-item role-item role-prof" data-value="professeur">Professeur
                                    </div>
                                    <div class="dropdown-item role-item role-etudiant" data-value="etudiant">Étudiant
                                    </div>
                                </div>
                                <input type="hidden" name="role" required>
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