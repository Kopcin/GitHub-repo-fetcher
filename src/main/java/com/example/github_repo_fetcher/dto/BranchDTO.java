package com.example.github_repo_fetcher.dto;

public class BranchDTO {
    private String name;
    private CommitDTO commit;

    public String getName() {
        return name;
    }

    public CommitDTO getCommit() {
        return commit;
    }
}
