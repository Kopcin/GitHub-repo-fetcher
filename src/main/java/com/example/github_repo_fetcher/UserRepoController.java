package com.example.github_repo_fetcher;

import com.example.github_repo_fetcher.dto.BranchDTO;
import com.example.github_repo_fetcher.dto.GithubRepoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserRepoController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GITHUB_API = "https://api.github.com";

    @GetMapping("/{username}/repos")
    public ResponseEntity<?> getUserRepositories(@PathVariable String username) {
        String url = GITHUB_API + "/users/" + username + "/repos";

        try {
            ResponseEntity<GithubRepoDTO[]> response = restTemplate.getForEntity(url, GithubRepoDTO[].class);
            GithubRepoDTO[] repos = response.getBody();

            if (repos == null || repos.length == 0) {
                return ResponseEntity.ok("User has no public repositories.");
            }

            List<Map<String, Object>> result = Arrays.stream(repos)
                    .filter(repo -> !repo.isFork())
                    .map(repo -> Map.of(
                            "repositoryName", repo.getName(),
                            "ownerLogin", repo.getOwner().getLogin(),
                            "branches", fetchBranches(username, repo.getName())
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);

        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "GitHub user not found"));
        } catch (RestClientException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse(
                            HttpStatus.BAD_GATEWAY.value(),
                            "GitHub API error: " + e.getMessage()
                    ));
        }
    }

    private List<Map<String, String>> fetchBranches(String owner, String repoName) {
        String url = GITHUB_API + "/repos/" + owner + "/" + repoName + "/branches";

        try {
            ResponseEntity<BranchDTO[]> response = restTemplate.getForEntity(url, BranchDTO[].class);
            BranchDTO[] branches = response.getBody();

            if (branches == null) return List.of();

            return Arrays.stream(branches)
                    .map(branch -> Map.of(
                            "name", branch.getName(),
                            "lastCommitSha", branch.getCommit().getSha()
                    ))
                    .collect(Collectors.toList());

        } catch (RestClientException e) {
            return List.of(Map.of("error", "GitHub API error: " + e.getMessage()));
        } catch (Exception e) {
            return List.of(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }
}
