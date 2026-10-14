ALTER TABLE chat_messages ADD COLUMN message_order bigserial;
GRANT USAGE,SELECT ON SEQUENCE knowledge.chat_messages_message_order_seq TO sis_knowledge;
CREATE INDEX chat_message_order ON chat_messages(session_id,message_order);
