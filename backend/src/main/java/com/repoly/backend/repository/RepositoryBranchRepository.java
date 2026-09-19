package com.repoly.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repoly.backend.entity.GithubRepository;
import com.repoly.backend.entity.RepositoryBranch;

@Repository
public interface RepositoryBranchRepository extends JpaRepository<RepositoryBranch, Long> {

    List<RepositoryBranch> findByRepository(GithubRepository repository);

    RepositoryBranch findByRepositoryAndBranchName(GithubRepository repository,String branchName);
}