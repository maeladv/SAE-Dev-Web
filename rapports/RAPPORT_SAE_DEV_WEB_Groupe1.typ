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
  //bib-yaml: bibliography("sources.yaml"),  // référence vers une bibliographie
  logo: image("assets/insa-hdf.png", width: 33%),
  doc
)


= Cahier des charges



== Contexte & objectif
Dans le cadre de la SAE développement d'application interactive il est demandé aux étudiants de développer une application web à destination d'un service de scolarité.

L'objectif de cette application est de dématérialiser la gestion des notes, le suivi des étudiants et l'évaluation des modules d'enseignement (EVE).

== Périmètre technique

=== Architecture : Modèle MCV2

- Controleur : serveur Java

- Vues : JSP, HTML, CSS

- Modèle : JavaBeans

=== Persistance des données

- Design pattern : Design Record

- SGBD : PostgreSQL

=== Serveur d'application
- apache-tomcat-10.1.0

=== Gestion de version et collaboration :
- Git (Flow avec protection des branches)

- Hébergement : #link("https://github.com/maeladv/SAE-Dev-Web")[GitHub]

=== Outils de conception 
- Figma (UI)
- Draw.io (UML)
- #link("https://www.notion.so/louisonbdz/SAE-ICY-Developpement-Web-2e6e2a1195f3802a9f77c8be503265d9?source=copy_link")[Notion] (Gestion de projet).

== Cibles

Cette application devra permettre *trois types d’accès* :

- *Étudiant* : 
Consulte ses résultats académiques et renseigne les EVE qui le concernent.

- *Professeur* :
Consulte les données relatives à ses cours (notes, listes étudiantes et EVE).

- *Administrateur* :
Possède des droits globaux sur l'application (gestion des utilisateurs, lance EVE, saisie des notes...).

#linebreak()
#text("Ces 3 rôles auront des droits et des fonctionnalités différents au sein de l’application qui sont précisés dans le sujet et seront détaillés dans la partie spécificités fonctionnelles.")

== Spécificités fonctionnelles.

=== Création mot de passe et authentification

*Authentification : * Page de login commune. Redirection contextuelle selon le rôle.

*Création mot de passe*
- L'admin crée une fiche utilisateur (Nom, Prénom, Rôle, date de naissance et si étudiant INE). -> voir section ci-dessous.

- Le système génère un lien d'activation unique (durée de validité limitée).

- L'utilisateur clique, définit son mot de passe et active son compte.

*Réinitialisation de mot de passe :* Lien envoyé par email sur demande de l'admin ou via "Mot de passe oublié".

=== Fonctionnalités Administrateur

==== Gestion de la scolarité

- Gestion des *Spécialités* (Créer, Modifier, Supprimer).

- Gestion des *Matières* (Semestre, Coefficient).

- Affectation de *professeurs* aux matières.

==== Gestion des utilisateurs

- Gestion des comptes (Créer, Modifier, Supprimer).

- Liste filtrable (par rôles, spécialités, matières) des utilisateurs.

==== Gestion des Notes

- Saisie des notes d'une matière pour une liste d'étudiants.

- Modifications des notes existantes.

==== Gestion des Évaluations (EVE)

- Lancement des EVE avec renseignement de la date de clôture.

- Consultation des statistiques liées aux EVE.

=== Fonctionnalités Étudiant

- Consultation des notes.

- Remplissage des EVE.

=== Fonctionnalités Professeur

- Consultation des listes d'étudiants et notes par matières.

- Visualisation des résultats des EVE avec restitution graphique.

== Spécificités techniques

=== Vues

==== Page d'activation de compte (tous les rôles)
- Permet à un utilisateur d'activer son compte via le lien reçu.

- L'utilisateur peut saisir son mot de passe.

- La validité du lien est temporaire.

==== Page de login (tous les rôles)
- Permet de se connecter avec son mail comme identifiants et son mot de passe.

