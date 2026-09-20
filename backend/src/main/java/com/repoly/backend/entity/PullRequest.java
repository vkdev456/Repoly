package com.repoly.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "repository_pull_requests",
    uniqueConstraints = { @UniqueConstraint(columnNames = {"repository_id", "github_pr_id"})
    }
)
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long githubPrId;

    private Integer prNumber;

    private String title;

    private String state;

    private Boolean merged;

    private String author;

    private String createdAt;

    private String updatedAt;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private GithubRepository repository;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGithubPrId() {
        return githubPrId;
    }

    public void setGithubPrId(Long githubPrId) {
        this.githubPrId = githubPrId;
    }

    public Integer getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(Integer prNumber) {
        this.prNumber = prNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getMerged() {
        return merged;
    }

    public void setMerged(Boolean merged) {
        this.merged = merged;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public void setRepository(GithubRepository repository) {
        this.repository = repository;
    }

}
