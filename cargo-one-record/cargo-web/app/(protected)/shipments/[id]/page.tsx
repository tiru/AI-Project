"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { ArrowLeft, Package, Clock, FileText, Plus } from "lucide-react";
import {
  getShipment, getShipmentLogisticsEvents, getShipmentAuditTrail,
  createLogisticsEvent
} from "@/lib/api";
import { Shipment, LogisticsEvent, AuditTrailEntry } from "@/types";
import StatusBadge from "@/components/common/StatusBadge";
import { useAuth } from "@/context/AuthContext";

const EVENT_CODES = ["RCS","DEP","ARR","DLV","NFD","AWD","PRE","MAN","TFD","CCD","AWR"];

export default function ShipmentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { isOperator } = useAuth();
  const [shipment, setShipment]   = useState<Shipment | null>(null);
  const [events, setEvents]       = useState<LogisticsEvent[]>([]);
  const [audit, setAudit]         = useState<AuditTrailEntry[]>([]);
  const [tab, setTab]             = useState<"details" | "events" | "audit">("details");
  const [loading, setLoading]     = useState(true);
  const [showEventForm, setShowEventForm] = useState(false);
  const [eventForm, setEventForm] = useState({ eventCode: "RCS", eventDate: "", location: "", description: "" });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!id) return;
    const load = async () => {
      try {
        const [s, e, a] = await Promise.all([
          getShipment(id),
          getShipmentLogisticsEvents(id),
          getShipmentAuditTrail(id),
        ]);
        setShipment(s.data);
        setEvents(e.data);
        setAudit(a.data);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  const submitEvent = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await createLogisticsEvent(id, {
        eventCode:   eventForm.eventCode,
        eventDate:   eventForm.eventDate || undefined,
        location:    eventForm.location  || undefined,
        description: eventForm.description || undefined,
      });
      const res = await getShipmentLogisticsEvents(id);
      setEvents(res.data);
      setShowEventForm(false);
      setEventForm({ eventCode: "RCS", eventDate: "", location: "", description: "" });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="animate-pulse space-y-4"><div className="h-8 bg-gray-200 rounded w-64" /><div className="h-48 bg-gray-200 rounded" /></div>;
  if (!shipment) return <div className="text-gray-500">Shipment not found.</div>;

  return (
    <div className="max-w-4xl">
      <div className="flex items-center gap-3 mb-6">
        <Link href="/shipments" className="text-gray-400 hover:text-gray-600"><ArrowLeft className="w-5 h-5" /></Link>
        <div className="flex-1">
          <div className="flex items-center gap-3">
            <h1 className="text-2xl font-bold text-gray-900">{shipment.goodsDescription}</h1>
          </div>
          <p className="text-sm text-gray-500 mt-0.5">{shipment.shipperName} → {shipment.consigneeName}</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 border-b border-gray-200 mb-6">
        {(["details", "events", "audit"] as const).map(t => (
          <button key={t} onClick={() => setTab(t)}
            className={`px-4 py-2 text-sm font-medium capitalize transition-colors border-b-2 -mb-px ${
              tab === t ? "border-blue-600 text-blue-600" : "border-transparent text-gray-500 hover:text-gray-700"
            }`}>
            {t === "events" ? `Events (${events.length})` : t === "audit" ? `Audit Trail (${audit.length})` : t}
          </button>
        ))}
      </div>

      {tab === "details" && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="card">
            <div className="flex items-center gap-2 mb-4">
              <Package className="w-4 h-4 text-blue-600" />
              <h3 className="font-semibold text-gray-900">Shipment Details</h3>
            </div>
            <dl className="space-y-3 text-sm">
              <div className="flex justify-between"><dt className="text-gray-500">Shipper</dt><dd className="font-medium">{shipment.shipperName ?? "—"}</dd></div>
              <div className="flex justify-between"><dt className="text-gray-500">Consignee</dt><dd>{shipment.consigneeName ?? "—"}</dd></div>
              <div className="flex justify-between"><dt className="text-gray-500">Gross Weight</dt><dd>{shipment.totalGrossWeight ? `${shipment.totalGrossWeight.value} ${shipment.totalGrossWeight.unit}` : "—"}</dd></div>
              <div className="flex justify-between"><dt className="text-gray-500">Pieces</dt><dd>{shipment.pieceCount ?? 0}</dd></div>
              {shipment.specialHandlingCodes?.length ? (
                <div><dt className="text-gray-500 mb-1">Special Handling</dt>
                  <dd className="flex flex-wrap gap-1">{shipment.specialHandlingCodes.map(c => (
                    <span key={c} className="px-2 py-0.5 bg-yellow-100 text-yellow-800 text-xs rounded font-mono">{c}</span>
                  ))}</dd>
                </div>
              ) : null}
            </dl>
          </div>

          <div className="card">
            <div className="flex items-center gap-2 mb-4">
              <FileText className="w-4 h-4 text-blue-600" />
              <h3 className="font-semibold text-gray-900">Linked Objects</h3>
            </div>
            <dl className="space-y-3 text-sm">
              <div className="flex justify-between">
                <dt className="text-gray-500">Waybill</dt>
                <dd>{shipment.waybillNumber
                  ? <Link href="/waybills" className="text-blue-600 hover:underline">{shipment.waybillNumber}</Link>
                  : "—"}
                </dd>
              </div>
              <div className="flex justify-between"><dt className="text-gray-500">ID</dt><dd className="font-mono text-xs text-gray-400">{shipment.id}</dd></div>
            </dl>
          </div>
        </div>
      )}

      {tab === "events" && (
        <div>
          {isOperator() && (
            <div className="mb-4">
              {!showEventForm ? (
                <button className="btn-primary" onClick={() => setShowEventForm(true)}>
                  <Plus className="w-4 h-4" /> Record Event
                </button>
              ) : (
                <div className="card mb-4">
                  <h3 className="font-semibold text-gray-900 mb-4">Record Logistics Event</h3>
                  <form onSubmit={submitEvent} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="label">Event Code</label>
                        <select className="input" value={eventForm.eventCode}
                          onChange={e => setEventForm(p => ({ ...p, eventCode: e.target.value }))}>
                          {EVENT_CODES.map(c => <option key={c}>{c}</option>)}
                        </select>
                      </div>
                      <div>
                        <label className="label">Event Date/Time</label>
                        <input className="input" type="datetime-local" value={eventForm.eventDate}
                          onChange={e => setEventForm(p => ({ ...p, eventDate: e.target.value }))} />
                      </div>
                    </div>
                    <div>
                      <label className="label">Location</label>
                      <input className="input" placeholder="e.g. IATA airport code" value={eventForm.location}
                        onChange={e => setEventForm(p => ({ ...p, location: e.target.value }))} />
                    </div>
                    <div>
                      <label className="label">Description</label>
                      <input className="input" placeholder="Optional notes" value={eventForm.description}
                        onChange={e => setEventForm(p => ({ ...p, description: e.target.value }))} />
                    </div>
                    <div className="flex gap-3">
                      <button type="submit" disabled={submitting} className="btn-primary">
                        {submitting ? "Saving..." : "Save Event"}
                      </button>
                      <button type="button" className="btn-secondary" onClick={() => setShowEventForm(false)}>Cancel</button>
                    </div>
                  </form>
                </div>
              )}
            </div>
          )}

          {events.length === 0 ? (
            <div className="card text-center py-12 text-gray-400">No logistics events recorded yet.</div>
          ) : (
            <div className="relative">
              <div className="absolute left-5 top-0 bottom-0 w-0.5 bg-gray-200" />
              <div className="space-y-4">
                {events.map((ev, i) => (
                  <div key={ev.id ?? i} className="relative flex gap-4">
                    <div className="relative z-10 flex-shrink-0">
                      <div className="w-10 h-10 rounded-full bg-white border-2 border-blue-600 flex items-center justify-center">
                        <Clock className="w-4 h-4 text-blue-600" />
                      </div>
                    </div>
                    <div className="card flex-1 py-3 px-4">
                      <div className="flex items-center gap-2 mb-1">
                        <StatusBadge status={ev.eventCode} />
                        {ev.location && <span className="text-xs text-gray-500">{ev.location}</span>}
                        <span className="ml-auto text-xs text-gray-400">
                          {ev.eventDate ? new Date(ev.eventDate).toLocaleString() : ""}
                        </span>
                      </div>
                      {ev.description && <p className="text-sm text-gray-600">{ev.description}</p>}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {tab === "audit" && (
        <div className="card p-0 overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="table-th">Action</th>
                <th className="table-th">Performed By</th>
                <th className="table-th">Timestamp</th>
                <th className="table-th">Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {audit.length === 0 ? (
                <tr><td colSpan={4} className="table-td text-center text-gray-400 py-8">No audit entries</td></tr>
              ) : audit.map((a, i) => (
                <tr key={i} className="hover:bg-gray-50">
                  <td className="table-td"><span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded">{a.action}</span></td>
                  <td className="table-td">{a.performedBy}</td>
                  <td className="table-td text-xs text-gray-400">{a.timestamp ? new Date(a.timestamp).toLocaleString() : "—"}</td>
                  <td className="table-td text-xs text-gray-500 max-w-xs truncate">{a.details}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}