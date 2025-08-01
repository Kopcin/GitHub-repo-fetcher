package com.example.github_repo_fetcher.dto;

public class GithubRepoDTO {
    private String name;
    private OwnerDTO owner;
    private boolean fork;

    public String getName() {
        return name;
    }

    public OwnerDTO getOwner() {
        return owner;
    }

    public boolean isFork() {
        return fork;
    }
}
