"use client";
import { useEffect, useState } from "react";
import { Plus, Search } from "lucide-react";
import { getWaybills, createWaybill, getCompanies } from "@/lib/api";
import { Waybill, Company } from "@/types";
import { useAuth } from "@/context/AuthContext";

export default function WaybillsPage() {
  const { isOperator } = useAuth();
  const [waybills, setWaybills] = useState<Waybill[]>([]);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [total, setTotal]         = useState(0);
  const [page, setPage]           = useState(0);
  const [search, setSearch]       = useState("");
  const [loading, setLoading]     = useState(true);
  const [showForm, setShowForm]   = useState(false);
  const [form, setForm]           = useState({ awbNumber: "", issuerId: "", consigneeId: "", originAirport: "", destinationAirport: "" });
  const [submitting, setSubmitting] = useState(false);
  const size = 10;

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const [wb, co] = await Promise.all([getWaybills(p, size), getCompanies(0, 100)]);
      setWaybills(wb.data.content);
      setTotal(wb.data.totalElements);
      setCompanies(co.data.content);
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
      await createWaybill({
        awbNumber:        form.awbNumber,
        issuerId:         form.issuerId         ? Number(form.issuerId)    : undefined,
        consigneeId:      form.consigneeId      ? Number(form.consigneeId) : undefined,
        originAirport:      form.originAirport      || undefined,
        destinationAirport: form.destinationAirport || undefined,
      });
      setShowForm(false);
      setForm({ awbNumber: "", issuerId: "", consigneeId: "", originAirport: "", destinationAirport: "" });
      load(0);
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = waybills.filter(w =>
    w.awbNumber?.toLowerCase().includes(search.toLowerCase())
  );
  const totalPages = Math.ceil(total / size);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Air Waybills</h1>
          <p className="text-sm text-gray-500 mt-0.5">{total} total waybills</p>
        </div>
        {isOperator() && (
          <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
            <Plus className="w-4 h-4" /> New Waybill
          </button>
        )}
      </div>

      {showForm && (
        <div className="card mb-6">
          <h3 className="font-semibold text-gray-900 mb-4">Create Air Waybill</h3>
          <form onSubmit={handleCreate} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">AWB Number <span className="text-red-500">*</span></label>
                <input className="input" placeholder="e.g. 123-45678901" value={form.awbNumber}
                  onChange={e => setForm(p => ({ ...p, awbNumber: e.target.value }))} required />
              </div>
              <div>
                <label className="label">Issuer (Airline)</label>
                <select className="input" value={form.issuerId} onChange={e => setForm(p => ({ ...p, issuerId: e.target.value }))}>
                  <option value="">Select company...</option>
                  {companies.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Consignee</label>
                <select className="input" value={form.consigneeId} onChange={e => setForm(p => ({ ...p, consigneeId: e.target.value }))}>
                  <option value="">Select company...</option>
                  {companies.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Origin Airport</label>
                <input className="input" placeholder="e.g. JFK" value={form.originAirport}
                  onChange={e => setForm(p => ({ ...p, originAirport: e.target.value }))} />
              </div>
              <div>
                <label className="label">Destination Airport</label>
                <input className="input" placeholder="e.g. LHR" value={form.destinationAirport}
                  onChange={e => setForm(p => ({ ...p, destinationAirport: e.target.value }))} />
              </div>
            </div>
            <div className="flex gap-3">
              <button type="submit" disabled={submitting} className="btn-primary">{submitting ? "Creating..." : "Create"}</button>
              <button type="button" className="btn-secondary" onClick={() => setShowForm(false)}>Cancel</button>
            </div>
          </form>
        </div>
      )}

      <div className="card p-4 mb-4">
        <div className="relative">
          <Search className="absolute left-3 top-2.5 w-4 h-4 text-gray-400" />
          <input className="input pl-9" placeholder="Search by AWB number..." value={search}
            onChange={e => setSearch(e.target.value)} />
        </div>
      </div>

      <div className="card p-0 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="table-th">AWB Number</th>
              <th className="table-th">Origin</th>
              <th className="table-th">Destination</th>
              <th className="table-th">Issuer</th>
              <th className="table-th">Consignee</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              Array.from({ length: 4 }).map((_, i) => (
                <tr key={i}>{Array.from({length:5}).map((_,j)=>(
                  <td key={j} className="table-td"><div className="h-4 bg-gray-200 rounded animate-pulse w-20"/></td>
                ))}</tr>
              ))
            ) : filtered.length === 0 ? (
              <tr><td colSpan={5} className="table-td text-center text-gray-400 py-12">No waybills found</td></tr>
            ) : filtered.map(w => (
              <tr key={w.id} className="hover:bg-gray-50">
                <td className="table-td font-medium font-mono">{w.awbNumber}</td>
                <td className="table-td">{w.originAirport ?? "—"}</td>
                <td className="table-td">{w.destinationAirport ?? "—"}</td>
                <td className="table-td">{w.issuer?.name ?? "—"}</td>
                <td className="table-td">{w.consignee?.name ?? "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-gray-200">
            <p className="text-sm text-gray-500">Page {page + 1} of {totalPages}</p>
            <div className="flex gap-2">
              <button className="btn-secondary py-1 px-3 text-xs" disabled={page === 0} onClick={() => load(page - 1)}>Prev</button>
              <button className="btn-secondary py-1 px-3 text-xs" disabled={page >= totalPages - 1} onClick={() => load(page + 1)}>Next</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}