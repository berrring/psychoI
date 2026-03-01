import { NavLink, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../auth";
import { isDoctorRole, isPatientRole } from "../role";
import type { UserRole } from "../types";

interface NavItem {
  to: string;
  label: string;
}

function getHeaderLinks(isAuthenticated: boolean, role: UserRole | undefined): NavItem[] {
  if (!isAuthenticated) {
    return [{ to: "/", label: "Health Library" }];
  }

  if (role === "ADMIN") {
    return [
      { to: "/admin-app", label: "Admin App" },
      { to: "/clinics", label: "Clinics" },
      { to: "/users", label: "People" },
      { to: "/knowledge-admin", label: "Content" }
    ];
  }

  if (isDoctorRole(role)) {
    return [
      { to: "/doctor-app", label: "Doctor App" },
      { to: "/appointments", label: "Appointments" },
      { to: "/events", label: "Timeline" },
      { to: "/", label: "Health Library" }
    ];
  }

  if (role === "RECEPTIONIST") {
    return [
      { to: "/reception-app", label: "Reception App" },
      { to: "/appointments", label: "Appointments" },
      { to: "/users", label: "People" },
      { to: "/dashboard", label: "Dashboard" }
    ];
  }

  if (isPatientRole(role)) {
    return [
      { to: "/patient-app", label: "Patient App" },
      { to: "/doctors", label: "Doctors" },
      { to: "/appointments", label: "Booking" },
      { to: "/", label: "Health Library" }
    ];
  }

  return [
    { to: "/workspace", label: "Workspace" },
    { to: "/", label: "Health Library" }
  ];
}

function getPortalLinks(role: UserRole | undefined): NavItem[] {
  if (role === "ADMIN") {
    return [
      { to: "/admin-app", label: "Admin App" },
      { to: "/dashboard", label: "KPI Dashboard" },
      { to: "/clinics", label: "Clinics" },
      { to: "/users", label: "People" },
      { to: "/knowledge-admin", label: "Knowledge" },
      { to: "/audit", label: "Audit" }
    ];
  }

  if (isDoctorRole(role)) {
    return [
      { to: "/doctor-app", label: "Doctor App" },
      { to: "/appointments", label: "My Appointments" },
      { to: "/events", label: "Visit Events" },
      { to: "/knowledge-admin", label: "Clinical Articles" }
    ];
  }

  if (role === "RECEPTIONIST") {
    return [
      { to: "/reception-app", label: "Reception App" },
      { to: "/appointments", label: "Appointments" },
      { to: "/users", label: "People" },
      { to: "/clinics", label: "Clinics" },
      { to: "/dashboard", label: "Dashboard" }
    ];
  }

  if (isPatientRole(role)) {
    return [
      { to: "/patient-app", label: "Patient App" },
      { to: "/appointments", label: "My Bookings" },
      { to: "/doctors", label: "Doctor Directory" }
    ];
  }

  return [{ to: "/workspace", label: "Workspace" }];
}

function topLinkClassName({ isActive }: { isActive: boolean }) {
  return `top-link${isActive ? " top-link-active" : ""}`;
}

function sideLinkClassName({ isActive }: { isActive: boolean }) {
  return `nav-link${isActive ? " nav-link-active" : ""}`;
}

export function AppShell() {
  const { isAuthenticated, session, logout } = useAuth();
  const location = useLocation();
  const role = session?.role;
  const canOpenEditorial = role === "ADMIN" || isDoctorRole(role);

  const headerLinks = getHeaderLinks(isAuthenticated, role);
  const portalLinks = getPortalLinks(role);
  const isPublicRoute =
    location.pathname === "/" ||
    location.pathname.startsWith("/knowledge/") ||
    location.pathname === "/login" ||
    location.pathname === "/register";

  const showPortalSidebar = isAuthenticated && !isPublicRoute;

  return (
    <div className="app-shell">
      <header className="site-header">
        <div className="utility-bar">
          <p>National care line: +77 (717) 555-1024</p>
          <span>Integrated diagnostics, treatment and rehabilitation</span>
        </div>
        <div className="topbar">
          <NavLink to="/" className="brand-block">
            <div className="brand-mark" />
            <div>
              <h1>Bering Clinics</h1>
              <p>Professional healthcare network and patient platform</p>
            </div>
          </NavLink>

          <nav className="top-nav">
            {headerLinks.map((item) => (
              <NavLink key={item.to} to={item.to} className={topLinkClassName}>
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="session-block">
            {isAuthenticated ? (
              <>
                <div className="user-pill">
                  <span>{session?.name}</span>
                  <small>{session?.role}</small>
                </div>
                <button type="button" className="danger-btn" onClick={logout}>
                  Logout
                </button>
              </>
            ) : (
              <div className="auth-actions">
                <NavLink to="/login" className={topLinkClassName}>
                  Login
                </NavLink>
                <NavLink to="/register" className="cta-link">
                  Register
                </NavLink>
              </div>
            )}
          </div>
        </div>
      </header>

      <div className={`layout-grid${showPortalSidebar ? " layout-grid-portal" : " layout-grid-public"}`}>
        {showPortalSidebar && (
          <nav className="sidebar">
            <p className="sidebar-title">Role Workspace</p>
            {portalLinks.map((item) => (
              <NavLink key={item.to} to={item.to} className={sideLinkClassName}>
                {item.label}
              </NavLink>
            ))}
            <div className="auth-links">
              <NavLink to="/" className={sideLinkClassName}>
                Public Encyclopedia
              </NavLink>
              {canOpenEditorial && (
                <NavLink to="/knowledge-admin" className={sideLinkClassName}>
                  Editorial
                </NavLink>
              )}
            </div>
          </nav>
        )}

        <main className="content">
          <Outlet />
        </main>
      </div>

      <footer className="site-footer">
        <div className="footer-grid">
          <section className="footer-col">
            <h4>Bering Clinics</h4>
            <p>Professional multi-specialty network for diagnostics, treatment and rehabilitation.</p>
          </section>
          <section className="footer-col">
            <h4>Patient Services</h4>
            <NavLink to="/patient-app" className="footer-link">
              Patient Application
            </NavLink>
            <NavLink to="/doctors" className="footer-link">
              Doctor Directory
            </NavLink>
            <NavLink to="/" className="footer-link">
              Medical Encyclopedia
            </NavLink>
          </section>
          <section className="footer-col">
            <h4>Clinical Platform</h4>
            <NavLink to="/doctor-app" className="footer-link">
              Doctor Workspace
            </NavLink>
            <NavLink to="/admin-app" className="footer-link">
              Admin Workspace
            </NavLink>
            <NavLink to="/audit" className="footer-link">
              Audit and History
            </NavLink>
          </section>
          <section className="footer-col">
            <h4>Contact</h4>
            <p>+77 (717) 555-1024</p>
            <p>support@bering.clinic</p>
            <p>Arsenal Avenue, Astana</p>
            <p>Mon-Sun, 24/7 Patient Line</p>
          </section>
        </div>
      </footer>
    </div>
  );
}
