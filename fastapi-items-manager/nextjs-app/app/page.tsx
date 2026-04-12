"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { getItems, Pagination } from "@/lib/api";
import DeleteButton from "@/components/DeleteButton";
import ProtectedRoute from "@/components/ProtectedRoute";

const PAGE_SIZE = 5;

export default function HomePage() {
  const { token, isAdmin } = useAuth();
  const [items, setItems] = useState<Record<string, any>>({});
  const [pagination, setPagination] = useState<Pagination | null>(null);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [loading, setLoading] = useState(false);

  const fetchItems = async (targetPage = page) => {
    if (!token) return;
    setLoading(true);
    const data = await getItems(token, {
      search: search || undefined,
      min_price: minPrice || undefined,
      max_price: maxPrice || undefined,
      page: targetPage,
      page_size: PAGE_SIZE,
    });
    setItems(data.items || {});
    setPagination(data.pagination);
    setLoading(false);
  };

  useEffect(() => {
    fetchItems(page);
  }, [token, page]);

  const handleFilter = (e: { preventDefault: () => void }) => {
    e.preventDefault();
    setPage(1);
    fetchItems(1);
  };

  const handleReset = () => {
    setSearch("");
    setMinPrice("");
    setMaxPrice("");
    setPage(1);
    if (token) {
      getItems(token, { page: 1, page_size: PAGE_SIZE }).then((data) => {
        setItems(data.items || {});
        setPagination(data.pagination);
      });
    }
  };

  const itemList = Object.entries(items);

  return (
    <ProtectedRoute>
      <main className="min-h-screen bg-gray-50 p-8">
        <div className="max-w-4xl mx-auto">
          <div className="flex items-center justify-between mb-6">
            <h1 className="text-3xl font-bold text-gray-800">All Items</h1>
            {isAdmin && (
              <Link
                href="/items/new"
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition"
              >
                + Add Item
              </Link>
            )}
          </div>

          {/* Search & Filter Bar */}
          <form
            onSubmit={handleFilter}
            className="bg-white rounded-xl shadow p-4 mb-6 flex flex-wrap gap-3 items-end"
          >
            <div className="flex-1 min-w-[160px]">
              <label className="block text-xs font-medium text-gray-600 mb-1">Search</label>
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Name or description..."
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="w-28">
              <label className="block text-xs font-medium text-gray-600 mb-1">Min Price</label>
              <input
                type="number"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                placeholder="0"
                min="0"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="w-28">
              <label className="block text-xs font-medium text-gray-600 mb-1">Max Price</label>
              <input
                type="number"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                placeholder="9999"
                min="0"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <button
              type="submit"
              className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition text-sm"
            >
              Search
            </button>
            <button
              type="button"
              onClick={handleReset}
              className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 transition text-sm"
            >
              Reset
            </button>
          </form>

          {/* Items List */}
          {loading ? (
            <p className="text-center text-gray-400 py-10">Loading...</p>
          ) : itemList.length === 0 ? (
            <div className="text-center py-20 text-gray-400 text-lg">
              No items found.{" "}
              {isAdmin && (
                <Link href="/items/new" className="text-blue-500 underline">
                  Add your first item
                </Link>
              )}
            </div>
          ) : (
            <>
              <div className="grid gap-4">
                {itemList.map(([id, item]) => (
                  <div
                    key={id}
                    className="bg-white rounded-xl shadow p-5 flex items-center justify-between"
                  >
                    <div>
                      <h2 className="text-xl font-semibold text-gray-800">{item.name}</h2>
                      <p className="text-gray-500 text-sm mt-1">{item.description || "No description"}</p>
                      <p className="text-blue-600 font-bold mt-2">${item.price.toFixed(2)}</p>
                    </div>
                    {isAdmin && (
                      <div className="flex gap-3">
                        <Link
                          href={`/items/${id}/edit`}
                          className="bg-yellow-400 text-white px-3 py-1.5 rounded-lg hover:bg-yellow-500 transition text-sm"
                        >
                          Edit
                        </Link>
                        <DeleteButton id={Number(id)} onDeleted={() => fetchItems(page)} />
                      </div>
                    )}
                  </div>
                ))}
              </div>

              {/* Pagination Controls */}
              {pagination && pagination.total_pages > 1 && (
                <div className="flex items-center justify-between mt-6">
                  <p className="text-sm text-gray-500">
                    Showing{" "}
                    <span className="font-medium">
                      {(pagination.page - 1) * pagination.page_size + 1}–
                      {Math.min(pagination.page * pagination.page_size, pagination.total)}
                    </span>{" "}
                    of <span className="font-medium">{pagination.total}</span> items
                  </p>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => setPage((p) => p - 1)}
                      disabled={!pagination.has_prev}
                      className="px-3 py-1.5 rounded-lg border border-gray-300 text-sm hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed transition"
                    >
                      ← Prev
                    </button>
                    {Array.from({ length: pagination.total_pages }, (_, i) => i + 1).map((p) => (
                      <button
                        key={p}
                        onClick={() => setPage(p)}
                        className={`px-3 py-1.5 rounded-lg text-sm border transition ${
                          p === pagination.page
                            ? "bg-blue-600 text-white border-blue-600"
                            : "border-gray-300 hover:bg-gray-100"
                        }`}
                      >
                        {p}
                      </button>
                    ))}
                    <button
                      onClick={() => setPage((p) => p + 1)}
                      disabled={!pagination.has_next}
                      className="px-3 py-1.5 rounded-lg border border-gray-300 text-sm hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed transition"
                    >
                      Next →
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </main>
    </ProtectedRoute>
  );
}
