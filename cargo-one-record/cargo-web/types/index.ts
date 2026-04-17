// ── Auth ────────────────────────────────────────────────────────────
export interface LoginRequest  { username: string; password: string }
export interface RegisterRequest {
  username: string; password: string; email: string; fullName?: string;
  role?: string; companyIdentifier?: string;
}
export interface AuthResponse { accessToken: string; token?: string; username: string; role: string }

// ── Shipment ────────────────────────────────────────────────────────
export interface Shipment {
  id: string;
  logisticsObjectRef?: string;
  goodsDescription?: string;
  shipperName?: string;
  consigneeName?: string;
  totalGrossWeight?: WeightDto;
  declaredValueForCarriage?: number;
  declaredValueCurrency?: string;
  specialHandlingCodes?: string[];
  pieceCount?: number;
  waybillNumber?: string;
  revision?: number;
  createdAt?: string; updatedAt?: string;
}
export interface WeightDto { value: number; unit: string }

export interface ShipmentRequest {
  goodsDescription: string;
  shipperName: string;
  consigneeName: string;
  totalGrossWeight?: WeightDto;
  declaredValueForCarriage?: number;
  declaredValueCurrency?: string;
  specialHandlingCodes?: string[];
}

// ── Piece ───────────────────────────────────────────────────────────
export interface Piece {
  id: string; shipmentId?: string;
  grossWeight?: number; grossWeightUnit?: string;
  loadType?: string; stackable?: boolean;
  createdAt?: string;
}

// ── Waybill ─────────────────────────────────────────────────────────
export interface Waybill {
  id: string;
  waybillNumber?: string;
  waybillType?: string;
  carrierCode?: string;
  originCode?: string;
  originName?: string;
  destinationCode?: string;
  destinationName?: string;
  issueDate?: string;
  numberOfPieces?: number;
  totalWeight?: number;
  totalWeightUnit?: string;
  shipmentId?: string;
  revision?: number;
  createdAt?: string;
}
export interface WaybillRequest {
  waybillNumber: string;
  waybillType: string;
  carrierCode: string;
  originCode: string;
  originName?: string;
  destinationCode: string;
  destinationName?: string;
  issueDate?: string;
  numberOfPieces?: number;
  totalWeight?: number;
  totalWeightUnit?: string;
  shipmentId?: string;
}

// ── Company ─────────────────────────────────────────────────────────
export interface AddressDto {
  streetAddressLine1?: string;
  cityName?: string;
  postalCode?: string;
  countryCode?: string;
  iataLocationCode?: string;
}
export interface Company {
  id: string;
  name: string;
  shortName?: string;
  companyType?: string;
  iataCarrierCode?: string;
  icaoCode?: string;
  cassCode?: string;
  address?: AddressDto;
  createdAt?: string;
}
export interface CompanyRequest {
  name: string;
  shortName?: string;
  companyType: string;
  iataCarrierCode?: string;
  icaoCode?: string;
  cassCode?: string;
  taxId?: string;
  address?: AddressDto;
}

// ── LogisticsEvent ──────────────────────────────────────────────────
export interface LogisticsEvent {
  id?: string;
  eventCode: string;
  eventDate?: string;
  location?: string;
  description?: string;
  recordedBy?: string;
  createdAt?: string;
}
export interface LogisticsEventRequest {
  eventCode: string;
  eventDate?: string;
  location?: string;
  description?: string;
}

// ── BookingRequest ──────────────────────────────────────────────────
export interface BookingRequestDto {
  id: number;
  shipment?: Shipment;
  origin?: string;
  destination?: string;
  requestedFlightDate?: string;
  status: string;
  createdAt?: string;
}
export interface BookingRequestPayload {
  shipmentId: number;
  origin?: string;
  destination?: string;
  requestedFlightDate?: string;
}

// ── Booking ─────────────────────────────────────────────────────────
export interface Booking {
  id: number;
  bookingReference?: string;
  shipment?: Shipment;
  flightNumber?: string;
  origin?: string;
  destination?: string;
  departureDate?: string;
  status: string;
  createdAt?: string; updatedAt?: string;
}

// ── ChangeRequest ───────────────────────────────────────────────────
export interface ChangeRequestDto {
  id: number;
  shipment?: Shipment;
  changeType: string;
  description: string;
  proposedValue?: string;
  status: string;
  requestedBy?: string;
  createdAt?: string;
}
export interface ChangeRequestPayload {
  shipmentId: number;
  changeType: string;
  description: string;
  proposedValue?: string;
}

// ── AuditTrail ──────────────────────────────────────────────────────
export interface AuditTrailEntry {
  id?: string;
  action?: string;
  performedBy?: string;
  timestamp?: string;
  details?: string;
  operation?: string;
  changedBy?: string;
  changedAt?: string;
}

// ── Pagination ──────────────────────────────────────────────────────
export interface Page<T> {
  content: T[]; totalElements: number; totalPages: number;
  number: number; size: number;
}