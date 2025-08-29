import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/Home';
import HowItWorks from './components/HowItWorks';
import Organizations from './components/Organizations';
import ViewOrganization from './components/ViewOrganization';
import Events from './components/Events';
import ViewEvent from './components/ViewEvents'; 
import About from './components/About';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Profile from './components/Profile';
import ProfileSetup from './components/ProfileSetup';
import Settings from './components/Settings';
import Footer from './components/Footer';
import { ProtectedRoute, ProfileSetupRoute } from './components/ProfileSetupRoute';

// ScrollToTop component - scrolls to top on route change
function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);

  return null;
}

function App() {
  return (
    <Router>
      <ScrollToTop />
      <div className="App">
        <Routes>
          {/* Public Routes */}
          <Route 
            path="/" 
            element={
              <>
                <Navbar />
                <Home />
                <Footer />
              </>
            } 
          />
          
          <Route 
            path="/how-it-works" 
            element={
              <>
                <Navbar />
                <HowItWorks />
                <Footer />
              </>
            } 
          />
          
          <Route 
            path="/find-organizations" 
            element={
              <>
                <Navbar />
                <Organizations />
                <Footer />
              </>
            } 
          />

          <Route 
            path="/find-organizations/:id" 
            element={
              <>
                <Navbar />
                <ViewOrganization />
                <Footer />
              </>
            } 
          />
          
          <Route 
            path="/find-events" 
            element={
              <>
                <Navbar />
                <Events />
                <Footer />
              </>
            } 
          />

          {/* ADD THIS NEW ROUTE FOR EVENT DETAILS */}
          <Route 
            path="/find-events/:id" 
            element={
              <>
                <Navbar />
                <ViewEvent />
                <Footer />
              </>
            } 
          />
          
          <Route 
            path="/about" 
            element={
              <>
                <Navbar />
                <About />
                <Footer />
              </>
            } 
          />
          
          {/* Login route WITH navbar and footer */}
          <Route 
            path="/login" 
            element={
              <>
                <Navbar />
                <Login />
                <Footer />
              </>
            } 
          />

          {/* Profile Setup Route - For new users who need to complete their profile */}
          <Route 
            path="/profile-setup" 
            element={
              <ProfileSetupRoute>
                <Navbar />
                <ProfileSetup />
                <Footer />
              </ProfileSetupRoute>
            } 
          />
          
          {/* Protected Routes - Only for users with complete profiles */}
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Navbar />
                <Dashboard />
                <Footer />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/profile" 
            element={
              <ProtectedRoute>
                <Navbar />
                <Profile />
                <Footer />
              </ProtectedRoute>
            } 
          />
          
          <Route 
            path="/settings" 
            element={
              <ProtectedRoute>
                <Navbar />
                <Settings />
                <Footer />
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
                    Go Home
                  </button>
                </div>
                <Footer />
              </>
            } 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;