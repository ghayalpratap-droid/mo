import { useState } from "react";
export function validate(v) {
  const e = {};
  for (const key of ["firstName", "lastName", "address"]) {
    if (!v[key]?.trim()) e[key] = "This field is required";
    else if (v[key].trim().length > (key === "address" ? 500 : 100))
      e[key] = "This value is too long";
  }
  for (const key of ["firstName", "lastName"]) {
    if (!e[key] && !/^\p{L}[\p{L}\p{M} '’-]*$/u.test(v[key].trim()))
      e[key] = "Use letters, spaces, apostrophes or hyphens only";
  }
  if (
    !String(v.age).trim() ||
    !Number.isInteger(Number(v.age)) ||
    Number(v.age) < 18 ||
    Number(v.age) > 100
  )
    e.age = "Enter a whole number between 18 and 100";
  return e;
}
export default function EmployeeForm({
  employee,
  onSubmit,
  onCancel,
  busy,
  serverErrors = {},
}) {
  const [values, setValues] = useState(
    employee || { firstName: "", lastName: "", age: "", address: "" },
  );
  const [errors, setErrors] = useState({});
  const fields = [
    ["firstName", "First name"],
    ["lastName", "Last name"],
    ["age", "Age"],
    ["address", "Address"],
  ];
  function submit(e) {
    e.preventDefault();
    const next = validate(values);
    setErrors(next);
    if (!Object.keys(next).length)
      onSubmit({
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        age: Number(values.age),
        address: values.address.trim(),
      });
  }
  return (
    <form onSubmit={submit} noValidate>
      <div className="form-grid">
        {fields.map(([key, label]) => (
          <label key={key} className={key === "address" ? "wide" : ""}>
            {label}
            {key === "address" ? (
              <textarea
                value={values[key]}
                onChange={(e) =>
                  setValues({ ...values, [key]: e.target.value })
                }
                maxLength={500}
                aria-label={label}
                aria-describedby={
                  errors[key] || serverErrors[key] ? `error-${key}` : undefined
                }
                aria-invalid={!!(errors[key] || serverErrors[key])}
              />
            ) : (
              <input
                type={key === "age" ? "number" : "text"}
                value={values[key]}
                min={key === "age" ? 18 : undefined}
                max={key === "age" ? 100 : undefined}
                maxLength={key === "age" ? undefined : 100}
                onChange={(e) =>
                  setValues({ ...values, [key]: e.target.value })
                }
                aria-label={label}
                aria-describedby={
                  errors[key] || serverErrors[key] ? `error-${key}` : undefined
                }
                aria-invalid={!!(errors[key] || serverErrors[key])}
              />
            )}
            {(errors[key] || serverErrors[key]) && (
              <span className="field-error" id={`error-${key}`}>
                {errors[key] || serverErrors[key]}
              </span>
            )}
          </label>
        ))}
      </div>
      <div className="form-actions">
        <button
          type="button"
          className="secondary"
          onClick={onCancel}
          disabled={busy}
        >
          Cancel
        </button>
        <button disabled={busy}>
          {busy ? "Saving…" : employee ? "Save changes" : "Create employee"}
        </button>
      </div>
    </form>
  );
}
