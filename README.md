# GitHub Repo Fetcher

## Description

A Spring Boot application exposing a REST endpoint /users/{username}/repos that fetches a GitHub user's public repositories, filters out forks, and for each repository returns:

- Repository name
- Owner login
- List of branches with their name and the last commit SHA

## Endpoint

GET `/users/{username}/repos`

### Sample Response

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

### Errors

For a non-existent user, the API returns status 404 in the format:
```json
{
"status": 404,
"message": "GitHub user not found"
}
```

## Technologies
Java 21

Spring Boot 3.5

RestTemplate for HTTP calls

## Running the Application

1. Clone the repository.
2. Run the Spring Boot application (e.g. from your IDE or using ./mvnw spring-boot:run).
3. Call the REST endpoint at http://localhost:8080/users/{username}/repos.

## Tests
The repository contains one integration test verifying the happy path of the endpoint.