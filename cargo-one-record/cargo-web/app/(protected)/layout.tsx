"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import Sidebar from "@/components/layout/Sidebar";

export default function ProtectedLayout({ children }: { children: React.ReactNode }) {
  const { user, initialized } = useAuth();
  const router = useRouter();

  useEffect(() => {
    // Only redirect after localStorage has been read
    if (initialized && !user) router.replace("/login");
  }, [user, initialized, router]);

  // Show nothing while restoring session to avoid flash-redirect
  if (!initialized) return null;
  if (!user) return null;

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 overflow-auto">
        <div className="p-8">{children}</div>
      </main>
    </div>
  );
}