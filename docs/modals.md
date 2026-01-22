# Système de Modals - Gradibou

On appelle Modal une vue qui permet de concentrer l'utilisateur sur un certain endroit de la page, en pop up.
Cela rend inactif tout élément de l'arrière plan en attendant que le madal soit fermé.


Le système de modals est composé de 2 fichiers principaux :
- `modals.css` : Styles pour tous les composants modals
- `modals.js` : Logique JavaScript pour gérer les interactions
Ils sont intégrés dans les différentes pages d'administration

## Modals disponibles

### 1. Modal de création de compte (`createAccountModal`)
Formulaire en 2 colonnes pour créer un nouvel utilisateur avec les champs :
- Prénom, Nom, Email (INSA), Date de naissance
- Rôle (dropdown avec badges), Spécialité, INE

**Utilisation :**
```html
<button onclick="openModal('createAccountModal')">Créer un compte</button>
```

### 2. Modal d'upload CSV (`csvUploadModal`)
Permet de télécharger un fichier CSV contenant une liste d'utilisateurs.

**Utilisation :**
```html
<button onclick="openModal('csvUploadModal')">Importer CSV</button>
```

### 3. Modal d'erreur CSV (`csvErrorModal`)
Affiche le même formulaire que l'upload CSV mais avec un message d'erreur quand le format est invalide.

### 4. Modal de confirmation de suppression (`deleteConfirmModal`)
Dialogue de confirmation avant de supprimer un utilisateur.

**Utilisation :**
```html
<button onclick="confirmDelete(userId, userName)">Supprimer</button>
```

## API JavaScript

### ModalManager

La classe principale qui gère tous les modals.

#### Méthodes publiques

**`openModal(modalId)`**
Ouvre un modal par son ID.
```javascript
openModal('createAccountModal');
```

**`closeModal()`**
Ferme le modal actuellement ouvert.
```javascript
closeModal();
```

**`confirmDelete(userId, userName)`**
Ouvre le modal de confirmation de suppression avec les informations de l'utilisateur.
```javascript
confirmDelete(200, 'John Doe');
```

### Fonctionnalités automatiques

1. **Fermeture par overlay** : Cliquer en dehors du modal le ferme
2. **Fermeture par Escape** : Appuyer sur Escape ferme le modal actif
3. **Validation de formulaire** : Validation automatique des champs requis
4. **Dropdowns personnalisés** : Gestion complète des menus déroulants
5. **File input personnalisé** : Interface améliorée pour l'upload de fichiers

## Composants réutilisables

### Dropdown personnalisé

Structure HTML :
```html
<div class="custom-dropdown">
    <div class="dropdown-toggle">
        <div class="dropdown-value">Texte du placeholder</div>
        <svg class="dropdown-arrow"><!-- SVG arrow --></svg>
    </div>
    <div class="dropdown-menu">
        <div class="dropdown-item" data-value="valeur1">Option 1</div>
        <div class="dropdown-item" data-value="valeur2">Option 2</div>
    </div>
    <input type="hidden" name="champNom" required>
</div>
```

### Dropdown avec badges de rôle

Pour afficher des badges colorés (admin, prof, étudiant) :
```html
<div class="dropdown-item role-item role-admin" data-value="admin">Administrateur</div>
<div class="dropdown-item role-item role-prof" data-value="professeur">Professeur</div>
<div class="dropdown-item role-item role-etudiant" data-value="etudiant">Étudiant</div>
```

### File input personnalisé

```html
<div class="file-input-wrapper">
    <input type="file" id="csvFile" name="csvFile" accept=".csv" required>
    <div class="file-input-display">Texte du placeholder</div>
</div>
```

## Validation des formulaires

### Création de compte

La validation vérifie :
- Tous les champs requis sont remplis
- L'email doit être au format `*@insa-hdf.fr`
- Un rôle a été sélectionné

### Upload CSV

La validation vérifie :
- Un fichier a été sélectionné
- Le fichier a l'extension `.csv`
- Si le format est invalide, affiche `csvErrorModal`

## Classes CSS principales

### Overlay et conteneur
- `.modal-overlay` : Fond semi-transparent (overlay)
- `.modal` : Conteneur du modal
- `.modal-small` : Variante plus petite pour dialogues simples

### Formulaires
- `.modal-form-grid` : Grille 2 colonnes pour formulaires
- `.form-group` : Groupe de champ (label + input + erreur)
- `.error` : Classe pour état d'erreur
- `.error-message` : Message d'erreur

### Dropdowns
- `.custom-dropdown` : Conteneur principal
- `.dropdown-toggle` : Bouton déclencheur
- `.dropdown-menu` : Menu déroulant
- `.dropdown-item` : Élément du menu
- `.role-item` : Élément avec badge de rôle

### Boutons
- `.modal-btn` : Style de base pour boutons
- `.modal-btn-secondary` : Bouton secondaire (Annuler)
- `.modal-btn-primary` : Bouton primaire (action principale)

## Personnalisation

### Couleurs des rôles

Les couleurs sont définies dans `modals.css` :
```css
.role-admin { background-color: #fe3232; } /* Rouge */
.role-prof { background-color: #2d65c6; }  /* Bleu */
.role-etudiant { background-color: #6b6f76; } /* Gris */
```

### Responsive

Le système est responsive avec breakpoint à 768px :
- Sur mobile : formulaire en 1 colonne
- Sur mobile : boutons empilés verticalement
- Sur mobile : padding réduit

## Intégration dans une nouvelle page

1. Inclure les fichiers CSS et JS :
```jsp
<link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/modals.css">
<script src="<%= request.getContextPath() %>/static/js/modals.js"></script>
```

2. Définir la variable `contextPath` :
```jsp
<script>
    const contextPath = '<%= request.getContextPath() %>';
</script>
```

3. Ajouter votre modal dans le HTML :
```html
<div id="monModal" class="modal-overlay">
    <div class="modal">
        <h2 class="modal-title">Mon titre</h2>
        <!-- Contenu -->
        <div class="modal-actions">
            <button class="modal-btn modal-btn-secondary" onclick="closeModal()">Annuler</button>
            <button class="modal-btn modal-btn-primary">Confirmer</button>
        </div>
    </div>
</div>
```

4. Utiliser les fonctions globales :
```html
<button onclick="openModal('monModal')">Ouvrir</button>
```

## Exemples d'utilisation

### Ouvrir un modal au clic
```html
<button onclick="openModal('createAccountModal')">Créer un compte</button>
```

### Soumettre un formulaire avec validation
```html
<form onsubmit="return submitCreateAccount(event)">
    <!-- Champs du formulaire -->
    <button type="submit">Créer</button>
</form>
```

### Confirmer une suppression
```javascript
// Dans le template
<button onclick="confirmDelete(<%= user.getId() %>, '<%= user.getNom() %>')">
    Supprimer
</button>
```

## Notes importantes

- Le modal actif bloque le scroll de la page
- Un seul modal peut être ouvert à la fois
- Les dropdowns se ferment automatiquement si on clique ailleurs
- La validation est déclenchée à la soumission du formulaire
- Les fichiers uploadés doivent avoir l'extension `.csv`
