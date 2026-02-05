# SAE-Dev-Web

Ce dépôt contient le code de l'application web que nous développons dans le cadre du projet web intégratif SAE de 3ème année de spécialité ingénieur en Informatique et CYbersécurité à l'INSA Hauts-de-France

## Membres du groupe :
- Maël ADVISSE @maeladv
- Louison BEDNAROWICZ @BillyTheSecond
- Clément Cano @Ipios02D


### Ressources Typst
Le modèle Typst utilisé pour rédiger ce rapport est disponible ici :
- [shafoin-typst-template](https://github.com/Shafoin/shafoin-typst-template) @SHAfoin

Pour utiliser ce modèle, il suffit de clone le repository dans le dossier `rapports/` de ce repo.

### Deploiement du projet

Pour deployer le projet, nous avons utilisé un serveur Apache Tomcat 10.1.0. Il est nécessaire d'utiliser la version 10.1.x ou plus récente pour la compatibilité avec Jakarta.

Après avoir cloné le projet, il faut compiler avec la commande `mvn clean package` puis on deploie le serveur avec le fichier compilé `gradibou.war`

Avant de lancer le serveur, il faut ajouter la base de données. Pour ce faire, il faut dans un premier temps ajouter un fichier .env (en se basant sur le fichier .env.example que l'on peut retrouver dans les fichier du dépot) dans le dossier de configuration du serveur. (Sur un système linux qui utilise l'extension community server connexion de vscode, ce dossier ce trouve généralement au chemin suivant `/home/<user>/.rsp/redhat-community-server-connector/runtimes/installations/tomcat-10.1.0/apache-tomcat-10.1.0/`. Si le dossier ne se trouve pas a cet emplacement, il est possible d'ajouter la ligne de debug suivante au debut du controller: `System.out.println("CATALINA_BASE = " + System.getenv("CATALINA_BASE"));`). Ensuite il faut executer les commandes suivantes pour creer la base de données:
```
# Se connecter a postgres
sudo -u postgres psql

# Creer la database
CREATE DATABASE <database_name>;
CREATE USER <database_user> WITH PASSWORD <database_password>;
GRANT ALL PRIVILEGES ON DATABASE <database_name> TO <database_user>;

# Quitter postgres
\q
```

Après avoir réaliser ces étapes, il est enfin possible d'acceder a notre Application