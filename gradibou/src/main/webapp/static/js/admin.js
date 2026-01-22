// Gestion du tri des colonnes du tableau
let currentSortColumn = null;
let currentSortDirection = 'asc'; // 'asc' ou 'desc'

/**
 * Affiche une notification toast
 */
function showNotification(message, type = 'info') {
    // Créer le conteneur si nécessaire
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

    // Supprimer après 3 secondes
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-in-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Ajouter les animations CSS pour les notifications
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

/**
 * Trie le tableau en fonction de la colonne sélectionnée
 * @param {number} columnIndex - L'index de la colonne à trier (0-based)
 * @param {string} columnType - Le type de données ('text', 'number')
 */
function sortTable(columnIndex, columnType = 'text') {
    const table = document.getElementById('usersTableBody');
    const rows = Array.from(table.getElementsByTagName('tr'));
    
    // Déterminer la direction du tri
    if (currentSortColumn === columnIndex) {
        // Alterner entre croissant et décroissant
        currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
    } else {
        // Nouvelle colonne, commencer par croissant
        currentSortColumn = columnIndex;
        currentSortDirection = 'asc';
    }
    
    // Trier les lignes
    rows.sort((rowA, rowB) => {
        const cellA = rowA.getElementsByTagName('td')[columnIndex];
        const cellB = rowB.getElementsByTagName('td')[columnIndex];
        
        // Extraire le texte (en gérant les badges et spans)
        let valueA = cellA.textContent.trim();
        let valueB = cellB.textContent.trim();
        
        // Gérer les valeurs vides ("-")
        if (valueA === '-') valueA = '';
        if (valueB === '-') valueB = '';
        
        let comparison = 0;
        
        if (columnType === 'number') {
            // Comparaison numérique
            const numA = parseInt(valueA) || 0;
            const numB = parseInt(valueB) || 0;
            comparison = numA - numB;
        } else {
            // Comparaison textuelle (insensible à la casse)
            comparison = valueA.toLowerCase().localeCompare(valueB.toLowerCase());
        }
        
        return currentSortDirection === 'asc' ? comparison : -comparison;
    });
    
    // Réorganiser les lignes dans le tableau
    rows.forEach(row => table.appendChild(row));
    
    // Mettre à jour les indicateurs visuels
    updateSortIndicators(columnIndex);
}

/**
 * Met à jour les indicateurs visuels de tri dans les en-têtes
 * @param {number} activeColumnIndex - L'index de la colonne actuellement triée
 */
function updateSortIndicators(activeColumnIndex) {
    const headers = document.querySelectorAll('.users-table thead th.sortable');
    
    headers.forEach((header, index) => {
        const indicator = header.querySelector('.sort-indicator');
        if (indicator) {
            if (index === activeColumnIndex) {
                // Afficher l'indicateur approprié
                indicator.textContent = currentSortDirection === 'asc' ? '▲' : '▼';
                indicator.style.opacity = '1';
            } else {
                // Masquer l'indicateur
                indicator.textContent = '▲';
                indicator.style.opacity = '0.3';
            }
        }
    });
}

// Initialiser les indicateurs de tri au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    const headers = document.querySelectorAll('.users-table thead th.sortable');
    headers.forEach(header => {
        const indicator = header.querySelector('.sort-indicator');
        if (indicator) {
            indicator.style.opacity = '0.3';
        }
    });

    initDropdowns();
});

/**
 * Initialisation des dropdowns personnalisés (rôle et spécialité)
 */
function initDropdowns() {
    const dropdowns = document.querySelectorAll('.dropdown');

    dropdowns.forEach(dropdown => {
        const toggle = dropdown.querySelector('.dropdown-toggle');
        const menu = dropdown.querySelector('.dropdown-menu');
        const options = dropdown.querySelectorAll('.dropdown-option');

        if (!toggle || !menu || !options.length) return;

        toggle.addEventListener('click', event => {
            event.stopPropagation();
            const isOpen = dropdown.classList.contains('open');
            closeAllDropdowns();
            if (!isOpen) {
                dropdown.classList.add('open');
                toggle.setAttribute('aria-expanded', 'true');
            }
        });

        options.forEach(option => {
            option.addEventListener('click', event => {
                event.stopPropagation();
                selectDropdownOption(dropdown, option);
                applyFilters();
            });
        });
    });

    document.addEventListener('click', closeAllDropdowns);
    document.addEventListener('keydown', event => {
        if (event.key === 'Escape') closeAllDropdowns();
    });
}

