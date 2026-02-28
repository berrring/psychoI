import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../auth";
import type { UserRole } from "../types";

interface RoleRouteProps {
  allowedRoles: UserRole[];
  children: React.ReactNode;
}

export function RoleRoute({ allowedRoles, children }: RoleRouteProps) {
  const { isAuthenticated, session } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (!session || !allowedRoles.includes(session.role)) {
    return (
      <section className="panel panel-narrow">
        <h2>Access Restricted</h2>
        <p className="muted">Your account role does not have access to this area.</p>
      </section>
    );
  }

  return <>{children}</>;
}
