package edu.sis.knowledge;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.*;
import java.util.*;

public final class TokenChunks {
  static final Encoding ENCODING =
      Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);

  public static int count(String s) {
    return ENCODING.countTokensOrdinary(s);
  }

  public static List<KnowledgeStore.Chunk> split(String text, int maximum, int overlap) {
    if (maximum < 100 || maximum > 2000 || overlap < 0 || overlap >= maximum / 2)
      throw new IllegalArgumentException("Invalid chunk budget");
    List<KnowledgeStore.Chunk> out = new ArrayList<>();
    String[] lines = text.split("\n", -1);
    String heading = "Document";
    StringBuilder part = new StringBuilder();
    int start = 1;
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i] + "\n";
      boolean boundary = lines[i].startsWith("#");
      if ((boundary || count(part + line) > maximum) && !part.isEmpty()) {
        String old = part.toString();
        out.add(new KnowledgeStore.Chunk(heading, start, i, old));
        part.setLength(0);
        start = i + 1;
        if (!boundary && overlap > 0) {
          String[] tail = old.split("\n");
          String kept = "";
          int keptLines = 0;
          for (int t = tail.length - 1; t >= 0; t--) {
            String candidate = tail[t] + "\n" + kept;
            if (count(candidate) > overlap) break;
            kept = candidate;
            keptLines++;
          }
          part.append(kept);
          start = i + 1 - keptLines;
        }
      }
      if (boundary)
        heading =
            lines[i]
                .replaceFirst("^#+\\s*", "")
                .substring(0, Math.min(450, lines[i].replaceFirst("^#+\\s*", "").length()));
      if (count(line) > maximum) {
        if (!part.isEmpty()) {
          out.add(new KnowledgeStore.Chunk(heading, start, i, part.toString()));
          part.setLength(0);
        }
        while (count(line) > maximum) {
          int low = 1, high = line.codePointCount(0, line.length());
          while (low < high) {
            int mid = (low + high + 1) / 2;
            int end = line.offsetByCodePoints(0, mid);
            if (count(line.substring(0, end)) <= maximum) low = mid;
            else high = mid - 1;
          }
          int end = line.offsetByCodePoints(0, low);
          out.add(new KnowledgeStore.Chunk(heading, i + 1, i + 1, line.substring(0, end)));
          line = line.substring(end);
        }
        start = i + 1;
      }
      part.append(line);
    }
    if (!part.isEmpty())
      out.add(new KnowledgeStore.Chunk(heading, start, lines.length, part.toString()));
    return out;
  }
}
