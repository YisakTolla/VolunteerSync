import { useState } from 'react'
import HomePage from './components/HomePage'
import BackendTest from './components/BackendTest'
import './App.css'

function App() {
  const [showBackendTest, setShowBackendTest] = useState(false)

  return (
    <div className="App">
      {/* Development Tools Toggle */}
      <div style={{
        position: 'fixed',
        top: '20px',
        right: '20px',
        zIndex: 1000
      }}>
        <button
          onClick={() => setShowBackendTest(!showBackendTest)}
          style={{
            backgroundColor: showBackendTest ? '#dc3545' : '#28a745',
            color: 'white',
            border: 'none',
            padding: '10px 15px',
            borderRadius: '5px',
            cursor: 'pointer',
            fontSize: '12px',
            boxShadow: '0 2px 5px rgba(0,0,0,0.2)'
          }}
        >
          {showBackendTest ? '❌ Hide Dev Tools' : '🔧 Show Dev Tools'}
        </button>
      </div>

      {/* Backend Test Panel (Development Only) */}
      {showBackendTest && (
        <div style={{
          position: 'fixed',
          top: '70px',
          right: '20px',
          width: '400px',
          maxHeight: '80vh',
          overflow: 'auto',
          backgroundColor: 'white',
          borderRadius: '10px',
          boxShadow: '0 10px 30px rgba(0,0,0,0.3)',
          zIndex: 999,
          border: '2px solid #dee2e6'
        }}>
          <BackendTest />
        </div>
      )}

      {/* Main Application */}
      <HomePage />
    </div>
  )
}

export default App