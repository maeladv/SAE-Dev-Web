#import "../shafoin-typst-template/vibrant-color.typ": *

#show: doc => vibrant-color(
  theme: "green-theme",
  title: "Guide Utilisateur - Étudiant",
  authors: (
    "Équipe Gradibou",
  ),
  lang: "fr",
  heading-numbering: true,
  sub-authors: "Documentation",
  description: "Guide d'utilisation de l'application Gradibou pour les étudiants.",
  date: datetime.today(),
  subject: "Utilisation Gradibou",
  doc
)

= Introduction

Bienvenue sur *Gradibou*. Ce guide vous aidera à prendre en main votre espace étudiant pour suivre votre scolarité.

= Démarrage

== Accès à la plateforme
Connectez-vous à l'adresse fournie par votre établissement avec votre email étudiant et votre mot de passe.

== Première connexion
Lors de votre première connexion (ou si vous avez reçu un lien d'activation), il peut vous être demandé de compléter votre profil ou de changer votre mot de passe provisoire.

= Mon Espace Scolaire

== Consultation des Notes
L'onglet *Mes Notes* est votre outil principal. Il vous permet de voir :
- Vos notes récentes.
- Vos moyennes par matière.
- Votre moyenne générale.

#info[Les notes apparaissent en temps réel dès que votre professeur les a publiées.]

== Statistiques et Classements
En plus de vos notes brutes, Gradibou vous fournit des statistiques pour vous situer :
- *Moyennes de promotion* : Comparez vos résultats à la moyenne de la classe.
- *Classement* : (Si activé) Votre position dans la promotion.

= Mon Profil

Dans la section *Mon Compte*, vous pouvez consulter vos informations administratives :
- Votre *INE* (Identifiant National Étudiant).
- Votre *Spécialité* actuelle.
- Modifier vos informations personnelles (email, mot de passe).

#warning[Si vous constatez une erreur dans votre nom, prénom ou INE, veuillez contacter l'administration de l'école, car certains champs sont en lecture seule.]
