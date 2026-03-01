import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "./components/AppShell";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { RoleRoute } from "./components/RoleRoute";
import { useAuth } from "./auth";
import { ArticlePage } from "./pages/ArticlePage";
import { AdminWorkspacePage } from "./pages/AdminWorkspacePage";
import { AppointmentsPage } from "./pages/AppointmentsPage";
import { AuditPage } from "./pages/AuditPage";
import { ClientWorkspacePage } from "./pages/ClientWorkspacePage";
import { ClinicsPage } from "./pages/ClinicsPage";
import { DashboardPage } from "./pages/DashboardPage";
import { DoctorDetailsPage } from "./pages/DoctorDetailsPage";
import { DoctorsDirectoryPage } from "./pages/DoctorsDirectoryPage";
import { DoctorWorkspacePage } from "./pages/DoctorWorkspacePage";
import { EventsPage } from "./pages/EventsPage";
import { KnowledgeAdminPage } from "./pages/KnowledgeAdminPage";
import { LoginPage } from "./pages/LoginPage";
import { PublicKnowledgePage } from "./pages/PublicKnowledgePage";
import { ReceptionWorkspacePage } from "./pages/ReceptionWorkspacePage";
import { RegisterPage } from "./pages/RegisterPage";
import { UsersPage } from "./pages/UsersPage";
import { getWorkspacePath } from "./role";

function NotFoundPage() {
  return (
    <section className="panel panel-narrow">
      <h2>Page Not Found</h2>
      <p className="muted">The requested route does not exist.</p>
    </section>
  );
}

function WorkspaceRedirectPage() {
  const { session } = useAuth();
  return <Navigate to={getWorkspacePath(session?.role)} replace />;
}

export default function App() {
  return (
    <Routes>
      <Route element={<AppShell />}>
        <Route index element={<PublicKnowledgePage />} />
        <Route path="/health-library" element={<PublicKnowledgePage />} />
        <Route path="/knowledge" element={<PublicKnowledgePage />} />
        <Route path="/knowledge/:slug" element={<ArticlePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/workspace"
          element={
            <ProtectedRoute>
              <WorkspaceRedirectPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/patient-app"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["PATIENT", "CLIENT"]}>
                <ClientWorkspacePage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route path="/client-app" element={<Navigate to="/patient-app" replace />} />
        <Route
          path="/reception-app"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["RECEPTIONIST"]}>
                <ReceptionWorkspacePage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctor-app"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["DOCTOR", "PSYCHOLOGIST"]}>
                <DoctorWorkspacePage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin-app"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["ADMIN"]}>
                <AdminWorkspacePage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["ADMIN", "RECEPTIONIST", "DOCTOR", "PSYCHOLOGIST"]}>
                <DashboardPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/clinics"
          element={
            <ProtectedRoute>
              <ClinicsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["ADMIN", "RECEPTIONIST", "DOCTOR", "PSYCHOLOGIST"]}>
                <UsersPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctors"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["PATIENT", "CLIENT"]}>
                <DoctorsDirectoryPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctors/:id"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["PATIENT", "CLIENT"]}>
                <DoctorDetailsPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/appointments"
          element={
            <ProtectedRoute>
              <AppointmentsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/events"
          element={
            <ProtectedRoute>
              <EventsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/audit"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["ADMIN", "RECEPTIONIST", "DOCTOR", "PSYCHOLOGIST"]}>
                <AuditPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/knowledge-admin"
          element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={["ADMIN", "DOCTOR", "PSYCHOLOGIST"]}>
                <KnowledgeAdminPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />

        <Route path="/home" element={<Navigate to="/" replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
