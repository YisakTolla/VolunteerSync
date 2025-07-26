  import React from 'react';
  import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
  import Navbar from './components/Navbar';
  import Home from './components/Home';
  import HowItWorks from './components/HowItWorks';
  import Organizations from './components/Organizations';
  import Events from './components/Events';
  import About from './components/About';
  import Login from './components/Login';
  import Dashboard from './components/Dashboard';
  import Profile from './components/Profile';
  import ProfileSetup from './components/ProfileSetup';
  import Settings from './components/Settings';
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
            
            <Route 
              path="/how-it-works" 
              element={
                <>
                  <Navbar />
                  <HowItWorks />
                </>
              } 
            />
            
            <Route 
              path="/find-organizations" 
              element={
                <>
                  <Navbar />
                  <Organizations />
                </>
              } 
            />
            
            <Route 
              path="/find-events" 
              element={
                <>
                  <Navbar />
                  <Events />
                </>
              } 
            />
            
            <Route 
              path="/about" 
              element={
                <>
                  <Navbar />
                  <About />
                </>
              } 
            />
            
            {/* Login route WITH navbar if you want it */}
            <Route 
              path="/login" 
              element={
                <>
                  <Navbar />
                  <Login />
                </>
              } 
            />
            
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
            
            <Route 
              path="/settings" 
              element={
                <ProtectedRoute>
                  <Navbar />
                  <Settings />
                </ProtectedRoute>
              } 
            />
            
            {/* Catch-all route for 404 */}
            <Route 
              path="*" 
              element={
                <>
                  <Navbar />
                  <div style={{ 
                    padding: '80px 20px', 
                    textAlign: 'center',
                    minHeight: '60vh',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center'
                  }}>
                    <h1 style={{ fontSize: '3rem', marginBottom: '1rem', color: '#ef4444' }}>404</h1>
                    <h2 style={{ fontSize: '1.5rem', marginBottom: '1rem', color: '#1f2937' }}>Page Not Found</h2>
                    <p style={{ fontSize: '1.1rem', color: '#6b7280', marginBottom: '2rem' }}>
                      The page you're looking for doesn't exist.
                    </p>
                    <button 
                      onClick={() => window.location.href = '/'}
                      style={{
                        padding: '12px 24px',
                        backgroundColor: '#10b981',
                        color: 'white',
                        border: 'none',
                        borderRadius: '8px',
                        fontSize: '1rem',
                        fontWeight: '600',
                        cursor: 'pointer'
                      }}
                    >
                      🏠 Go Home
                    </button>
                  </div>
                </>
              } 
            />
          </Routes>
        </div>
      </Router>
    );
  }

  export default App;