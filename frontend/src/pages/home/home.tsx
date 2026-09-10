import "./home.css";
import repolyLogo from "../../assets/Repoly.png";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
    getGithubAuthorizationUrl,
    getGithubRepositories,
    getGithubStatus
} from "../../services/GithubService";

export default function Home() {

    const [menuOpen, setMenuOpen] = useState(false);
    const navigate = useNavigate();

    const [repositories, setRepositories] = useState<any[]>([]);
    const [githubConnected, setGithubConnected] =
        useState<boolean | null>(null);

    const handleSignout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    };

    const connectGithub = async () => {

        try {

            const githubUrl = await getGithubAuthorizationUrl();

            window.location.href = githubUrl;

        } catch (error) {

            console.error("GitHub connection failed:", error);

        }
    };

    useEffect(() => {

        const loadGithub = async () => {

            try {

                const status = await getGithubStatus();

                console.log("GitHub status:", status);

                setGithubConnected(status.connected);

                if (status.connected) {

                    const data = await getGithubRepositories();

                    console.log("GitHub repositories:", data);

                    setRepositories(data);
                }

            } catch (error) {

                console.error("Failed to load GitHub:", error);

                setGithubConnected(false);
            }
        };

        loadGithub();

    }, []);

    return (
        <>
            <div className="nav-bar">

                <div className="log">
                    <img src={repolyLogo} alt="Reploy" />
                </div>

                <div className="menu-container">

                    <div
                        className="menu"
                        onClick={() => setMenuOpen(!menuOpen)}
                    >
                        <i className="fa-solid fa-bars"></i>
                    </div>

                    {menuOpen && (
                        <div className="dropdown">

                            <button onClick={handleSignout}>
                                <i className="fa-solid fa-right-from-bracket"></i>
                                Sign out
                            </button>

                        </div>
                    )}

                </div>

            </div>

            <div className="home">

                {/* Checking GitHub connection */}
                {githubConnected === null && (
                    <p>Checking GitHub connection...</p>
                )}

                {/* GitHub not connected */}
                {githubConnected === false && (
                    <button
                        className="github-connect"
                        onClick={connectGithub}
                    >
                        <i className="fa-brands fa-github"></i>
                        Connect GitHub
                    </button>
                )}

                {/* GitHub connected */}
                {githubConnected === true &&
                    repositories.map(repo => (

                        <div
                            className="repo"
                            key={repo.id}
                        >

                            <h2>{repo.name}</h2>

                            <div className="repo-stats">

                                <div className="stat">
                                    <span>Open Issues</span>
                                    <strong>
                                        {repo.open_issues_count}
                                    </strong>
                                </div>

                                <div className="stat">
                                    <span>Stars</span>
                                    <strong>
                                        {repo.stargazers_count}
                                    </strong>
                                </div>

                                <div className="stat">
                                    <span>Forks</span>
                                    <strong>
                                        {repo.forks_count}
                                    </strong>
                                </div>

                                <div className="stat">
                                    <span>Language</span>
                                    <strong>
                                        {repo.language || "N/A"}
                                    </strong>
                                </div>

                            </div>

                        </div>

                    ))
                }

            </div>
        </>
    );
}