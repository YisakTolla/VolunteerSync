import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Profile from './components/Profile';
import ProfileSetup from './components/ProfileSetup';
import { ProtectedRoute, ProfileSetupRoute } from './components/ProfileSetupRoute';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Public Routes */}
          <Route 
            path="/" 
            element={
              <>
                <Navbar />
                <Home />
              </>
            } 
          />
          <Route path="/login" element={<Login />} />
          
          {/* Profile Setup Route - Only for incomplete profiles */}
          <Route 
            path="/profile-setup" 
            element={
              <ProfileSetupRoute>
                <ProfileSetup />
              </ProfileSetupRoute>
            } 
          />
          
          {/* Protected Routes - Only for complete profiles */}
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Navbar />
                <Dashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/profile" 
            element={
              <ProtectedRoute>
                <Navbar />
                <Profile />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;