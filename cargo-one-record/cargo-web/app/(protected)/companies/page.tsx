"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { Plus, Search, Building2 } from "lucide-react";
import { getCompanies } from "@/lib/api";
import { Company } from "@/types";
import { useAuth } from "@/context/AuthContext";

export default function CompaniesPage() {
  const { isOperator } = useAuth();
  const [companies, setCompanies] = useState<Company[]>([]);
  const [total, setTotal]         = useState(0);
  const [page, setPage]           = useState(0);
  const [search, setSearch]       = useState("");
  const [loading, setLoading]     = useState(true);
  const size = 12;

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const res = await getCompanies(p, size);
      setCompanies(res.data.content);
      setTotal(res.data.totalElements);
      setPage(p);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const filtered = companies.filter(c =>
    c.name?.toLowerCase().includes(search.toLowerCase()) ||
    c.iataCarrierCode?.toLowerCase().includes(search.toLowerCase())
  );
  const totalPages = Math.ceil(total / size);

  const typeColor: Record<string, string> = {
    AIRLINE: "bg-blue-100 text-blue-800",
    FREIGHT_FORWARDER: "bg-purple-100 text-purple-800",
    GROUND_HANDLER: "bg-orange-100 text-orange-800",
    CUSTOMS: "bg-red-100 text-red-800",
    OTHER: "bg-gray-100 text-gray-800",
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Companies</h1>
          <p className="text-sm text-gray-500 mt-0.5">{total} total companies</p>
        </div>
        {isOperator() && (
          <Link href="/companies/new" className="btn-primary">
            <Plus className="w-4 h-4" /> New Company
          </Link>
        )}
      </div>

      <div className="card p-4 mb-4">
        <div className="relative">
          <Search className="absolute left-3 top-2.5 w-4 h-4 text-gray-400" />
          <input className="input pl-9" placeholder="Search by name or IATA code..." value={search}
            onChange={e => setSearch(e.target.value)} />
        </div>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {Array.from({length: 6}).map((_,i) => <div key={i} className="card h-32 animate-pulse"/>)}
        </div>
      ) : filtered.length === 0 ? (
        <div className="card text-center py-12 text-gray-400">No companies found</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.map(c => (
            <div key={c.id} className="card hover:shadow-md transition-shadow">
              <div className="flex items-start gap-3">
                <div className="p-2 bg-gray-100 rounded-lg flex-shrink-0">
                  <Building2 className="w-5 h-5 text-gray-500" />
                </div>
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2 flex-wrap">
                    <h3 className="font-semibold text-gray-900 truncate">{c.name}</h3>
                    {c.iataCarrierCode && (
                      <span className="font-mono text-xs bg-blue-50 text-blue-700 px-1.5 py-0.5 rounded">{c.iataCarrierCode}</span>
                    )}
                  </div>
                  {c.companyType && (
                    <span className={`mt-1 inline-block text-xs px-2 py-0.5 rounded font-medium ${typeColor[c.companyType] ?? typeColor.OTHER}`}>
                      {c.companyType.replace("_", " ")}
                    </span>
                  )}
                  {c.address?.cityName && <p className="text-xs text-gray-400 mt-1">{c.address.cityName}{c.address.countryCode ? `, ${c.address.countryCode}` : ""}</p>}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex items-center justify-between mt-4">
          <p className="text-sm text-gray-500">Page {page + 1} of {totalPages}</p>
          <div className="flex gap-2">
            <button className="btn-secondary py-1 px-3 text-xs" disabled={page === 0} onClick={() => load(page - 1)}>Prev</button>
            <button className="btn-secondary py-1 px-3 text-xs" disabled={page >= totalPages - 1} onClick={() => load(page + 1)}>Next</button>
          </div>
        </div>
      )}
    </div>
  );
}