package com.repoly.backend.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.repoly.backend.entity.GithubRepository;
import com.repoly.backend.entity.RepositoryIssue;

public interface RepositoryIssueRepository extends JpaRepository<RepositoryIssue, Long> {

    List<RepositoryIssue> findByRepository(GithubRepository repository);

    RepositoryIssue findByRepositoryAndGithubIssueId(GithubRepository repository,Long githubIssueId);

    List<RepositoryIssue> findByRepositoryAndState(GithubRepository repository,String state);
}