/**
 * Sélection d'une option dans un dropdown
 * @param {HTMLElement} dropdown
 * @param {HTMLElement} option
 */
function selectDropdownOption(dropdown, option) {
    const toggle = dropdown.querySelector('.dropdown-toggle');
    const labelSpan = dropdown.querySelector('.dropdown-label');
    const value = option.getAttribute('data-value');
    const label = option.getAttribute('data-label') || option.textContent.trim();
    const role = option.getAttribute('data-role');

    if (!toggle || !labelSpan) return;

    if (role) {
        // Affichage d'un pill coloré pour les rôles
        labelSpan.innerHTML = `<span class="role-badge role-${role}">${label.toUpperCase()}</span>`;
    } else {
        labelSpan.textContent = label;
    }

    dropdown.dataset.value = value;
    closeAllDropdowns();
}

/**
 * Applique les filtres rôle / spécialité sur les lignes du tableau
 */
function applyFilters() {
    const roleFilter = document.querySelector('.dropdown[data-dropdown="role"]')?.dataset.value || '';
    const speFilter = document.querySelector('.dropdown[data-dropdown="specialite"]')?.dataset.value || '';

    const rows = document.querySelectorAll('#usersTableBody tr');

    rows.forEach(row => {
        const cells = row.getElementsByTagName('td');
        const roleCell = cells[3];
        const speCell = cells[6];

        const roleText = roleCell ? roleCell.textContent.trim().toLowerCase() : '';
        const speText = speCell ? speCell.textContent.trim().toLowerCase() : '';

        const roleMatch = !roleFilter || roleText.includes(roleFilter);
        const speMatch = !speFilter || speText.includes(speFilter);

        row.style.display = roleMatch && speMatch ? '' : 'none';
    });
}

/**
 * Réinitialise les filtres rôle / spécialité et affiche toutes les lignes
 */
function resetFilters() {
    const roleDropdown = document.querySelector('.dropdown[data-dropdown="role"]');
    const speDropdown = document.querySelector('.dropdown[data-dropdown="specialite"]');

    if (roleDropdown) {
        roleDropdown.dataset.value = '';
        const roleLabel = roleDropdown.querySelector('.dropdown-label');
        if (roleLabel) roleLabel.textContent = 'Choisir un rôle';
    }

    if (speDropdown) {
        speDropdown.dataset.value = '';
        const speLabel = speDropdown.querySelector('.dropdown-label');
        if (speLabel) speLabel.textContent = 'Choisir une spécialité';
    }

    closeAllDropdowns();
    applyFilters();
}

/**
 * Ferme tous les dropdowns ouverts
 */
function closeAllDropdowns() {
    const dropdowns = document.querySelectorAll('.dropdown');
    dropdowns.forEach(dropdown => {
        const toggle = dropdown.querySelector('.dropdown-toggle');
        dropdown.classList.remove('open');
        if (toggle) {
            toggle.setAttribute('aria-expanded', 'false');
        }
    });
}

// ==================== Gestion des utilisateurs ====================

/**
 * Soumet le formulaire de création de compte
 */
