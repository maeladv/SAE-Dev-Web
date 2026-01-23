// Mon Compte - JavaScript

document.addEventListener("DOMContentLoaded", function () {
  const profileForm = document.getElementById("profileForm");
  const motDePasseField = document.getElementById("motDePasse");
  const confirmPasswordField = document.getElementById("confirmPassword");

  // Validation et soumission
  profileForm.addEventListener("submit", function (e) {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    // Soumettre le formulaire
    this.submit();
  });

  function validateForm() {
    const nom = document.getElementById("nom").value.trim();
    const prenom = document.getElementById("prenom").value.trim();
    const email = document.getElementById("email").value.trim();

    if (!nom || !prenom || !email) {
      alert("Veuillez remplir tous les champs requis");
      return false;
    }

    // Vérifier que l'email est valide
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      alert("Veuillez entrer une adresse email valide");
      return false;
    }

    // Vérifier les mots de passe s'ils sont remplis
    const motDePasse = motDePasseField.value;
    if (motDePasse) {
      const confirmPassword = confirmPasswordField.value;

      if (motDePasse.length < 6) {
        alert("Le mot de passe doit contenir au moins 6 caractères");
        return false;
      }

      if (motDePasse !== confirmPassword) {
        alert("Les mots de passe ne correspondent pas");
        return false;
      }
    }

    return true;
  }
});

/**
 * Confirme la suppression d'un utilisateur
 */
function confirmDelete(idUtilisateur, userName) {
  userToDelete = idUtilisateur;
  openModal("deleteConfirmModal");
}

/**
 * Exécute la suppression d'un utilisateur
 */
function executeDelete() {
  if (!userToDelete) return;

  const params = new URLSearchParams();
  params.append("id", userToDelete);

  fetch(contextPath + "/app/admin/supprimer-utilisateur", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: params.toString(),
  })
    .then((response) => response.text())
    .then((text) => {
      try {
        const data = JSON.parse(text);
        if (data.success) {
          closeModal();
          showNotification(
            data.message || "Utilisateur supprimé avec succès",
            "success",
          );
          setTimeout(() => location.reload(), 1000);
        } else {
          showNotification(
            data.message || "Erreur lors de la suppression",
            "error",
          );
        }
      } catch (parseError) {
        console.error(
          "Erreur de parsing JSON:",
          parseError,
          "Texte reçu:",
          text,
        );
        showNotification("Erreur: réponse invalide du serveur", "error");
      }
    })
    .catch((error) => {
      console.error("Erreur réseau:", error);
      showNotification("Erreur réseau: " + error.message, "error");
    });
}
