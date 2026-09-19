package com.repoly.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "repository_branches",
    uniqueConstraints = {   @UniqueConstraint(columnNames = {"repository_id", "branch_name"})}
)
public class RepositoryBranch{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "github_branch_sha")
    private String githubBranchSha;

    @ManyToOne
    @JoinColumn(name = "repository_id", nullable = false)
    private GithubRepository repository;

    public Long getId() {
        return id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getGithubBranchSha() {
        return githubBranchSha;
    }

    public void setGithubBranchSha(String githubBranchSha) {
        this.githubBranchSha = githubBranchSha;
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public void setRepository(GithubRepository repository) {
        this.repository = repository;
    }
}