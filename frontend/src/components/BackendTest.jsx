import React, { useState, useEffect } from 'react';

const BackendTest = () => {
  const [healthData, setHealthData] = useState(null);
  const [testData, setTestData] = useState(null);
  const [echoData, setEchoData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const API_BASE_URL = 'http://localhost:8080/api';

  // Test health endpoint
  const testHealth = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${API_BASE_URL}/health`);
      if (!response.ok) throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      const data = await response.json();
      setHealthData(data);
      console.log('Health check successful:', data);
    } catch (err) {
      setError(`Health Error: ${err.message}`);
      console.error('Health check failed:', err);
    } finally {
      setLoading(false);
    }
  };

  // Test simple GET endpoint
  const testGet = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${API_BASE_URL}/test`);
      if (!response.ok) throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      const data = await response.json();
      setTestData(data);
      console.log('GET test successful:', data);
    } catch (err) {
      setError(`GET Error: ${err.message}`);
      console.error('GET test failed:', err);
    } finally {
      setLoading(false);
    }
  };

  // Test POST endpoint
  const testPost = async () => {
    setLoading(true);
    setError(null);
    try {
      const payload = {
        message: 'Hello from React!',
        timestamp: new Date().toISOString(),
        source: 'VolunteerSync Frontend'
      };

      const response = await fetch(`${API_BASE_URL}/echo`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      const data = await response.json();
      setEchoData(data);
      console.log('POST test successful:', data);
    } catch (err) {
      setError(`POST Error: ${err.message}`);
      console.error('POST test failed:', err);
    } finally {
      setLoading(false);
    }
  };

  // Auto-test health on component mount
  useEffect(() => {
    testHealth();
  }, []);

  return (
    <div style={{ 
      padding: '20px', 
      fontFamily: 'Arial, sans-serif',
      maxWidth: '800px',
      margin: '0 auto'
    }}>
      <h2>🔌 Backend Connection Test</h2>
      <p>Testing connection to VolunteerSync Backend API</p>
      
      {loading && (
        <p style={{ 
          color: '#007bff', 
          fontWeight: 'bold',
          padding: '10px',
          backgroundColor: '#e7f3ff',
          borderRadius: '5px'
        }}>
          🔄 Loading...
        </p>
      )}
      
      {error && (
        <p style={{ 
          color: '#dc3545', 
          fontWeight: 'bold',
          padding: '10px',
          backgroundColor: '#f8d7da',
          borderRadius: '5px',
          border: '1px solid #f5c6cb'
        }}>
          ❌ {error}
        </p>
      )}

      <div style={{ marginBottom: '20px' }}>
        <button 
          onClick={testHealth} 
          disabled={loading}
          style={{ 
            padding: '10px 15px', 
            margin: '5px',
            backgroundColor: '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.6 : 1
          }}
        >
          🏥 Test Health
        </button>
        
        <button 
          onClick={testGet} 
          disabled={loading}
          style={{ 
            padding: '10px 15px', 
            margin: '5px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.6 : 1
          }}
        >
          📡 Test GET
        </button>
        
        <button 
          onClick={testPost} 
          disabled={loading}
          style={{ 
            padding: '10px 15px', 
            margin: '5px',
            backgroundColor: '#6f42c1',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.6 : 1
          }}
        >
          📤 Test POST
        </button>
      </div>

      {/* Health Data */}
      {healthData && (
        <div style={{ 
          marginBottom: '20px', 
          padding: '15px', 
          border: '2px solid #28a745', 
          borderRadius: '8px',
          backgroundColor: '#d4edda'
        }}>
          <h3>✅ Health Check Response:</h3>
          <pre style={{ 
            backgroundColor: '#f8f9fa', 
            padding: '10px', 
            borderRadius: '5px',
            overflow: 'auto'
          }}>
            {JSON.stringify(healthData, null, 2)}
          </pre>
        </div>
      )}

      {/* Test Data */}
      {testData && (
        <div style={{ 
          marginBottom: '20px', 
          padding: '15px', 
          border: '2px solid #007bff', 
          borderRadius: '8px',
          backgroundColor: '#d1ecf1'
        }}>
          <h3>📡 GET Test Response:</h3>
          <pre style={{ 
            backgroundColor: '#f8f9fa', 
            padding: '10px', 
            borderRadius: '5px',
            overflow: 'auto'
          }}>
            {JSON.stringify(testData, null, 2)}
          </pre>
        </div>
      )}

      {/* Echo Data */}
      {echoData && (
        <div style={{ 
          marginBottom: '20px', 
          padding: '15px', 
          border: '2px solid #6f42c1', 
          borderRadius: '8px',
          backgroundColor: '#e2d9f3'
        }}>
          <h3>🔄 POST Echo Response:</h3>
          <pre style={{ 
            backgroundColor: '#f8f9fa', 
            padding: '10px', 
            borderRadius: '5px',
            overflow: 'auto'
          }}>
            {JSON.stringify(echoData, null, 2)}
          </pre>
        </div>
      )}

      <div style={{ 
        marginTop: '20px', 
        padding: '15px', 
        backgroundColor: '#f8f9fa', 
        borderRadius: '8px',
        border: '1px solid #dee2e6'
      }}>
        <h4>🔗 Connection Info:</h4>
        <p><strong>Backend URL:</strong> <code>{API_BASE_URL}</code></p>
        <p><strong>Frontend URL:</strong> <code>http://localhost:3000</code></p>
        <p><strong>Status:</strong> {healthData ? '🟢 Connected' : '🔴 Not Connected'}</p>
        <p><strong>Last Updated:</strong> {new Date().toLocaleTimeString()}</p>
      </div>
    </div>
  );
};

export default BackendTest;