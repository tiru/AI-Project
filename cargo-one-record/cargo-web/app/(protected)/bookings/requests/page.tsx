"use client";
import { useEffect, useState } from "react";
import { Plus, X, Check } from "lucide-react";
import { getBookingRequests, createBookingRequest, cancelBookingRequest, getShipments } from "@/lib/api";
import { BookingRequestDto, Shipment } from "@/types";
import StatusBadge from "@/components/common/StatusBadge";
import { useAuth } from "@/context/AuthContext";

export default function BookingRequestsPage() {
  const { isOperator } = useAuth();
  const [requests, setRequests]   = useState<BookingRequestDto[]>([]);
  const [shipments, setShipments] = useState<Shipment[]>([]);
  const [total, setTotal]         = useState(0);
  const [page, setPage]           = useState(0);
  const [loading, setLoading]     = useState(true);
  const [showForm, setShowForm]   = useState(false);
  const [form, setForm]           = useState({ shipmentId: "", requestedFlightDate: "", origin: "", destination: "" });
  const [submitting, setSubmitting] = useState(false);
  const size = 10;

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const [br, sh] = await Promise.all([getBookingRequests(p, size), getShipments(0, 100)]);
      setRequests(br.data.content);
      setTotal(br.data.totalElements);
      setShipments(sh.data.content);
      setPage(p);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await createBookingRequest({
        shipmentId:          Number(form.shipmentId),
        requestedFlightDate: form.requestedFlightDate || undefined,
        origin:              form.origin              || undefined,
        destination:         form.destination         || undefined,
      });
      setShowForm(false);
      setForm({ shipmentId: "", requestedFlightDate: "", origin: "", destination: "" });
      load(0);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = async (id: number) => {
    if (!confirm("Cancel this booking request?")) return;
    await cancelBookingRequest(id);
    load(page);
  };

  const totalPages = Math.ceil(total / size);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Booking Requests</h1>
          <p className="text-sm text-gray-500 mt-0.5">{total} total requests</p>
        </div>
        {isOperator() && (
          <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
            <Plus className="w-4 h-4" /> New Request
          </button>
        )}
      </div>

      {showForm && (
        <div className="card mb-6">
          <h3 className="font-semibold text-gray-900 mb-4">Submit Booking Request</h3>
          <form onSubmit={handleCreate} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="label">Shipment <span className="text-red-500">*</span></label>
                <select className="input" value={form.shipmentId}
                  onChange={e => setForm(p => ({ ...p, shipmentId: e.target.value }))} required>
                  <option value="">Select shipment...</option>
                  {shipments.map(s => <option key={s.id} value={s.id}>{s.goodsDescription}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Requested Flight Date</label>
                <input className="input" type="date" value={form.requestedFlightDate}
                  onChange={e => setForm(p => ({ ...p, requestedFlightDate: e.target.value }))} />
              </div>
              <div />
              <div>
                <label className="label">Origin Airport</label>
                <input className="input" placeholder="JFK" value={form.origin}
                  onChange={e => setForm(p => ({ ...p, origin: e.target.value.toUpperCase() }))} />
              </div>
              <div>
                <label className="label">Destination Airport</label>
                <input className="input" placeholder="LHR" value={form.destination}
                  onChange={e => setForm(p => ({ ...p, destination: e.target.value.toUpperCase() }))} />
              </div>
            </div>
            <div className="flex gap-3">
              <button type="submit" disabled={submitting} className="btn-primary">{submitting ? "Submitting..." : "Submit"}</button>
              <button type="button" className="btn-secondary" onClick={() => setShowForm(false)}>Cancel</button>
            </div>
          </form>
        </div>
      )}

      <div className="card p-0 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="table-th">ID</th>
              <th className="table-th">Shipment</th>
              <th className="table-th">Route</th>
              <th className="table-th">Flight Date</th>
              <th className="table-th">Status</th>
              <th className="table-th">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              Array.from({length:4}).map((_,i) => <tr key={i}>{Array.from({length:6}).map((_,j)=>(
                <td key={j} className="table-td"><div className="h-4 bg-gray-200 rounded animate-pulse w-16"/></td>
              ))}</tr>)
            ) : requests.length === 0 ? (
              <tr><td colSpan={6} className="table-td text-center text-gray-400 py-12">No booking requests</td></tr>
            ) : requests.map(r => (
              <tr key={r.id} className="hover:bg-gray-50">
                <td className="table-td font-mono text-xs text-gray-400">#{r.id}</td>
                <td className="table-td font-medium">{r.shipment?.goodsDescription ?? "—"}</td>
                <td className="table-td text-sm">{r.origin && r.destination ? `${r.origin} → ${r.destination}` : "—"}</td>
                <td className="table-td text-sm">{r.requestedFlightDate ?? "—"}</td>
                <td className="table-td"><StatusBadge status={r.status} /></td>
                <td className="table-td">
                  {isOperator() && r.status === "PENDING" && (
                    <button onClick={() => handleCancel(r.id)}
                      className="inline-flex items-center gap-1 text-red-500 hover:text-red-700 text-sm">
                      <X className="w-3.5 h-3.5" /> Cancel
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-gray-200">
            <p className="text-sm text-gray-500">Page {page + 1} of {totalPages}</p>
            <div className="flex gap-2">
              <button className="btn-secondary py-1 px-3 text-xs" disabled={page === 0} onClick={() => load(page-1)}>Prev</button>
              <button className="btn-secondary py-1 px-3 text-xs" disabled={page >= totalPages-1} onClick={() => load(page+1)}>Next</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}