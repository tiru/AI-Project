"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { ArrowLeft } from "lucide-react";
import { createShipment } from "@/lib/api";

export default function NewShipmentPage() {
  const router = useRouter();
  const [form, setForm] = useState({
    goodsDescription: "",
    shipperName: "",
    consigneeName: "",
    weightValue: "",
    weightUnit: "KGM",
    specialHandlingCodes: "",
    declaredValue: "",
    currency: "USD",
  });
  const [error, setError]     = useState("");
  const [loading, setLoading] = useState(false);

  const set = (key: string, val: string) => setForm(p => ({ ...p, [key]: val }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const payload = {
        goodsDescription: form.goodsDescription,
        shipperName:      form.shipperName,
        consigneeName:    form.consigneeName,
        totalGrossWeight: form.weightValue
          ? { value: parseFloat(form.weightValue), unit: form.weightUnit }
          : undefined,
        declaredValueForCarriage: form.declaredValue ? parseFloat(form.declaredValue) : undefined,
        declaredValueCurrency:    form.declaredValue ? form.currency : undefined,
        specialHandlingCodes: form.specialHandlingCodes
          ? form.specialHandlingCodes.split(",").map(s => s.trim()).filter(Boolean)
          : undefined,
      };
      const res = await createShipment(payload);
      router.push(`/shipments/${res.data.id}`);
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(msg || "Failed to create shipment. Please check the details and try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl">
      <div className="flex items-center gap-3 mb-6">
        <Link href="/shipments" className="text-gray-400 hover:text-gray-600">
          <ArrowLeft className="w-5 h-5" />
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">New Shipment</h1>
          <p className="text-sm text-gray-500">Create a new ONE Record shipment</p>
        </div>
      </div>

      <div className="card">
        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="label">Goods Description <span className="text-red-500">*</span></label>
            <textarea className="input min-h-[80px] resize-none" placeholder="Describe the goods being shipped..."
              value={form.goodsDescription} onChange={e => set("goodsDescription", e.target.value)} required />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Shipper Name <span className="text-red-500">*</span></label>
              <input className="input" placeholder="e.g. Acme Corp" value={form.shipperName}
                onChange={e => set("shipperName", e.target.value)} required />
            </div>
            <div>
              <label className="label">Consignee Name <span className="text-red-500">*</span></label>
              <input className="input" placeholder="e.g. Global Imports Ltd" value={form.consigneeName}
                onChange={e => set("consigneeName", e.target.value)} required />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div className="col-span-2">
              <label className="label">Gross Weight</label>
              <input className="input" type="number" step="0.01" placeholder="0.00"
                value={form.weightValue} onChange={e => set("weightValue", e.target.value)} />
            </div>
            <div>
              <label className="label">Unit</label>
              <select className="input" value={form.weightUnit} onChange={e => set("weightUnit", e.target.value)}>
                <option value="KGM">KGM (kg)</option>
                <option value="LBR">LBR (lb)</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div className="col-span-2">
              <label className="label">Declared Value</label>
              <input className="input" type="number" step="0.01" placeholder="0.00"
                value={form.declaredValue} onChange={e => set("declaredValue", e.target.value)} />
            </div>
            <div>
              <label className="label">Currency</label>
              <select className="input" value={form.currency} onChange={e => set("currency", e.target.value)}>
                <option>USD</option><option>EUR</option><option>GBP</option><option>SGD</option>
              </select>
            </div>
          </div>

          <div>
            <label className="label">Special Handling Codes</label>
            <input className="input" placeholder="e.g. DGR, PER, VAL (comma-separated)"
              value={form.specialHandlingCodes} onChange={e => set("specialHandlingCodes", e.target.value)} />
            <p className="text-xs text-gray-400 mt-1">Separate multiple codes with commas</p>
          </div>

          <div className="flex gap-3 pt-2">
            <button type="submit" disabled={loading} className="btn-primary">
              {loading ? "Creating..." : "Create Shipment"}
            </button>
            <Link href="/shipments" className="btn-secondary">Cancel</Link>
          </div>
        </form>
      </div>
    </div>
  );
}