"use client";
import { useEffect, useState } from "react";
import { Plus, Search } from "lucide-react";
import { getWaybills, createWaybill } from "@/lib/api";
import { Waybill, WaybillRequest } from "@/types";
import { useAuth } from "@/context/AuthContext";

const WAYBILL_TYPES = ["MASTER", "HOUSE", "DIRECT"];

export default function WaybillsPage() {
  const { isOperator } = useAuth();
  const [waybills, setWaybills]   = useState<Waybill[]>([]);
  const [total, setTotal]         = useState(0);
  const [page, setPage]           = useState(0);
  const [search, setSearch]       = useState("");
  const [loading, setLoading]     = useState(true);
  const [showForm, setShowForm]   = useState(false);
  const [error, setError]         = useState("");
  const [form, setForm]           = useState<{
    waybillNumber: string; waybillType: string; carrierCode: string;
    originCode: string; originName: string;
    destinationCode: string; destinationName: string;
    numberOfPieces: string; totalWeight: string; totalWeightUnit: string;
  }>({
    waybillNumber: "", waybillType: "MASTER", carrierCode: "",
    originCode: "", originName: "",
    destinationCode: "", destinationName: "",
    numberOfPieces: "", totalWeight: "", totalWeightUnit: "KGM",
  });
  const [submitting, setSubmitting] = useState(false);
  const size = 10;

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const res = await getWaybills(p, size);
      setWaybills(res.data.content);
      setTotal(res.data.totalElements);
      setPage(p);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const set = (k: string, v: string) => setForm(p => ({ ...p, [k]: v }));

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(""); setSubmitting(true);
    try {
      const payload: WaybillRequest = {
        waybillNumber:   form.waybillNumber,
        waybillType:     form.waybillType,
        carrierCode:     form.carrierCode,
        originCode:      form.originCode.toUpperCase(),
        originName:      form.originName      || undefined,
        destinationCode: form.destinationCode.toUpperCase(),
        destinationName: form.destinationName || undefined,
        numberOfPieces:  form.numberOfPieces  ? parseInt(form.numberOfPieces)  : undefined,
        totalWeight:     form.totalWeight     ? parseFloat(form.totalWeight)   : undefined,
        totalWeightUnit: form.totalWeightUnit || undefined,
      };
      await createWaybill(payload);
      setShowForm(false);
      setForm({
        waybillNumber: "", waybillType: "MASTER", carrierCode: "",
        originCode: "", originName: "",
        destinationCode: "", destinationName: "",
        numberOfPieces: "", totalWeight: "", totalWeightUnit: "KGM",
      });
      load(0);
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(msg || "Failed to create waybill.");
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = waybills.filter(w =>
    w.waybillNumber?.toLowerCase().includes(search.toLowerCase()) ||
    w.carrierCode?.toLowerCase().includes(search.toLowerCase())
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
          <button className="btn-primary" onClick={() => { setShowForm(!showForm); setError(""); }}>
            <Plus className="w-4 h-4" /> New Waybill
          </button>
        )}
      </div>

      {showForm && (
        <div className="card mb-6">
          <h3 className="font-semibold text-gray-900 mb-4">Create Air Waybill</h3>
          {error && <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">{error}</div>}
          <form onSubmit={handleCreate} className="space-y-4">
            <div className="grid grid-cols-3 gap-4">
              <div className="col-span-2">
                <label className="label">AWB Number <span className="text-red-500">*</span></label>
                <input className="input" placeholder="e.g. 180-12345678" value={form.waybillNumber}
                  onChange={e => set("waybillNumber", e.target.value)} required />
                <p className="text-xs text-gray-400 mt-1">Format: NNN-NNNNNNNN</p>
              </div>
              <div>
                <label className="label">Type <span className="text-red-500">*</span></label>
                <select className="input" value={form.waybillType} onChange={e => set("waybillType", e.target.value)}>
                  {WAYBILL_TYPES.map(t => <option key={t}>{t}</option>)}
                </select>
              </div>
            </div>

            <div>
              <label className="label">Carrier Code <span className="text-red-500">*</span></label>
              <input className="input" placeholder="e.g. SQ" maxLength={3} value={form.carrierCode}
                onChange={e => set("carrierCode", e.target.value.toUpperCase())} required />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">Origin Airport <span className="text-red-500">*</span></label>
                <input className="input" placeholder="e.g. SIN" maxLength={3} value={form.originCode}
                  onChange={e => set("originCode", e.target.value.toUpperCase())} required />
              </div>
              <div>
                <label className="label">Origin City</label>
                <input className="input" placeholder="e.g. Singapore" value={form.originName}
                  onChange={e => set("originName", e.target.value)} />
              </div>
              <div>
                <label className="label">Destination Airport <span className="text-red-500">*</span></label>
                <input className="input" placeholder="e.g. LHR" maxLength={3} value={form.destinationCode}
                  onChange={e => set("destinationCode", e.target.value.toUpperCase())} required />
              </div>
              <div>
                <label className="label">Destination City</label>
                <input className="input" placeholder="e.g. London" value={form.destinationName}
                  onChange={e => set("destinationName", e.target.value)} />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="label">Pieces</label>
                <input className="input" type="number" min="0" placeholder="0" value={form.numberOfPieces}
                  onChange={e => set("numberOfPieces", e.target.value)} />
              </div>
              <div>
                <label className="label">Total Weight</label>
                <input className="input" type="number" step="0.01" placeholder="0.00" value={form.totalWeight}
                  onChange={e => set("totalWeight", e.target.value)} />
              </div>
              <div>
                <label className="label">Unit</label>
                <select className="input" value={form.totalWeightUnit} onChange={e => set("totalWeightUnit", e.target.value)}>
                  <option value="KGM">KGM (kg)</option>
                  <option value="LBR">LBR (lb)</option>
                </select>
              </div>
            </div>

            <div className="flex gap-3">
              <button type="submit" disabled={submitting} className="btn-primary">
                {submitting ? "Creating..." : "Create Waybill"}
              </button>
              <button type="button" className="btn-secondary" onClick={() => setShowForm(false)}>Cancel</button>
            </div>
          </form>
        </div>
      )}

      <div className="card p-4 mb-4">
        <div className="relative">
          <Search className="absolute left-3 top-2.5 w-4 h-4 text-gray-400" />
          <input className="input pl-9" placeholder="Search by AWB number or carrier code..." value={search}
            onChange={e => setSearch(e.target.value)} />
        </div>
      </div>

      <div className="card p-0 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="table-th">AWB Number</th>
              <th className="table-th">Type</th>
              <th className="table-th">Carrier</th>
              <th className="table-th">Origin</th>
              <th className="table-th">Destination</th>
              <th className="table-th">Pieces</th>
              <th className="table-th">Weight</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              Array.from({ length: 4 }).map((_, i) => (
                <tr key={i}>{Array.from({ length: 7 }).map((_, j) => (
                  <td key={j} className="table-td"><div className="h-4 bg-gray-200 rounded animate-pulse w-20" /></td>
                ))}</tr>
              ))
            ) : filtered.length === 0 ? (
              <tr><td colSpan={7} className="table-td text-center text-gray-400 py-12">No waybills found</td></tr>
            ) : filtered.map(w => (
              <tr key={w.id} className="hover:bg-gray-50">
                <td className="table-td font-medium font-mono text-blue-600">{w.waybillNumber}</td>
                <td className="table-td">
                  <span className="text-xs bg-gray-100 px-2 py-0.5 rounded font-medium">{w.waybillType}</span>
                </td>
                <td className="table-td font-mono text-sm">{w.carrierCode ?? "—"}</td>
                <td className="table-td">{w.originCode ?? "—"}{w.originName ? ` (${w.originName})` : ""}</td>
                <td className="table-td">{w.destinationCode ?? "—"}{w.destinationName ? ` (${w.destinationName})` : ""}</td>
                <td className="table-td">{w.numberOfPieces ?? "—"}</td>
                <td className="table-td">{w.totalWeight ? `${w.totalWeight} ${w.totalWeightUnit}` : "—"}</td>
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