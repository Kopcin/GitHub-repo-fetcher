package com.github_repo_fetcher.controller;

import com.github_repo_fetcher.dto.Repository;
import com.github_repo_fetcher.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserRepoController {

    private final GithubService githubService;

    @GetMapping("/{username}/repos")
    public List<Repository> getUserRepositories(@PathVariable String username) {
        log.info("Fetching repositories for user: {}", username);
        return githubService.fetchUserRepositoriesWithBranches(username);
    }
}
