"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard, Package, FileText, Building2,
  BookOpen, CalendarCheck, GitPullRequest, Plane, LogOut
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";
import clsx from "clsx";

const nav = [
  { href: "/dashboard",        label: "Dashboard",        icon: LayoutDashboard },
  { href: "/shipments",        label: "Shipments",        icon: Package },
  { href: "/waybills",         label: "Waybills",         icon: FileText },
  { href: "/companies",        label: "Companies",        icon: Building2 },
  { href: "/bookings/requests",label: "Booking Requests", icon: BookOpen },
  { href: "/bookings",         label: "Bookings",         icon: CalendarCheck },
  { href: "/change-requests",  label: "Change Requests",  icon: GitPullRequest },
];

export default function Sidebar() {
  const pathname = usePathname();
  const { user, logout, isAdmin } = useAuth();
  const router = useRouter();

  const handleLogout = () => { logout(); router.push("/login"); };

  return (
    <aside className="w-64 min-h-screen bg-gray-900 text-white flex flex-col">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-gray-700">
        <div className="flex items-center gap-2">
          <Plane className="w-6 h-6 text-blue-400" />
          <div>
            <p className="font-bold text-sm leading-tight">Cargo ONE Record</p>
            <p className="text-xs text-gray-400">IATA v2.0</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {nav.map(({ href, label, icon: Icon }) => {
          const active = pathname === href || pathname.startsWith(href + "/");
          return (
            <Link key={href} href={href}
              className={clsx(
                "flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors",
                active
                  ? "bg-blue-600 text-white"
                  : "text-gray-300 hover:bg-gray-800 hover:text-white"
              )}>
              <Icon className="w-4 h-4 flex-shrink-0" />
              {label}
            </Link>
          );
        })}
      </nav>

      {/* User info */}
      <div className="px-4 py-4 border-t border-gray-700">
        <div className="mb-3">
          <p className="text-sm font-medium text-white">{user?.username}</p>
          <span className={clsx(
            "text-xs px-2 py-0.5 rounded-full font-medium",
            isAdmin() ? "bg-purple-600 text-white" :
            user?.role === "OPERATOR" ? "bg-blue-600 text-white" :
            "bg-gray-600 text-white"
          )}>
            {user?.role}
          </span>
        </div>
        <button onClick={handleLogout}
          className="flex items-center gap-2 text-sm text-gray-400 hover:text-red-400 transition-colors w-full">
          <LogOut className="w-4 h-4" /> Sign out
        </button>
      </div>
    </aside>
  );
}