export type UserRole =
  | "ADMIN"
  | "DOCTOR"
  | "PATIENT"
  | "RECEPTIONIST"
  | "CLIENT"
  | "PSYCHOLOGIST";

export type AppointmentStatus =
  | "BOOKED"
  | "CANCELLED"
  | "COMPLETED"
  | "CREATED"
  | "CONFIRMED"
  | "IN_PROGRESS"
  | "NO_SHOW";

export type MessageType = "CHAT" | "NOTE" | "SYSTEM";

export type KnowledgeCategory =
  | "PREVENTION"
  | "DISEASES"
  | "DIAGNOSTICS"
  | "TREATMENT"
  | "REHABILITATION"
  | "NUTRITION"
  | "MENTAL_HEALTH"
  | "FAQ"
  | "NEWS";

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  traceId: string;
  validationErrors: Record<string, string> | null;
}

export interface AuthResponse {
  token: string;
  userId: number;
  role: UserRole;
  name: string;
  email: string;
}

export interface Clinic {
  id: number;
  name: string;
  city: string;
  address: string;
  phone?: string;
  email?: string;
  description?: string;
  active: boolean;
}

export interface Department {
  id: number;
  name: string;
  description?: string;
  clinicId: number;
  clinicName: string;
}

export interface MedicalService {
  id: number;
  code: string;
  name: string;
  description?: string;
  durationMinutes: number;
  basePrice: number;
  departmentId: number;
  departmentName: string;
}

export interface UserInfo {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  phone?: string;
  specialization?: string;
  yearsOfExperience?: number;
  about?: string;
  clinicId?: number;
  clinicName?: string;
  active: boolean;
}

export interface PublicDoctorSummary {
  id: number;
  fullName: string;
  specialization?: string;
  clinic?: string;
  shortBio?: string;
  avatarUrl?: string | null;
  experienceYears?: number;
  tags?: string[];
}

export interface PublicDoctorDetails {
  id: number;
  fullName: string;
  specialization?: string;
  clinic?: string;
  clinicCity?: string;
  clinicAddress?: string;
  shortBio?: string;
  fullBio?: string;
  avatarUrl?: string | null;
  experienceYears?: number;
  tags?: string[];
}

export interface Appointment {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  clinicId: number;
  clinicName: string;
  departmentId?: number;
  departmentName?: string;
  medicalServiceId?: number;
  medicalServiceName?: string;
  time: string;
  endTime?: string;
  status: AppointmentStatus;
  complaint?: string;
  diagnosis?: string;
  treatmentPlan?: string;
  notes?: string;
  cancellationReason?: string;
}

export interface MessageEvent {
  id: number;
  senderId: number;
  senderName: string;
  appointmentId: number;
  type: MessageType;
  text: string;
  metadata?: string;
  time: string;
}

export interface AuditEvent {
  id: number;
  entityName: string;
  entityId: number;
  action: string;
  actorId: number;
  actorEmail: string;
  details: string;
  createdAt: string;
}

export interface DashboardSummary {
  clinics: number;
  doctors: number;
  patients: number;
  appointments: number;
  articles: number;
}

export interface Article {
  id: number;
  slug: string;
  title: string;
  summary?: string;
  content: string;
  category: KnowledgeCategory;
  tags?: string;
  published: boolean;
  authorId?: number;
  authorName?: string;
  publishedAt?: string;
  createdAt: string;
  updatedAt: string;
}
