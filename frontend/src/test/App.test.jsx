import { vi, test, expect, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import App from "../App";
import EmployeeForm, { validate } from "../components/EmployeeForm";
import { ApiService } from "../ApiService";
vi.mock("../ApiService", () => ({
  ApiService: {
    history: vi.fn(),
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
  ApiService.history.mockResolvedValue([]);
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

test("rejects numeric and mixed names but accepts international names", () => {
  for (const name of ["123", "Sharma43", "１２３", "Rahul@", "---"]) {
    expect(validate({ ...employee, firstName: name }).firstName).toBeTruthy();
    expect(validate({ ...employee, lastName: name }).lastName).toBeTruthy();
  }
  for (const name of ["राहुल", "José", "O’Connor", "Anne-Marie", "De Silva"]) {
    expect(validate({ ...employee, firstName: name, lastName: name })).toEqual(
      {},
    );
  }
});
test("prevents numeric names from submitting on create and edit", async () => {
  const submit = vi.fn();
  const { rerender } = render(
    <EmployeeForm
      employee={{ ...employee, lastName: "Sharma43" }}
      onSubmit={submit}
      onCancel={() => {}}
    />,
  );
  await userEvent.click(screen.getByRole("button", { name: "Save changes" }));
  expect(submit).not.toHaveBeenCalled();
  expect(
    screen.getByText("Use letters, spaces, apostrophes or hyphens only"),
  ).toBeInTheDocument();
  rerender(<EmployeeForm onSubmit={submit} onCancel={() => {}} />);
  await userEvent.click(
    screen.getByRole("button", { name: "Create employee" }),
  );
  expect(submit).not.toHaveBeenCalled();
});

test("opens persisted history and displays deleted employee", async () => {
  ApiService.history.mockResolvedValue([
    {
      id: 1,
      employeeId: 7,
      employeeName: "Former Teammate",
      action: "DELETED",
      occurredAt: "2026-10-10T12:00:00Z",
    },
  ]);
  render(<App />);
  await userEvent.click(screen.getByRole("button", { name: "History" }));
  expect(await screen.findByText("Former Teammate")).toBeInTheDocument();
  expect(screen.getByText(/Removed from the team/)).toBeInTheDocument();
});
test("shows about and supports muting click sounds", async () => {
  render(<App />);
  const sound = screen.getByRole("button", { name: "Click sounds" });
  expect(sound).toHaveAttribute("aria-pressed", "true");
  await userEvent.click(sound);
  expect(sound).toHaveAttribute("aria-pressed", "false");
  await userEvent.click(screen.getByRole("button", { name: "About" }));
  expect(
    screen.getByRole("heading", { name: "About People" }),
  ).toBeInTheDocument();
});
test("saved employee gets a bounce highlight", async () => {
  ApiService.update.mockResolvedValue(employee);
  render(<App />);
  await userEvent.click(
    await screen.findByRole("button", { name: "Edit Rahul Sharma" }),
  );
  await userEvent.click(screen.getByRole("button", { name: "Save changes" }));
  await waitFor(() =>
    expect(
      screen.getByRole("button", { name: "Edit Rahul Sharma" }).closest("tr"),
    ).toHaveClass("employee-bounce"),
  );
});
