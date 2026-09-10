package com.repoly.backend.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.repoly.backend.service.GithubService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/github/connect")
    public String connectGithub(Authentication authentication) {
        String username = authentication.getName();
        return githubService.getAuthorizationUrl(username);
    }

    @GetMapping("/github/callback")
    public void githubCallback(@RequestParam String code, @RequestParam String state, HttpServletResponse response)
            throws IOException {

        try {
            githubService.handleCallback(code, state);
            response.sendRedirect("http://localhost:5173/repolyhq");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("http://localhost:5173/home?github=error");
        }
    }

    @GetMapping("/github/repos")
    public Object getRepositories(Authentication authentication) {
        String username = authentication.getName();
        return githubService.getRepositories(username);
    }

    @GetMapping("/github/status")
    public Map<String, Boolean> githubStatus(Authentication authentication) {

        String username = authentication.getName();
        boolean connected = githubService.isGithubConnected(username);

        return Map.of("connected", connected);
    }
}