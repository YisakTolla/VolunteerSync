import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Login from './components/Login';
import HowItWorks from './components/HowItWorks';
import Footer from './components/Footer';
import BackendTest from './components/BackendTest';

// Main App Content Component
function AppContent() {
  const [showBackendTest, setShowBackendTest] = useState(false);
  const location = useLocation();

  // Determine if footer should show
  const showFooter = location.pathname === '/' || location.pathname === '/how-it-works';

  return (
    <div className="App">
      {/* Navigation */}
      <Navbar />

      {/* Development Tools Toggle */}
      <button
        className={`dev-toggle-button ${showBackendTest ? 'active' : 'inactive'}`}
        onClick={() => setShowBackendTest(!showBackendTest)}
      >
        {showBackendTest ? '❌ Hide Dev' : '🔧 Dev Tools'}
      </button>

      {/* Backend Test Panel (Development Only) */}
      {showBackendTest && (
        <div className="dev-panel">
          <div className="dev-panel-header">
            <h3 className="dev-panel-title">
              🔧 Development Tools
            </h3>
            <button
              className="dev-panel-close"
              onClick={() => setShowBackendTest(false)}
            >
              ✕
            </button>
          </div>
          <BackendTest />
        </div>
      )}

      {/* Main Application Routes */}
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/how-it-works" element={<HowItWorks />} />
        </Routes>
      </main>

      {/* Footer - show on specific pages */}
      {showFooter && <Footer />}
    </div>
  );
}

// Main App Component with Router
function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;