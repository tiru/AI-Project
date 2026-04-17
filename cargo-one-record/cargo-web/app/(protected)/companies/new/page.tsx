"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { ArrowLeft } from "lucide-react";
import { createCompany } from "@/lib/api";

const COMPANY_TYPES = ["AIRLINE", "FREIGHT_FORWARDER", "GROUND_HANDLER", "CUSTOMS", "OTHER"];

export default function NewCompanyPage() {
  const router = useRouter();
  const [form, setForm] = useState({
    name: "", iataCode: "", companyType: "AIRLINE", country: "", email: "", phone: "", address: ""
  });
  const [error, setError]     = useState("");
  const [loading, setLoading] = useState(false);

  const set = (k: string, v: string) => setForm(p => ({ ...p, [k]: v }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      await createCompany({
        name:        form.name,
        iataCode:    form.iataCode    || undefined,
        companyType: form.companyType || undefined,
        country:     form.country     || undefined,
        email:       form.email       || undefined,
        phone:       form.phone       || undefined,
        address:     form.address     || undefined,
      });
      router.push("/companies");
    } catch {
      setError("Failed to create company. Please check the details.");
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
          <p className="text-sm text-gray-500">Register a new company in the ONE Record network</p>
        </div>
      </div>

      <div className="card">
        {error && <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">{error}</div>}
        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="label">Company Name <span className="text-red-500">*</span></label>
              <input className="input" placeholder="e.g. Singapore Airlines" value={form.name}
                onChange={e => set("name", e.target.value)} required />
            </div>
            <div>
              <label className="label">IATA Code</label>
              <input className="input" placeholder="e.g. SQ" maxLength={3} value={form.iataCode}
                onChange={e => set("iataCode", e.target.value.toUpperCase())} />
            </div>
            <div>
              <label className="label">Company Type</label>
              <select className="input" value={form.companyType} onChange={e => set("companyType", e.target.value)}>
                {COMPANY_TYPES.map(t => <option key={t}>{t.replace("_", " ")}</option>)}
              </select>
            </div>
            <div>
              <label className="label">Country</label>
              <input className="input" placeholder="e.g. Singapore" value={form.country}
                onChange={e => set("country", e.target.value)} />
            </div>
            <div>
              <label className="label">Email</label>
              <input className="input" type="email" placeholder="contact@airline.com" value={form.email}
                onChange={e => set("email", e.target.value)} />
            </div>
            <div>
              <label className="label">Phone</label>
              <input className="input" placeholder="+1-555-0100" value={form.phone}
                onChange={e => set("phone", e.target.value)} />
            </div>
            <div className="col-span-2">
              <label className="label">Address</label>
              <input className="input" placeholder="Street, City, Country" value={form.address}
                onChange={e => set("address", e.target.value)} />
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