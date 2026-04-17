import axios from "axios";
import type {
  AuthResponse, LoginRequest, RegisterRequest,
  Shipment, ShipmentRequest, Page,
  Piece, Waybill, WaybillRequest,
  Company, CompanyRequest,
  LogisticsEvent, LogisticsEventRequest,
  BookingRequestDto, BookingRequestPayload,
  Booking,
  ChangeRequestDto, ChangeRequestPayload,
  AuditTrailEntry,
} from "@/types";

const BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

const http = axios.create({ baseURL: BASE });

// Attach JWT on every request
http.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ── Auth ────────────────────────────────────────────────────────────
export const login    = (data: LoginRequest)    => http.post<AuthResponse>("/auth/login", data);
export const register = (data: RegisterRequest) => http.post<AuthResponse>("/auth/register", data);

// ── Server Info ─────────────────────────────────────────────────────
export const getServerInfo = () => http.get("/");

// ── Shipments ────────────────────────────────────────────────────────
export const getShipments   = (page = 0, size = 10) =>
  http.get<Page<Shipment>>(`/logistics-objects/shipments?page=${page}&size=${size}`);
export const getShipment    = (id: string) =>
  http.get<Shipment>(`/logistics-objects/shipments/${id}`);
export const createShipment = (data: ShipmentRequest) =>
  http.post<Shipment>("/logistics-objects/shipments", data);
export const updateShipment = (id: string, data: ShipmentRequest) =>
  http.put<Shipment>(`/logistics-objects/shipments/${id}`, data);
export const deleteShipment = (id: string) =>
  http.delete(`/logistics-objects/shipments/${id}`);

// ── Shipment sub-resources ───────────────────────────────────────────
export const getShipmentLogisticsEvents = (shipmentId: string) =>
  http.get<LogisticsEvent[]>(`/logistics-objects/shipments/${shipmentId}/events`);
export const createLogisticsEvent = (shipmentId: string, data: LogisticsEventRequest) =>
  http.post<LogisticsEvent>(`/logistics-objects/shipments/${shipmentId}/events`, data);
export const getShipmentAuditTrail = (shipmentId: string) =>
  http.get<AuditTrailEntry[]>(`/logistics-objects/shipments/${shipmentId}/audit-trail`);

// ── Pieces ───────────────────────────────────────────────────────────
export const getPiecesByShipment = (shipmentId: string) =>
  http.get<Page<Piece>>(`/logistics-objects/pieces/shipment/${shipmentId}`);

// ── Waybills ─────────────────────────────────────────────────────────
export const getWaybills   = (page = 0, size = 10) =>
  http.get<Page<Waybill>>(`/logistics-objects/waybills?page=${page}&size=${size}`);
export const getWaybill    = (id: string) =>
  http.get<Waybill>(`/logistics-objects/waybills/${id}`);
export const createWaybill = (data: WaybillRequest) =>
  http.post<Waybill>("/logistics-objects/waybills", data);

// ── Companies ─────────────────────────────────────────────────────────
export const getCompanies  = (page = 0, size = 10) =>
  http.get<Page<Company>>(`/logistics-objects/companies?page=${page}&size=${size}`);
export const getCompany    = (id: string) =>
  http.get<Company>(`/logistics-objects/companies/${id}`);
export const createCompany = (data: CompanyRequest) =>
  http.post<Company>("/logistics-objects/companies", data);
export const updateCompany = (id: string, data: CompanyRequest) =>
  http.put<Company>(`/logistics-objects/companies/${id}`, data);
export const deleteCompany = (id: string) =>
  http.delete(`/logistics-objects/companies/${id}`);

// ── Booking Requests ──────────────────────────────────────────────────
export const getBookingRequests   = (page = 0, size = 10) =>
  http.get<Page<BookingRequestDto>>(`/bookings/requests?page=${page}&size=${size}`);
export const createBookingRequest = (data: BookingRequestPayload) =>
  http.post<BookingRequestDto>("/bookings/requests", data);
export const cancelBookingRequest = (id: number) =>
  http.patch(`/bookings/requests/${id}/cancel`);

// ── Bookings ───────────────────────────────────────────────────────────
export const getBookings = (page = 0, size = 10) =>
  http.get<Page<Booking>>(`/bookings?page=${page}&size=${size}`);
export const getBooking  = (id: number) =>
  http.get<Booking>(`/bookings/${id}`);
export const cancelBooking = (id: number) =>
  http.patch<Booking>(`/bookings/${id}/cancel`);

// ── Change Requests ────────────────────────────────────────────────────
export const getChangeRequests   = (page = 0, size = 10) =>
  http.get<Page<ChangeRequestDto>>(`/change-requests?page=${page}&size=${size}`);
export const createChangeRequest = (data: ChangeRequestPayload) =>
  http.post<ChangeRequestDto>("/change-requests", data);
export const approveChangeRequest = (id: number) =>
  http.patch<ChangeRequestDto>(`/change-requests/${id}/approve`);
export const rejectChangeRequest  = (id: number) =>
  http.patch<ChangeRequestDto>(`/change-requests/${id}/reject`);
export const revokeChangeRequest  = (id: number) =>
  http.patch<ChangeRequestDto>(`/change-requests/${id}/revoke`);

// ── Audit Trail ────────────────────────────────────────────────────────
export const getAuditTrail = (objectId: string) =>
  http.get<AuditTrailEntry[]>(`/logistics-objects/${objectId}/audit-trail`);