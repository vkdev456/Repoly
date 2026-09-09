import "./Signup.css"
import {useState} from "react";
import { useNavigate } from "react-router-dom";
import type { SignupRequest } from "../../models/SingupRequest";
import { Signup } from "../../services/AuthService";

function Singup() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [email, setEmail]=useState("");
     
    const navigate = useNavigate();

    const handleSignup = async() => {

      try{ 

        const request:SignupRequest={
            email,
            username,
            password
        };

        const response=await Signup(request);
        console.log(response);
        
        navigate("/login");

        }catch(error){
           console.error(error);
        }

    };

    return (

        <div className="authcontainer">
              
            <div className="signup">
                <h2 className="text-center ">Signup</h2>
              
                <div className="mb-2">
                    <label className="form-label">Username</label>
                    <input id='username' type="text" className="form-control" placeholder="Enter username"
                     value={username}
                     onChange={(e)=>setUsername(e.target.value)}
                    /> 
                </div> 

                <div className="mb-2">
                    <label className="form-label">Email</label>
                    <input id='name' type="email" className="form-control" placeholder="Email"
                     value={email}
                     onChange={(e)=>setEmail(e.target.value)}
                    /> 
                </div> 

                <div className="mb-4">
                    <label className="form-label">Password</label>
                    <input type="password"  className="form-control" placeholder="Enter Password" value={password}
                     onChange={(e) => setPassword(e.target.value)} 
                    />
                </div>

                <button  className="btn btn-primary w-100" onClick={handleSignup}>Signup</button>
           </div>
           
        </div>

    )
}

export default Singup;