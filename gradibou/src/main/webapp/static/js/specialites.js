// ==================== Spécialités Management ====================

let currentDeleteId = null;
let currentDeleteTag = null;
let currentDeleteAnnee = null;

/**
 * Affiche une notification toast
 */
function showNotification(message, type = "info") {
  // Créer le conteneur si nécessaire
  let notificationContainer = document.getElementById("notification-container");
  if (!notificationContainer) {
    notificationContainer = document.createElement("div");
    notificationContainer.id = "notification-container";
    notificationContainer.style.cssText =
      "position: fixed; top: 20px; right: 20px; z-index: 10000;";
    document.body.appendChild(notificationContainer);
  }

  const notification = document.createElement("div");
  notification.className = "notification notification-" + type;

  const backgroundColor =
    type === "success" ? "#4CAF50" : type === "error" ? "#f44336" : "#2196F3";
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
    notification.style.animation = "slideOut 0.3s ease-in-out";
    setTimeout(() => notification.remove(), 300);
  }, 3000);
}

// Ajouter les animations CSS pour les notifications
if (!document.getElementById("notification-animations")) {
  const style = document.createElement("style");
  style.id = "notification-animations";
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
}

/**
 * Ouvrir le modal de modification de spécialité
 */
function openEditSpecialiteModal(id, nom, tag, annee) {
  document.getElementById("edit-id").value = id;
  document.getElementById("edit-nom").value = nom;
  document.getElementById("edit-tag").value = tag;
  document.getElementById("edit-annee").value = annee;
  openModal("editSpecialiteModal");
}

/**
 * Confirmer la suppression d'une spécialité
 */
function confirmDeleteSpecialite(id, tag, annee) {
  currentDeleteId = id;
  currentDeleteTag = tag;
  currentDeleteAnnee = annee;

  const title = document.getElementById("delete-title");
  title.textContent = `Supprimer la spécialité ${tag.toUpperCase()} ${annee}A ?`;

  openModal("deleteSpecialiteModal");
}

/**
 * Soumettre le formulaire de création de spécialité
 */
function submitCreateSpecialite(event) {
  event.preventDefault();

  const form = event.target;
  const formData = new FormData(form);

  // Validation
  const nom = formData.get("nom");
  const tag = formData.get("tag");
  const annee = formData.get("annee");

  if (!nom || !tag || !annee) {
    showFormError(form, "Tous les champs sont requis");
    return false;
  }

  if (annee < 1 || annee > 5) {
    showFormError(form, "L'année doit être entre 1 et 5");
    return false;
  }

  // Envoi du formulaire
  fetch(`${contextPath}/app/admin/creer-specialite`, {
    method: "POST",
    body: new URLSearchParams(formData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showSuccessMessage(
          form,
          data.message || "Spécialité créée avec succès",
        );
        setTimeout(() => {
          window.location.href = `${contextPath}/app/gestion/specialites`;
        }, 1000);
      } else {
        showFormError(form, data.message || "Erreur lors de la création");
      }
    })
    .catch((error) => {
      console.error("Erreur:", error);
      showFormError(form, "Une erreur est survenue");
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
  const nom = formData.get("nom");
  const tag = formData.get("tag");

  if (!nom) {
    showFormError(form, "Le nom est requis");
    return false;
  }
  if (!tag) {
    showFormError(form, "Le tag est requis");
    return false;
  }

  // Envoi du formulaire
  fetch(`${contextPath}/app/admin/modifier-specialite`, {
    method: "POST",
    body: new URLSearchParams(formData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showSuccessMessage(
          form,
          data.message || "Spécialité modifiée avec succès",
        );
        setTimeout(() => {
          window.location.href = `${contextPath}/app/gestion/specialites`;
        }, 1000);
      } else {
        showFormError(form, data.message || "Erreur lors de la modification");
      }
    })
    .catch((error) => {
      console.error("Erreur:", error);
      showFormError(form, "Une erreur est survenue");
    });

  return false;
}

/**
 * Exécuter la suppression d'une spécialité
 */
function executeDeleteSpecialite() {
  if (!currentDeleteId) {
    console.error("Aucun ID de spécialité à supprimer");
    return;
  }

  const formData = new URLSearchParams();
  formData.append("id", currentDeleteId);

  fetch(`${contextPath}/app/admin/supprimer-specialite`, {
    method: "POST",
    body: formData,
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        closeModal();
        window.location.href = `${contextPath}/app/gestion/specialites`;
      } else {
        showNotification(
          data.message || "Erreur lors de la suppression",
          "error",
        );
      }
    })
    .catch((error) => {
      console.error("Erreur:", error);
      showNotification(
        "Une erreur est survenue lors de la suppression",
        "error",
      );
    });
}
