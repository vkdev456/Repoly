import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {getGithubRepositoryBranches, getGithubRepositoryCommits} from "../../services/GithubService";

import "./repository.css";

export default function Repository() {

    const { repoId } = useParams();

    const [branches, setBranches] = useState<string[]>([]);
    const [selectedBranch, setSelectedBranch] = useState("");
    const [commits, setCommits] = useState<any[]>([]);

    useEffect(() => {

        const loadBranches = async () => {

            try {
                const data =await getGithubRepositoryBranches(Number(repoId));

                console.log("Branches:", data);
                setBranches(data);

                if (data.length > 0){
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
    if (!repoId || !selectedBranch){
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
        }catch (error){
            console.error("Failed to load commits:", error);
            setCommits([]);
        }
    };
    loadCommits();
    },[repoId, selectedBranch]);

    return (

        <div className="repository-page">
            <div className="repository-header">
                <div>
                    <p className="repository-label">
                        REPOSITORY
                    </p>

                    <h1>
                        Commit History
                    </h1>

                    <p className="repository-description">
                        View commits branch by branch
                    </p>

                </div>


                <div className="branch-selector">
                    <label>
                        Branch
                    </label>

                    <select
                        value={selectedBranch}
                        onChange={(e) =>
                            setSelectedBranch(e.target.value)
                        }
                    >

                        {branches.map((branch) => (

                            <option
                                key={branch}
                                value={branch}
                            >
                                {branch}
                            </option>

                        ))}

                    </select>

                </div>

            </div>


            <div className="commit-count">

                <span>
                    {commits.length}
                </span>

                <small>
                    Commits
                </small>

            </div>


            <div className="commit-list">

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
                                    {new Date(
                                        commit.date
                                    ).toLocaleString()}
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

        </div>

    );
}