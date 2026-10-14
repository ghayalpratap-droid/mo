import React, { createContext, useContext, useState, useRef } from "react";
import { createRoot } from "react-dom/client";
import {
  BrowserRouter,
  Routes,
  Route,
  NavLink,
  useParams,
  useNavigate,
} from "react-router-dom";
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import {
  api,
  streamAnswer,
  type User,
  type Row,
  type Application,
} from "./api";
import "./style.css";
const Auth = createContext<User | null>(null);
export const client = new QueryClient({
  defaultOptions: { queries: { retry: false } },
});
function useData<T>(path: string) {
  const u = useContext(Auth);
  return useQuery({
    queryKey: [u?.username, path],
    queryFn: () => api<T>(u, path),
  });
}
function ErrorBox({ error }: { error: unknown }) {
  return error ? (
    <p role="alert" className="error">
      {String(error instanceof Error ? error.message : error)}
    </p>
  ) : null;
}
function Table({ rows }: { rows: Row[] }) {
  if (!rows.length) return <p>No records found.</p>;
  const keys = Object.keys(rows[0]);
  return (
    <div className="scroll">
      <table>
        <thead>
          <tr>
            {keys.map((k) => (
              <th key={k}>{k}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              {keys.map((k) => (
                <td key={k}>
                  {typeof r[k] === "object"
                    ? JSON.stringify(r[k])
                    : String(r[k] ?? "—")}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
function Data({ title, path }: { title: string; path: string }) {
  const q = useData<Row[]>(path);
  return (
    <section>
      <h2>{title}</h2>
      <ErrorBox error={q.error} />
      {q.isPending ? (
        <p>Loading…</p>
      ) : (
        !q.error && <Table rows={q.data ?? []} />
      )}
    </section>
  );
}
function Field({
  name,
  label,
  type = "text",
  value,
  options,
}: {
  name: string;
  label: string;
  type?: string;
  value?: string;
  options?: string[];
}) {
  return (
    <label>
      {label}
      {options ? (
        <select aria-label={label} name={name} defaultValue={value}>
          {options.map((x) => (
            <option key={x}>{x}</option>
          ))}
        </select>
      ) : (
        <input
          aria-label={label}
          name={name}
          type={type}
          defaultValue={value}
          required={type !== "number"}
          step={type === "number" ? "0.01" : undefined}
        />
      )}
    </label>
  );
}
function Login({ done }: { done: (u: User) => void }) {
  const [e, setE] = useState("");
  return (
    <main className="login">
      <div className="mark">S</div>
      <p className="eyebrow">SIS / LLE LEARNING PLATFORM</p>
      <h1>
        A clearer view of
        <br />
        student finance.
      </h1>
      <p>Educational reconstruction · fictitious records only</p>
      <form
        onSubmit={async (e) => {
          e.preventDefault();
          try {
            done(
              await api<User>(
                null,
                "/auth/login",
                "POST",
                Object.fromEntries(new FormData(e.currentTarget)),
              ),
            );
          } catch (x) {
            setE(String(x));
          }
        }}
      >
        <Field
          name="username"
          label="Account"
          options={[
            "advisor-a",
            "advisor-b",
            "admin",
            "support",
            "developer",
            "reporter",
          ]}
        />
        <Field name="password" label="Local password" type="password" />
        <button>Sign in</button>
        <ErrorBox error={e} />
        <small>Use SIS_DEMO_PASSWORD from the local .env file.</small>
      </form>
    </main>
  );
}
function Applications() {
  const u = useContext(Auth)!;
  const [q, setQ] = useState("");
  const [page, setPage] = useState(0);
  const rows = useData<Application[]>(
    `/applications?q=${encodeURIComponent(q)}&page=${page}`,
  );
  const courses = useData<Row[]>("/courses");
  const [e, setE] = useState("");
  const nav = useNavigate();
  return (
    <>
      <p className="eyebrow">STUDENT FINANCE · PROVIDER {u.provider}</p>
      <h1>Applications</h1>
      <p>Intake, confirmations and a traceable history.</p>
      <label>
        Search
        <input
          placeholder="Source reference or student name"
          value={q}
          onChange={(e) => {
            setQ(e.target.value);
            setPage(0);
          }}
        />
      </label>
      <ErrorBox error={rows.error} />
      {rows.isPending ? (
        <p>Loading applications…</p>
      ) : (
        <div className="cards">
          {rows.data?.map((a) => (
            <NavLink className="card" to={"/applications/" + a.id} key={a.id}>
              <span className="badge">{a.source}</span>
              <h2>{a.studentName}</h2>
              <p>
                {a.sourceRef} · {a.courseId}
              </p>
              <strong>{a.status}</strong>
              <small>
                Registration {a.registration} · Attendance {a.attendance}
              </small>
            </NavLink>
          ))}
          {rows.data?.length === 0 && <p>No applications yet.</p>}
        </div>
      )}
      <div className="actions">
        <button disabled={!page} onClick={() => setPage(page - 1)}>
          Previous
        </button>
        <span>Page {page + 1}</span>
        <button
          disabled={(rows.data?.length ?? 0) < 20}
          onClick={() => setPage(page + 1)}
        >
          Next
        </button>
      </div>
      {u.roles.some((r) => ["ADMIN", "ADVISOR"].includes(r)) && (
        <section>
          <h2>New application</h2>
          <form
            className="grid"
            onSubmit={async (e) => {
              e.preventDefault();
              const f = Object.fromEntries(new FormData(e.currentTarget));
              try {
                const a = await api<Application>(u, "/applications", "POST", {
                  ...f,
                  academicYear: Number(f.academicYear),
                  fee: Number(f.fee),
                });
                nav("/applications/" + a.id);
              } catch (x) {
                setE(String(x));
              }
            }}
          >
            <Field
              name="provider"
              label="Provider"
              options={u.roles.includes("ADMIN") ? ["A", "B"] : [u.provider]}
            />
            <Field name="sourceRef" label="Source reference" />
            <Field name="studentName" label="Fictitious student name" />
            <Field
              name="source"
              label="Source"
              options={["UG", "PGL", "PGD", "PGNI", "APPLY_ONCE", "LLE"]}
            />
            <Field
              name="courseId"
              label="Course"
              options={courses.data?.map((c) => String(c.id)) ?? []}
            />
            <Field
              name="academicYear"
              label="Academic year"
              type="number"
              value="2026"
            />
            <Field
              name="fee"
              label="Demo fee (GBP)"
              type="number"
              value="9250.00"
            />
            <div>
              <button>Create application</button>
            </div>
          </form>
          <ErrorBox error={e} />
        </section>
      )}
    </>
  );
}
function Detail() {
  const { id } = useParams();
  const u = useContext(Auth)!;
  const cache = useQueryClient();
  const app = useData<Application>("/applications/" + id);
  const [e, setE] = useState("");
  const [result, setResult] = useState<unknown>();
  async function act(path: string, body?: unknown) {
    try {
      setE("");
      setResult(await api(u, `/applications/${id}/${path}`, "POST", body));
      await cache.invalidateQueries();
    } catch (x) {
      setE(String(x));
    }
  }
  if (!app.data)
    return (
      <>
        <p>Loading application…</p>
        <ErrorBox error={app.error} />
      </>
    );
  const a = app.data;
  const write = u.roles.some((r) => ["ADMIN", "ADVISOR"].includes(r));
  return (
    <>
      <p className="eyebrow">APPLICATION / {a.sourceRef}</p>
      <h1>{a.studentName}</h1>
      <div className="summary">
        {[
          ["Status", a.status],
          ["Registration", a.registration],
          ["Attendance", a.attendance],
          ["Demo fee", "£" + Number(a.fee).toFixed(2)],
        ].map(([k, v]) => (
          <div key={k}>
            <small>{k}</small>
            <h2>{v}</h2>
          </div>
        ))}
      </div>
      <ErrorBox error={e} />
      {write && (
        <>
          <section>
            <h2>Confirmations</h2>
            <div className="actions">
              <button
                onClick={() =>
                  act("registration", {
                    version: a.version,
                    status: "CONFIRMED",
                  })
                }
              >
                Confirm registration
              </button>
              <button
                onClick={() =>
                  act("attendance", { version: a.version, status: "CONFIRMED" })
                }
              >
                Confirm attendance
              </button>
              <button
                onClick={() =>
                  act("attendance", {
                    version: a.version,
                    status: "NOT_ATTENDING",
                  })
                }
              >
                Record not attending
              </button>
              <button
                onClick={() => act("payment-schedules?version=" + a.version)}
              >
                Schedule demo payments
              </button>
            </div>
          </section>
          <section>
            <h2>Change of circumstance</h2>
            <p>Deterministic demo rules, not official SLC policy.</p>
            <form
              className="grid"
              onSubmit={(e) => {
                e.preventDefault();
                const f = Object.fromEntries(new FormData(e.currentTarget));
                const button = (e.nativeEvent as SubmitEvent)
                  .submitter as HTMLButtonElement;
                act(button.value === "preview" ? "cocs/preview" : "cocs", {
                  ...f,
                  version: a.version,
                  amount: f.amount ? Number(f.amount) : null,
                  credits: f.credits ? Number(f.credits) : null,
                });
              }}
            >
              <Field
                name="type"
                label="Change type"
                options={[
                  "WITHDRAWAL",
                  "SUSPENSION",
                  "RESUMPTION",
                  "CANCELLATION",
                  "FEE_CHANGE",
                  "FEE_WAIVER",
                  "CREDITS",
                  "COURSE_TRANSFER",
                  "PROVIDER_TRANSFER",
                ]}
              />
              <Field
                name="effectiveDate"
                label="Effective date"
                type="date"
                value={new Date().toISOString().slice(0, 10)}
              />
              <Field name="amount" label="Amount" type="number" />
              <Field name="credits" label="Credits" type="number" />
              <label>
                Target course
                <input name="targetCourse" />
              </label>
              <div className="actions">
                <button value="preview">Validate only</button>
                <button value="submit">Submit CoC</button>
              </div>
            </form>
          </section>
        </>
      )}
      {result != null && (
        <section>
          <h2>Last action result</h2>
          <pre>{JSON.stringify(result, null, 2)}</pre>
        </section>
      )}
      <Data
        title="Payment schedule (demo 25/25/remainder)"
        path={`/applications/${id}/payment-schedules`}
      />
      <Data title="CoC decisions" path={`/applications/${id}/cocs`} />
      <Data
        title="Immutable application timeline"
        path={`/applications/${id}/history`}
      />
    </>
  );
}
function Imports() {
  const u = useContext(Auth)!;
  const [id, setId] = useState("");
  const [e, setE] = useState("");
  return (
    <>
      <h1>CSV submissions</h1>
      <p>Up to 500 rows; standard quoted CSV fields are supported. Header:</p>
      <pre>sourceRef,studentName,source,courseId,academicYear,fee</pre>
      <form
        onSubmit={async (e) => {
          e.preventDefault();
          const file = new FormData(e.currentTarget).get("file") as File;
          try {
            setId(
              (
                await api<{ id: string }>(u, "/imports", "POST", {
                  csv: await file.text(),
                })
              ).id,
            );
          } catch (x) {
            setE(String(x));
          }
        }}
      >
        <label>
          CSV file
          <input name="file" type="file" accept=".csv" required />
        </label>
        <button>Validate and import</button>
      </form>
      <ErrorBox error={e} />
      {id && (
        <>
          <Data title="Row results" path={"/imports/" + id} />
          <button
            onClick={async () => {
              const res = await fetch("/api/v1/imports/" + id + "/results", {
                headers: { Authorization: "Bearer " + u.token },
              });
              if (!res.ok) {
                setE("Download denied");
                return;
              }
              const url = URL.createObjectURL(await res.blob());
              const a = document.createElement("a");
              a.href = url;
              a.download = "import-results.csv";
              a.click();
              URL.revokeObjectURL(url);
            }}
          >
            Download results
          </button>
        </>
      )}
    </>
  );
}
function Knowledge() {
  const u = useContext(Auth)!;
  const status = useData<{ mode: string }>("/ai/status");
  const [session, setSession] = useState("");
  const [turn, setTurn] = useState("");
  const [answer, setAnswer] = useState<Row>();
  const [e, setE] = useState("");
  const [busy, setBusy] = useState(false);
  const [question, setQuestion] = useState(
    "Explain PG/NI notification processing",
  );
  const [source, setSource] = useState<unknown>();
  const abort = useRef<AbortController | null>(null);
  return (
    <>
      <p className="eyebrow">KNOWLEDGE ASSISTANT</p>
      <h1>Ask with evidence.</h1>
      <span className="badge">{status.data?.mode ?? "Loading"}</span>
      <p>
        Offline mode is simulated. Retrieved documents and live business facts
        are separate.
      </p>
      <form
        onSubmit={async (ev) => {
          ev.preventDefault();
          setBusy(true);
          setE("");
          setAnswer(undefined);
          setTurn("");
          abort.current = new AbortController();
          try {
            const id =
              session ||
              (await api<{ id: string }>(u, "/ai/sessions", "POST")).id;
            setSession(id);
            setAnswer(
              await streamAnswer(
                u,
                `/ai/sessions/${id}/messages/stream`,
                question,
                abort.current.signal,
                (name, value) => {
                  if (name === "start") setTurn(String((value as Row).turnId));
                  if (name === "delta")
                    setAnswer((old) => ({
                      ...old,
                      answer:
                        String(old?.answer ?? "") + String((value as Row).text),
                      citations: [],
                    }));
                },
              ),
            );
            client.invalidateQueries();
          } catch (x) {
            setE(String(x));
          } finally {
            setBusy(false);
          }
        }}
      >
        <label>
          Question
          <textarea
            aria-label="Question"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            maxLength={2000}
            required
          />
        </label>
        <div className="actions">
          <button disabled={busy}>Ask / retry</button>
          {busy && (
            <button
              type="button"
              onClick={async () => {
                if (turn)
                  await api(u, "/ai/turns/" + turn + "/cancel", "POST").catch(
                    () => {},
                  );
                abort.current?.abort();
              }}
            >
              Stop
            </button>
          )}
          <button
            type="button"
            onClick={() => {
              setSession("");
              setAnswer(undefined);
            }}
          >
            New session
          </button>
        </div>
      </form>
      <ErrorBox error={e} />
      {answer && (
        <section>
          <h2>{busy ? "Answer (partial)" : "Answer"}</h2>
          <p className="answer">{String(answer.answer)}</p>
          <div className="cards">
            {((answer.citations as Row[]) ?? []).map((c) => (
              <button
                className="citation"
                key={String(c.id)}
                onClick={async () => {
                  try {
                    setSource(await api(u, "/ai/citations/" + c.id));
                  } catch (x) {
                    setE(String(x));
                  }
                }}
              >
                {String(c.source)}
                <small>
                  {String(c.heading)} · lines {String(c.startLine)}–
                  {String(c.endLine)}
                </small>
              </button>
            ))}
          </div>
          <div className="actions">
            {Boolean(answer.id) &&
              [-1, 1].map((r) => (
                <button
                  key={r}
                  onClick={() =>
                    api(u, "/ai/feedback", "POST", {
                      messageId: answer.id,
                      rating: r,
                    }).catch((x) => setE(String(x)))
                  }
                >
                  {r === 1 ? "Helpful" : "Not helpful"}
                </button>
              ))}
          </div>
        </section>
      )}
      {source != null && (
        <section>
          <h2>Evidence / live result</h2>
          <pre>{JSON.stringify(source, null, 2)}</pre>
        </section>
      )}
      <section>
        <h2>Live application status</h2>
        <p>Reads the authorized business API without document retrieval.</p>
        <form
          onSubmit={async (e) => {
            e.preventDefault();
            const id = new FormData(e.currentTarget).get("id");
            try {
              setSource(
                await api(u, "/ai/tools/applications/" + id + "/summary"),
              );
            } catch (x) {
              setE(String(x));
            }
          }}
        >
          <Field name="id" label="Application ID" />
          <button>Read live status</button>
        </form>
      </section>
      <Data title="Your chat sessions" path="/ai/sessions" />
      {session && (
        <Data
          title="Current session history"
          path={`/ai/sessions/${session}/messages`}
        />
      )}
    </>
  );
}
function Sources() {
  const u = useContext(Auth)!;
  const [e, setE] = useState("");
  const cache = useQueryClient();
  return (
    <>
      <h1>Knowledge sources</h1>
      <p>
        Sanitized source files only (Markdown, Java, SQL, XML, YAML, properties,
        OpenAPI JSON). Reusing a filename atomically replaces its active
        version.
      </p>
      <form
        className="grid"
        onSubmit={async (e) => {
          e.preventDefault();
          const f = new FormData(e.currentTarget);
          const file = f.get("file") as File;
          try {
            await api(u, "/ai/sources", "POST", {
              name: file.name,
              text: await file.text(),
              provider: f.get("provider"),
              audience: f.get("audience"),
            });
            cache.invalidateQueries();
          } catch (x) {
            setE(String(x));
          }
        }}
      >
        <label>
          Source file
          <input
            type="file"
            name="file"
            accept=".md,.java,.sql,.xml,.yaml,.yml,.properties,.json"
            required
          />
        </label>
        <Field
          name="provider"
          label="Provider scope"
          options={["ALL", "A", "B"]}
        />
        <Field
          name="audience"
          label="Audience"
          options={["PUBLIC", "INTERNAL"]}
        />
        <button>Ingest source</button>
      </form>
      <ErrorBox error={e} />
      <Data title="Source versions" path="/ai/sources" />
      <Data title="Ingestion jobs" path="/ai/ingestion-jobs" />
      <form
        onSubmit={async (e) => {
          e.preventDefault();
          try {
            await api(
              u,
              "/ai/sources/" + new FormData(e.currentTarget).get("id"),
              "DELETE",
            );
            cache.invalidateQueries();
          } catch (x) {
            setE(String(x));
          }
        }}
      >
        <Field name="id" label="Source ID to withdraw" />
        <button>Withdraw source</button>
      </form>
    </>
  );
}
function Inventory() {
  const [page, setPage] = useState(0);
  const [q, setQ] = useState("");
  return (
    <>
      <h1>Documentary entity catalogue</h1>
      <p>
        Candidate inventory includes unknown tokens and documentary-only
        mappings. A row is not a claim of implementation.
      </p>
      <label>
        Filter source name
        <input
          value={q}
          onChange={(e) => {
            setQ(e.target.value);
            setPage(0);
          }}
        />
      </label>
      <Data
        title="Source mappings"
        path={`/ai/catalogue?page=${page}&q=${encodeURIComponent(q)}`}
      />
      <div className="actions">
        <button disabled={!page} onClick={() => setPage(page - 1)}>
          Previous
        </button>
        <span>Page {page + 1}</span>
        <button onClick={() => setPage(page + 1)}>Next</button>
      </div>
    </>
  );
}
function Architecture() {
  const data = useData<{ title: string; markdown: string }>("/ai/architecture");
  return (
    <>
      <h1>Architecture</h1>
      <ErrorBox error={data.error} />
      <pre>{data.data?.markdown ?? "Loading…"}</pre>
    </>
  );
}
export function App() {
  const [u, setU] = useState<User | null>(null);
  if (!u) return <Login done={setU} />;
  return (
    <Auth.Provider value={u}>
      <div className="shell">
        <aside>
          <div className="brand">
            <span className="mark">S</span>
            <div>
              SIS / LLE<small>Learning platform</small>
            </div>
          </div>
          <nav>
            {[
              ["/", "Applications"],
              ["/providers", "Providers"],
              ["/courses", "Courses"],
              ["/modules", "Modules"],
              ["/students", "Students"],
              ["/rules", "Demo rules"],
              ["/imports", "CSV submissions"],
              ["/reports", "Reports"],
              ["/operations", "Operations"],
              ["/knowledge", "Knowledge assistant"],
              ...(u.roles.some((r) =>
                ["ADMIN", "SUPPORT", "DEVELOPER"].includes(r),
              )
                ? [
                    ["/inventory", "Entity catalogue"],
                    ["/architecture", "Architecture"],
                  ]
                : []),
              ...(u.roles.includes("ADMIN")
                ? [["/sources", "Knowledge sources"]]
                : []),
            ]
              .filter(([p]) =>
                p === "/reports"
                  ? u.roles.some((r) => ["ADMIN", "REPORTER"].includes(r))
                  : p === "/operations"
                    ? u.roles.some((r) =>
                        ["ADMIN", "SUPPORT", "DEVELOPER"].includes(r),
                      )
                    : p === "/imports"
                      ? u.roles.some((r) => ["ADMIN", "ADVISOR"].includes(r))
                      : true,
              )
              .map(([p, l]) => (
                <NavLink key={p} to={p}>
                  {l}
                </NavLink>
              ))}
          </nav>
          <footer>
            <strong>{u.username}</strong>
            <small>Provider {u.provider}</small>
            <button
              onClick={() => {
                client.clear();
                setU(null);
              }}
            >
              Sign out
            </button>
          </footer>
        </aside>
        <main>
          <div className="topline">
            EDUCATIONAL DEMO <span>Fictitious data · synthetic policy</span>
          </div>
          <Routes>
            <Route path="/" element={<Applications />} />
            <Route path="/applications/:id" element={<Detail />} />
            <Route
              path="/providers"
              element={<Data title="Providers" path="/providers" />}
            />
            <Route
              path="/courses"
              element={<Data title="Courses & modules" path="/courses" />}
            />
            <Route
              path="/modules"
              element={<Data title="Modules" path="/modules" />}
            />
            <Route
              path="/students"
              element={
                <Data title="Masked fictitious students" path="/students" />
              }
            />
            <Route
              path="/rules"
              element={<Data title="Deterministic demo rules" path="/rules" />}
            />
            <Route path="/imports" element={<Imports />} />
            <Route
              path="/reports"
              element={<Data title="Financial summary" path="/reports" />}
            />
            <Route
              path="/operations"
              element={
                <Data title="Local publisher operations" path="/operations" />
              }
            />
            <Route path="/knowledge" element={<Knowledge />} />
            <Route path="/inventory" element={<Inventory />} />
            <Route path="/architecture" element={<Architecture />} />
            <Route path="/sources" element={<Sources />} />
          </Routes>
        </main>
      </div>
    </Auth.Provider>
  );
}
