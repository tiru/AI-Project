"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { ArrowLeft } from "lucide-react";
import { createCompany } from "@/lib/api";

const COMPANY_TYPES = [
  "AIRLINE", "FREIGHT_FORWARDER", "GROUND_HANDLER",
  "SHIPPER", "CONSIGNEE", "CUSTOMS_BROKER", "AIRPORT_AUTHORITY", "OTHER"
];

export default function NewCompanyPage() {
  const router = useRouter();
  const [form, setForm] = useState({
    name: "", shortName: "", companyType: "AIRLINE",
    iataCarrierCode: "", icaoCode: "", cassCode: "", taxId: "",
    street: "", city: "", postalCode: "", countryCode: "", iataLocationCode: "",
  });
  const [error, setError]     = useState("");
  const [loading, setLoading] = useState(false);

  const set = (k: string, v: string) => setForm(p => ({ ...p, [k]: v }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const hasAddress = form.street || form.city || form.postalCode || form.countryCode;
      await createCompany({
        name:            form.name,
        shortName:       form.shortName       || undefined,
        companyType:     form.companyType,
        iataCarrierCode: form.iataCarrierCode || undefined,
        icaoCode:        form.icaoCode        || undefined,
        cassCode:        form.cassCode        || undefined,
        taxId:           form.taxId           || undefined,
        address: hasAddress ? {
          streetAddressLine1: form.street       || undefined,
          cityName:           form.city         || undefined,
          postalCode:         form.postalCode   || undefined,
          countryCode:        form.countryCode  || undefined,
          iataLocationCode:   form.iataLocationCode || undefined,
        } : undefined,
      });
      router.push("/companies");
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(msg || "Failed to create company. Please check the details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl">
      <div className="flex items-center gap-3 mb-6">
        <Link href="/companies" className="text-gray-400 hover:text-gray-600"><ArrowLeft className="w-5 h-5" /></Link>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">New Company</h1>
          <p className="text-sm text-gray-500">Register a company in the ONE Record network</p>
        </div>
      </div>

      <div className="card">
        {error && <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">{error}</div>}
        <form onSubmit={handleSubmit} className="space-y-5">

          {/* Core info */}
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="label">Company Name <span className="text-red-500">*</span></label>
              <input className="input" placeholder="e.g. Singapore Airlines" value={form.name}
                onChange={e => set("name", e.target.value)} required />
            </div>
            <div>
              <label className="label">Short Name</label>
              <input className="input" placeholder="e.g. SIA" value={form.shortName}
                onChange={e => set("shortName", e.target.value)} />
            </div>
            <div>
              <label className="label">Company Type <span className="text-red-500">*</span></label>
              <select className="input" value={form.companyType} onChange={e => set("companyType", e.target.value)}>
                {COMPANY_TYPES.map(t => <option key={t} value={t}>{t.replace(/_/g, " ")}</option>)}
              </select>
            </div>
          </div>

          {/* Industry codes */}
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">Industry Codes</p>
            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="label">IATA Carrier Code</label>
                <input className="input" placeholder="e.g. SQ" maxLength={3} value={form.iataCarrierCode}
                  onChange={e => set("iataCarrierCode", e.target.value.toUpperCase())} />
              </div>
              <div>
                <label className="label">ICAO Code</label>
                <input className="input" placeholder="e.g. SIA" maxLength={4} value={form.icaoCode}
                  onChange={e => set("icaoCode", e.target.value.toUpperCase())} />
              </div>
              <div>
                <label className="label">CASS Code</label>
                <input className="input" placeholder="e.g. 0012" value={form.cassCode}
                  onChange={e => set("cassCode", e.target.value)} />
              </div>
            </div>
          </div>

          {/* Address */}
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">Address (optional)</p>
            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="label">Street Address</label>
                <input className="input" placeholder="e.g. 25 Airline Road" value={form.street}
                  onChange={e => set("street", e.target.value)} />
              </div>
              <div>
                <label className="label">City</label>
                <input className="input" placeholder="e.g. Singapore" value={form.city}
                  onChange={e => set("city", e.target.value)} />
              </div>
              <div>
                <label className="label">Postal Code</label>
                <input className="input" placeholder="e.g. 819829" value={form.postalCode}
                  onChange={e => set("postalCode", e.target.value)} />
              </div>
              <div>
                <label className="label">Country Code (ISO)</label>
                <input className="input" placeholder="e.g. SG" maxLength={2} value={form.countryCode}
                  onChange={e => set("countryCode", e.target.value.toUpperCase())} />
              </div>
              <div>
                <label className="label">IATA Location Code</label>
                <input className="input" placeholder="e.g. SIN" maxLength={3} value={form.iataLocationCode}
                  onChange={e => set("iataLocationCode", e.target.value.toUpperCase())} />
              </div>
            </div>
          </div>

          <div className="flex gap-3 pt-2">
            <button type="submit" disabled={loading} className="btn-primary">{loading ? "Creating..." : "Create Company"}</button>
            <Link href="/companies" className="btn-secondary">Cancel</Link>
          </div>
        </form>
      </div>
    </div>
  );
}