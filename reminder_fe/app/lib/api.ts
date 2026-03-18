import { Priority, Reminder, ReminderList } from "./types";

const BASE = "/api";

async function fetchJson<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, init);
  if (!res.ok) {
    throw new Error(`API error: ${res.status}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

export const listApi = {
  findAll: () => fetchJson<ReminderList[]>(`${BASE}/lists`),
  create: (name: string, color: string) =>
    fetchJson<ReminderList>(`${BASE}/lists`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, color }),
    }),
  update: (id: number, name: string, color: string) =>
    fetchJson<ReminderList>(`${BASE}/lists/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, color }),
    }),
  delete: (id: number) =>
    fetchJson<void>(`${BASE}/lists/${id}`, { method: "DELETE" }),
};

export const reminderApi = {
  findByListId: (listId: number) =>
    fetchJson<Reminder[]>(`${BASE}/lists/${listId}/reminders`),
  create: (listId: number, title: string) =>
    fetchJson<Reminder>(`${BASE}/reminders`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ listId, title }),
    }),
  update: (
    id: number,
    data: {
      title: string;
      memo?: string | null;
      dueDate?: string | null;
      dueTime?: string | null;
      priority?: Priority;
    }
  ) =>
    fetchJson<Reminder>(`${BASE}/reminders/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }),
  toggle: (id: number) =>
    fetchJson<Reminder>(`${BASE}/reminders/${id}/toggle`, { method: "PATCH" }),
  delete: (id: number) =>
    fetchJson<void>(`${BASE}/reminders/${id}`, { method: "DELETE" }),
};
