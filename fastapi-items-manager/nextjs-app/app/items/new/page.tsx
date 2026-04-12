"use client";

import ItemForm from "@/components/ItemForm";
import ProtectedRoute from "@/components/ProtectedRoute";

export default function NewItemPage() {
  return (
    <ProtectedRoute>
      <main className="min-h-screen bg-gray-50 p-8">
        <ItemForm />
      </main>
    </ProtectedRoute>
  );
}
