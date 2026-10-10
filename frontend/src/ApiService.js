import axios from "axios";
const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 15000,
});
export const ApiService = {
  history: () => client.get("/history").then((r) => r.data),
  all: () => client.get("/employees").then((r) => r.data),
  get: (id) => client.get(`/employees/${id}`).then((r) => r.data),
  create: (data) => client.post("/employees", data).then((r) => r.data),
  update: (id, data) =>
    client.put(`/employees/${id}`, data).then((r) => r.data),
  delete: (id) => client.delete(`/employees/${id}`),
};
export function errorMessage(e) {
  return (
    e.response?.data?.message || "Unable to reach the server. Please try again."
  );
}
