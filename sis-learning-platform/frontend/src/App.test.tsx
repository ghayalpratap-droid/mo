import React from "react";
import { it, expect, vi, afterEach } from "vitest";
import { render, screen, fireEvent, cleanup } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { QueryClientProvider } from "@tanstack/react-query";
import { App, client } from "./App";
afterEach(() => {
  cleanup();
  client.clear();
  vi.unstubAllGlobals();
});
function mount(path = "/") {
  return render(
    <QueryClientProvider client={client}>
      <MemoryRouter initialEntries={[path]}>
        <App />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}
it("keeps authentication failures visible and never opens the portal", async () => {
  vi.stubGlobal(
    "fetch",
    vi
      .fn()
      .mockResolvedValue(
        new Response(JSON.stringify({ message: "Invalid credentials" }), {
          status: 401,
        }),
      ),
  );
  mount();
  fireEvent.change(screen.getByLabelText("Local password"), {
    target: { value: "synthetic-only" },
  });
  fireEvent.click(screen.getByText("Sign in"));
  expect((await screen.findByRole("alert")).textContent).toContain(
    "Invalid credentials",
  );
  expect(screen.queryByText("New application")).toBeNull();
});
it("hides privileged navigation for provider advisors while rendering applications", async () => {
  vi.stubGlobal(
    "fetch",
    vi.fn(async (input: RequestInfo | URL) => {
      const path = String(input);
      return new Response(
        JSON.stringify(
          path.endsWith("/auth/login")
            ? {
                token: "synthetic-test-token",
                username: "advisor-a",
                provider: "A",
                roles: ["ADVISOR"],
              }
            : [],
        ),
        { status: 200 },
      );
    }),
  );
  mount();
  fireEvent.change(screen.getByLabelText("Local password"), {
    target: { value: "synthetic-only" },
  });
  fireEvent.click(screen.getByText("Sign in"));
  await screen.findByText("New application");
  expect(screen.queryByRole("link", { name: "Reports" })).toBeNull();
  expect(screen.queryByRole("link", { name: "Knowledge sources" })).toBeNull();
  expect(
    screen.getByRole("link", { name: "Knowledge assistant" }),
  ).toBeTruthy();
});
it("shows a server authorization denial on a directly entered protected route", async () => {
  vi.stubGlobal(
    "fetch",
    vi.fn(async (input: RequestInfo | URL) => {
      const login = String(input).endsWith("/auth/login");
      return new Response(
        JSON.stringify(
          login
            ? {
                token: "synthetic-test-token",
                username: "advisor-a",
                provider: "A",
                roles: ["ADVISOR"],
              }
            : { message: "Reporting role required" },
        ),
        { status: login ? 200 : 403 },
      );
    }),
  );
  mount("/reports");
  fireEvent.change(screen.getByLabelText("Local password"), {
    target: { value: "synthetic-only" },
  });
  fireEvent.click(screen.getByText("Sign in"));
  expect((await screen.findByRole("alert")).textContent).toContain(
    "Reporting role required",
  );
});
