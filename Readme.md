# Cahier des Charges - Baobab Academy

## 1. Présentation du Projet

### 1.1 Contexte

Développement de Baobab Academy, une plateforme d'apprentissage en ligne permettant la création et la diffusion de contenus éducatifs. La plateforme vise à connecter des formateurs avec des apprenants dans un environnement numérique structuré et accessible.

### 1.2 Objectifs Généraux

- Créer un espace de formation en ligne intuitif et efficace
- Faciliter la création et la gestion de contenus pédagogiques
- Offrir une expérience d'apprentissage personnalisée aux utilisateurs
- Assurer un suivi de progression pour chaque apprenant

### 1.3 Portée du Projet

Développement d'une solution web complète incluant interface d'administration, interface utilisateur et système de gestion de contenu éducatif.

### 1.4 Technologies Utilisées

### Backend

- **Framework** : Spring Boot (Java)
- **Sécurité** : Spring Security avec JWT
- **API** : REST API

### Frontend

- **Framework** : Next.js (React)
- **Styling** : CSS/Tailwind CSS

### Base de Données

- **SGBD** : MongoDB

## 2. Analyse des Utilisateurs

### 2.1 Profils d'Utilisateurs

### 2.1.1 Administrateur

**Rôle** : Gestionnaire de Baobab Academy et créateur de contenu
**Objectifs** :

- Créer et organiser des cours structurés
- Gérer les contenus pédagogiques (textes, images, vidéos)
- Suivre l'activité des apprenants
- Administrer les comptes utilisateurs

### 2.1.2 Utilisateur/Apprenant

**Rôle** : Consommateur de contenu éducatif
**Objectifs** :

- Accéder aux cours disponibles
- Suivre un parcours d'apprentissage structuré
- Visualiser sa progression
- Compléter sa formation à son rythme

## 3. Fonctionnalités Détaillées

### 3.1 Gestion des Comptes et Authentification

### 3.1.1 Inscription Utilisateur

- Création de compte avec email et mot de passe
- Validation par email
- Définition du profil (nom, prénom)
- Attribution automatique du rôle "USER"

### 3.1.2 Connexion et Sécurité

- Authentification sécurisée
- Gestion des sessions
- Récupération de mot de passe oublié
- Déconnexion sécurisée

### 3.2 Interface Administrateur

### 3.2.1 Dashboard d'Administration

- Vue d'ensemble des cours créés
- Statistiques de consultation
- Gestion des utilisateurs inscrits
- Accès rapide aux fonctions de création

### 3.2.2 Création et Gestion de Cours

**Structure hiérarchique** :

- **Cours** : Entité principale avec titre, description, catégorie
- **Chapitres** : Sections thématiques du cours
- **Leçons** : Unités de contenu au sein des chapitres

**Types de contenu supportés** :

- Texte formaté (titres, paragraphes, listes)
- Images (upload et intégration)
- Vidéos (intégration par URL ou upload)
- Documents téléchargeables

**Fonctionnalités d'édition** :

- Éditeur de contenu intuitif
- Prévisualisation avant publication
- Sauvegarde automatique des brouillons

### 3.2.3 Publication et Visibilité

- Statuts des cours (Brouillon, Publié)
- Contrôle de la visibilité publique
- Gestion des mises à jour de contenu

### 3.3 Interface Utilisateur

### 3.3.1 Catalogue de Cours

- Affichage en grille des cours disponibles
- Informations synthétiques (titre, description, durée estimée)
- Système de recherche par mots-clés
- Filtrage par catégories
- Indicateur de progression pour les cours commencés

### 3.3.2 Accès aux Contenus

**Cours non connecté** :

- Aperçu du cours (description, sommaire)
- Information sur le nombre de leçons
- Invitation à se connecter pour accéder au contenu complet

**Cours connecté** :

- Accès intégral au contenu des leçons
- Navigation séquentielle entre les leçons
- Marquage automatique des leçons complétées
- Barre de progression visuelle

### 3.3.3 Lecteur de Cours

- Interface de lecture optimisée
- Navigation intuitive (précédent/suivant)
- Table des matières interactive
- Adaptation responsive pour tous les écrans

### 3.4 Suivi de Progression

### 3.4.1 Pour l'Utilisateur

- Pourcentage de completion par cours
- Historique des leçons consultées
- Temps estimé restant
- Reprise automatique à la dernière leçon consultée

### 3.4.2 Pour l'Administrateur

- Statistiques de consultation par cours
- Taux de completion
- Identification des contenus les plus populaires

## 4. Cas d'Usage Détaillés

### 4.1 Création d'un Cours (Administrateur)

**Acteur** : Administrateur
**Prérequis** : Être connecté avec les droits d'administration
**Déclencheur** : Volonté de créer un nouveau contenu pédagogique

**Scénario principal** :

1. L'administrateur accède au dashboard d'administration
2. Il clique sur "Créer un nouveau cours"
3. Il saisit les informations générales (titre, description, catégorie)
4. Il ajoute une image de couverture
5. Il crée le premier chapitre
6. Il ajoute des leçons avec différents types de contenu
7. Il prévisualise le cours
8. Il publie le cours

**Scénarios alternatifs** :

- Sauvegarde en brouillon pour modification ultérieure
- Modification d'un cours existant
- Suppression d'un cours

### 4.2 Consultation d'un Cours (Utilisateur)

**Acteur** : Utilisateur
**Prérequis** : Avoir un compte sur la plateforme
**Déclencheur** : Recherche d'un contenu d'apprentissage

**Scénario principal** :

1. L'utilisateur navigue sur le catalogue de cours
2. Il sélectionne un cours qui l'intéresse
3. Il consulte la description et le sommaire
4. Il se connecte s'il ne l'est pas déjà
5. Il commence la première leçon
6. Il progresse séquentiellement dans les leçons
7. Sa progression est automatiquement sauvegardée
8. Il peut reprendre sa lecture ultérieurement

**Scénarios alternatifs** :

- Consultation sans inscription (accès limité)
- Navigation libre dans les leçons
- Interruption et reprise de la formation

### 4.3 Recherche de Contenu (Utilisateur)

**Acteur** : Utilisateur
**Prérequis** : Accès à la plateforme
**Déclencheur** : Besoin de formation sur un sujet spécifique

**Scénario principal** :

1. L'utilisateur utilise la barre de recherche
2. Il saisit des mots-clés liés à son besoin
3. Les résultats s'affichent avec pertinence
4. Il peut filtrer par catégorie
5. Il consulte les cours correspondants
6. Il sélectionne le cours le plus adapté

## 5. Contraintes et Exigences

### 5.1 Contraintes Techniques

- Interface responsive
- Temps de chargement optimisé
- Sécurité des données utilisateurs
- Sauvegarde automatique du contenu

### 5.2 Contraintes d'Usage

- Interface intuitive sans formation préalable
- Accessibilité pour utilisateurs ayant des difficultés techniques
- Navigation fluide et logique

### 5.3 Exigences de Performance

- Support de fichiers média de taille raisonnable
- Chargement rapide des pages de cours
- Gestion simultanée de multiple utilisateurs
- Sauvegarde fiable de la progression

## 6. Périmètre de la Version Initiale

### 6.1 Fonctionnalités Incluses

- Gestion complète des utilisateurs et administrateurs
- Création, édition et publication de cours
- Consultation de cours avec suivi de progression
- Recherche et filtrage de contenu
- Interface responsive

## Lien du site
 - https://baobab-academy-frontend.vercel.app/
### Admin par defaut
 - Email: admin@baobabacademy.com
 - Mot de passe: admin123!