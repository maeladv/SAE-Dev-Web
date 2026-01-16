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

= Cahier des charges

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




