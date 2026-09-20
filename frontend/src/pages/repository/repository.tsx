import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getGithubRepositoryBranches, getGithubRepositoryCommits, getGithubRepositoryIssues, getGithubRepositoryPullRequests } from "../../services/GithubService";

import "./repository.css";

export default function Repository() {

    const { repoId } = useParams();

    const [branches, setBranches] = useState<string[]>([]);
    const [selectedBranch, setSelectedBranch] = useState("");
    const [commits, setCommits] = useState<any[]>([]);
    const [activeTab, setActiveTab] = useState("commits");
    const [issues, setIssues] = useState<any[]>([]);
    const [issueFilter, setIssueFilter] = useState("all");
    const [pullRequests, setPullRequests] = useState<any[]>([]);
    const [prFilter, setPrFilter] = useState("all");

    useEffect(() => {
        const loadBranches = async () => {
            try {
                const data = await getGithubRepositoryBranches(Number(repoId));

                console.log("Branches:", data);
                setBranches(data);

                if (data.length > 0) {
                    setSelectedBranch(data[0]);
                }
            } catch (error) {
                console.error("Failed to load branches:", error);
            }
        };

        if (repoId) {
            loadBranches();
        }

    }, [repoId]);

    useEffect(() => {
        if (!repoId || !selectedBranch) {
            return;
        }

        const loadCommits = async () => {
            console.log("Loading commits for:", selectedBranch);
            try {
                const data = await getGithubRepositoryCommits(
                    Number(repoId),
                    selectedBranch
                );
                console.log(
                    "Received commits for:",
                    selectedBranch,
                    data
                );
                setCommits(data);
            } catch (error) {
                console.error("Failed to load commits:", error);
                setCommits([]);
            }
        };
        loadCommits();
    }, [repoId, selectedBranch]);

    useEffect(() => {

        if (!repoId || activeTab !== "issues") {
            return;
        }

        const loadIssues = async () => {
            try {
                const data = await getGithubRepositoryIssues(Number(repoId), issueFilter);
                setIssues(data);
            } catch (error) {
                console.error("Failed to load issues:", error);
                setIssues([]);
            }

        };
        loadIssues();
    }, [repoId, activeTab, issueFilter]);

    //pullrequest
    useEffect(() => {

        if (!repoId || activeTab !== "pulls") {
            return;
        }

        const loadPullRequests = async () => {
            try {
                const data = await getGithubRepositoryPullRequests(Number(repoId), prFilter);
                setPullRequests(data);

            } catch (error) {
                console.error("Failed to load pull requests:", error);
                setPullRequests([]);
            }
        };
        loadPullRequests();
    }, [repoId, activeTab, prFilter]);

    return (

        <div className="repository-page">
            <div className="repository-header">
                <div className="activity-tabs">

                    <button className={activeTab === "commits" ? "active" : ""}
                        onClick={() => setActiveTab("commits")}>
                        Commits
                    </button>

                    <button className={activeTab === "issues" ? "active" : ""}
                        onClick={() => setActiveTab("issues")}>
                        Issues
                    </button>

                    <button className={activeTab === "pulls" ? "active" : ""}
                        onClick={() => setActiveTab("pulls")}>
                        Pull Requests
                    </button>

                </div>
                {activeTab === "commits" && (
                    <select value={selectedBranch}
                        onChange={(e) => setSelectedBranch(e.target.value)}>
                        {branches.map(branch => (
                            <option key={branch} value={branch}>
                                {branch}
                            </option>
                        ))}
                    </select>
                )}
            </div>

            {/* <div className="commit-count">
                <span>{commits.length}</span>
                <small> Commits</small>
            </div> */}

            {/* commits */}
            {activeTab === "commits" && (
                <div className="commit-list">
                    {/* <div className="commit-count">
                        <span>{commits.length}</span>
                        <small> Commits</small>
                    </div> */}
                    {commits.map((commit, index) => (
                        <div className="commit-card" key={commit.sha}>
                            <div className="commit-number">
                                {String(index + 1).padStart(2, "0")}
                            </div>
                            <div className="commit-content">
                                <h3 className="commit-message">
                                    {commit.message}
                                </h3>
                                <div className="commit-meta">
                                    <span>
                                        <i className="fa-solid fa-user"></i>
                                        {commit.author}
                                    </span>
                                    <span>
                                        <i className="fa-regular fa-clock"></i>
                                        {new Date(commit.date).toLocaleString()}
                                    </span>
                                </div>
                                <div className="commit-sha">
                                    <i className="fa-solid fa-code-commit"></i>
                                    {commit.sha.substring(0, 7)}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {activeTab === "issues" && (
                <div>
                    <div className="issue-filters">

                        <button
                            className={issueFilter === "all" ? "active" : ""}
                            onClick={() => setIssueFilter("all")}
                        >
                            All
                        </button>

                        <button
                            className={issueFilter === "open" ? "active" : ""}
                            onClick={() => setIssueFilter("open")}
                        >
                            Open
                        </button>

                        <button
                            className={issueFilter === "closed" ? "active" : ""}
                            onClick={() => setIssueFilter("closed")}
                        >
                            Closed
                        </button>

                    </div>
                    <div className="issues-list">

                        {issues.map(issue => (

                            <div
                                className="issue-card"
                                key={issue.id}
                            >

                                <div className="issue-number">
                                    #{issue.issueNumber}
                                </div>

                                <div className="issue-content">

                                    <h3>{issue.title}</h3>

                                    <span>
                                        {issue.author}
                                    </span>

                                </div>

                                <div className="issue-state">
                                    {issue.state}
                                </div>

                            </div>

                        ))}

                    </div>
                </div>
            )}

            {activeTab === "pulls" && (
                <>
                    <div className="issue-filters">

                        <button className={prFilter === "all" ? "active" : ""}
                            onClick={() => setPrFilter("all")}>
                            All
                        </button>

                        <button
                            className={prFilter === "open" ? "active" : ""}
                            onClick={() => setPrFilter("open")}>
                            Open
                        </button>

                        <button
                            className={prFilter === "closed" ? "active" : ""}
                            onClick={() => setPrFilter("closed")}>
                            Closed
                        </button>

                        <button
                            className={prFilter === "merged" ? "active" : ""}
                            onClick={() => setPrFilter("merged")}>
                            Merged
                        </button>

                    </div>

                    <div className="issues-list">
                        {pullRequests.map(pr => (

                            <div className="issue-card"
                                key={pr.id}>
                                <div className="issue-number">
                                    #{pr.prNumber}
                                </div>

                                <div className="issue-content">
                                    <h3>{pr.title}</h3>
                                    <span>
                                        {pr.author}
                                    </span>
                                </div>

                                <div className="issue-state">
                                    {pr.merged ? "Merged": pr.state}
                                </div>
                            </div>
                        ))}
                    </div>
                </>
            )}
        </div>
    );
}