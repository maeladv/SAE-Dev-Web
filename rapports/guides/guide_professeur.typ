#import "../shafoin-typst-template/vibrant-color.typ": *

#show: doc => vibrant-color(
  theme: "blue-theme",
  title: "Guide Utilisateur - Professeur",
  authors: (
    "Équipe Gradibou",
  ),
  lang: "fr",
  heading-numbering: true,
  sub-authors: "Documentation",
  description: "Guide d'utilisation de l'application Gradibou pour les enseignants.",
  date: datetime.today(),
  subject: "Utilisation Gradibou",
  doc
)

= Introduction

Ce guide est destiné aux professeurs utilisant la plateforme *Gradibou* pour la gestion de leurs cours et l'évaluation des étudiants.

= Connexion et Tableau de Bord

Connectez-vous avec vos identifiants académiques.
Une fois connecté, votre tableau de bord vous permet d'accéder rapidement à vos tâches principales.

#info[Assurez-vous de garder vos identifiants confidentiels.]

= Gestion des Matières

Vous êtes responsable de certaines matières.

== Voir mes matières
Dans la section *Mon Compte*, vous trouverez la liste des matières que vous enseignez ("Matières Enseignées").
Cela vous permet de vérifier rapidement vos affectations pour le semestre en cours.

= Évaluation des Étudiants

== Créer un Examen
Pour évaluer les étudiants :
1. Naviguez vers la section de gestion des examens.
2. Créez un nouvel examen en spécifiant :
   - La matière concernée.
   - La date.
   - Le coefficient.

== Saisie des Notes
Une fois l'examen créé, vous pouvez saisir les notes des étudiants.
- Vous pouvez saisir les notes individuellement.
- Assurez-vous de valider la saisie pour qu'elle soit prise en compte dans la moyenne.

= Suivi des Résultats

Vous avez accès aux statistiques de vos étudiants pour les matières que vous enseignez.
- *Moyennes* : Consultez la moyenne de la classe pour vos examens.
- *Progression* : Identifiez les étudiants en difficulté.

= FAQ

*Je ne vois pas ma matière dans la liste.*
Contactez l'administrateur pour qu'il vous assigne correctement à la matière dans le système.

*Puis-je modifier une note après validation ?*
Oui, vous disposez des droits de modification sur les notes de vos matières.
