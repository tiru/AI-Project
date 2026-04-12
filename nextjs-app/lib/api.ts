const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8000";

function authHeaders(token: string) {
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
}

export interface Pagination {
  page: number;
  page_size: number;
  total: number;
  total_pages: number;
  has_prev: boolean;
  has_next: boolean;
}

export interface ItemsResponse {
  items: Record<string, any>;
  pagination: Pagination;
}

export async function getItems(
  token: string,
  filters?: { search?: string; min_price?: string; max_price?: string; page?: number; page_size?: number }
): Promise<ItemsResponse> {
  const params = new URLSearchParams();
  if (filters?.search) params.set("search", filters.search);
  if (filters?.min_price) params.set("min_price", filters.min_price);
  if (filters?.max_price) params.set("max_price", filters.max_price);
  if (filters?.page) params.set("page", String(filters.page));
  if (filters?.page_size) params.set("page_size", String(filters.page_size));
  const query = params.toString() ? `?${params.toString()}` : "";
  const res = await fetch(`${API_URL}/items${query}`, {
    headers: authHeaders(token),
    cache: "no-store",
  });
  return res.json();
}

export async function getItem(id: number, token: string) {
  const res = await fetch(`${API_URL}/items/${id}`, {
    headers: authHeaders(token),
    cache: "no-store",
  });
  return res.json();
}

export async function createItem(
  item: { name: string; description: string; price: number },
  token: string
) {
  const res = await fetch(`${API_URL}/items`, {
    method: "POST",
    headers: authHeaders(token),
    body: JSON.stringify(item),
  });
  return res.json();
}

export async function updateItem(
  id: number,
  item: { name: string; description: string; price: number },
  token: string
) {
  const res = await fetch(`${API_URL}/items/${id}`, {
    method: "PUT",
    headers: authHeaders(token),
    body: JSON.stringify(item),
  });
  return res.json();
}

export async function deleteItem(id: number, token: string) {
  const res = await fetch(`${API_URL}/items/${id}`, {
    method: "DELETE",
    headers: authHeaders(token),
  });
  return res.json();
}
