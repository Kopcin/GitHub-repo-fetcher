package com.github_repo_fetcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Repository(
        String name,
        Owner owner,
        boolean fork,
        List<Branch> branches
) {
    public Repository withBranches(List<Branch> branches) {
        return new Repository(this.name, this.owner, this.fork, branches);
    }
}
