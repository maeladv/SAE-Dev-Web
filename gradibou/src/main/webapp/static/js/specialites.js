// ==================== Spécialités Management ====================

let currentDeleteId = null;
let currentDeleteTag = null;
let currentDeleteAnnee = null;

/**
 * Ouvrir le modal de modification de spécialité
 */
function openEditSpecialiteModal(id, nom, tag, annee) {
    document.getElementById('edit-id').value = id;
    document.getElementById('edit-nom').value = nom;
    openModal('editSpecialiteModal');
}

/**
 * Confirmer la suppression d'une spécialité
 */
function confirmDeleteSpecialite(id, tag, annee) {
    currentDeleteId = id;
    currentDeleteTag = tag;
    currentDeleteAnnee = annee;
    
    const title = document.getElementById('delete-title');
    title.textContent = `Supprimer la spécialité ${tag.toUpperCase()} ${annee}A ?`;
    
    openModal('deleteSpecialiteModal');
}

/**
 * Soumettre le formulaire de création de spécialité
 */
function submitCreateSpecialite(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    
    // Validation
    const nom = formData.get('nom');
    const tag = formData.get('tag');
    const annee = formData.get('annee');
    
    if (!nom || !tag || !annee) {
        showError(form, 'Tous les champs sont requis');
        return false;
    }
    
    if (annee < 1 || annee > 5) {
        showError(form, 'L\'année doit être entre 1 et 5');
        return false;
    }
    
    // Envoi du formulaire
    fetch(`${contextPath}/app/admin/creer-specialite`, {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showSuccess(form, data.message || 'Spécialité créée avec succès');
            setTimeout(() => {
                window.location.href = `${contextPath}/app/gestion/specialites`;
            }, 1000);
        } else {
            showError(form, data.message || 'Erreur lors de la création');
        }
    })
    .catch(error => {
        console.error('Erreur:', error);
        showError(form, 'Une erreur est survenue');
    });
    
    return false;
}

/**
 * Soumettre le formulaire de modification de spécialité
 */
function submitEditSpecialite(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    
    // Validation
    const nom = formData.get('nom');
    
    if (!nom) {
        showError(form, 'Le nom est requis');
        return false;
    }
    
    // Envoi du formulaire
    fetch(`${contextPath}/app/admin/modifier-specialite`, {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showSuccess(form, data.message || 'Spécialité modifiée avec succès');
            setTimeout(() => {
                window.location.href = `${contextPath}/app/gestion/specialites`;
            }, 1000);
        } else {
            showError(form, data.message || 'Erreur lors de la modification');
        }
    })
    .catch(error => {
        console.error('Erreur:', error);
        showError(form, 'Une erreur est survenue');
    });
    
    return false;
}

/**
 * Exécuter la suppression d'une spécialité
 */
function executeDeleteSpecialite() {
    if (!currentDeleteId) {
        console.error('Aucun ID de spécialité à supprimer');
        return;
    }
    
    const formData = new URLSearchParams();
    formData.append('id', currentDeleteId);
    
    fetch(`${contextPath}/app/admin/supprimer-specialite`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            closeModal();
            window.location.href = `${contextPath}/app/gestion/specialites`;
        } else {
            alert(data.message || 'Erreur lors de la suppression');
        }
    })
    .catch(error => {
        console.error('Erreur:', error);
        alert('Une erreur est survenue lors de la suppression');
    });
}

/**
 * Afficher un message d'erreur dans le formulaire
 */
function showError(form, message) {
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
 * Afficher un message de succès dans le formulaire
 */
function showSuccess(form, message) {
    let successContainer = form.querySelector('.form-success-message');
    if (!successContainer) {
        successContainer = document.createElement('div');
        successContainer.className = 'form-success-message';
        form.insertBefore(successContainer, form.firstChild);
    }
    
    successContainer.innerHTML = `<p style="color: #2d65c6; padding: 12px; background-color: rgba(45, 101, 198, 0.1); border-radius: 8px; margin-bottom: 16px;">${message}</p>`;
    successContainer.style.display = 'block';
}
