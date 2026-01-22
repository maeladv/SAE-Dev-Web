// Gestion du tri des colonnes du tableau
let currentSortColumn = null;
let currentSortDirection = 'asc'; // 'asc' ou 'desc'

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
