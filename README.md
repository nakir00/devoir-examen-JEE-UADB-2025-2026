# Gestion des produits

API REST Spring Boot de gestion de produits : identifiant, nom, catégorie, prix et quantité.

## Prérequis

- JDK 21 ou supérieur compatible avec Spring Boot 4.1.1 ; le projet cible Java 21.
- Docker et Docker Compose pour la base PostgreSQL locale, ou une instance PostgreSQL existante.
- Postman pour exécuter les scénarios de vérification.

Le Maven Wrapper est fourni : une installation séparée de Maven n'est pas nécessaire.
Les premiers appels au wrapper peuvent nécessiter une connexion Internet pour télécharger Maven et les dépendances.

## Structure

```text
src/main/java/sn/uadb/devoir/
├── controller/   # Endpoints REST
├── dto/          # Objets de requête et de réponse
├── entity/       # Entité JPA Product
├── exception/    # Gestion centralisée des erreurs
├── mapper/       # Conversion Entity ↔ DTO
├── repository/   # Accès aux données et query methods
└── service/      # Règles métier
```

L'entité et les DTO possèdent des constructeurs, getters et setters manuels, sans Lombok.
Le contrôleur manipule les DTO, le mapper assure les conversions et le service utilise le repository JPA.
Les écritures sont transactionnelles et les lectures utilisent des transactions en lecture seule.

## PostgreSQL

La configuration locale est fournie dans `compose.yaml` :

- base : `gestion_produits`
- utilisateur : `gestion_user`
- mot de passe : `gestion_password`
- port : `5432`

Ces identifiants sont destinés au développement local. Les mêmes valeurs sont les valeurs par défaut de
`application.properties`. Elles peuvent être remplacées sans modifier le projet : `DB_URL`,
`DB_USERNAME` et `DB_PASSWORD`. Ces variables configurent la connexion de l'application ; elles ne changent
pas les identifiants du conteneur définis dans `compose.yaml`.

## Compilation et démarrage

Depuis le dossier contenant `pom.xml`, vérifier la compilation et créer le JAR :

```bash
./mvnw -DskipTests package
```

Démarrer PostgreSQL et attendre qu'il accepte les connexions :

```bash
docker compose up -d --wait
```

Puis démarrer l'application :

```bash
./mvnw spring-boot:run
```

L'API est accessible à l'adresse `http://localhost:8080/api/products`.
Sous Windows, utiliser `mvnw.cmd` à la place de `./mvnw`.

Hibernate crée ou met à jour la table `products` au démarrage grâce à `ddl-auto=update`.
Cette configuration est choisie pour le mini-projet local. Les données PostgreSQL sont conservées dans
le volume Docker `postgres_data`. `docker compose stop` arrête la base sans effacer ce volume.
Le port `5432` doit être disponible pour le conteneur.

## API

| Méthode | Endpoint | Rôle |
| --- | --- | --- |
| POST | `/api/products` | Créer un produit |
| GET | `/api/products` | Lister les produits |
| GET | `/api/products/{id}` | Consulter un produit |
| PUT | `/api/products/{id}` | Modifier un produit |
| DELETE | `/api/products/{id}` | Supprimer un produit |
| GET | `/api/products/category/{category}` | Rechercher par catégorie |
| GET | `/api/products/search?name=...` | Rechercher par nom |
| GET | `/api/products/low-stock?threshold=...` | Rechercher les stocks faibles |
| GET | `/api/products/price-range?min=...&max=...` | Rechercher par plage de prix |

## Validation et règles de recherche

- `name` et `category` sont obligatoires, non blancs et limités à 100 caractères.
- `price` est obligatoire, positif ou nul, avec au plus 10 chiffres entiers et 2 décimales.
- `quantity` est obligatoire, entière, positive ou nulle. Les valeurs telles que `8.9` et `-0.5` sont rejetées.
- Les identifiants doivent être strictement positifs.
- La catégorie correspond exactement à la valeur stockée, y compris sa casse.
- La recherche par nom utilise une correspondance partielle sans tenir compte de la casse.
- Le stock faible correspond à `quantity < threshold` : un stock de 10 est exclu avec un seuil de 10.
- La plage de prix inclut ses deux bornes ; `min` et `max` sont obligatoires, positifs ou nuls, avec `min <= max`.
- Une recherche sans résultat renvoie `200` avec une liste vide.

## Réponses et erreurs

La création renvoie `201`, les lectures et modifications `200`, et la suppression `204` sans corps.
La consultation, modification ou suppression d'un produit absent renvoie `404`.
Les erreurs sont centralisées dans `GlobalExceptionHandler` avec le format suivant :

```json
{
  "timestamp": "2026-10-16T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Les données fournies sont invalides.",
  "validationErrors": {
    "name": "Le nom est obligatoire."
  }
}
```

Les champs invalides, JSON malformés et paramètres manquants ou incorrects produisent `400`.
Les méthodes HTTP non autorisées produisent `405` et les types de contenu non pris en charge `415`.
Une erreur interne produit `500` avec un message générique ; les détails sont journalisés côté serveur.

## Vérifications Postman

Importer `postman/Gestion-Produits.postman_collection.json`, puis ouvrir le Collection Runner.
Adapter la variable de collection `baseUrl` si nécessaire et exécuter les 41 requêtes dans leur ordre initial.

Le scénario prépare trois produits avec un marqueur unique, vérifie le CRUD et les quatre recherches,
puis les erreurs et le nettoyage. Chaque requête contient des assertions sur le statut et, selon le cas,
les données retournées ou le format d'erreur. Les recherches vérifient notamment la casse, le seuil de stock
strict et les bornes de prix inclusives. Les quantités décimales font partie des cas de rejet.

Les identifiants sont enregistrés automatiquement dans `productId`, `mouseId` et `notebookId`.
Les recherches de stock et de prix tolèrent la présence d'autres produits dans la base.
Le nettoyage supprime uniquement les trois produits créés pendant cette exécution, puis vérifie les `404`.
Exécuter la collection complète : un dossier isolé peut dépendre des produits préparés précédemment.
Si l'exécution est interrompue avant le nettoyage, les produits préparés restent en base ; leurs identifiants
figurent dans les variables de collection.

Aucun test Java automatisé n'est ajouté ; les scénarios fonctionnels du devoir sont fournis dans Postman.
