package com.github_repo_fetcher.client;

import com.github_repo_fetcher.dto.Branch;
import com.github_repo_fetcher.dto.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange
public interface GithubClient {

    @GetExchange("/users/{username}/repos")
    List<Repository> getUserRepositories(@PathVariable String username);

    @GetExchange("/repos/{owner}/{repo}/branches")
    List<Branch> getBranches(@PathVariable String owner, @PathVariable String repo);
}