function submitCreateAccount(event) {
    event.preventDefault();
    
    const nom = document.getElementById('nom').value.trim();
    const prenom = document.getElementById('prenom').value.trim();
    const email = document.getElementById('email').value.trim();
    const role = document.querySelector('.dropdown[data-dropdown="modal-role"]')?.dataset.value;
    
    if (!nom || !prenom || !email || !role) {
        showNotification('Veuillez remplir tous les champs obligatoires', 'error');
        return false;
    }
    
    const params = new URLSearchParams();
    params.append('nom', nom);
    params.append('prenom', prenom);
    params.append('email', email);
    params.append('role', role);
    
    fetch(contextPath + '/app/admin/creer-utilisateur', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(response => response.text())
    .then(text => {
        try {
            const data = JSON.parse(text);
            if (data.success) {
                closeModal();
                showNotification(data.message || 'Utilisateur créé avec succès', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                showNotification(data.message || 'Erreur lors de la création', 'error');
            }
        } catch (parseError) {
            console.error('Erreur de parsing JSON:', parseError, 'Texte reçu:', text);
            showNotification('Erreur: réponse invalide du serveur', 'error');
        }
    })
    .catch(error => {
        console.error('Erreur réseau:', error);
        showNotification('Erreur réseau: ' + error.message, 'error');
    });
    
    return false;
}

/**
 * Édite un utilisateur existant
 */
function editUser(userId, nom, prenom, email, role) {
    document.getElementById('editUserId').value = userId;
    document.getElementById('editNom').value = nom;
    document.getElementById('editPrenom').value = prenom;
    document.getElementById('editEmail').value = email;
    
    // Sélectionner le rôle dans le dropdown
    const roleDropdown = document.querySelector('.dropdown[data-dropdown="modal-edit-role"]');
    const roleOption = roleDropdown?.querySelector(`[data-value="${role}"]`);
    if (roleOption) {
        selectDropdownOption(roleDropdown, roleOption);
    }
    
    openModal('editAccountModal');
}

/**
 * Soumet le formulaire de modification d'utilisateur
 */
function submitEditAccount(event) {
    event.preventDefault();
    
    const userId = document.getElementById('editUserId').value;
    const nom = document.getElementById('editNom').value.trim();
    const prenom = document.getElementById('editPrenom').value.trim();
    const email = document.getElementById('editEmail').value.trim();
    const role = document.querySelector('.dropdown[data-dropdown="modal-edit-role"]')?.dataset.value;
    
    if (!userId || !nom || !prenom || !email || !role) {
        showNotification('Veuillez remplir tous les champs obligatoires', 'error');
        return false;
    }
    
    const params = new URLSearchParams();
    params.append('id', userId);
    params.append('nom', nom);
    params.append('prenom', prenom);
    params.append('email', email);
    params.append('role', role);
    
    fetch(contextPath + '/app/admin/modifier-utilisateur', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(response => response.text())
    .then(text => {
        try {
            const data = JSON.parse(text);
            if (data.success) {
                closeModal();
                showNotification(data.message || 'Utilisateur modifié avec succès', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                showNotification(data.message || 'Erreur lors de la modification', 'error');
            }
        } catch (parseError) {
            console.error('Erreur de parsing JSON:', parseError, 'Texte reçu:', text);
            showNotification('Erreur: réponse invalide du serveur', 'error');
        }
    })
    .catch(error => {
        console.error('Erreur réseau:', error);
        showNotification('Erreur réseau: ' + error.message, 'error');
    });
    
    return false;
}

let userToDelete = null;

/**
 * Confirme la suppression d'un utilisateur
 */
function confirmDelete(userId, userName) {
    userToDelete = userId;
    openModal('deleteConfirmModal');
}

/**
 * Exécute la suppression d'un utilisateur
 */
function executeDelete() {
    if (!userToDelete) return;
    
    const params = new URLSearchParams();
    params.append('id', userToDelete);
    
    fetch(contextPath + '/app/admin/supprimer-utilisateur', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(response => response.text())
    .then(text => {
        try {
            const data = JSON.parse(text);
            if (data.success) {
                closeModal();
                showNotification(data.message || 'Utilisateur supprimé avec succès', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                showNotification(data.message || 'Erreur lors de la suppression', 'error');
            }
        } catch (parseError) {
            console.error('Erreur de parsing JSON:', parseError, 'Texte reçu:', text);
            showNotification('Erreur: réponse invalide du serveur', 'error');
        }
    })
    .catch(error => {
        console.error('Erreur réseau:', error);
        showNotification('Erreur réseau: ' + error.message, 'error');
    });
}

/**
 * Copie le lien de réinitialisation du mot de passe
 */
function copyResetLink(userId) {
    fetch(contextPath + '/app/admin/get-reset-link?userId=' + encodeURIComponent(userId))
    .then(response => response.text())
    .then(text => {
        try {
            const data = JSON.parse(text);
            if (data.success && data.link) {
                const link = data.link;
                
                // Copier le lien dans le presse-papiers
                navigator.clipboard.writeText(link).then(() => {
                    showNotification('Lien de réinitialisation copié dans le presse-papiers', 'success');
                }).catch(() => {
                    // Fallback si clipboard.writeText échoue
                    const textArea = document.createElement('textarea');
                    textArea.value = link;
                    document.body.appendChild(textArea);
                    textArea.select();
                    document.execCommand('copy');
                    document.body.removeChild(textArea);
                    showNotification('Lien de réinitialisation copié (fallback)', 'success');
                });
            } else {
                showNotification(data.message || 'Impossible de générer le lien', 'error');
            }
        } catch (parseError) {
            console.error('Erreur de parsing JSON:', parseError, 'Texte reçu:', text);
            showNotification('Erreur: réponse invalide du serveur', 'error');
        }
    })
    .catch(error => {
        console.error('Erreur réseau:', error);
        showNotification('Erreur réseau: ' + error.message, 'error');
    });
}
