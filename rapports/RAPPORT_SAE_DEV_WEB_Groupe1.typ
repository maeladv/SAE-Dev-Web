#import "shafoin-typst-template/vibrant-color.typ" : *

#show: doc => vibrant-color(
  theme: "pastel-theme",  // choix du theme parmi pastel-theme, blue-theme, green-theme, red-theme
  title: "Projet intégratif d'application web",  // titre du document
  authors: (  // liste des auteurs
    "Maël ADVISSE",
    "Louison BEDNAROWICZ",
    "Clément CANO",
  ),
  lang: "fr",
  heading-numbering: true,
  sub-authors: "3A ICY",  // texte optionnel au dessus des auteurs ex : groupe 2, 4A ICY 
  description: "SAE Développement D'applications Web Interactives - Spécialité Informatique et Cybersécurité - 3ème année - INSA Hauts-de-France", // description du document
  date: datetime(day: 10, month: 3, year: 2025), // date du document, sous format datetime
  subject: "Matière", // matière du document ou texte en bas
  bib-yaml: bibliography("sources.yaml"),  // référence vers une bibliographie
  logo: image("assets/insa-hdf.png", width: 33%),
  doc
)


= Cahier des charges

L’objectif de ce projet est de réaliser une application web permettant la gestion des notes des étudiants au sein d’un service de scolarité, la visualisation de ces notes par les enseignants et les étudiants, ainsi que l’évaluation des modules suivis.

== Contexte & objectif
Dans le cadre de la SAE développement d'application intéractive il est demandé aux étudiants de developpé une application web à destination d'un service de scolarité.

L'objectif de cet application est de dematérialisé la gestion des notes, le suivi des étudiants et l'évaluation des modules d'enseignements(EVE).

== Perimètre technique

=== Architecture : Modèle MCV2
- Controleur : servlet java
- Vues : JSP, HTML, CSS
- Modèle : JavaBeans

=== Persistance des données
- Design pattern : Design Record
- SGBD : PostgreSQL
=== Serveur d'application : apache-tomcat-10.1.0

=== Gestion de version et collaboration :
- Git (Flow avec protection des branches)
- Hébergement : GitHub

=== Outils de conception 
- Figma (UI)
- Draw.io (UML)
- Notion (Gestion de projet).

== Cibles

Cette application devra permettre *trois types d’accès* :

- *Étudiant* : 
Consulte ses résultats académiques et renseigne les EVE qui le concerne.

- *Professeur* :
Consulte les données relatives à ses cours (notes, listes étudiants et EVE).

- *Administrateur* :
Possède des droits globaux sur l'application ( gestion utilisateurs, lance EVE, saisie des notes...)

#linebreak()
#text("Ces 3 rôles auront des droits et des fonctionnalités différents au sein de l’application qui sont précisés dans le sujet et seront détaillés dans la partie spécificités fonctionnelles.")

== Spécificités fonctionnelles.

=== Création mot de passe et Authentification

*Authentification :* Page de login commune. Redirection contextuelle selon le rôle.

*Création mot de passe*
- L'admin crée une fiche utilisateur (Nom, Prénom, Rôle, date de naissance et si étudiant INE). -> voir section ci-dessous

- Le système génère un lien d'activation unique (durée de validité limitée).

- L'utilisateur clique, définit son mot de passe et active son compte.

*Réinitialisation de mot de passe :* Lien envoyé par email sur demande de l'admin ou via "Mot de passe oublié".

=== Fonctionnalités Administrateur

==== Gestion de la scolarité

- Gestion des *Spécialités* (Créer, Modifier, Supprimer)

- Gestion des *Matières* (Semestre, Coefficient)

- Affectation de *professeurs* aux matières

==== Gestion des utilisateurs

- Gestion des comptes (Créer, Modifier, Supprimer)
- Liste filtrable(par rôles, spécialités, matières) des utilisateurs

==== Gestion des Notes

- Saisie des notes d'une matière pour une liste d'étudiant

- Modifications des notes existantes

==== Gestion des Évaluations (EVE)

- Lancement des EVE avec renseignement de la date de cloture.

- Consultation des statistiques liés aux EVE.

=== Fonctionnalités Etudiant

- Consultation des notes

