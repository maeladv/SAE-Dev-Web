<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Utilisateur" %>
<%@ page import="java.time.LocalDate" %>
<%
    Utilisateur sessionUser = (Utilisateur) session.getAttribute("utilisateur");
    if (sessionUser == null) {
        response.sendRedirect(request.getContextPath() + "/app/login");
        return;
    }
    
    // Déterminer quel utilisateur afficher
    Utilisateur displayUser = sessionUser;
    Utilisateur utilisateurVu = (Utilisateur) request.getAttribute("utilisateurVu");
    boolean isViewingOther = false;
    if (utilisateurVu != null) {
        displayUser = utilisateurVu;
        isViewingOther = true;
    }
    
    String specialiteTag = "";
    String ine = "";
    try {
        specialiteTag = displayUser.getSpecialiteTag();
        ine = displayUser.getIne();
    } catch (Exception e) {
        // Silencieusement ignorer les erreurs pour les non-étudiants
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon compte - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/moncompte.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/modals.css">
</head>
<body>
<div class="page-container moncompte-page">
    <%@ include file="includes/header.jsp" %>
    
    <main class="moncompte-main">
        <% if (request.getAttribute("error") != null) { %>
            <div class="status-message error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="status-message success"><%= request.getAttribute("success") %></div>
        <% } %>

        <form class="profile-container" id="profileForm" method="post" action="<%= request.getContextPath() %>/app/moncompte">
            <!-- Champ caché pour la date de naissance (non modifiable dans ce formulaire) -->
            <input type="hidden" name="dateNaissance" value="<%= displayUser.getDateNaissance() %>">
            <% if (isViewingOther) { %>
            <!-- Champ caché pour indiquer quel utilisateur on modifie -->
            <input type="hidden" name="idUtilisateur" value="<%= displayUser.getId() %>">
            <% } %>
            
            <!-- Colonne Gauche: Votre GradiCompte -->
            <section class="profile-sidebar">
                <h2 class="sidebar-title">Votre GradiCompte</h2>
                
                <div class="profile-card">
                    <div class="profile-header">
                        <div class="profile-avatar">
                            <svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <circle cx="30" cy="30" r="30" fill="#E8E8E8"/>
                                <circle cx="30" cy="22" r="8" fill="#999"/>
                                <ellipse cx="30" cy="42" rx="12" ry="10" fill="#999"/>
                            </svg>
                        </div>
                        <div class="profile-info">
                            <p class="profile-name"><%= displayUser.getPrenom() %> <%= displayUser.getNom() %></p>
                            <span class="role-badge role-<%= displayUser.getRole().toLowerCase() %>">
                                <%= displayUser.getRole().toUpperCase() %>
                            </span>
                        </div>
                    </div>

                    <div class="profile-actions">
                        <% if ("etudiant".equalsIgnoreCase(displayUser.getRole())) { %>
                            <a href="<%= request.getContextPath() %>/app/etudiant/evaluations" class="action-btn">
                                Notes
                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M7 10H13M13 10L10 7M13 10L10 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                            </a>
                        <% } else if ("professeur".equalsIgnoreCase(displayUser.getRole())) { %>
                            <a href="<%= request.getContextPath() %>/app/gestion/specialites" class="action-btn">
                                Mes matières
                                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M7 10H13M13 10L10 7M13 10L10 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                            </a>
                        <% } %>
                        <button type="button" class="action-btn secondary">
                            Demander mes informations [RGPD]
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M7 10H13M13 10L10 7M13 10L10 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                        </button>
                        <button type="button" class="action-btn danger"onclick="confirmDelete(<%= displayUser.getId() %>, '<%= displayUser.getPrenom() %> <%= displayUser.getNom() %>')">
                            Supprimer mon compte
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M3 5H17M8 5V3H12V5M8 9V16H12V9M8 9H12M5 5H15L14 17C14 17.5304 13.7893 18.0391 13.4142 18.4142C13.0391 18.7893 12.5304 19 12 19H8C7.46957 19 6.96086 18.7893 6.58579 18.4142C6.21071 18.0391 6 17.5304 6 17L5 5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
                            </svg>
                        </button>
                    </div>
                </div>
            </section>

            <!-- Colonne Milieu: Informations personnelles -->
            <section class="profile-section">
                <h2 class="section-title">Vos informations personnelles</h2>
                
                <div class="form-group">
                    <label class="field-label" for="nom">Nom de famille</label>
                    <input class="input-field" id="nom" type="text" name="nom" 
                           value="<%= displayUser.getNom() %>" required>
                </div>

                <div class="form-group">
                    <label class="field-label" for="prenom">Prénom</label>
                    <input class="input-field" id="prenom" type="text" name="prenom" 
                           value="<%= displayUser.getPrenom() %>" required>
                </div>

                <div class="form-group">
                    <label class="field-label" for="email">Adresse mail</label>
                    <input class="input-field" id="email" type="email" name="email" 
                           value="<%= displayUser.getemail() %>" required>
                </div>

                <button type="submit" class="btn btn-primary btn-icon-right" id="savePersonalBtn">
                    Enregistrer les modifications
                    <img src="<%= request.getContextPath() %>/static/icons/white/floppy-disk.svg" width="20" height="20" alt="">
                </button>
            </section>

            <!-- Colonne Droite: Sécurité -->
            <section class="profile-section security">
                <h2 class="section-title">Sécurité de votre compte</h2>
                
                <% if (isViewingOther) { %>
                    <!-- Admin ne peut pas changer le mdp, mais peut copier le lien de réinitialisation -->
                    <p style="margin-bottom: 1rem; color: #666;">En tant qu'administrateur, vous ne pouvez pas modifier le mot de passe d'un autre utilisateur. Vous pouvez cependant générer un lien de réinitialisation.</p>
                    <button type="button" class="btn btn-secondary btn-icon-right" onclick="copyResetLinkForUser(<%= displayUser.getId() %>)">
                        Copier le lien de réinitialisation
                        <img src="<%= request.getContextPath() %>/static/icons/black/link.svg" width="20" height="20" alt="">
                    </button>
                <% } else { %>
                    <div class="form-group">
                        <label class="field-label" for="motDePasse">Nouveau Mot de Passe</label>
                        <input class="input-field" id="motDePasse" type="password" name="motDePasse" 
                               placeholder="Rentrez un nouveau mot de passe">
                    </div>

                    <div class="form-group" id="confirmPasswordGroup">
                        <label class="field-label" for="confirmPassword">Confirmez votre nouveau Mot de Passe</label>
                        <input class="input-field" id="confirmPassword" type="password" name="confirmPassword" 
                               placeholder="Rentrez à nouveau votre nouveau mot de passe">
                    </div>

                    <button type="submit" class="btn btn-primary btn-icon-right" id="savePasswordBtn">
                        Changer le mot de passe
                        <img src="<%= request.getContextPath() %>/static/icons/white/floppy-disk.svg" width="20" height="20" alt="">
                    </button>
                <% } %>
            </section>

            <!-- Colonne Bas: Champs spécifiques -->
            <section class="profile-section specific-fields">
                <h2 class="section-title">Champs spécifiques</h2>
                
                <% if ("etudiant".equalsIgnoreCase(displayUser.getRole())) { %>
                    <div class="form-group">
                        <label class="field-label" for="ine">INE</label>
                        <input class="input-field readonly-field" id="ine" type="text" name="ine" 
                               value="<%= ine %>" readonly>
                    </div>

                    <div class="form-group">
                        <label class="field-label">Spécialité</label>
                        <%
                            String specialiteClass = "";
                            if (specialiteTag != null && !specialiteTag.isEmpty() && !specialiteTag.equals("-")) {
                                specialiteClass = "specialite-badge spe-" + specialiteTag.toLowerCase();
                        %>
                            <div class="specialite-display">
                                <span class="<%= specialiteClass %>"><%= specialiteTag.toUpperCase() %></span>
                            </div>
                        <%
                            } else {
                        %>
                            <div class="specialite-display">
                                <span class="no-specialite">Aucune spécialité</span>
                            </div>
                        <%
                            }
                        %>
                    </div>
                <% } else if ("professeur".equalsIgnoreCase(displayUser.getRole())) { %>
                    <p class="no-specific-fields">Aucun champ spécifique pour les professeurs</p>
                <% } else if ("admin".equalsIgnoreCase(displayUser.getRole())) { %>
                    <p class="no-specific-fields">Aucun champ spécifique pour les administrateurs</p>
                <% } %>
            </section>
        </form>
    </main>
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

<script>
const contextPath = '<%= request.getContextPath() %>';

function copyResetLinkForUser(idUtilisateur) {
    fetch(contextPath + '/app/admin/get-reset-link?idUtilisateur=' + encodeURIComponent(idUtilisateur))
    .then(response => response.text())
    .then(text => {
        try {
            const data = JSON.parse(text);
            if (data.success && data.link) {
                const link = data.link;
                
                navigator.clipboard.writeText(link).then(() => {
                    showNotification('Lien de réinitialisation copié dans le presse-papiers', 'success');
                }).catch(() => {
                    const textArea = document.createElement('textarea');
                    textArea.value = link;
                    document.body.appendChild(textArea);
                    textArea.select();
                    document.execCommand('copy');
                    document.body.removeChild(textArea);
                    showNotification('Lien de réinitialisation copié', 'success');
                });
            } else {
                showNotification(data.message || 'Impossible de générer le lien', 'error');
            }
        } catch (parseError) {
            showNotification('Erreur: réponse invalide du serveur', 'error');
        }
    })
    .catch(error => {
        showNotification('Erreur réseau: ' + error.message, 'error');
    });
}

function showNotification(message, type = 'info') {
    let notificationContainer = document.getElementById('notification-container');
    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'notification-container';
        notificationContainer.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 10000;';
        document.body.appendChild(notificationContainer);
    }

    const notification = document.createElement('div');
    notification.className = 'notification notification-' + type;
    
    const backgroundColor = type === 'success' ? '#4CAF50' : type === 'error' ? '#f44336' : '#2196F3';
    notification.style.cssText = `
        background: ${backgroundColor};
        color: white;
        padding: 16px;
        margin-bottom: 10px;
        border-radius: 4px;
        box-shadow: 0 2px 5px rgba(0,0,0,0.2);
        min-width: 300px;
        animation: slideIn 0.3s ease-in-out;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    `;
    notification.textContent = message;
    notificationContainer.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-in-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
</script>
<script src="<%= request.getContextPath() %>/static/js/moncompte.js"></script>
</body>
</html>
