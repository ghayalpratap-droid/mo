import { useEffect, useRef, useState } from "react";
import { ApiService, errorMessage } from "./ApiService";
import EmployeeList from "./components/EmployeeList";
import EmployeeForm from "./components/EmployeeForm";
import EditEmployee from "./components/EditEmployee";
import DeleteConfirmation from "./components/DeleteConfirmation";
function Modal({ title, children, onClose, busy }) {
  const ref = useRef();
  useEffect(() => {
    const dialog = ref.current;
    dialog.showModal();
    return () => dialog.close();
  }, []);
  return (
    <dialog
      ref={ref}
      onCancel={(e) => {
        e.preventDefault();
        if (!busy) onClose();
      }}
    >
      <div className="modal-heading">
        <div>
          <span className="eyebrow">EMPLOYEE DIRECTORY</span>
          <h2>{title}</h2>
        </div>
        <button
          className="close"
          aria-label="Close dialog"
          onClick={onClose}
          disabled={busy}
        >
          ×
        </button>
      </div>
      {children}
    </dialog>
  );
}
export default function App() {
  const [employees, setEmployees] = useState([]),
    [loading, setLoading] = useState(true),
    [error, setError] = useState(""),
    [query, setQuery] = useState(""),
    [modal, setModal] = useState(null),
    [busy, setBusy] = useState(false),
    [notice, setNotice] = useState(null),
    [serverErrors, setServerErrors] = useState({});
  async function load() {
    setLoading(true);
    setError("");
    try {
      setEmployees(await ApiService.all());
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    load();
  }, []);
  function open(type, employee) {
    setServerErrors({});
    setModal({ type, employee });
  }
  async function save(data) {
    setBusy(true);
    setNotice(null);
    try {
      if (modal.type === "edit")
        await ApiService.update(modal.employee.id, data);
      else await ApiService.create(data);
      setModal(null);
      setNotice({
        kind: "success",
        text:
          modal.type === "edit"
            ? "Employee updated successfully."
            : "Employee created successfully.",
      });
      await load();
    } catch (e) {
      setServerErrors(e.response?.data?.errors || {});
      setNotice({ kind: "error", text: errorMessage(e) });
    } finally {
      setBusy(false);
    }
  }
  async function remove() {
    setBusy(true);
    try {
      await ApiService.delete(modal.employee.id);
      setModal(null);
      setNotice({ kind: "success", text: "Employee deleted successfully." });
      await load();
    } catch (e) {
      setNotice({ kind: "error", text: errorMessage(e) });
    } finally {
      setBusy(false);
    }
  }
  async function view(e) {
    try {
      open("view", await ApiService.get(e.id));
    } catch (err) {
      setNotice({ kind: "error", text: errorMessage(err) });
    }
  }
  const visible = employees.filter((e) =>
    `${e.firstName} ${e.lastName} ${e.address} ${e.id}`
      .toLowerCase()
      .includes(query.toLowerCase()),
  );
  return (
    <div className="app">
      <aside>
        <a className="brand" href="/">
          p<span>People</span>
          <i>WORKSPACE</i>
        </a>
        <div className="workspace">
          P
          <span>
            People workspace<small>Employee management</small>
          </span>
        </div>
        <span className="nav-label">WORKSPACE</span>
        <nav>
          <a className="active" href="/">
            ▦ <span>Employees</span>
            <span className="nav-count">{employees.length}</span>
          </a>
        </nav>
        <div className="sidebar-bottom">
          <span className="status-dot" /> Your people, in one place
        </div>
      </aside>
      <main>
        <header>
          <span>
            Workspace <span className="slash">/</span>{" "}
            <strong>Employees</strong>
          </span>
          <span className="profile">PW</span>
        </header>
        <section className="content">
          <div className="page-heading">
            <div>
              <span className="eyebrow">YOUR TEAM, CONNECTED</span>
              <h1>
                Employee directory<span>.</span>
              </h1>
              <p>A little organization. A lot more room for your people.</p>
            </div>
            <button onClick={() => open("create")}>＋ Add employee</button>
          </div>
          <div className="stats">
            <div>
              <span>Total employees</span>
              <strong>
                {loading ? "—" : employees.length}
                <small>people in your workspace</small>
              </strong>
            </div>
            <div className="stat-note">
              <span className="leaf">✳</span>
              <div>
                <strong>Good teams start with people.</strong>
                <p>Keep their information organized and up to date.</p>
              </div>
            </div>
          </div>
          {notice && (
            <div
              className={`notice ${notice.kind}`}
              role={notice.kind === "error" ? "alert" : "status"}
            >
              {notice.text}
              <button
                aria-label="Dismiss notification"
                onClick={() => setNotice(null)}
              >
                ×
              </button>
            </div>
          )}
          <section className="directory">
            <div className="directory-heading">
              <div>
                <h2>
                  All employees <span>{employees.length}</span>
                </h2>
                <p>Your team’s details, all in one place.</p>
              </div>
              <label className="search">
                <span>⌕</span>
                <input
                  aria-label="Search employees"
                  placeholder="Search name, address or ID…"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                />
              </label>
            </div>
            <EmployeeList
              employees={visible}
              loading={loading}
              error={error}
              onRetry={load}
              onView={view}
              onEdit={(e) => open("edit", e)}
              onDelete={(e) => open("delete", e)}
            />
            <footer>
              {visible.length} employee{visible.length === 1 ? "" : "s"} shown
              <span>Built for the people behind the work.</span>
            </footer>
          </section>
          <div className="page-footer">
            PEOPLE WORKSPACE <span>Simple by design. Organized by you.</span>
          </div>
        </section>
      </main>
      {modal && (
        <Modal
          title={
            {
              create: "Add an employee",
              edit: "Edit employee",
              delete: "Delete employee?",
              view: "Employee details",
            }[modal.type]
          }
          onClose={() => setModal(null)}
          busy={busy}
        >
          {(modal.type === "create" || modal.type === "edit") &&
            (modal.type === "edit" ? (
              <EditEmployee
                employee={modal.employee}
                onSubmit={save}
                onCancel={() => setModal(null)}
                busy={busy}
                serverErrors={serverErrors}
              />
            ) : (
              <EmployeeForm
                onSubmit={save}
                onCancel={() => setModal(null)}
                busy={busy}
                serverErrors={serverErrors}
              />
            ))}
          {modal.type === "delete" && (
            <DeleteConfirmation
              employee={modal.employee}
              onConfirm={remove}
              onCancel={() => setModal(null)}
              busy={busy}
            />
          )}
          {modal.type === "view" && (
            <>
              <dl>
                {[
                  ["Employee ID", modal.employee.id],
                  [
                    "Name",
                    `${modal.employee.firstName} ${modal.employee.lastName}`,
                  ],
                  ["Age", modal.employee.age],
                  ["Address", modal.employee.address],
                ].map(([label, value]) => (
                  <div key={label}>
                    <dt>{label}</dt>
                    <dd>{value}</dd>
                  </div>
                ))}
              </dl>
              <button onClick={() => open("edit", modal.employee)}>
                Edit employee
              </button>
            </>
          )}
          {notice?.kind === "error" && (
            <p role="alert" className="field-error">
              {notice.text}
            </p>
          )}
        </Modal>
      )}
    </div>
  );
}
