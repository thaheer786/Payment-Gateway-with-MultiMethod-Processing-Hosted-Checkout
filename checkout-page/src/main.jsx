import React from 'react';
import ReactDOM from 'react-dom/client';
import Checkout from './pages/Checkout';
import Success from './pages/Success';
import Failure from './pages/Failure';
import './styles.css';

function Router() {
  const params = new URLSearchParams(window.location.search);
  const status = params.get('status');
  if (status === 'success') return <Success />;
  if (status === 'failure') return <Failure />;
  return <Checkout />;
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Router />
  </React.StrictMode>,
);
