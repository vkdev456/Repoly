package com.repoly.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "github_repositories")
public class GithubRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long githubRepoId;

    private String name;

    private String fullName;

    private String owner;

    private String htmlUrl;

    private long stars;

    private long forks;

    private long watchers;

    private long commitsCount;

    private long mergesCount;

    private Long openIssues;

    private String language;

    private Long branches;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public Long getGithubRepoId() {
        return githubRepoId;
    }

    public void setGithubRepoId(Long githubRepoId) {
        this.githubRepoId = githubRepoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public long getStars() {
        return stars;
    }

    public void setStars(long stars) {
        this.stars = stars;
    }

    public long getForks() {
        return forks;
    }

    public void setForks(long forks) {
        this.forks = forks;
    }

    public long getWatchers() {
        return watchers;
    }

    public void setWatchers(long watchers) {
        this.watchers = watchers;
    }

    public long getCommitsCount() {
        return commitsCount;
    }

    public void setCommitsCount(long commitsCount) {
        this.commitsCount = commitsCount;
    }

    public long getMergesCount() {
        return mergesCount;
    }

    public void setMergesCount(long mergesCount) {
        this.mergesCount = mergesCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(Long openIssues) {
        this.openIssues = openIssues;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getBranches() {
        return branches;
    }

    public void setBranches(long branches) {
        this.branches = branches;
    }
}