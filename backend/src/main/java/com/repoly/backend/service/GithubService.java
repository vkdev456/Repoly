package com.repoly.backend.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.repoly.backend.entity.User;
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

    public GithubService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
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

    // exchange GitHub code for access token
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

        String url = "https://api.github.com/user/repos" + "?per_page=100" + "&sort=updated";

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(user.getGithubAccessToken());
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2026-03-10");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Object.class).getBody();
    }

    public boolean isGithubConnected(String username) {

        User user = userRepository.getByUsername(username);
        if (user == null) {
            return false;
        }
        return user.getGithubAccessToken() != null;
    }

}