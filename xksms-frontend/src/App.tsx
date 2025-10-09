import { Link, Route, Routes } from 'react-router-dom';
import DashboardPage from './pages/DashboardPage';
import NotificationsPage from './pages/NotificationsPage';

function App() {
  return (
    <div className="app">
      <header className="app__header">
        <h1>XKSMS Cloud 控制台</h1>
        <nav className="app__nav">
          <Link to="/">概览</Link>
          <Link to="/notifications">通知流</Link>
        </nav>
      </header>
      <main className="app__content">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
        </Routes>
      </main>
      <footer className="app__footer">© {new Date().getFullYear()} XKSMS Cloud</footer>
    </div>
  );
}

export default App;