- Remplissage des EVE

=== Fonctionnalités Professeur

- Consultation des listes d'étudiants et notes par matières

- Visualisation des résultats des EVE avec restitution graphique

== Spécificités techniques

== Vues

=== Page d'activation de compte (tous les rôles)
- Permet à un utilisateur d'activer son compte via le lien reçu
- L'utilisateur peut saisir son mot de passe
- La validité du lien est temporaire

=== Page de login (tous les rôles)
- Permet de se connecter avec son mail comme identifiants et son mot de passe.
- Si le mot de passe ou l'identifians n'est pas valide cela affiche un texte d'erreur.
- Permet de reinitialiser son mot de passe.
- Si la connection est valide le sytème fourni un token temporaire donnat l'accès à certaines pages selon son rôle.

=== Page liste des comptes utilisateurs (admin)
- Page qui liste tous les comptes des utilisateurs

- Permettre à l'aministrateur de visualiser facilement les rôles (étudiants, professeurs, administrateurs...) et les spécialités des étudiants avec un badge coloré 

- Interface sous forme de tableau (lignes) qui contiennent les informations principales (INE, nom, prenom) et un bouton qui permet d'accéder à la page de gestion de l'étudiant en question.

- Permet à l'administrateur d'ajouter un utilisateur avec un bouton en haut de la page

- Lors de la création d'un compte, on génère un lien d'activation de compte destiné à l'utilisateur, un pop-up s'ouvre. Il faut saisir le mail, le nom, le prenom, spé de l'utilisateur, INE et son rôle.

- Sur la ligne d'un utilisateur il y a 3 boutons :
  - Consulter le profil
  - Reinitialiser le mot de passe
  - Supprimer l'utilisateur

- En cas de suppression une pop-up de confirmation s'affiche

=== Page de compte utilisateur (admin)

- Tout les champs mise à part le mot de passe sont apparents et modifiables

- Un bouton de reinitialisation de mot de passe est présent

- Toutes les notes de l'utilisateur sont présentes s'il s'agit d'un étudiant

- Si il s'agit d'un prof on voit la liste de ses matières

=== Page liste des spécialités

- Page qui liste toutes les spécialités

- Quand on clique sur une spécialités on ouvre la page de spécialité

- Au bout de la ligne d'une spécialité il y a un bouton de suppression

- En cas de suppression une pop-up de confirmation s'affiche

- Permet à l'administrateur d'ajouter une spécialité avec un bouton en haut de la page

=== Page de Spécialité (admin)

- Dashboard avec liste des matières et des étudiants

- Possibilité d’ajouter/supprimer les membres de la spé

- Possibilité d’affecter des profs à une matière en modifiant le champ dédié

- en cliquant sur la ligne d'une matière on ouvre la page de la matière

- Possibilité de rajouter/supprimer/modifier des matières par le biès de boutons

- Les lignes d'étudiants comporte un bouton pour regarder leurs profils

=== Page de matière

- Les champs nom, profs, semestre et coefficient sont apparents et modifiables

- Un bouton permet la saisie des notes

- Une liste des notes est affiché

=== Page d’EVE (admin)

- La liste des spécialités est affiché, l'on clique sur une ligne on va sur la page d'EVE de la spécialité

- Sur la page principale, on peut “lancer les EVE” pour tout les étudiants qui ouvre une pop-up où l'admin renseignela date début et la date fin)

- Si un EVE est en cours, pas possible d’en lancer un nouveau et cela affiche un message d'erreur en rouge

=== Page d'EVE d'une spécialité (admin)

- La liste des matières de la spécialité est affiché avec un bouton au bout qui permet de voir les réusltats d'EVE de la spécialité

=== Affichage d’un EVE (prof/admin)

- Visualiser le formulaire EVE avec toutes les réponses ou télécharger le .csv

- tableau classique avec toutes les réponses

- Graphiques


== Schémas UML de l'application

=== Diagramme de cas d'utilisation

=== Diagramme de classes

=== Diagramme de séquences

=== Diagramme d'activités

== Interface UI/UX

=== Maquette de l'application

===

== Amélioration prévue

- Envoie du lien de validation de compte par mail
- Barre de recherche dans la page d'EVE admin




