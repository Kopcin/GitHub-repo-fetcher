package com.github_repo_fetcher.dto;

public record Branch(
        String name,
        Commit commit
) {}