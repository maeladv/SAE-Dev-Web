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
                <select class="filter-dropdown">
                    <option value="">Rôle</option>
                    <option value="admin">Administrateur</option>
                    <option value="professeur">Professeur</option>
                    <option value="etudiant">Étudiant</option>
                </select>
                <select class="filter-dropdown">
                    <option value="">Spécialité</option>
                    <option value="icy">ICY</option>
                    <option value="iia">IIA</option>
                    <option value="gcb">GCB</option>
                    <option value="me">ME</option>
                </select>
                <button class="btn btn-ghost btn-with-icon" onclick="openModal('csvUploadModal')">
                    Importer depuis un .csv
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <path d="M10 2V14M10 2L6 6M10 2L14 6M2 14V16C2 17.1046 2.89543 18 4 18H16C17.1046 18 18 17.1046 18 16V14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
                <button class="btn btn-primary btn-with-icon" onclick="openModal('createAccountModal')">
                    Ajouter un utilisateur
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <path d="M9 11H5C3.89543 11 3 11.8954 3 13V17M15 7L17 9M17 9L15 11M17 9H11M11 3.5C11 5.433 9.433 7 7.5 7C5.567 7 4 5.433 4 3.5C4 1.567 5.567 0 7.5 0C9.433 0 11 1.567 11 3.5Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
            </div>
        </div>

        <!-- Tableau des utilisateurs -->
        <div class="users-table-container">
            <table class="users-table">
                <thead>
                    <tr>
                        <th>Numéro</th>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Rôle</th>
                        <th>Email</th>
                        <th>INE</th>
                        <th>Spécialité</th>
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
                                <button class="action-btn" title="Éditer" onclick="openModal('createAccountModal')">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                                        <path d="M14.5 2.5L17.5 5.5M10 18H18M3 15L2 18L5 17L16.5 5.5L13.5 2.5L3 15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                    </svg>
                                </button>
                                <button class="action-btn" title="Supprimer" onclick="confirmDelete(200, 'John Doe')">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                                        <path d="M3 5H17M8 9V15M12 9V15M7 5V3C7 2.44772 7.44772 2 8 2H12C12.5523 2 13 2.44772 13 3V5M5 5H15L14 17C14 17.5523 13.5523 18 13 18H7C6.44772 18 6 17.5523 6 17L5 5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                    </svg>
                                </button>
                                <button class="action-btn action-btn-primary" title="Voir le profil">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                                        <path d="M2 10C2 10 5 4 10 4C15 4 18 10 18 10C18 10 15 16 10 16C5 16 2 10 2 10Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                        <circle cx="10" cy="10" r="2" stroke="white" stroke-width="1.5"/>
                                    </svg>
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
                                <button class="action-btn" title="Éditer" onclick="openModal('createAccountModal')">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M14.5 2.5L17.5 5.5M10 18H18M3 15L2 18L5 17L16.5 5.5L13.5 2.5L3 15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                                </button>
                                <button class="action-btn" title="Supprimer" onclick="confirmDelete(201, 'Jane Smith')">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M3 5H17M8 9V15M12 9V15M7 5V3C7 2.44772 7.44772 2 8 2H12C12.5523 2 13 2.44772 13 3V5M5 5H15L14 17C14 17.5523 13.5523 18 13 18H7C6.44772 18 6 17.5523 6 17L5 5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                                </button>
                                <button class="action-btn action-btn-primary" title="Voir le profil">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M2 10C2 10 5 4 10 4C15 4 18 10 18 10C18 10 15 16 10 16C5 16 2 10 2 10Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><circle cx="10" cy="10" r="2" stroke="white" stroke-width="1.5"/></svg>
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
                                <button class="action-btn" title="Éditer" onclick="openModal('createAccountModal')">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M14.5 2.5L17.5 5.5M10 18H18M3 15L2 18L5 17L16.5 5.5L13.5 2.5L3 15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                                </button>
                                <button class="action-btn" title="Supprimer" onclick="confirmDelete(202, 'Emily Brown')">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M3 5H17M8 9V15M12 9V15M7 5V3C7 2.44772 7.44772 2 8 2H12C12.5523 2 13 2.44772 13 3V5M5 5H15L14 17C14 17.5523 13.5523 18 13 18H7C6.44772 18 6 17.5523 6 17L5 5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                                </button>
                                <button class="action-btn action-btn-primary" title="Voir le profil">
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M2 10C2 10 5 4 10 4C15 4 18 10 18 10C18 10 15 16 10 16C5 16 2 10 2 10Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><circle cx="10" cy="10" r="2" stroke="white" stroke-width="1.5"/></svg>
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
        
        <form action="<%= request.getContextPath() %>/app/admin/creer-utilisateur" method="post" onsubmit="return submitCreateAccount(event)">
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
                                <path d="M6 8L10 12L14 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                        </div>
                        <div class="dropdown-menu">
                            <div class="dropdown-item role-item role-admin" data-value="admin">Administrateur</div>
                            <div class="dropdown-item role-item role-prof" data-value="professeur">Professeur</div>
                            <div class="dropdown-item role-item role-etudiant" data-value="etudiant">Étudiant</div>
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
                <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                <button type="submit" class="modal-btn modal-btn-primary">Créer le compte</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Upload CSV -->
<div id="csvUploadModal" class="modal-overlay">
    <div class="modal modal-small">
        <h2 class="modal-title">Uploader une liste de comptes via un CSV</h2>
        
        <form action="<%= request.getContextPath() %>/app/admin/upload-csv" method="post" enctype="multipart/form-data" onsubmit="return submitCSVUpload(event)">
            <div class="form-group">
                <label for="csvFile">Fichier</label>
                <div class="file-input-wrapper">
                    <input type="file" id="csvFile" name="csvFile" accept=".csv" required>
                    <div class="file-input-display">Déposez un fichier .csv dans le bon format</div>
                </div>
                <span class="error-message">Ce champ est invalide</span>
            </div>
            
            <div class="modal-actions">
                <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
                <button type="submit" class="modal-btn modal-btn-primary">Upload du fichier</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Upload CSV - Erreur -->
<div id="csvErrorModal" class="modal-overlay">
    <div class="modal modal-small">
        <h2 class="modal-title">Uploader une liste de comptes via un CSV</h2>
        
        <form action="<%= request.getContextPath() %>/app/admin/upload-csv" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="csvFileError">Fichier</label>
                <div class="file-input-wrapper">
                    <input type="file" id="csvFileError" name="csvFile" accept=".csv" required>
                    <div class="file-input-display error">Déposez un fichier .csv dans le bon format</div>
                </div>
                <span class="error-message" style="display: block; color: #fe3232;">Le format de fichier est invalide</span>
            </div>
            
            <div class="modal-actions">
                <button type="button" class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
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
            <button type="button" class="modal-btn modal-btn-primary" onclick="executeDelete()">Supprimer définitivement</button>
        </div>
    </div>
</div>

<script src="<%= request.getContextPath() %>/static/js/modals.js"></script>
</body>
</html>
