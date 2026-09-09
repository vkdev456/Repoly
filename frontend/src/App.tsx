import { useState } from 'react'
import './App.css'
import {Route,Routes,BrowserRouter as Router} from 'react-router-dom';
import LandingPage from './pages/landingpage/LandingPage';
import Signup from './pages/authentication/Signup';
import Login from './pages/authentication/Login';
import Home from './pages/home/home';

function App(){
  const [count, setCount] = useState(0)
 
  return(
      <>
        <Router>
          <Routes>
             <Route path="/" element={<LandingPage></LandingPage>}/>
             <Route path="/signup" element={<Signup></Signup>}/>
             <Route path="/login" element={<Login></Login>}/>
             <Route path="/repolyhq" element={<Home></Home>}/>
          </Routes>
        </Router>
      </>
  )
  
}

export default App
