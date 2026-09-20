package com.repoly.backend.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import com.repoly.backend.entity.GithubRepository;
import com.repoly.backend.entity.PullRequest;

public interface PulllRequestRepository extends JpaRepository<PullRequest, Long>{

    List<PullRequest> findByRepository(GithubRepository repository);
    PullRequest findByRepositoryAndGithubPrId(GithubRepository repository,Long githubPrId);
    List<PullRequest> findByRepositoryAndState(GithubRepository repository,String state);
    List<PullRequest> findByRepositoryAndMerged(GithubRepository repository,Boolean merged);

}
