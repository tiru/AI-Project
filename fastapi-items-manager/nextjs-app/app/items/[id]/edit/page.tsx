"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { getItem } from "@/lib/api";
import { useAuth } from "@/context/AuthContext";
import ItemForm from "@/components/ItemForm";
import ProtectedRoute from "@/components/ProtectedRoute";

export default function EditItemPage() {
  const { id } = useParams<{ id: string }>();
  const { token } = useAuth();
  const [item, setItem] = useState<{ name: string; description: string; price: number } | null>(null);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    if (!token) return;
    getItem(Number(id), token).then((data) => {
      if (data.error) setNotFound(true);
      else setItem(data);
    });
  }, [id, token]);

  return (
    <ProtectedRoute>
      <main className="min-h-screen bg-gray-50 p-8">
        {notFound ? (
          <p className="text-center text-red-500 mt-20">Item not found.</p>
        ) : item ? (
          <ItemForm id={Number(id)} defaultValues={item} />
        ) : (
          <p className="text-center text-gray-400 mt-20">Loading...</p>
        )}
      </main>
    </ProtectedRoute>
  );
}
