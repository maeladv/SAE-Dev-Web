// Mon Compte - JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const profileForm = document.getElementById('profileForm');
    const motDePasseField = document.getElementById('motDePasse');
    const confirmPasswordField = document.getElementById('confirmPassword');
    
    // Validation et soumission
    profileForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }
        
        // Soumettre le formulaire
        this.submit();
    });
    
    function validateForm() {
        const nom = document.getElementById('nom').value.trim();
        const prenom = document.getElementById('prenom').value.trim();
        const email = document.getElementById('email').value.trim();
        
        if (!nom || !prenom || !email) {
            alert('Veuillez remplir tous les champs requis');
            return false;
        }
        
        // Vérifier que l'email est valide
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            alert('Veuillez entrer une adresse email valide');
            return false;
        }
        
        // Vérifier les mots de passe s'ils sont remplis
        const motDePasse = motDePasseField.value;
        if (motDePasse) {
            const confirmPassword = confirmPasswordField.value;
            
            if (motDePasse.length < 6) {
                alert('Le mot de passe doit contenir au moins 6 caractères');
                return false;
            }
            
            if (motDePasse !== confirmPassword) {
                alert('Les mots de passe ne correspondent pas');
                return false;
            }
        }
        
        return true;
    }
});
