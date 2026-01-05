import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { fetchStats } from '../api';

export default function Dashboard() {
  const [stats, setStats] = useState({ total_transactions: 0, total_amount: 0, success_rate: 0 });
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const merchant = JSON.parse(localStorage.getItem('merchant') || 'null');

  useEffect(() => {
    if (!merchant) {
      navigate('/login');
      return;
    }
    const load = async () => {
      try {
        const result = await fetchStats(merchant.api_key, merchant.api_secret);
        setStats(result);
      } catch (err) {
        setError(err?.error?.description || 'Failed to load stats');
      }
    };
    load();
  }, [merchant, navigate]);

  if (!merchant) return null;

  return (
    <div className="container">
      <div className="nav">
        <Link to="/dashboard">Home</Link>
        <Link to="/dashboard/transactions">Transactions</Link>
      </div>
      <div className="card" data-test-id="dashboard">
        <h2>API Credentials</h2>
        <div data-test-id="api-credentials" style={{ display: 'grid', gap: 12 }}>
          <div>
            <label className="label">API Key</label>
            <div className="value" data-test-id="api-key">{merchant.api_key}</div>
          </div>
          <div>
            <label className="label">API Secret</label>
            <div className="value" data-test-id="api-secret">{merchant.api_secret}</div>
          </div>
        </div>
        <div className="flex" data-test-id="stats-container" style={{ marginTop: 20 }}>
          <div className="card stat">
            <div className="label">Total Transactions</div>
            <div className="value" data-test-id="total-transactions">{stats.total_transactions}</div>
          </div>
          <div className="card stat">
            <div className="label">Total Amount</div>
            <div className="value" data-test-id="total-amount">₹{stats.total_amount}</div>
          </div>
          <div className="card stat">
            <div className="label">Success Rate</div>
            <div className="value" data-test-id="success-rate">{stats.success_rate}%</div>
          </div>
        </div>
        {error && <p style={{ color: '#f87171', marginTop: 10 }}>{error}</p>}
      </div>
    </div>
  );
}
