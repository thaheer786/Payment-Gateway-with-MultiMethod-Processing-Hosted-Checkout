import React, { useEffect, useState } from 'react';

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8000';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw err;
  }
  return response.json();
}

export default function Checkout() {
  const params = new URLSearchParams(window.location.search);
  const orderId = params.get('order_id');
  const [order, setOrder] = useState(null);
  const [method, setMethod] = useState('upi');
  const [vpa, setVpa] = useState('');
  const [card, setCard] = useState({ number: '', expiry: '', cvv: '', name: '' });
  const [processing, setProcessing] = useState(false);
  const [paymentId, setPaymentId] = useState('');
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (!orderId) {
      setError('Missing order_id');
      return;
    }
    request(`/api/v1/orders/${orderId}/public`)
      .then(setOrder)
      .catch(() => setError('Order not found'));
  }, [orderId]);

  useEffect(() => {
    if (!paymentId) return undefined;
    if (status && status !== 'processing') return undefined;
    const interval = setInterval(async () => {
      try {
        const p = await request(`/api/v1/payments/${paymentId}/public`);
        setStatus(p.status);
      } catch (err) {
        setError(err?.error?.description || 'Could not fetch payment');
        clearInterval(interval);
      }
    }, 2000);
    return () => clearInterval(interval);
  }, [paymentId, status]);

  const submitUpi = async (e) => {
    e.preventDefault();
    setProcessing(true);
    setError('');
    try {
      const payment = await request('/api/v1/payments/public', {
        method: 'POST',
        body: JSON.stringify({ order_id: orderId, method: 'upi', vpa }),
      });
      setPaymentId(payment.id);
      setStatus(payment.status);
      if (payment.status === 'failed') setError(payment.error_description || 'Payment failed');
    } catch (err) {
      setError(err?.error?.description || 'Payment failed');
    } finally {
      setProcessing(false);
    }
  };

  const submitCard = async (e) => {
    e.preventDefault();
    setProcessing(true);
    setError('');
    const [expiry_month, expiry_year] = (card.expiry || '').split('/');
    try {
      const payment = await request('/api/v1/payments/public', {
        method: 'POST',
        body: JSON.stringify({
          order_id: orderId,
          method: 'card',
          card: {
            number: card.number,
            expiry_month,
            expiry_year,
            cvv: card.cvv,
            holder_name: card.name,
          },
        }),
      });
      setPaymentId(payment.id);
      setStatus(payment.status);
      if (payment.status === 'failed') setError(payment.error_description || 'Payment failed');
    } catch (err) {
      setError(err?.error?.description || 'Payment failed');
    } finally {
      setProcessing(false);
    }
  };

  const amountDisplay = order ? `₹${(order.amount / 100).toFixed(2)}` : '₹0.00';

  return (
    <div className="container">
      <div className="card" data-test-id="checkout-container">
        <div data-test-id="order-summary">
          <h2>Complete Payment</h2>
          <div><span>Amount: </span><span data-test-id="order-amount">{amountDisplay}</span></div>
          <div><span>Order ID: </span><span data-test-id="order-id">{orderId}</span></div>
        </div>

        <div className="section" data-test-id="payment-methods">
          <button
            data-test-id="method-upi"
            data-method="upi"
            className="badge"
            onClick={() => setMethod('upi')}
          >
            UPI
          </button>
          <button
            data-test-id="method-card"
            data-method="card"
            className="badge"
            onClick={() => setMethod('card')}
          >
            Card
          </button>
        </div>

        <form
          data-test-id="upi-form"
          style={{ display: method === 'upi' ? 'block' : 'none' }}
          onSubmit={submitUpi}
        >
          <input
            data-test-id="vpa-input"
            placeholder="username@bank"
            type="text"
            value={vpa}
            onChange={(e) => setVpa(e.target.value)}
          />
          <button className="button" data-test-id="pay-button" type="submit" disabled={processing}>
            Pay {amountDisplay}
          </button>
        </form>

        <form
          data-test-id="card-form"
          style={{ display: method === 'card' ? 'block' : 'none' }}
          onSubmit={submitCard}
        >
          <input
            data-test-id="card-number-input"
            placeholder="Card Number"
            type="text"
            value={card.number}
            onChange={(e) => setCard({ ...card, number: e.target.value })}
          />
          <input
            data-test-id="expiry-input"
            placeholder="MM/YY"
            type="text"
            value={card.expiry}
            onChange={(e) => setCard({ ...card, expiry: e.target.value })}
          />
          <input
            data-test-id="cvv-input"
            placeholder="CVV"
            type="text"
            value={card.cvv}
            onChange={(e) => setCard({ ...card, cvv: e.target.value })}
          />
          <input
            data-test-id="cardholder-name-input"
            placeholder="Name on Card"
            type="text"
            value={card.name}
            onChange={(e) => setCard({ ...card, name: e.target.value })}
          />
          <button className="button" data-test-id="pay-button" type="submit" disabled={processing}>
            Pay {amountDisplay}
          </button>
        </form>

        <div
          data-test-id="processing-state"
          style={{ display: processing || status === 'processing' ? 'flex' : 'none', alignItems: 'center', marginTop: 12 }}
        >
          <div className="spinner" />
          <span data-test-id="processing-message">Processing payment...</span>
        </div>

        <div data-test-id="success-state" style={{ display: status === 'success' ? 'block' : 'none' }}>
          <h2>Payment Successful!</h2>
          <div>
            <span>Payment ID: </span>
            <span data-test-id="payment-id">{paymentId}</span>
          </div>
          <span data-test-id="success-message">Your payment has been processed successfully</span>
        </div>

        <div data-test-id="error-state" style={{ display: error && status !== 'success' ? 'block' : 'none' }}>
          <h2>Payment Failed</h2>
          <span data-test-id="error-message">{error || 'Payment could not be processed'}</span>
          <button data-test-id="retry-button" className="button" type="button" onClick={() => window.location.reload()}>
            Try Again
          </button>
        </div>
      </div>
    </div>
  );
}
