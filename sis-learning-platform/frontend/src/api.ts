export type User = {
  token: string;
  username: string;
  provider: string;
  roles: string[];
};
export type Row = Record<string, unknown>;
export type Application = {
  id: string;
  provider: string;
  sourceRef: string;
  studentName: string;
  source: string;
  courseId: string;
  academicYear: number;
  fee: number;
  currency: string;
  status: string;
  registration: string;
  attendance: string;
  version: number;
};
export function normalize(v: unknown): unknown {
  if (Array.isArray(v)) return v.map(normalize);
  if (v && typeof v === "object")
    return Object.fromEntries(
      Object.entries(v).map(([k, x]) => [
        (k === k.toUpperCase() ? k.toLowerCase() : k).replace(
          /_([a-z])/g,
          (_, c: string) => c.toUpperCase(),
        ),
        normalize(x),
      ]),
    );
  return v;
}
export async function api<T>(
  u: User | null,
  path: string,
  method = "GET",
  body?: unknown,
  signal?: AbortSignal,
): Promise<T> {
  const r = await fetch("/api/v1" + path, {
    method,
    signal,
    headers: {
      "Content-Type": "application/json",
      ...(u ? { Authorization: "Bearer " + u.token } : {}),
      "Idempotency-Key": crypto.randomUUID(),
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  if (!r.ok) {
    const e = await r.json().catch(() => ({ message: r.statusText }));
    throw Error(
      `${r.status}: ${e.message || "Request denied"}${e.correlationId ? " · " + e.correlationId : ""}`,
    );
  }
  const text = await r.text();
  return text ? (normalize(JSON.parse(text)) as T) : (undefined as T);
}
export async function streamAnswer(
  u: User,
  path: string,
  question: string,
  signal: AbortSignal,
  onEvent: (event: string, value: unknown) => void,
): Promise<Row> {
  const response = await fetch("/api/v1" + path, {
    method: "POST",
    signal,
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + u.token,
    },
    body: JSON.stringify({ question }),
  });
  if (!response.ok) throw Error(`Chat request failed (${response.status})`);
  if (!response.body) throw Error("Streaming unavailable");
  const reader = response.body.getReader(),
    decoder = new TextDecoder();
  let pending = "",
    complete: Row | undefined;
  try {
    while (true) {
      const { value, done } = await reader.read();
      pending += decoder
        .decode(value, { stream: !done })
        .replace(/\r\n/g, "\n");
      let split;
      while ((split = pending.indexOf("\n\n")) >= 0) {
        const block = pending.slice(0, split);
        pending = pending.slice(split + 2);
        let name = "message";
        const data: string[] = [];
        for (const line of block.split("\n")) {
          if (line.startsWith("event:")) name = line.slice(6).trim();
          if (line.startsWith("data:")) data.push(line.slice(5).trimStart());
        }
        if (data.length) {
          const raw = data.join("\n");
          let payload: unknown;
          try {
            payload = JSON.parse(raw);
          } catch {
            payload = raw;
          }
          if (name === "failure") throw Error(String((payload as Row).message));
          onEvent(name, payload);
          if (name === "complete") complete = payload as Row;
        }
      }
      if (done) break;
    }
    if (!complete)
      throw Error(
        "Stream ended before completion; partial output was not confirmed",
      );
    return complete;
  } finally {
    reader.releaseLock();
  }
}
