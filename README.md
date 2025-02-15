# challenge-backend

# Requirements
- Java 17

- PostgreSQL running locally on port 5432 with user: postgress, password: pass

You can start a docker container for PostgreSQL using the compose file located at the root of the repo:
```
docker-compose -f compose.challenge.yml up -d
```

Create a challenge database and grant all privileges to the postgres role:

```
CREATE DATABASE challenge;

GRANT ALL PRIVILEGES ON DATABASE "challenge" TO postgres;
```
# Migrations
Tables and demo data can be migrated using flyway:
```
mvn flyway:clean flyway:migrate
```
# Running the application
You can run the application on port 8080 with:
```
mvn spring-boot:run
```

# App design
JWT token have been implemented for authentication.

I reused code from https://github.com/ali-bouali/spring-boot-3-jwt-security for the JWT implementation.

The Lombok library has been used for this JWT implementation.

The MapStruct library has been used for mappings between Entities and DTOs exposed in the REST API. Java records have been used for these DTOs.

The exposed endpoints are :

- POST /api/auth/register
- POST /api/auth/authenticate
- GET /api/signingRequest
- POST /api/signingRequest
- POST /api/signingRequest/sign
- POST /api/document
- GET /api/document/{id}
- GET /api/document/{id}/content
- POST /api/document/{id}/confirm
