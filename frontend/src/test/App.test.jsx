import { vi, test, expect, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import App from "../App";
import EmployeeForm, { validate } from "../components/EmployeeForm";
import { ApiService } from "../ApiService";
vi.mock("../ApiService", () => ({
  ApiService: {
    all: vi.fn(),
    get: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn(),
  },
  errorMessage: () => "Server unavailable",
}));
const employee = {
  id: 1,
  firstName: "Rahul",
  lastName: "Sharma",
  age: 28,
  address: "Pune, Maharashtra",
};
beforeEach(() => {
  vi.clearAllMocks();
  ApiService.all.mockResolvedValue([employee]);
});
test("validates required fields and integer age", () => {
  expect(
    Object.keys(
      validate({ firstName: "", lastName: "", age: "abc", address: "" }),
    ),
  ).toHaveLength(4);
  expect(validate({ ...employee, age: 18.5 }).age).toBeTruthy();
  expect(validate(employee)).toEqual({});
});
test("submits trimmed valid employee", async () => {
  const submit = vi.fn();
  render(<EmployeeForm onSubmit={submit} onCancel={() => {}} />);
  const user = userEvent.setup();
  await user.type(screen.getByLabelText("First name"), " Rahul ");
  await user.type(screen.getByLabelText("Last name"), "Sharma");
  await user.type(screen.getByLabelText("Age"), "28");
  await user.type(screen.getByLabelText("Address"), "Pune");
  await user.click(screen.getByRole("button", { name: "Create employee" }));
  expect(submit).toHaveBeenCalledWith({
    ...employee,
    id: undefined,
    address: "Pune",
  });
});
test("loads persisted employees and filters directory", async () => {
  render(<App />);
  expect(await screen.findByText("Rahul Sharma")).toBeInTheDocument();
  await userEvent.type(screen.getByLabelText("Search employees"), "missing");
  expect(screen.getByText("No employees found")).toBeInTheDocument();
});
test("edits employee and refreshes list", async () => {
  ApiService.update.mockResolvedValue({ ...employee, age: 29 });
  render(<App />);
  await userEvent.click(
    await screen.findByRole("button", { name: "Edit Rahul Sharma" }),
  );
  await userEvent.clear(screen.getByLabelText("Age"));
  await userEvent.type(screen.getByLabelText("Age"), "29");
  await userEvent.click(screen.getByRole("button", { name: "Save changes" }));
  await waitFor(() =>
    expect(ApiService.update).toHaveBeenCalledWith(1, {
      firstName: "Rahul",
      lastName: "Sharma",
      age: 29,
      address: "Pune, Maharashtra",
    }),
  );
  expect(
    await screen.findByText("Employee updated successfully."),
  ).toBeInTheDocument();
});
test("requires confirmation before deleting", async () => {
  ApiService.delete.mockResolvedValue({});
  render(<App />);
  await userEvent.click(
    await screen.findByRole("button", { name: "Delete Rahul Sharma" }),
  );
  expect(ApiService.delete).not.toHaveBeenCalled();
  await userEvent.click(
    screen.getByRole("button", { name: "Delete employee" }),
  );
  await waitFor(() => expect(ApiService.delete).toHaveBeenCalledWith(1));
});
test("offers retry after an API error", async () => {
  ApiService.all.mockRejectedValueOnce(new Error());
  render(<App />);
  expect(await screen.findByText("Directory unavailable")).toBeInTheDocument();
  await userEvent.click(screen.getByRole("button", { name: "Try again" }));
  expect(await screen.findByText("Rahul Sharma")).toBeInTheDocument();
});
