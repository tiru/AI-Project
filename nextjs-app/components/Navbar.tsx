"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function Navbar() {
  const { username, role, logout, isLoggedIn } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  if (!isLoggedIn) return null;

  return (
    <nav className="bg-white shadow-sm px-8 py-4 flex items-center justify-between">
      <Link href="/" className="text-xl font-bold text-blue-600">
        Items Manager
      </Link>
      <div className="flex items-center gap-4">
        <span className="text-sm text-gray-600">
          Hello, <span className="font-semibold">{username}</span>
        </span>
        <span className={`text-xs px-2 py-1 rounded-full font-medium ${role === "admin" ? "bg-purple-100 text-purple-700" : "bg-gray-100 text-gray-600"}`}>
          {role}
        </span>
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-3 py-1.5 rounded-lg hover:bg-red-600 transition text-sm"
        >
          Logout
        </button>
      </div>
    </nav>
  );
}
