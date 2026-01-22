// Système de gestion des modals

class ModalManager {
    constructor() {
        this.activeModal = null;
        this.init();
    }

    init() {
        // Fermer le modal en cliquant sur l'overlay
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal-overlay')) {
                this.close();
            }
        });

        // Fermer avec la touche Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.activeModal) {
                this.close();
            }
        });

        // Initialiser les dropdowns personnalisés
        this.initDropdowns();
    }

    open(modalId) {
        const overlay = document.getElementById(modalId);
        if (overlay) {
            overlay.classList.add('active');
            this.activeModal = modalId;
            document.body.style.overflow = 'hidden'; // Empêcher le scroll
        }
    }

    close() {
        if (this.activeModal) {
            const overlay = document.getElementById(this.activeModal);
            if (overlay) {
                overlay.classList.remove('active');
            }
            this.activeModal = null;
            document.body.style.overflow = ''; // Restaurer le scroll
        }
    }

    initDropdowns() {
        // Gérer tous les dropdowns personnalisés
        document.addEventListener('click', (e) => {
            const toggle = e.target.closest('.dropdown-toggle');
            
            if (toggle) {
                e.preventDefault();
                e.stopPropagation();
                
                const dropdown = toggle.closest('.custom-dropdown');
                const menu = dropdown.querySelector('.dropdown-menu');
                
                // Fermer tous les autres dropdowns
                document.querySelectorAll('.dropdown-menu.open').forEach(m => {
                    if (m !== menu) {
                        m.classList.remove('open');
                        m.previousElementSibling.classList.remove('open');
                    }
                });
                
                // Toggle le dropdown actuel
                menu.classList.toggle('open');
                toggle.classList.toggle('open');
            } else {
                // Fermer tous les dropdowns si on clique ailleurs
                document.querySelectorAll('.dropdown-menu.open').forEach(menu => {
                    menu.classList.remove('open');
                    menu.previousElementSibling.classList.remove('open');
                });
            }
        });

        // Gérer la sélection dans les dropdowns
        document.addEventListener('click', (e) => {
            const item = e.target.closest('.dropdown-item');
            if (item) {
                const dropdown = item.closest('.custom-dropdown');
                const toggle = dropdown.querySelector('.dropdown-toggle');
                const valueDisplay = toggle.querySelector('.dropdown-value');
                const menu = dropdown.querySelector('.dropdown-menu');
                const hiddenInput = dropdown.querySelector('input[type="hidden"]');
                
                // Mettre à jour la valeur
                const value = item.dataset.value;
                hiddenInput.value = value;
                
                // Mettre à jour l'affichage
                if (item.classList.contains('role-item')) {
                    // Pour les dropdowns de rôles, afficher le badge
                    const roleClass = item.className.split(' ').find(c => c.startsWith('role-'));
                    valueDisplay.innerHTML = `<span class="role-badge-selected ${roleClass}">${item.textContent}</span>`;
                } else {
                    valueDisplay.textContent = item.textContent;
                }
                
                valueDisplay.classList.add('selected');
                
                // Fermer le menu
                menu.classList.remove('open');
                toggle.classList.remove('open');
                
                // Enlever l'état d'erreur si présent
                toggle.classList.remove('error');
            }
        });
    }

    // Gestion du file input personnalisé
    setupFileInput(inputId) {
        const input = document.getElementById(inputId);
        const display = input.closest('.file-input-wrapper').querySelector('.file-input-display');
        
        input.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                display.textContent = e.target.files[0].name;
                display.classList.add('has-file');
                display.classList.remove('error');
            } else {
                display.textContent = 'Déposez un fichier .csv dans le bon format';
                display.classList.remove('has-file');
            }
        });
        
        // Permettre le clic sur le display pour ouvrir le file picker
        display.addEventListener('click', () => {
            input.click();
        });
    }

    // Validation du formulaire de création de compte
    validateCreateAccountForm() {
        let isValid = true;
        const form = document.querySelector('#createAccountModal form');
        
        // Valider tous les champs requis
        const requiredFields = form.querySelectorAll('input[required], select[required]');
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add('error');
                isValid = false;
            } else {
                field.classList.remove('error');
            }
        });
        
        // Valider l'email
        const emailField = form.querySelector('input[type="email"]');
        if (emailField && emailField.value) {
            const emailRegex = /^[^\s@]+@insa-hdf\.fr$/;
            if (!emailRegex.test(emailField.value)) {
                emailField.classList.add('error');
                isValid = false;
            }
        }
        
        return isValid;
    }

    // Validation du formulaire CSV
    validateCSVUpload() {
        const fileInput = document.getElementById('csvFile');
        const display = fileInput.closest('.file-input-wrapper').querySelector('.file-input-display');
        
        if (fileInput.files.length === 0) {
            display.classList.add('error');
            return false;
        }
        
        const file = fileInput.files[0];
        if (!file.name.endsWith('.csv')) {
            display.classList.add('error');
            // Afficher le modal d'erreur
            this.close();
            this.open('csvErrorModal');
            return false;
        }
        
        return true;
    }
}

// Initialiser le gestionnaire de modals
const modalManager = new ModalManager();

// Fonctions globales pour l'utilisation dans le HTML
function openModal(modalId) {
    modalManager.open(modalId);
}

function closeModal() {
    modalManager.close();
}

function submitCreateAccount(event) {
    event.preventDefault();
    
    if (modalManager.validateCreateAccountForm()) {
        // Soumettre le formulaire
        event.target.submit();
    }
}

function submitCSVUpload(event) {
    event.preventDefault();
    
    if (modalManager.validateCSVUpload()) {
        // Soumettre le formulaire
        event.target.submit();
    }
}

function confirmDelete(userId, userName) {
    // Mettre à jour le titre du modal de confirmation
    const modal = document.getElementById('deleteConfirmModal');
    const title = modal.querySelector('.modal-title');
    title.textContent = `Supprimer l'utilisateur ${userName}`;
    
    // Stocker l'ID de l'utilisateur pour la suppression
    modal.dataset.userId = userId;
    
    // Ouvrir le modal
    modalManager.open('deleteConfirmModal');
}

function executeDelete() {
    const modal = document.getElementById('deleteConfirmModal');
    const userId = modal.dataset.userId;
    
    if (userId) {
        // Rediriger vers l'URL de suppression ou soumettre un formulaire
        window.location.href = `${contextPath}/app/admin/supprimer-utilisateur?id=${userId}`;
    }
}

// Initialiser les file inputs au chargement
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('csvFile')) {
        modalManager.setupFileInput('csvFile');
    }
    if (document.getElementById('csvFileError')) {
        modalManager.setupFileInput('csvFileError');
    }
});
