import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../api';

export default function Login() {
  const [email, setEmail] = useState('test@example.com');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const merchant = await login(email);
      localStorage.setItem('merchant', JSON.stringify(merchant));
      navigate('/dashboard');
    } catch (err) {
      setError(err?.error?.description || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="card">
        <h2>Merchant Login</h2>
        <p style={{ color: '#94a3b8' }}>Use your test merchant email to view API keys.</p>
        <form data-test-id="login-form" onSubmit={handleSubmit}>
          <input
            data-test-id="email-input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email"
            required
          />
          <input
            data-test-id="password-input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
          />
          <button className="button" data-test-id="login-button" type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Login'}
          </button>
        </form>
        {error && <p style={{ color: '#f87171', marginTop: 10 }}>{error}</p>}
      </div>
    </div>
  );
}
