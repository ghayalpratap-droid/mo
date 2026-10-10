import EmployeeTable from "./EmployeeTable";
export default function EmployeeList({
  employees,
  loading,
  error,
  onRetry,
  ...props
}) {
  if (loading)
    return (
      <div className="empty" role="status">
        Loading your directory…
      </div>
    );
  if (error)
    return (
      <div className="empty">
        <h3>Directory unavailable</h3>
        <p>{error}</p>
        <button onClick={onRetry}>Try again</button>
      </div>
    );
  if (!employees.length)
    return (
      <div className="empty">
        <span className="empty-symbol">◎</span>
        <h3>No employees found</h3>
        <p>Add your first employee or adjust your search.</p>
      </div>
    );
  return <EmployeeTable employees={employees} {...props} />;
}
