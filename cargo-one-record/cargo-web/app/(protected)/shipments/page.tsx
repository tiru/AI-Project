"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { Plus, Search, Eye } from "lucide-react";
import { getShipments } from "@/lib/api";
import { Shipment } from "@/types";

import { useAuth } from "@/context/AuthContext";

export default function ShipmentsPage() {
  const { isOperator } = useAuth();
  const [shipments, setShipments] = useState<Shipment[]>([]);
  const [total, setTotal]         = useState(0);
  const [page, setPage]           = useState(0);
  const [search, setSearch]       = useState("");
  const [loading, setLoading]     = useState(true);
  const size = 10;

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const res = await getShipments(p, size);
      setShipments(res.data.content);
      setTotal(res.data.totalElements);
      setPage(p);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const filtered = shipments.filter(s =>
    s.goodsDescription?.toLowerCase().includes(search.toLowerCase()) ||
    s.shipperName?.toLowerCase().includes(search.toLowerCase())
  );
  const totalPages = Math.ceil(total / size);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Shipments</h1>
          <p className="text-sm text-gray-500 mt-0.5">{total} total shipments</p>
        </div>
        {isOperator() && (
          <Link href="/shipments/new" className="btn-primary">
            <Plus className="w-4 h-4" /> New Shipment
          </Link>
        )}
      </div>

      <div className="card p-4 mb-4">
        <div className="relative">
          <Search className="absolute left-3 top-2.5 w-4 h-4 text-gray-400" />
          <input className="input pl-9" placeholder="Search by shipment number or goods..."
            value={search} onChange={e => setSearch(e.target.value)} />
        </div>
      </div>

      <div className="card p-0 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="table-th">Goods Description</th>
              <th className="table-th">Shipper</th>
              <th className="table-th">Consignee</th>
              <th className="table-th">Weight</th>
              <th className="table-th">Pieces</th>
              <th className="table-th">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <tr key={i}>
                  {Array.from({ length: 6 }).map((_, j) => (
                    <td key={j} className="table-td"><div className="h-4 bg-gray-200 rounded animate-pulse w-24" /></td>
                  ))}
                </tr>
              ))
            ) : filtered.length === 0 ? (
              <tr><td colSpan={6} className="table-td text-center text-gray-400 py-12">No shipments found</td></tr>
            ) : (
              filtered.map(s => (
                <tr key={s.id} className="hover:bg-gray-50 transition-colors">
                  <td className="table-td font-medium max-w-xs truncate">{s.goodsDescription}</td>
                  <td className="table-td">{s.shipperName ?? "—"}</td>
                  <td className="table-td">{s.consigneeName ?? "—"}</td>
                  <td className="table-td">{s.totalGrossWeight ? `${s.totalGrossWeight.value} ${s.totalGrossWeight.unit}` : "—"}</td>
                  <td className="table-td">{s.pieceCount ?? 0}</td>
                  <td className="table-td">
                    <Link href={`/shipments/${s.id}`} className="inline-flex items-center gap-1 text-blue-600 hover:underline text-sm">
                      <Eye className="w-3.5 h-3.5" /> View
                    </Link>
                  </td>
                </tr>
              ))
            )}
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