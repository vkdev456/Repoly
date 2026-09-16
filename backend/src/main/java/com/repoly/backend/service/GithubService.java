package com.repoly.backend.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.repoly.backend.entity.GithubRepository;
import com.repoly.backend.entity.User;
import com.repoly.backend.repository.GithubRepositoryRepository;
import com.repoly.backend.repository.UserRepository;

@Service
public class GithubService {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final GithubRepositoryRepository githubRepositoryRepository;

    public GithubService(JwtService jwtService, UserRepository userRepository,
            GithubRepositoryRepository githubRepositoryRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.githubRepositoryRepository = githubRepositoryRepository;
    }

    // GitHub authorization URL
    public String getAuthorizationUrl(String username) {

        String state = jwtService.generateGithubState(username);

        return "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=repo"
                + "&state=" + state;
    }

    public void handleCallback(String code, String state) {

        String username = jwtService.extractGithubUsername(state);

        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new RuntimeException("Reploy user not found");
        }

        // Exchange code for GitHub token
        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(
                java.util.List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                Map.class);

        Map<String, Object> tokenData = tokenResponse.getBody();

        if (tokenData == null || tokenData.get("access_token") == null) {
            throw new RuntimeException("Failed to get GitHub access token");
        }

        String accessToken = (String) tokenData.get("access_token");
        String refreshToken = (String) tokenData.get("refresh_token");

        // get github user
        HttpHeaders githubHeaders = new HttpHeaders();

        githubHeaders.setBearerAuth(accessToken);
        githubHeaders.set("Accept", "application/vnd.github+json");
        githubHeaders.set("X-GitHub-Api-Version", "2026-03-10");

        HttpEntity<Void> githubRequest = new HttpEntity<>(githubHeaders);

