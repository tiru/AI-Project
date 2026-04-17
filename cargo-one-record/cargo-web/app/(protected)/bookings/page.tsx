"use client";
import { useEffect, useState } from "react";
import { X } from "lucide-react";
import { getBookings, cancelBooking } from "@/lib/api";
import { Booking } from "@/types";
import StatusBadge from "@/components/common/StatusBadge";
import { useAuth } from "@/context/AuthContext";

export default function BookingsPage() {
  const { isOperator } = useAuth();
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [total, setTotal]       = useState(0);
  const [page, setPage]         = useState(0);
  const [loading, setLoading]   = useState(true);
  const size = 10;

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const res = await getBookings(p, size);
      setBookings(res.data.content);
      setTotal(res.data.totalElements);
      setPage(p);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleCancel = async (id: number) => {
    if (!confirm("Cancel this booking?")) return;
    await cancelBooking(id);
    load(page);
  };

  const totalPages = Math.ceil(total / size);

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Bookings</h1>
        <p className="text-sm text-gray-500 mt-0.5">{total} total bookings</p>
      </div>

      <div className="card p-0 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="table-th">Booking Ref</th>
              <th className="table-th">Shipment</th>
              <th className="table-th">Flight #</th>
              <th className="table-th">Route</th>
              <th className="table-th">Departure</th>
              <th className="table-th">Status</th>
              <th className="table-th">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              Array.from({length:4}).map((_,i) => <tr key={i}>{Array.from({length:7}).map((_,j) => (
                <td key={j} className="table-td"><div className="h-4 bg-gray-200 rounded animate-pulse w-16"/></td>
              ))}</tr>)
            ) : bookings.length === 0 ? (
              <tr><td colSpan={7} className="table-td text-center text-gray-400 py-12">No bookings found</td></tr>
            ) : bookings.map(b => (
              <tr key={b.id} className="hover:bg-gray-50">
                <td className="table-td font-mono font-medium text-blue-600">{b.bookingReference}</td>
                <td className="table-td">{b.shipment?.shipmentNumber ?? "—"}</td>
                <td className="table-td font-mono text-sm">{b.flightNumber ?? "—"}</td>
                <td className="table-td text-sm">{b.origin && b.destination ? `${b.origin} → ${b.destination}` : "—"}</td>
                <td className="table-td text-sm">{b.departureDate ?? "—"}</td>
                <td className="table-td"><StatusBadge status={b.status} /></td>
                <td className="table-td">
                  {isOperator() && b.status === "CONFIRMED" && (
                    <button onClick={() => handleCancel(b.id)}
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