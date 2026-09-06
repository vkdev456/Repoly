import "./LandingPage.css";
import { useNavigate } from "react-router-dom";

export default function LandingPage() {
    const navigate = useNavigate();

    return (
        <div className="Landingpage">

            <div className="container">

                <h1>Repoly</h1>

                <h2>Your GitHub, understood.</h2>

                <p>
                    Track your repositories, commits, pull requests,
                    and developer activity all in one place.
                </p>

                <div className="buttons">

                    <button className="signup-btn" onClick={() => navigate("/signup")}>
                        Get Started
                    </button>

                    <button className="login-btn" onClick={() => navigate("/login")}>
                        Log In
                    </button>

                </div>
            </div>
        </div>
    );
}