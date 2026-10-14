import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api/v1/ai": "http://127.0.0.1:8092",
      "/api": "http://127.0.0.1:8091",
    },
  },
  test: { environment: "jsdom" },
});
