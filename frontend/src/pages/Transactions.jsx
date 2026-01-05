import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { fetchPayments } from '../api';

export default function Transactions() {
  const [payments, setPayments] = useState([]);
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
        const data = await fetchPayments(merchant.api_key, merchant.api_secret);
        setPayments(data);
      } catch (err) {
        setError(err?.error?.description || 'Failed to load payments');
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
      <div className="card">
        <h2>Transactions</h2>
        {error && <p style={{ color: '#f87171' }}>{error}</p>}
        <table className="table" data-test-id="transactions-table">
          <thead>
            <tr>
              <th>Payment ID</th>
              <th>Order ID</th>
              <th>Amount</th>
              <th>Method</th>
              <th>Status</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            {payments.map((p) => (
              <tr key={p.id} data-test-id="transaction-row" data-payment-id={p.id}>
                <td data-test-id="payment-id">{p.id}</td>
                <td data-test-id="order-id">{p.order_id}</td>
                <td data-test-id="amount">{p.amount}</td>
                <td data-test-id="method">{p.method}</td>
                <td data-test-id="status">{p.status}</td>
                <td data-test-id="created-at">{new Date(p.created_at).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
