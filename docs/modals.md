# Système de Modals - Gradibou

On appelle Modal une vue qui permet de concentrer l'utilisateur sur un certain endroit de la page, en pop up.
Cela rend inactif tout élément de l'arrière plan en attendant que le madal soit fermé.


Le système de modals est composé de 3 fichiers principaux :
- `modals.css` : Styles pour tous les composants modals
- `modals.js` : Logique JavaScript pour gérer les interactions et validations
- `admin.js` : Gestion des dropdowns personnalisés (ouverture/fermeture, sélection, filtrage)

Ils sont intégrés dans les différentes pages d'administration.

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

Gère les modals : ouverture, fermeture, validation.

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
Ouvre le modal de confirmation de suppression.
```javascript
confirmDelete(200, 'John Doe');
```

## Dropdowns personnalisés

Les dropdowns dans les pages admin utilisent une gestion JavaScript unifiée via `initDropdowns()` dans `admin.js`.

### Structure HTML standard
```html
<div class="dropdown" data-dropdown="identifier">
    <button type="button" class="dropdown-toggle" aria-haspopup="listbox" aria-expanded="false">
        <span class="dropdown-label">Libellé par défaut</span>
        <span class="dropdown-icon">▾</span>
    </button>
    <ul class="dropdown-menu" role="listbox">
        <li class="dropdown-option" role="option" data-value="val1" data-label="Label 1">Label 1</li>
        <li class="dropdown-option" role="option" data-value="val2" data-label="Label 2">Label 2</li>
    </ul>
</div>
```

### Dropdown avec badges de rôle
Ajouter `data-role` pour afficher un badge coloré :
```html
<li class="dropdown-option" role="option" data-value="admin" data-label="Administrateur" data-role="admin">Administrateur</li>
```

Les rôles disponibles : `admin` (rouge), `prof` (bleu), `etudiant` (gris).

### File input personnalisé

```html
<div class="file-input-wrapper">
    <input type="file" id="csvFile" name="csvFile" accept=".csv" required>
    <div class="file-input-display">Texte du placeholder</div>
</div>
```

## Fonctionnalités

1. **Fermeture par overlay** : Cliquer en dehors du modal le ferme
2. **Fermeture par Escape** : Appuyer sur Escape ferme le modal actif
3. **Validation de formulaire** : Validation automatique des champs requis
4. **Dropdowns** : Ouverture/fermeture, affichage des badges de rôle
5. **File input** : Interface améliorée pour l'upload de fichiers
6. **Filtrage** : Les dropdowns de la barre admin permettent de filtrer les utilisateurs
