import "./home.css";
import repolyLogo from "../../assets/Repoly.png";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Home() {

    const [menuOpen, setMenuOpen] = useState(false);
    const navigate = useNavigate();

    const handleSignout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    };

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
                
                <div className="repo">

                    <h2>Reponame</h2>

                    <div className="repo-stats">

                        <div className="stat">
                            <span>Commits</span>
                            <strong>500</strong>
                        </div>

                        <div className="stat">
                            <span>Pull Requests</span>
                            <strong>8</strong>
                        </div>

                        <div className="stat">
                            <span>Open Issues</span>
                            <strong>10</strong>
                        </div>

                        <div className="stat">
                            <span>Merges</span>
                            <strong>2</strong>
                        </div>

                    </div>

                </div>
            </div>
        </>
    );
}