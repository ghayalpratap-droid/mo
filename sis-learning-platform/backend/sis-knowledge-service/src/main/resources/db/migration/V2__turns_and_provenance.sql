CREATE TABLE message_sources(message_id uuid NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,chunk_id uuid NOT NULL REFERENCES document_chunks(id),PRIMARY KEY(message_id,chunk_id));
CREATE TABLE chat_turns(id uuid PRIMARY KEY,session_id uuid NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,status varchar(20) NOT NULL,created_at timestamptz NOT NULL DEFAULT now());
CREATE UNIQUE INDEX one_active_turn ON chat_turns(session_id) WHERE status='GENERATING';
GRANT SELECT,INSERT,UPDATE,DELETE ON knowledge.message_sources,knowledge.chat_turns TO sis_knowledge;
