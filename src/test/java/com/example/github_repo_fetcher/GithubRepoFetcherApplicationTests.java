package com.example.github_repo_fetcher;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubRepoFetcherApplicationTests {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void givenExistingUser_whenGetRepos_thenReturnFilteredReposWithBranches() {
        // Given
        String username = "octocat"; // popular test user on GitHub

        // When
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "http://localhost:" + port + "/users/" + username + "/repos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<?> repos = response.getBody();
        assertThat(repos).isNotNull();
        assertThat(repos).isNotEmpty();

        @SuppressWarnings("unchecked")
        Map<String, Object> firstRepo = (Map<String, Object>) repos.get(0);
        assertThat(firstRepo).containsKeys("repositoryName", "ownerLogin", "branches");

        assertThat(firstRepo.get("ownerLogin")).isEqualTo(username);

        List<?> branches = (List<?>) firstRepo.get("branches");
        assertThat(branches).isNotNull();

        if (!branches.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> firstBranch = (Map<String, Object>) branches.get(0);
            assertThat(firstBranch).containsKeys("name", "lastCommitSha");
        }
    }
}
