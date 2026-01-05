import React from 'react';

export default function Failure() {
  return (
    <div className="container">
      <div className="card" data-test-id="error-state">
        <h2>Payment Failed</h2>
        <span data-test-id="error-message">Payment could not be processed</span>
        <button data-test-id="retry-button" className="button" type="button" onClick={() => window.history.back()}>
          Try Again
        </button>
      </div>
    </div>
  );
}