- Si le mot de passe ou l'identifiant n'est pas valide, cela affiche un texte d'erreur.

- Permet de réinitialiser son mot de passe.

- Si la connexion est valide, le système fournit un token temporaire donnant l'accès à certaines pages selon son rôle.

==== Page de réinitialisation de mot de passe (tous les rôles)

 - Deux champs permettent de mettre le nouveau mot de passe, puis de le confirmer.

==== Page liste des comptes utilisateurs (admin)

- Page qui liste tous les comptes des utilisateurs.

- Permettre à l'administrateur de visualiser facilement les rôles (étudiants, professeurs, administrateurs...) et les spécialités des étudiants avec un badge coloré.

- Interface sous forme de tableau (lignes) qui contient les informations principales (INE, nom, prénom) et un bouton qui permet d'accéder à la page de gestion de l'étudiant en question.

- Permet à l'administrateur d'ajouter un utilisateur avec un bouton en haut de la page.

- Lors de la création d'un compte, on génère un lien d'activation de compte destiné à l'utilisateur, un pop-up s'ouvre. Il faut saisir le mail, le nom, le prénom, spé de l'utilisateur, INE et son rôle.

- Sur la ligne d'un utilisateur, il y a 3 boutons :
  - Consulter le profil,
  - Réinitialiser le mot de passe,
  - Supprimer l'utilisateur.

- En cas de suppression, des pop-up de confirmation s'affiche.

==== Page de compte utilisateur (admin)

- Tous les champs mis à part le mot de passe sont apparents et modifiables.

- Un bouton de réinitialisation de mot de passe est présent.

- Toutes les notes de l'utilisateur sont présentes s'il s'agit d'un étudiant.

- S'il s'agit d'un prof, on voit la liste de ses matières.

==== Page liste des spécialités (Admin)

- Page qui liste toutes les spécialités.

- Quand on clique sur une spécialité, on ouvre la page de spécialité.

- Au bout de la ligne d'une spécialité, il y a un bouton de suppression.

- En cas de suppression, des pop-up de confirmation s'affiche.

- Permet à l'administrateur d'ajouter une spécialité avec un bouton en haut de la page.

==== Page de Spécialité (admin)

- Dashboard avec liste des matières et des étudiants.

- Possibilité d’ajouter/supprimer les membres de la spé.

- Possibilité d’affecter des profs à une matière en modifiant le champ dédié.

- en cliquant sur la ligne d'une matière, on ouvre la page de la matière.

- Possibilité de rajouter/supprimer/modifier des matières par le biais de boutons.

- Les lignes d'étudiants comportent un bouton pour regarder leurs profils.

==== Page de matière (admin)

- Les champs nom, profs, semestre et coefficient sont apparents et modifiables.

- Un bouton permet la saisie des notes.

- Une liste des notes est affichée avec l'étudiant associé.

==== Page liste des EVE (admin)

- La liste des spécialités est affichée, lorsque l'on clique sur une ligne, on va sur la page d'EVE de la spécialité.

- Sur la page principale, on peut “lancer les EVE” pour tous les étudiants qui ouvrent des pop-up où l'admin renseigne la date de début et la date de fin.

- Si un EVE est en cours, pas possible d’en lancer un nouveau et cela affiche un message d'erreur en rouge.

==== Page d'EVE d'une spécialité (admin)

- La liste des matières de la spécialité est affichée avec un bouton au bout qui permet de voir les résultats d'EVE de la spécialité.

==== Page liste des EVE (prof)

- La liste des matières du prof est affichée.

- Quand  on clique sur une matière, cela affiche la page de l'EVE de la matière.

==== page d'affichage d'un EVE (prof/admin)

- On peut visualiser le formulaire EVE avec toutes les réponses ou télécharger le .csv .

- Tableau classique avec toutes les réponses.

- Graphiques des résultats.

==== Page liste des matières enseignées (prof)

