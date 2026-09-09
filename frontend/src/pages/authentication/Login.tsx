import { useState } from "react";
import "./Login.css";
import type { LoginRequest } from "../../models/LoginRequest";
import { login } from "../../services/AuthService";
import { useNavigate } from "react-router-dom";


export default function Login() {

  let [username, setUsername] = useState("");
  let [password, setPassword] = useState("");

  const navigate = useNavigate();

  const handlelogin = async () => {

    try {

      const request: LoginRequest = {
        username,
        password
      };

      console.log("USERNAME ENTERED:", username);

      const response = await login(request);

      console.log("LOGIN SUCCESS:", response);
      console.log("USERNAME BEFORE STORAGE:", username);
      localStorage.setItem("token", response.data);

      console.log(
        "USERNAME AFTER STORAGE:",
        localStorage.getItem("username")
      );

      navigate("/chat");

    } catch (error) {
      console.error("LOGIN ERROR:", error);
    }
  }
  

  return (

    <div className="login-container">
      <div className="login">
        <h2 className="text-center"> Login </h2>
        <div className="mb-2">
          <label className='form-label'>Username</label>
          <input className="form-control" placeholder="Enter Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>

        <div className="mb-2">
          <label className='form-label'>password</label>
          <input className="form-control" type="password" placeholder="Enter Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={(e) => {if (e.key === "Enter") {
                if (!username.trim() || !password.trim()) {
                  return;
                }
                handlelogin();
              }}}
             />
        </div>

        <button className="btn btn-primary w-100" onClick={handlelogin}>login</button>
      </div>
    </div>

  )
}