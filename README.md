# GitHub Repo Fetcher

## Opis

Aplikacja Spring Boot wystawiająca endpoint REST `/users/{username}/repos`, który pobiera publiczne repozytoria użytkownika GitHub, filtruje forki i dla każdego repo zwraca:

- Nazwę repozytorium
- Login właściciela
- Listę branchów z nazwą i ostatnim SHA commita

## Endpoint

GET `/users/{username}/repos`

### Przykładowa odpowiedź

```json
[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "f5f369d..."
      }
    ]
  }
]
```

### Błędy

Dla nieistniejącego użytkownika zwracany jest status 404 w formacie:
```json
{
"status": 404,
"message": "GitHub user not found"
}
```

## Technologie
Java 21

Spring Boot 3.5

RestTemplate do wywołań HTTP

## Uruchomienie

1. Sklonuj repozytorium.
2. Uruchom aplikację Spring Boot (np. z IDE lub ./mvnw spring-boot:run).
3. Wywołaj endpoint REST pod adresem http://localhost:8080/users/{username}/repos.

## Testy
W repozytorium znajduje się test integracyjny sprawdzający happy path endpointu.