#import "../shafoin-typst-template/vibrant-color.typ": *

#show: doc => vibrant-color(
  theme: "red-theme",
  title: "Guide Utilisateur - Administrateur",
  authors: (
    "Équipe Gradibou",
  ),
  lang: "fr",
  heading-numbering: true,
  sub-authors: "Documentation",
  description: "Guide d'utilisation de l'application Gradibou pour les administrateurs.",
  date: datetime.today(),
  subject: "Utilisation Gradibou",
  doc
)

= Introduction

Bienvenue dans le guide administrateur de l'application *Gradibou*. Ce document détaille les fonctionnalités réservées aux administrateurs pour la gestion de la plateforme.

= Connexion et Accès

Pour accéder à l'interface d'administration :
1. Rendez-vous sur la page de connexion.
2. Saisissez votre identifiant (email) et votre mot de passe.
3. Une fois connecté, le menu de navigation vous donnera accès aux outils d'administration.

#info[Si vous avez oublié votre mot de passe, utilisez la fonction 'Mot de passe oublié' sur la page de login.]

= Gestion des Utilisateurs

En tant qu'administrateur, vous avez la responsabilité de gérer les comptes utilisateurs.

== Créer un utilisateur
Vous pouvez créer manuellement des comptes pour :
- Les Professeurs
- Les Étudiants
- D'autres Administrateurs

Accédez à la section *Administration > Créer un utilisateur*. Remplissez le formulaire avec les informations requises (Nom, Prénom, Email, Rôle).

== Modifier / Supprimer un utilisateur
Depuis la liste des utilisateurs ou la page de profil d'un utilisateur spécifique, vous pouvez :
- Modifier ses informations personnelles.
- Réinitialiser son mot de passe (générer un lien).
- Supprimer définitivement le compte (attention, action irréversible).

= Gestion Pédagogique

L'administration configure la structure pédagogique de l'établissement.

== Spécialités
Les spécialités regroupent les étudiants et les matières.
- *Créer une spécialité* : Via le menu dédié.
- *Lister les spécialités* : Pour voir l'ensemble des formations proposées.

== Matières
Les matières sont rattachées à une spécialité et à un professeur responsable.
- Lors de la création d'une matière, vous devez lui assigner un nom, un semestre et un professeur référent.

== Examens et Notes
Bien que les professeurs gèrent généralement les notes, l'administrateur possède des droits globaux pour :
- Créer des examens.
- Saisir ou modifier des notes pour n'importe quel étudiant.
- Consulter les bulletins de notes complets.

= FAQ

*Comment changer le professeur d'une matière ?*
Allez dans la gestion des matières, sélectionnez la matière et modifiez le professeur assigné.

*Un étudiant ne voit pas ses notes.*
Vérifiez qu'il est bien inscrit dans la bonne spécialité et que les notes ont été publiées.
