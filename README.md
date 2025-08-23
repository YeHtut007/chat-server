# chat-server

Spring Boot WebSocket chat backend (STOMP + JWT).
- Dev profile: H2 in-memory
- Prod profile: PostgreSQL via env vars
- WS endpoints: `/ws` (SockJS), `/ws-native` (native)