        ResponseEntity<Map> githubUserResponse = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                githubRequest,
                Map.class);

        Map<String, Object> githubUser = githubUserResponse.getBody();

        if (githubUser == null) {
            throw new RuntimeException("Failed to get GitHub user");
        }

        Long githubId = ((Number) githubUser.get("id")).longValue();

        String githubUsername = (String) githubUser.get("login");

        // save the connection
        user.setGithubId(githubId);
        user.setGithubUsername(githubUsername);
        user.setGithubAccessToken(accessToken);
        user.setGithubRefreshToken(refreshToken);

        userRepository.save(user);
    }

    public Object getRepositories(String username) {

        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getGithubAccessToken() == null) {
            throw new RuntimeException("GitHub account is not connected");
        }

        List<GithubRepository> savedRepositories = githubRepositoryRepository.findByUser(user);
        CompletableFuture.runAsync(() -> getRepositorieshelper(username));

        return savedRepositories;
    }

    public void getRepositorieshelper(String username) {

        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getGithubAccessToken() == null) {
            throw new RuntimeException("GitHub account is not connected");
        }

        String url = "https://api.github.com/user/repos"
                + "?per_page=100"
                + "&sort=updated"
                + "&visibility=public";

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(user.getGithubAccessToken());
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2026-03-10");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Object[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Object[].class);

        Object[] repositories = response.getBody();

        if (repositories == null) {
            throw new RuntimeException("no repositories found");
        }

        for (Object repositoryObject : repositories) {

            Map<String, Object> repo = (Map<String, Object>) repositoryObject;

            Map<String, Object> ownerData = (Map<String, Object>) repo.get("owner");

            String owner = ownerData.get("login").toString();
            String repoName = repo.get("name").toString();

            // Total commits
            long commitsCount = getCommitCount(owner, repoName, user.getGithubAccessToken());

            // Total merged pull requests
            long mergesCount = getMergedPullRequestCount(owner, repoName, user.getGithubAccessToken());

            Long githubRepoId = ((Number) repo.get("id")).longValue();

            long branchCount = getBranchCount(owner, repoName, user.getGithubAccessToken());

            GithubRepository githubRepository = githubRepositoryRepository.findByUserAndGithubRepoId(user,
                    githubRepoId);

            if (githubRepository == null) {
                githubRepository = new GithubRepository();
            }

            githubRepository.setGithubRepoId(githubRepoId);

            githubRepository.setName(repo.get("name").toString());

            githubRepository.setFullName(repo.get("full_name").toString());

            githubRepository.setOwner(owner);

            githubRepository.setHtmlUrl(repo.get("html_url").toString());

            githubRepository.setStars(((Number) repo.get("stargazers_count")).longValue());

            githubRepository.setForks(((Number) repo.get("forks_count")).longValue());

            githubRepository.setWatchers(((Number) repo.get("watchers_count")).longValue());

            githubRepository.setOpenIssues(((Number) repo.get("open_issues_count")).longValue());

            githubRepository.setLanguage(repo.get("language") != null ? repo.get("language").toString() : null);

            githubRepository.setCommitsCount(commitsCount);

            githubRepository.setMergesCount(mergesCount);

            githubRepository.setBranches(branchCount);

            githubRepository.setUser(user);

            githubRepositoryRepository.save(githubRepository);

            // repo.put("commits_count", commitsCount);
            // repo.put("merges_count", mergesCount);

            // result.add(repo);
        }

    }

    public boolean isGithubConnected(String username) {

        User user = userRepository.getByUsername(username);
        if (user == null) {
            return false;
        }
        return user.getGithubAccessToken() != null;

    }

    private long getCommitCount(String owner, String repoName, String accessToken) {

        String url = "https://api.github.com/repos/"
                + owner
                + "/"
                + repoName
                + "/commits?per_page=100";

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2026-03-10");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Object[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Object[].class);

        Object[] commits = response.getBody();

        if (commits == null || commits.length == 0) {
            return 0;
        }

        String linkHeader = response.getHeaders().getFirst("Link");

        // Only one page
        if (linkHeader == null) {
            return commits.length;
        }

        long lastPage = getLastPage(linkHeader);

        // Only one page
        if (lastPage <= 1) {
            return commits.length;
        }

        // Get the last page
        String lastPageUrl = "https://api.github.com/repos/"
                + owner
                + "/"
                + repoName
                + "/commits?per_page=100&page="
                + lastPage;

        ResponseEntity<Object[]> lastPageResponse = restTemplate.exchange(
                lastPageUrl,
                HttpMethod.GET,
                request,
                Object[].class);

        Object[] lastPageCommits = lastPageResponse.getBody();

        int lastPageCount = lastPageCommits == null ? 0 : lastPageCommits.length;

        return ((lastPage - 1) * 100) + lastPageCount;
    }

    private long getMergedPullRequestCount(String owner, String repoName, String accessToken) {

        String url = "https://api.github.com/repos/"
                + owner
                + "/"
                + repoName
                + "/pulls?state=closed&per_page=100";

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2026-03-10");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Object[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Object[].class);

        Object[] pullRequests = response.getBody();

        if (pullRequests == null) {
            return 0;
        }

        long mergedCount = 0;

        for (Object pullRequestObject : pullRequests) {

            Map<String, Object> pullRequest = (Map<String, Object>) pullRequestObject;

            Object mergedAt = pullRequest.get("merged_at");

            if (mergedAt != null) {
                mergedCount++;
            }
        }

        return mergedCount;
    }

    // branches
    private long getBranchCount(String owner, String repoName, String accessToken) {

        String url = "https://api.github.com/repos/"
                + owner
                + "/"
                + repoName
                + "/branches?per_page=100";

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2026-03-10");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Object[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Object[].class);

        Object[] branches = response.getBody();

        return branches == null ? 0 : branches.length;
    }

    private long getLastPage(String linkHeader) {

        String[] links = linkHeader.split(",");

        for (String link : links) {

            if (link.contains("rel=\"last\"")) {

                int pageStart = link.indexOf("page=") + 5;
                int pageEnd = link.indexOf("&", pageStart);

                if (pageEnd == -1) {
                    pageEnd = link.indexOf(">", pageStart);
                }

                return Long.parseLong(
                        link.substring(pageStart, pageEnd));
            }
        }

        return 1;
    }
}