- Le prof peut voir la liste des matières qu'il enseigne.

- Quand il clique sur une matière, il ouvre la page de la matière.

==== Page de matière (prof)

- Les champs nom, profs, semestre et coefficient sont apparents.

- Une liste des notes est affichée avec l'étudiant associé.

==== Page liste des étudiants (prof)

- Le prof peut voir la liste de ses étudiants avec la matière associée à côté.

==== Page mes notes (étudiant)

- L'étudiant peut voir la liste de ses notes avec la matière associée.

==== Page liste d'EVE (étudiant)

- Un tableau avec les EVE de chaque matière de la spé de l'étudiant à remplir.

- En cliquant sur un des EVE, on va sur la page de remplissage d'EVE.

==== Page de remplissage d'EVE (étudiant)

- Une série de boutons pour mettre une note chiffrée à la matière.

- Un encadré pour mettre un commentaire sur la matière.

=== Contrôleur

==== Servlet

- Gère les fonctions DoPost et DoGet permettant la redirection des pages.

=== Modèle

==== JavaBeans

- Chaque classe est mis sous la forme JavaBeans et contient les fonctions pour suivre le design pattern : design record, de plus elles contiennent les fonctions de recherche dans la db.

=== Sécurité

- Protection de toutes les routes en fonction du rôle de l'utilisateur.

- Droits différents selon les utilisateurs.

- Protection de l'accès à la db avec un mot de passe stocké dans un .env .

- Hash du mdp dans la db en cas de fuite de données.

- Protection contre les XSS et injection SQL.

== Planification et Livrables

=== Phase 1 : Conception

- Livrables : Diagrammes UML, Maquettes Figma, Schéma BDD.

- Date : 20/01

=== Phase 2 : Développement Back-end & BDD

- Mise en place Git, structure MVC, connexion BDD.

- Date : 20/01

=== Phase 3 : Développement Front-end & Intégration

- Pages JSP, CSS, Graphiques JS.

- Date : 22/01

=== Phase 4 : Test et Rapport

Tests, Peuplage de données, Rapport.

Date : 23/01

== Schémas UML de l'application

=== Diagramme de cas d'utilisation

#figure(
    caption: [Diagramme de cas d'utilisation.], 
    image("/rapports/assets/diagrammes/version_png/diagramme_cas_utilisation.png", width: 100%)
  )<cas_utilisation>

=== Diagramme de classes

#figure(
    caption: [Diagramme de classes.], 
    image("/rapports/assets/diagrammes/version_png/diagramme_classe.png", width: 100%)
  )<classe>

=== Diagramme de séquences

#figure(
    caption: [Diagramme de séquences.], 
    image("/rapports/assets/diagrammes/version_png/Diagramme sequence icy sae.png", width: 100%)
  )<sequence>

=== Diagramme d'activités

#figure(
    caption: [Diagramme d'activités.], 
    image("/rapports/assets/diagrammes/version_png/diagramme_activité.png", width: 100%)
  )<activite>

#figure(
  caption: [Diagramme d'activités de la création de compte.], 
  image("/rapports/assets/diagrammes/version_png/diagramme_activité_creation_compte.png", width: 100%)
)<activite_crea_compte>

== Schéma de la base de données

#figure(
  caption: [Schéma de la BDD version 1.], 
  image("/rapports/assets/diagrammes/version_png/Diagramme_db.png", width: 100%)
)<db>

#linebreak()

Après avoir travaillé sur l'application, il est devenu évident que certains changements étaient nécessaires pour faire fonctionner correctement et efficacement l'application.
#figure(
  caption: [Schéma de la BDD version 2.], 
  image("/rapports/assets/diagrammes/version_png/Diagramme_db_v2.png", width: 100%)
)<db>

= Interface UI/UX

=== Maquette de l'application

