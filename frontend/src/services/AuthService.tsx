import type { LoginRequest } from "../models/LoginRequest";
import type { SignupRequest } from "../models/SingupRequest";
import api from "./AxiosInstance";

const Signup =async(request:SignupRequest)=>{
     
    const repsone=await api.post('/signup',request);

    return repsone
  
}

const login=async(request:LoginRequest)=>{
     
    const repsone=await api.post('/login',request);

    return repsone;
  
}

export {Signup,login};