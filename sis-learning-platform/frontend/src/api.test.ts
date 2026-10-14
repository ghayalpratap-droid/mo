import { it, expect } from "vitest";
import { normalize } from "./api";
it("normalizes both database profiles without changing Java DTOs", () => {
  expect(
    normalize({
      SOURCE_REF: "R",
      academicYear: 2026,
      child: [{ created_at: "now" }],
    }),
  ).toEqual({
    sourceRef: "R",
    academicYear: 2026,
    child: [{ createdAt: "now" }],
  });
});
