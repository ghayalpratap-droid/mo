export default function DeleteConfirmation({
  employee,
  onConfirm,
  onCancel,
  busy,
}) {
  return (
    <>
      <p>
        Remove{" "}
        <strong>
          {employee.firstName} {employee.lastName}
        </strong>{" "}
        from your employee directory? This action cannot be undone.
      </p>
      <div className="form-actions">
        <button className="secondary" onClick={onCancel} disabled={busy}>
          Keep employee
        </button>
        <button
          className="danger"
          onClick={onConfirm}
          disabled={busy}
          aria-label={busy ? "Deleting…" : "Delete employee"}
        >
          <span aria-hidden="true">😢</span>{" "}
          {busy ? "Deleting…" : "Delete employee"}
        </button>
      </div>
    </>
  );
}
