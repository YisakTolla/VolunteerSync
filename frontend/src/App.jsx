import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import HowItWorks from './components/HowItWorks';
import Footer from './components/Footer';
import Organizations from './components/Organizations';
import About from './components/About';
import Events from './components/Events';
import Profile from './components/Profile';
import Settings from './components/Settings';

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login onBackToHome={() => window.location.href = '/'} />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/how-it-works" element={<HowItWorks />} />
          <Route path="/find-organizations" element = {<Organizations/>}/>
          <Route path="/find-events" element = {<Events/>}/>
          <Route path="/about" element = {<About/>}/>
          <Route path="/profile" element = {<Profile userType="organization" />  }/>
          <Route path="/settings" element = {<Settings/>}/>

          
          {/* Fallback route */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
        
        <Footer />
      </div>
    </Router>
  );
}

export default App;