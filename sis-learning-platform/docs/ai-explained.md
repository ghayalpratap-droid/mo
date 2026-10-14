# AI explained through this project

| Concept | Meaning | Purpose / component | Input | Output | Example |
|---|---|---|---|---|---|
| LLM | Generates language from context | ChatModel answer generation | Question and evidence | Explanation | Explain a documented CoC rule |
| OpenAI | Model API provider | Backend adapter only | Masked text | Answer or embeddings | Configured chat and embedding model IDs |
| Spring AI | Java integration framework | ChatClient, ChatModel, EmbeddingModel, ChatMemory, @Tool | Typed Java request | Model/retrieval/tool interactions | Register bounded read-only BusinessTools |
| Embedding | Numeric representation | Semantic candidate matching | Source passage | 1536 floats | Match PGNI process terminology |
| Vector database | Vector storage and distance search | PostgreSQL vector extension | Query vector | Neighbor passages | Cosine distance search with scope filters |
| RAG | Evidence retrieval before generation | KnowledgeStore + ChatTurns | Question | Cited answer or abstention | Explain PG/NI notification processing |
| Tool calling | Bounded live API execution | BusinessTools | UUID / fixed read operation | Authorized current fact | Latest attendance status |
| Chat memory | Bounded conversation context | OwnedChatMemory | Prior authorized turns | At most 20 messages / 8000 characters | Follow-up context; never identity |
| Relational database | Authoritative transactional storage | Domain schema | Validated command | Records and immutable history | Registration and instalments |

For **Explain PG/NI notification processing**, the supplied document describes notification validation, configured rule order, persistence, LAP publication using `XXAQ_SIS_COC_JMS_TXT_Q`, and Reg/ATN publication through sis-publisher (Ne.md:235–260). Retrieval supplies passages; the LLM explains them with identifiers and line references. These are documentary claims.

For **Latest attendance status UUID**, a bounded read-only tool calls the authenticated domain summary endpoint, propagating the user's validated bearer identity. The domain checks provider ownership. A successful live fact can answer without retrieved text and carries its source and as-of timestamp.

For **Why was this CoC rejected?**, the CoC history tool supplies the actual synthetic decision and the retrieved rule supplies explanatory context. A configured LLM can combine them, but cannot change the decision. Offline mode currently displays retrieved excerpts and supports explicit live status routing; it does not simulate real reasoning quality or autonomous tool choice.

Missing/unauthorized sources cause abstention. Unsupported citations cause an error. Valid citation IDs establish that a passage was retrieved, not that every sentence is logically supported; human groundedness review is still required. Indexing is not model training. Hashed offline embeddings are a deterministic test adapter, not OpenAI embeddings or evidence of semantic quality.
