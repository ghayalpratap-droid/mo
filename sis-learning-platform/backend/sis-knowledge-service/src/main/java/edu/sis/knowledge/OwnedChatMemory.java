package edu.sis.knowledge;

import edu.sis.shared.Identity;
import java.util.*;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;

/** One persistence design: chat_messages. No second Spring AI memory table. */
public final class OwnedChatMemory implements ChatMemory {
  private final KnowledgeStore store;
  private final Identity who;

  public OwnedChatMemory(KnowledgeStore store, Identity who) {
    this.store = store;
    this.who = who;
  }

  @Override
  public List<Message> get(String conversationId) {
    UUID id = UUID.fromString(conversationId);
    store.own(who, id);
    var rows =
        store.db.queryForList(
            "select id,role,content_masked from chat_messages where session_id=? and"
                + " status='COMPLETED' order by message_order desc limit 20",
            id);
    Collections.reverse(rows);
    List<Message> result = new ArrayList<>();
    int used = 0;
    for (var row : rows) {
      if (!store.visibleMessage(who, (UUID) row.get("id"))) continue;
      String content = row.get("content_masked").toString();
      if (used + content.length() > 8000) continue;
      used += content.length();
      result.add(
          row.get("role").equals("USER")
              ? new UserMessage(content)
              : new AssistantMessage(content));
    }
    return result;
  }

  @Override
  public void add(String conversationId, List<Message> messages) {
    UUID id = UUID.fromString(conversationId);
    store.own(who, id);
    if (messages.stream().anyMatch(m -> m.getMessageType() != MessageType.USER))
      throw KnowledgeStore.fail(400, "Assistant memory requires citation provenance");
    store.tx.executeWithoutResult(
        t -> {
          for (Message m : messages)
            store.db.update(
                "insert into chat_messages(id,session_id,role,content_masked,status)"
                    + " values(?,?,?,?,?)",
                UUID.randomUUID(),
                id,
                "USER",
                ModelGateway.scrub(m.getText()),
                "COMPLETED");
        });
  }

  @Override
  public void clear(String conversationId) {
    UUID id = UUID.fromString(conversationId);
    store.own(who, id);
    store.db.update("delete from chat_messages where session_id=?", id);
  }
}
