package com.repoly.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repoly.backend.entity.GithubRepository;
import com.repoly.backend.entity.User;

@Repository
public interface GithubRepositoryRepository extends JpaRepository<GithubRepository, Long>{
    List<GithubRepository> findByUser(User user);
    GithubRepository findByUserAndGithubRepoId(User user, Long githubRepoId);
} 
