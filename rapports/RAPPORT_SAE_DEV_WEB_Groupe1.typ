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


= Besoins fonctionnels

L’objectif de ce projet est de réaliser une application web permettant la gestion des notes des étudiants au sein d’un service de scolarité, la visualisation de ces notes par les enseignants et les étudiants, ainsi que l’évaluation des modules suivis.

Cette application devra permettre *trois types d’accès* :
- Étudiant
- Professeur
- Administrateur

#text("Ces 3 rôles auront des droits et des fonctionnalités différents au sein de l’application qui sont précisés dans le sujet et seront plus détaillés dans le cahier des charges.")


Afin de réaliser cette application, vous devez dans un premier temps créer un espace collaboratif Git afin d’assurer le partage du code, le suivi des versions, la gestion des contributions de l’équipe et la traçabilité des évolutions tout au long du développement. Vous pouvez dans ce cas utiliser Gitlab UPHF.

Ensuite, vous devrez mener une phase de conception et de modélisation consistant principalement à analyser les besoins du système de gestion des notes, à identifier les acteurs et les fonctionnalités, puis à modéliser la structure, les comportements et les interactions de l’application à l’aide des principaux diagrammes UML (cas d’utilisation, classes, séquences et activités).
Une fois la structure des données nécessaire à l’application définie et les relations ainsi que les contraintes modélisées, vous devrez concevoir le schéma de la base de données correspondant.

Enfin, vous passerez à la phase de création des pages et de développement backend (côté serveur), qui consistera à concevoir des interfaces web ergonomiques et adaptées à chaque type d’utilisateur (administrateur, étudiant et enseignant), puis à implémenter la logique métier, les services backend et les mécanismes d’accès aux données garantissant le bon fonctionnement, la sécurité et la cohérence de l’application.

La phase de développement devra suivre le modèle MVC2, avec une Servlet jouant le rôle de contrôleur, chargée de récupérer les requêtes des utilisateurs, d’en déterminer le traitement approprié et de diriger les réponses vers les vues correspondantes.

La gestion des données de l’application devra suivre le design pattern Active Record, en s’appuyant sur l’utilisation de JavaBeans, où chaque entité métier encapsule ses attributs, ses accesseurs et les opérations de persistance associées à la base de données.

L’accès aux différentes actions définies pour l’application se fera via une page d’accueil, affichée après une authentification réalisée par l’utilisateur à travers une page dédiée. Les fonctionnalités proposées sur la page d’accueil seront adaptées en fonction du profil de l’utilisateur authentifié.

L’évaluation du projet portera sur les compétences suivantes :

Organisation et travail collaboratif

Qualité de votre conception et modélisation

Fiabilité de l’application

Ergonomie et facilité d’utilisation de l’application


= Cahier des charges

== Les différentes pages et leurs fonctionnalités

=== A\] La page d'activation de compte (tous les rôles)
- Permet à un utilisateur d'activer son compte via le lien reçu par email
- L'utilisateur peut saisir ses données personnelles et son mot de passe

=== B\] Page des comptes utilisateurs (admin)
- Page qui liste tous les comptes des utilisateurs
- Permettre à l'aministrateur de visualiser facilement les rôles (étudiants, professeurs, administrateurs...) et les spécialités des étudiants avec un badge coloré 
- Interface sous forme de tableau (lignes) qui contiennent les informations principales (INE, nom, prenom) et un bouton qui permet d'accéder à la page de gestion de l'étudiant en question.
- Permet à l'administrateur d'ajouter un utilisateur avec un bouton "+" en haut de la page
- Lors de la création d'un compte, on génère un lien de création de compte destiné à l'utilisateur, un pop-up s'ouvre. Il faut saisir le mail, le nom, le prenom, spé de l'utilisateur et son rôle.
- L'utilisateur devra saisir ses coordonnées et son mot de passe pour activer son compte via le lien donné
- Le lien d'activation de compte a une durée de validité maximale de 7 jours

Quand on clique sur la ligne d’un compte, on ouvre la page de gestion de compte
- Elle permet - pour tout type de compte - de modifier les informations du compte (mail, INE, renvoyer le lien pour reset le mdp, roles, etc…). Selon le rôle, les champs sont modifiables ou non. 

Accès spéciaux : 
- L'administrateur peut tout modifier
- Le prof peut voir voir la page de ses étudiants mais ne peut pas modifier les infos
- L'étudiant ne peux pas voir les pages des autres comptes, il peut modifier ses propres infos uniquement (mail, mdp)

=== C\] Page 

= Outils de collaboration & logiciels utilisés

= Architecture de l'application

== Schémas UML de l'application

=== Diagramme de cas d'utilisation

=== Diagramme de classes

=== Diagramme de séquences

=== Diagramme d'activités

== Interface UI/UX

=== Maquette de l'application

===

==




