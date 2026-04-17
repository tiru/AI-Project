"use client";
import { useEffect, useState } from "react";
import { Plus, Check, X } from "lucide-react";
import { getChangeRequests, createChangeRequest, approveChangeRequest, rejectChangeRequest, revokeChangeRequest, getShipments } from "@/lib/api";
import { ChangeRequestDto, Shipment } from "@/types";
import StatusBadge from "@/components/common/StatusBadge";
import { useAuth } from "@/context/AuthContext";

export default function ChangeRequestsPage() {
  const { isOperator, isAdmin } = useAuth();
  const [crs, setCrs]           = useState<ChangeRequestDto[]>([]);
  const [shipments, setShipments] = useState<Shipment[]>([]);
  const [total, setTotal]       = useState(0);
  const [page, setPage]         = useState(0);
  const [loading, setLoading]   = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm]         = useState({ shipmentId: "", changeType: "", description: "", proposedValue: "" });
  const [submitting, setSubmitting] = useState(false);
  const size = 10;

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const [cr, sh] = await Promise.all([getChangeRequests(p, size), getShipments(0, 100)]);
      setCrs(cr.data.content);
      setTotal(cr.data.totalElements);
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
      await createChangeRequest({
        shipmentId:    Number(form.shipmentId),
        changeType:    form.changeType,
        description:   form.description,
        proposedValue: form.proposedValue || undefined,
      });
      setShowForm(false);
      setForm({ shipmentId: "", changeType: "", description: "", proposedValue: "" });
      load(0);
    } finally {
      setSubmitting(false);
    }
  };

  const totalPages = Math.ceil(total / size);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Change Requests</h1>
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
          <h3 className="font-semibold text-gray-900 mb-4">Submit Change Request</h3>
          <form onSubmit={handleCreate} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="label">Shipment <span className="text-red-500">*</span></label>
                <select className="input" value={form.shipmentId}
                  onChange={e => setForm(p => ({ ...p, shipmentId: e.target.value }))} required>
                  <option value="">Select shipment...</option>
                  {shipments.map(s => <option key={s.id} value={s.id}>{s.shipmentNumber} — {s.goodsDescription}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Change Type <span className="text-red-500">*</span></label>
                <input className="input" placeholder="e.g. WEIGHT_UPDATE, ROUTING_CHANGE" value={form.changeType}
                  onChange={e => setForm(p => ({ ...p, changeType: e.target.value }))} required />
              </div>
              <div>
                <label className="label">Proposed Value</label>
                <input className="input" placeholder="New value (if applicable)" value={form.proposedValue}
                  onChange={e => setForm(p => ({ ...p, proposedValue: e.target.value }))} />
              </div>
              <div className="col-span-2">
                <label className="label">Description <span className="text-red-500">*</span></label>
                <textarea className="input min-h-[80px] resize-none" placeholder="Explain the change..."
                  value={form.description}
                  onChange={e => setForm(p => ({ ...p, description: e.target.value }))} required />
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
              <th className="table-th">Change Type</th>
              <th className="table-th">Description</th>
              <th className="table-th">Status</th>
              <th className="table-th">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              Array.from({length:4}).map((_,i) => <tr key={i}>{Array.from({length:6}).map((_,j) => (
                <td key={j} className="table-td"><div className="h-4 bg-gray-200 rounded animate-pulse w-16"/></td>
              ))}</tr>)
            ) : crs.length === 0 ? (
              <tr><td colSpan={6} className="table-td text-center text-gray-400 py-12">No change requests</td></tr>
            ) : crs.map(r => (
              <tr key={r.id} className="hover:bg-gray-50">
                <td className="table-td font-mono text-xs text-gray-400">#{r.id}</td>
                <td className="table-td font-medium">{r.shipment?.shipmentNumber ?? "—"}</td>
                <td className="table-td">
                  <span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded">{r.changeType}</span>
                </td>
                <td className="table-td max-w-xs truncate text-gray-500">{r.description}</td>
                <td className="table-td"><StatusBadge status={r.status} /></td>
                <td className="table-td">
                  <div className="flex items-center gap-2">
                    {isAdmin() && r.status === "PENDING" && (
                      <>
                        <button onClick={async () => { await approveChangeRequest(r.id); load(page); }}
                          className="inline-flex items-center gap-1 text-green-600 hover:text-green-800 text-sm">
                          <Check className="w-3.5 h-3.5" /> Approve
                        </button>
                        <button onClick={async () => { await rejectChangeRequest(r.id); load(page); }}
                          className="inline-flex items-center gap-1 text-red-500 hover:text-red-700 text-sm">
                          <X className="w-3.5 h-3.5" /> Reject
                        </button>
                      </>
                    )}
                    {isOperator() && r.status === "PENDING" && (
                      <button onClick={async () => { await revokeChangeRequest(r.id); load(page); }}
                        className="inline-flex items-center gap-1 text-gray-500 hover:text-gray-700 text-sm">
                        Revoke
                      </button>
                    )}
                  </div>
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