# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## Project Overview

This repository is a scaffold for building admin management systems.

- **Frontend**: React 19 + TypeScript + Vite + Ant Design 6
- **Backend**: Java 21 + Spring Boot 4.0.6
- **Base package (backend)**: `io.github.lystrosaurus.admin`
- **Repo layout**: `frontend/`, `backend/`, `deploy/`, `docs/`

Core domains in this scaffold:
- Auth (login/logout/JWT/third-party login/account binding)
- System management (user/role/permission/menu/dict/config)
- Organization and employee master data
- External identity mapping and sync
- Audit logs and file storage

## Agent Behavior Principles

Derived from Andrej Karpathy’s observations on LLM coding pitfalls. These rules take precedence over speed when they conflict.

### 1. Think Before Coding

State assumptions explicitly. When uncertain, ask — don’t guess. Present alternatives when ambiguity exists. Push back if a simpler path is available.

**Any task must use `sequential-thinking` first** — structured thinking before implementation. Depth scales with complexity:
- Simple (typo, single-line): 1–2 steps
- Regular (new endpoint, bug fix): 3–5 steps
- Complex (cross-module refactor): 5–10 steps

### 2. Simplicity First

Minimum code that solves the problem. No speculative features, no abstraction for single-use code, no “flexibility” that wasn’t requested. If 200 lines could be 50, rewrite. Generalize only after a pattern repeats 3+ times.

### 3. Surgical Changes

Touch only what you must. Don’t “improve” adjacent code, comments, or formatting. Match existing style. Remove only imports / variables / functions that YOUR changes made unused. Mention pre-existing dead code — don’t delete it.

### 4. Goal-Driven Execution

Transform imperative tasks into verifiable goals:
- “Add validation” → “Write tests for invalid inputs, then make them pass”
- “Fix the bug” → “Write a reproducing test, then make it pass”
- “Refactor X” → “Ensure tests pass before and after”

For this project:
- State a brief plan for multi-step tasks
- Backend changes: ensure `mvn test` passes (when local dependencies are available)
- Frontend changes: ensure `npm run test` / relevant e2e passes
- If the same problem fails 3 times in a row, pause and reassess

## Common Commands

```bash
# Backend
cd backend
mvn test
mvn spotless:apply

# Frontend
cd frontend
npm install
npm run dev
npm run test
```

## Local Services

- **MySQL**: required for backend runtime and integration tests
- **Redis**: required for auth/session/token-version related flows and caching
- **Integration tests**: use `@ActiveProfiles("test")` with local MySQL/Redis in first version

## Architecture

Backend is a modular monolith.

```
io.github.lystrosaurus.admin
├── config          # Spring beans and framework configuration
├── common          # ApiResponse<T>, ErrorCode, BusinessException, constants
├── web             # Exception handling, request logging, validation helpers
├── infra           # persistence, redis, storage adapters
├── auth            # authentication and external account binding
├── system          # user / role / permission / menu / dict / config
├── organization    # employee / orgunit
├── integration     # external sources / principals / identity links
├── audit           # login and operation logs
├── file            # upload and file metadata
└── ops             # ops/diagnostic endpoints
```

Feature module internal structure:
- `controller`, `service`, `dao`, `dao.impl`, `mapper`, `entity`, `dto`, `vo`, `mapstruct`

### Layer rules

```
Controller -> Service -> DAO -> DAO Impl -> Mapper -> Database
```

- Controllers must NOT depend on DAOs/mappers/entities
- Services must NOT depend on MyBatis-Plus mappers directly
- Only `dao.impl` may depend on mappers
- Entities must NOT be used as API response contracts
- DTO/VO should prefer Java records; entities use classes

## Auth Model

- `/api/public/**` is unauthenticated
- `/api/open/**` uses `X-API-Token`
- `/api/app/**` uses Sa-Token JWT
- Login: `POST /api/auth/login`
- Logout: `POST /api/auth/logout`
- Current user profile: `GET /api/app/profile`

JWT carries minimal claims only; runtime roles/permissions should be fetched from Redis/MySQL as required by the design.

## API Response and Error Codes

Unified response:

```json
{
  "code": "0",
  "message": "success",
  "data": {}
}
```

Error code format:
- `CATEGORY_NUMBER`
- Examples: `COMMON_400`, `COMMON_401`, `AUTH_403`, `PERMISSION_403`

## Database and Migration

- Table/field names: lowercase + underscore
- Primary keys: `BIGINT` (no auto-increment convention here)
- Soft delete: `deleted`
- Audit columns: `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted`
- Migration dir: `backend/src/main/resources/db/migration/`
- Migration naming: `V{version}__{description}.sql`
- Never modify released migration scripts
- Local seed data must be marked `LOCAL-ONLY`

## Testing

| Type | Focus |
|------|-------|
| Unit | business rules and exception paths |
| MVC | controller request/response behavior |
| Arch | layer dependency enforcement |
| Integration | MySQL/Redis/Flyway/auth chain |

First-version integration tests should use local MySQL/Redis rather than mandating Testcontainers.

## Key Constraints

- No Spring Security web stack (only `spring-security-crypto` for BCrypt is allowed)
- No entities in API responses — use VOs
- No service bypass of DAO layer — services use DAOs, not mappers
- No logging of secrets, tokens, or third-party credentials
- Third-party login flows must validate `state`, `nonce`, and redirect whitelists

## Standards

Use the following project-specific docs once they exist:
- `docs/admin-management-scaffold-design.md` for domain and architecture decisions
- `CODING_STANDARDS.md` (when added) for detailed coding conventions
