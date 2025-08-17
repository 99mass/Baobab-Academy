# Utiliser une image Java 21 officielle et légère
FROM eclipse-temurin:21-jdk-alpine

# Installer Maven
RUN apk add --no-cache maven

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml en premier (pour le cache Docker)
COPY pom.xml .

# Télécharger les dépendances (mise en cache)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests -B

# Créer le répertoire pour les uploads temporaires
RUN mkdir -p /tmp/uploads

# Exposer le port (Railway utilise la variable PORT)
EXPOSE 8080

# Définir les variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Commande de démarrage
CMD java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar target/baobab_academy-0.0.1-SNAPSHOT.jar