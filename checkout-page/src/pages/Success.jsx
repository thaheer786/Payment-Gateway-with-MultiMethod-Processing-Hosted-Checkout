import React from 'react';

export default function Success() {
  return (
    <div className="container">
      <div className="card" data-test-id="success-state">
        <h2>Payment Successful!</h2>
        <span data-test-id="success-message">Your payment has been processed successfully</span>
      </div>
    </div>
  );
}
