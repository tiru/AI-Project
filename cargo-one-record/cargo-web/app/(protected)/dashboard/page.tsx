"use client";
import { useEffect, useState } from "react";
import { Package, FileText, Building2, Calendar, TrendingUp, AlertCircle } from "lucide-react";
import { getShipments, getWaybills, getCompanies, getBookingRequests, getBookings, getChangeRequests } from "@/lib/api";
import { useAuth } from "@/context/AuthContext";

interface Stats {
  shipments: number;
  waybills: number;
  companies: number;
  bookingRequests: number;
  bookings: number;
  changeRequests: number;
}

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [s, w, c, br, b, cr] = await Promise.all([
          getShipments(0, 1),
          getWaybills(0, 1),
          getCompanies(0, 1),
          getBookingRequests(0, 1),
          getBookings(0, 1),
          getChangeRequests(0, 1),
        ]);
        setStats({
          shipments: s.data.totalElements,
          waybills: w.data.totalElements,
          companies: c.data.totalElements,
          bookingRequests: br.data.totalElements,
          bookings: b.data.totalElements,
          changeRequests: cr.data.totalElements,
        });
      } catch {
        // silently fail — stats are non-critical
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const cards = [
    { label: "Shipments",        value: stats?.shipments,       icon: Package,    color: "bg-blue-500",   href: "/shipments" },
    { label: "Waybills",         value: stats?.waybills,        icon: FileText,   color: "bg-indigo-500", href: "/waybills" },
    { label: "Companies",        value: stats?.companies,       icon: Building2,  color: "bg-purple-500", href: "/companies" },
    { label: "Booking Requests", value: stats?.bookingRequests, icon: Calendar,   color: "bg-orange-500", href: "/bookings/requests" },
    { label: "Bookings",         value: stats?.bookings,        icon: TrendingUp, color: "bg-green-500",  href: "/bookings" },
    { label: "Change Requests",  value: stats?.changeRequests,  icon: AlertCircle,color: "bg-red-500",    href: "/change-requests" },
  ];

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-500 mt-1">Welcome back, <span className="font-medium text-gray-700">{user?.username}</span></p>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="card animate-pulse h-28" />
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {cards.map(card => {
            const Icon = card.icon;
            return (
              <a key={card.label} href={card.href} className="card hover:shadow-md transition-shadow flex items-center gap-4">
                <div className={`${card.color} p-3 rounded-xl`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
                <div>
                  <p className="text-sm text-gray-500">{card.label}</p>
                  <p className="text-2xl font-bold text-gray-900">{card.value ?? "—"}</p>
                </div>
              </a>
            );
          })}
        </div>
      )}

      <div className="mt-10 card">
        <h2 className="text-base font-semibold text-gray-900 mb-3">About this System</h2>
        <p className="text-sm text-gray-600 leading-relaxed">
          This portal implements the <span className="font-medium text-blue-600">IATA ONE Record v2.0</span> standard —
          an ontology-driven cargo data sharing model using JSON-LD and Linked Data principles.
          All entities are modelled as <em>Logistics Objects</em> with a unique IRI, enabling seamless
          interoperability across airlines, freight forwarders, ground handlers, and customs authorities.
        </p>
      </div>
    </div>
  );
}