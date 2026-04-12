"use client";

import { useAuth } from "@/context/AuthContext";
import { deleteItem } from "@/lib/api";

export default function DeleteButton({ id, onDeleted }: { id: number; onDeleted?: () => void }) {
  const { token } = useAuth();

  const handleDelete = async () => {
    if (!confirm("Are you sure you want to delete this item?")) return;
    if (!token) return;
    await deleteItem(id, token);
    onDeleted?.();
  };

  return (
    <button
      onClick={handleDelete}
      className="bg-red-500 text-white px-3 py-1.5 rounded-lg hover:bg-red-600 transition text-sm"
    >
      Delete
    </button>
  );
}

