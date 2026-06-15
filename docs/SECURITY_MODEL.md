# Security Model

The Death Note kills players, so its security posture matters. This document
describes the defences and the threats they address.

## 1. Item signing (never trust the display name)

A renamed vanilla book must never act as a Death Note. Authenticity is proven by
an **HMAC-SHA256 signature**, not by display name or lore text.

- The signed payload is canonical and reconstructable from the item:
  `DN1|<createdAtMillis>|<createdBy>`.
- The signature is computed with a server secret (`security.hmacSecret`).
- **Legacy:** the payload + signature are stored in a *hidden lore line* encoded
  as a run of colour codes (`§<hexdigit>`), which renders invisibly.
- **Modern:** they are stored in the item's `PersistentDataContainer` under the
  `deathnote` namespace (`item_type`, `signature`, `created_at`, `created_by`,
  `uses`).
- `isDeathNoteItem` requires **both** the correct base material **and** a
  signature that verifies against the secret. Copied/forged items fail.

### Secret management
- `security.hmacSecret` is read from `config.yml`.
- If blank, a 256-bit random secret is generated on first startup and saved.
- The secret never leaves the server. Treat `config.yml` as sensitive.

## 2. Command-injection prevention (name validation)

The single most important control. Before any kill, the written name must match
`^[A-Za-z0-9_]{3,16}$` and is additionally rejected for:

- selectors (`@p`, `@a`, `@r`, `@e`, `@s`, or any `@`),
- slashes (`/`, `\`), semicolons (`;`), quotes (`"`, `'`, `` ` ``),
- whitespace, and Unicode control/format characters.

Only the validated name is ever placed into a command. **Raw page text is never
executed.** Selectors are never used in kill commands.

## 3. Kill strategy (highest authority first)

`executeKill` tries strategies in a fixed priority order and stops at the first
success:

1. `minecraft:kill <name>` (namespaced, highest authority)
2. `kill <name>`
3. native platform damage
4. `setHealth(0)`

Each step uses the validated name, runs on the **main server thread**, and the
command path uses the console sender. Toggles in `config.yml` let admins disable
the native fallbacks.

## 4. Abuse prevention

- **Permissions:** `deathnote.use`, `deathnote.give`, `deathnote.admin`,
  `deathnote.bypass`.
- **Bypass / immunity:** holders of `deathnote.bypass` are immune; op-targets are
  immune when `security.blockOpTarget` is on.
- **Self-target:** blocked when `security.blockSelfTarget` is on.
- **Cooldown:** per-actor cooldown (`cooldown.seconds`) throttles spam.
- **Book sanitization:** on use, the written name is wiped from the book
  (`input.clearBookAfterUse`) so altered/malicious page data is not duplicated.
- **Online-only targets:** only currently online players can be targeted.
- **Audit log:** every attempt (success or not) is logged with timestamp, actor
  name + UUID, target, chosen strategy, result, platform and version.

## 5. Chat input

Chat input is **not** the primary flow. A debug-only chat fallback exists behind
`security.debugChatInput` and is **disabled by default**.

## Threat coverage summary

| Threat                                   | Control |
|------------------------------------------|---------|
| Renamed/forged book                      | HMAC signature + material check |
| Command injection via the name           | strict `NameValidator`, validated name only |
| Selector abuse (`@a`, ...)               | rejected by validator; never used in commands |
| Killing protected players                | bypass perm, op-target block |
| Self-grief / spam                        | self-target block, cooldown |
| Off-thread/native API misuse             | all execution on main thread |
| Silent misuse                            | mandatory audit log |