Ce qui démarque véritablement notre projet, c'est le niveau d'aboutissement de notre maquette Figma. Nous ne nous sommes pas contentés d'illustrer l'application : nous avons réalisé le design complet de chacune des pages, transformant nos idées en un prototype fidèle, immersif et prêt pour le développement.

Notre démarche s'appuie sur trois axes qui témoignent de la maturité technique de notre travail :

- Exhaustivité du design : L'intégralité de l'application a été maquettée, page par page. De plus pour assurer une cohérence parfaite et gagner en efficacité, nous avons développé une bibliothèque de composants réutilisables. Celle-ci inclut l'ensemble des éléments d'interface : headers, boutons, bibliothèques d'icônes, champs de saisie (inputs), et fenêtres modales (pop-ups).
#figure(
  caption: [ensemble des composants réutilisables sur figma.], 
  image("/rapports/assets/composant_figma.png", width: 100%)
)<figma>

- Maîtrise des Flux Utilisateurs : Nous avons défini et géré l'ensemble des chemins logiques entre les pages. La navigation n'est pas laissée au hasard ; chaque clic a été pensé pour guider l'utilisateur naturellement d'un écran à un autre, sans rupture dans son parcours.
#figure(
  caption: [Intéractions entre les pages], 
  image("/rapports/assets/interaction_figma.png", width: 75%)
)<figma>

- Une interactivité poussée au maximum : Notre maquette est entièrement cliquable et interactive. Nous avons simulé le comportement réel de l'application, incluant non seulement les transitions entre les pages, mais aussi le déclenchement des interactions complexes comme l'ouverture et la fermeture des pop-ups.
#figure(
  caption: [page intéractive sur figma.], 
  image("/rapports/assets/page_interactive_figma.png", width: 100%)
)<figma>

Nous invitons formetement les lecteurs de ce rapports à jeter un coup d'oeil à #link("https://www.figma.com/design/QpvqlQgX5KHlphDzAcWdPm/Maquette-SAE?node-id=20-123&t=ANUNhfVW9MvuBUDI-0")[notre maquette sur figma] et à #link("https://www.figma.com/proto/QpvqlQgX5KHlphDzAcWdPm/Maquette-SAE?node-id=46-440&p=f&t=LEVjaKnMcMwqyAvx-8&scaling=contain&content-scaling=responsive&page-id=0%3A1&starting-point-node-id=26%3A317&show-proto-sidebar=1&hide-ui=1")[notre maquette intéractive sur figma], elles représentent une bonne part de notre travail de réflexion sur la conception de notre application.
===

= Amélioration prévue

- Envoie des liens de validation de compte et de réinitialisation de mot de passe par mail.
- Barre de recherche dans la page d'EVE admin.
- Aide à la saisie d'un étudiant et des profs (ajout à une matière, assignation, etc..) qui se fait actuellement en donnant le mail
- Boutons de retour pour rendre la navigation plus intuitive
- Intégration d’IA générative pour résumer synthétiquement les retours textuels des EVE de chaque matière et faire un commentaire global sur la spécialité
- sauvegarder les stats des EVE pour ne pas faire des requêtes a la db et recalculer a chaque fois que quelqu’un veut consulter les stats
- Ajouter une page de mentions légales. Important pour un site en production mais pas prioritaire pour notre projet
- Ajouter les matières d’un prof dans la section `Champs Spécifiques` de la page `moncompte`
- Liste d’appel pour les professeur

= bibliographie

- #link("https://koor.fr/Java/SupportStruts2/slide15.wp")[Le framework struts 2]

- #link("https://www.conventionalcommits.org/en/v1.0.0/")[Conventional commits]

- #link("https://youtu.be/jevdND1NBVs?si=Qq4JyL0pw7eBoP7A")[Développer avec Java EE]

- #link("https://www.nubios.be/cahier-des-charges-application-mobile-guide-complet")[Créer un cahier des charges : Le guide complet]

- #link("https://www.nubios.be/cahier-des-charges-application-mobile-guide-complet")[Comment faire un cahier des charges]




