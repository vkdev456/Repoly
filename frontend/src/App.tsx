import { useState } from 'react'
import './App.css'
import {Route,Routes,BrowserRouter as Router} from 'react-router-dom';
import LandingPage from './pages/landingpage/LandingPage';

function App() {
  const [count, setCount] = useState(0)
 
  return(
      <>
        <Router>
          <Routes>
             <Route path="/" element={<LandingPage></LandingPage>}/>
          </Routes>
        </Router>
         
      </>
  )
  
}

export default App
