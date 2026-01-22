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
            
            // Initialiser les dropdowns du modal
            setTimeout(() => initializeModalDropdowns(), 100);
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
    event.stopPropagation();
    
    const form = event.target;
    
    // Récupérer la valeur du dropdown de rôle
    const modal = form.closest('.modal');
    const roleDropdown = modal ? modal.querySelector('.dropdown[data-dropdown="modal-role"]') : null;
    const roleValue = roleDropdown?.dataset.value;
    
    console.log('Tentative soumission - Role value:', roleValue);
    
    if (!roleValue) {
        showFormError(form, "Veuillez sélectionner un rôle");
        return false;
    }
    
    // Vérifier les champs obligatoires
    if (!form.prenom.value || !form.nom.value || !form.email.value || !form.dateNaissance.value) {
        showFormError(form, "Tous les champs obligatoires doivent être complétés");
        return false;
    }
    
    // Créer URLSearchParams pour x-www-form-urlencoded
    const params = new URLSearchParams();
    params.append('nom', form.nom.value);
    params.append('prenom', form.prenom.value);
    params.append('email', form.email.value);
    params.append('dateNaissance', form.dateNaissance.value);
    params.append('role', roleValue);
    params.append('ine', form.ine.value || '');
    params.append('specialite', form.specialite.value || '');
    
    console.log('Données envoyées:', {
        nom: form.nom.value,
        prenom: form.prenom.value,
        email: form.email.value,
        dateNaissance: form.dateNaissance.value,
        role: roleValue,
        ine: form.ine.value || '',
        specialite: form.specialite.value || ''
    });
    
    // Afficher un état de chargement
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Création en cours...';
    
    // Envoyer la requête AJAX avec x-www-form-urlencoded
    fetch(`${contextPath}/app/admin/creer-utilisateur`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(response => {
        console.log('Status:', response.status);
        return response.text().then(text => {
            console.log('Response text:', text);
            try {
                return { status: response.status, data: JSON.parse(text) };
            } catch (e) {
                console.error('Erreur parsing JSON:', e);
                throw new Error('Réponse non-JSON du serveur');
            }
        });
    })
    .then(({ status, data }) => {
        console.log('Data reçue:', data);
        
        if (data.success) {
            showSuccessMessage(form, "Compte créé avec succès!");
            setTimeout(() => {
                form.reset();
                if (roleDropdown) {
                    roleDropdown.dataset.value = '';
                    roleDropdown.querySelector('.dropdown-label').textContent = 'Choisir un rôle';
                }
                closeModal();
                location.reload();
            }, 1500);
        } else {
            showFormError(form, data.message || "Une erreur est survenue lors de la création du compte");
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    })
    .catch(error => {
        console.error('Erreur AJAX:', error);
        showFormError(form, error.message || "Une erreur est survenue lors de la création du compte");
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    });
    
    return false;
}

/**
 * Affiche un message d'erreur dans le formulaire du modal
 */
function showFormError(form, message) {
    let errorContainer = form.querySelector('.form-error-message');
    if (!errorContainer) {
        errorContainer = document.createElement('div');
        errorContainer.className = 'form-error-message';
        form.insertBefore(errorContainer, form.firstChild);
    }
    
    errorContainer.innerHTML = `<p style="color: #fe3232; padding: 12px; background-color: rgba(254, 50, 50, 0.1); border-radius: 8px; margin-bottom: 16px;">${message}</p>`;
    errorContainer.style.display = 'block';
}

/**
 * Affiche un message de succès dans le formulaire du modal
 */
function showSuccessMessage(form, message) {
    let successContainer = form.querySelector('.form-success-message');
    if (!successContainer) {
        successContainer = document.createElement('div');
        successContainer.className = 'form-success-message';
        form.insertBefore(successContainer, form.firstChild);
    }
    
    successContainer.innerHTML = `<p style="color: #2d65c6; padding: 12px; background-color: rgba(45, 101, 198, 0.1); border-radius: 8px; margin-bottom: 16px;">${message}</p>`;
    successContainer.style.display = 'block';
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

/**
 * Initialise les dropdowns personnalisés du modal
 */
function initializeModalDropdowns() {
    const modal = document.querySelector('.modal-overlay.active .modal');
    if (!modal) return;
    
    const dropdowns = modal.querySelectorAll('.dropdown[data-dropdown="modal-role"]');
    
    dropdowns.forEach(dropdown => {
        const toggle = dropdown.querySelector('.dropdown-toggle');
        const options = dropdown.querySelectorAll('.dropdown-option');
        
        if (!toggle || !options.length) return;
        
        // Ajouter event listener au toggle
        toggle.addEventListener('click', (e) => {
            e.stopPropagation();
            const isOpen = dropdown.classList.contains('open');
            
            // Fermer tous les autres
            modal.querySelectorAll('.dropdown').forEach(d => {
                if (d !== dropdown) d.classList.remove('open');
            });
            
            if (!isOpen) {
                dropdown.classList.add('open');
            } else {
                dropdown.classList.remove('open');
            }
        });
        
        // Ajouter event listeners aux options
        options.forEach(option => {
            option.addEventListener('click', (e) => {
                e.stopPropagation();
                const value = option.getAttribute('data-value');
                const label = option.getAttribute('data-label') || option.textContent.trim();
                const role = option.getAttribute('data-role');
                
                const labelSpan = dropdown.querySelector('.dropdown-label');
                if (labelSpan) {
                    if (role) {
                        labelSpan.innerHTML = `<span class="role-badge role-${role}">${label.toUpperCase()}</span>`;
                    } else {
                        labelSpan.textContent = label;
                    }
                }
                
                dropdown.dataset.value = value;
                dropdown.classList.remove('open');
            });
        });
    });
    
    // Fermer les dropdowns si on clique ailleurs
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.dropdown')) {
            modal.querySelectorAll('.dropdown').forEach(d => d.classList.remove('open'));
        }
    });
}
