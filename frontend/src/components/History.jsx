import { useEffect, useState } from "react";
import { ApiService, errorMessage } from "../ApiService";
export default function History() {
  const [items, setItems] = useState([]),
    [loading, setLoading] = useState(true),
    [error, setError] = useState("");
  async function load() {
    setLoading(true);
    setError("");
    try {
      setItems(await ApiService.history());
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    load();
  }, []);
  if (loading) return <p role="status">Loading activity…</p>;
  if (error)
    return (
      <div role="alert">
        <p>{error}</p>
        <button onClick={load}>Try again</button>
      </div>
    );
  return (
    <div className="history-list">
      <p className="history-note">
        Latest 200 changes · saved in your database
      </p>
      {!items.length ? (
        <p>No activity yet. Add your first employee 😊</p>
      ) : (
        items.map((a) => (
          <article key={a.id}>
            <span
              className={`activity-icon ${a.action.toLowerCase()}`}
              aria-hidden="true"
            >
              {{ CREATED: "😊", UPDATED: "🛠️", DELETED: "😢" }[a.action]}
            </span>
            <div>
              <strong>{a.employeeName}</strong>
              <p>
                EMP-{String(a.employeeId).padStart(4, "0")} ·{" "}
                {
                  {
                    CREATED: "Added to the team",
                    UPDATED: "Details updated",
                    DELETED: "Removed from the team",
                  }[a.action]
                }
              </p>
              <time dateTime={a.occurredAt}>
                {new Date(a.occurredAt).toLocaleString()}
              </time>
            </div>
          </article>
        ))
      )}
    </div>
  );
}
