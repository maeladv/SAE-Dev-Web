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
});
