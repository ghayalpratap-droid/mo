import { useEffect, useRef, useState } from "react";
import { ApiService, errorMessage } from "./ApiService";
import EmployeeList from "./components/EmployeeList";
import EmployeeForm from "./components/EmployeeForm";
import EditEmployee from "./components/EditEmployee";
import DeleteConfirmation from "./components/DeleteConfirmation";
import SocialLinks from "./components/SocialLinks";
import History from "./components/History";
import { playClick } from "./sounds";
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
  const [highlightId, setHighlightId] = useState(null);
  const [soundOn, setSoundOn] = useState(() => {
    try {
      return localStorage.getItem("people-sound") !== "off";
    } catch {
      return true;
    }
  });
  useEffect(() => {
    if (!highlightId) return;
    const timer = setTimeout(() => setHighlightId(null), 1500);
    return () => clearTimeout(timer);
  }, [highlightId]);
  useEffect(() => {
    if (!loading && highlightId)
      document
        .querySelector(`[data-employee-id="${highlightId}"]`)
        ?.scrollIntoView?.({
          behavior: window.matchMedia?.("(prefers-reduced-motion: reduce)")
            ?.matches
            ? "auto"
            : "smooth",
          block: "nearest",
        });
  }, [loading, highlightId]);
  function toggleSound() {
    setSoundOn((v) => {
      try {
        localStorage.setItem("people-sound", v ? "off" : "on");
      } catch {}
      return !v;
    });
  }
  function sound(e) {
    if (soundOn && e.target.closest("button:not(:disabled),a")) playClick();
  }
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
    setHighlightId(null);
    try {
      const saved =
        modal.type === "edit"
          ? await ApiService.update(modal.employee.id, data)
          : await ApiService.create(data);
      setQuery("");
      setModal(null);
      setNotice({
        kind: "success",
        text:
          modal.type === "edit"
            ? "Employee updated successfully."
            : "Employee created successfully.",
      });
      await load();
      setHighlightId(saved.id);
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
    <div className="app" onClickCapture={sound}>
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
          <div className="header-tools">
            <div className="header-navigation">
              <button onClick={() => open("history")}>History</button>
              <button onClick={() => open("about")}>About</button>
              <button
                className="sound-toggle"
                aria-label="Click sounds"
                aria-pressed={soundOn}
                onClick={toggleSound}
              >
                {soundOn ? "🔊" : "🔇"}
              </button>
            </div>
            <SocialLinks />
            <span className="profile">PW</span>
          </div>
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
            <button onClick={() => open("create")} aria-label="Add employee">
              😊 Add employee
            </button>
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
              <span>
                <span aria-hidden="true">
                  {notice.kind === "error" ? "⚠️" : "✨"}
                </span>{" "}
                {notice.text}
              </span>
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
              highlightId={highlightId}
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
          <section className="social-banner" aria-label="Social spotlight">
            <div>
              <span className="eyebrow">
                A LITTLE BREAK, A LITTLE INSPIRATION
              </span>
              <h2>People make the story. Stay inspired.</h2>
              <p>Explore Instagram & YouTube</p>
              <div className="ticker-window">
                <div className="social-ticker">
                  <span>📸 Instagram · Moments that matter</span>
                  <span>▶ YouTube · Ideas worth watching</span>
                  <span aria-hidden="true">
                    📸 Instagram · Moments that matter
                  </span>
                  <span aria-hidden="true">
                    ▶ YouTube · Ideas worth watching
                  </span>
                </div>
              </div>
            </div>
            <SocialLinks />
          </section>
          <div className="page-footer">
            PEOPLE WORKSPACE{" "}
            <span>Visit again — your team is always welcome 😊</span>
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
              history: "Activity history",
              about: "About People",
            }[modal.type]
          }
          onClose={() => setModal(null)}
          busy={busy}
        >
          {["create", "edit", "delete"].includes(modal.type) && (
            <div className={`modal-mood ${modal.type}`} aria-hidden="true">
              {{ create: "😊", edit: "🧰", delete: "😢" }[modal.type]}
            </div>
          )}
          {modal.type === "history" && <History />}
          {modal.type === "about" && (
            <div className="about-content">
              <span className="about-icon" aria-hidden="true">
                🌿
              </span>
              <p>
                <strong>People Workspace</strong> keeps your employee
                information organized, with thoughtful details and a little
                personality.
              </p>
              <p>
                Add, view, edit and remove employees. Activity history is stored
                in your database and survives refreshes and restarts.
              </p>
              <p>Built with React, Spring Boot and H2 for local development.</p>
              <SocialLinks />
            </div>
          )}
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
