package com.github_repo_fetcher.service;

import com.github_repo_fetcher.client.GithubClient;
import com.github_repo_fetcher.dto.Branch;
import com.github_repo_fetcher.dto.Repository;
import com.github_repo_fetcher.exception.GithubUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final GithubClient githubClient;

    public List<Repository> fetchUserRepositoriesWithBranches(String username) {
        try {
            List<Repository> repos = githubClient.getUserRepositories(username);

            if (repos == null) {
                return List.of();
            }

            return repos.stream()
                    .filter(repo -> !repo.fork())
                    .map(repo -> {
                        List<Branch> branches = githubClient.getBranches(username, repo.name());
                        return repo.withBranches(branches != null ? branches : List.of());
                    })
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound e) {
            throw new GithubUserNotFoundException("GitHub user not found: " + username);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("GitHub API error: " + e.getResponseBodyAsString(), e);
        }
    }
}
