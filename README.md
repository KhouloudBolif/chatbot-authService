# Chatbot AuthService

Ce projet est une partie backend du projet de chatbot en utilisant Spring Boot pour la gestion de l'authentification.

## Prérequis

1. JDK 21 installé
2. IntelliJ IDEA ou tout autre IDE Java supportant Maven et Docker

## Installation

1. Clonez le dépôt :

   ```bash
   git clone https://github.com/KhouloudBolif/chatbot-authService.git

## Configuration du Projet

1. Accédez au répertoire du projet :
   
      ```bash
      cd repertoire/de/projet
3. Importez les dépendances Maven :
   
      ```bash
      mvn clean install
Cela créera un fichier JAR nommé spring-boot-docker.jar sous le répertoire target.

##Build et Lancer l'application avec Docker

1.Construction de l'image Docker :
     
     
     docker build -t spring-boot-docker.jar .
     
2.Exécutez l'application avec Docker :
   
      
      docker run -p 9090:8080 spring-boot-docker.jar
      
==> Votre application sera accessible à l'adresse http://localhost:9090


