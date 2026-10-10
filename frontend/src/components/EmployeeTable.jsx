export default function EmployeeTable({ employees, onView, onEdit, onDelete }) {
  return (
    <div className="table-scroll">
      <table>
        <thead>
          <tr>
            <th>Employee</th>
            <th>Employee ID</th>
            <th>Age</th>
            <th>Address</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((e) => (
            <tr key={e.id}>
              <td>
                <button className="person" onClick={() => onView(e)}>
                  <span className="avatar">
                    {e.firstName[0]}
                    {e.lastName[0]}
                  </span>
                  <span>
                    {e.firstName} {e.lastName}
                    <small>Employee</small>
                  </span>
                </button>
              </td>
              <td className="muted">EMP-{String(e.id).padStart(4, "0")}</td>
              <td>{e.age}</td>
              <td className="address">{e.address}</td>
              <td>
                <div className="row-actions">
                  <button
                    className="text-button"
                    aria-label={`Edit ${e.firstName} ${e.lastName}`}
                    onClick={() => onEdit(e)}
                  >
                    Edit
                  </button>
                  <button
                    className="text-button delete"
                    aria-label={`Delete ${e.firstName} ${e.lastName}`}
                    onClick={() => onDelete(e)}
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
