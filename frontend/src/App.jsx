import { useState } from 'react';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Login from './components/Login';
import Footer from './components/Footer';
import BackendTest from './components/BackendTest';

function App() {
  const [showBackendTest, setShowBackendTest] = useState(false);
  const [currentPage, setCurrentPage] = useState('home');
  const [loginState, setLoginState] = useState(null);

  const handleNavigateToLogin = (state) => {
    setLoginState(state);
    setCurrentPage('login');
  };

  const handleBackToHome = () => {
    setCurrentPage('home');
    setLoginState(null);
  };

  return (
    <div className="App">
      {/* Navigation */}
      <Navbar 
        onNavigateToLogin={handleNavigateToLogin} 
        onNavigateToHome={handleBackToHome}
      />

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

      {/* Main Application */}
      <main>
        {currentPage === 'home' && (
          <Home onNavigateToLogin={handleNavigateToLogin} />
        )}
        {currentPage === 'login' && (
          <Login 
            initialState={loginState} 
            onBackToHome={handleBackToHome}
          />
        )}
      </main>

      {/* Footer - only show on home page */}
      {currentPage === 'home' && <Footer />}
    </div>
  );
}

export default App;