
## AI-assisted edits: hard guardrails and acceptance checks

- Scope boundaries
  - Do NOT modify core architecture, navigation, or existing data structures.
  - All DeepSeek work MUST be confined to `app/src/main/java/com/example/wtsaskingforsignature/ai/DeepSeekInterpreter.kt` and `app/src/main/java/com/example/wtsaskingforsignature/modules/**`.
  - Forbidden: creating parallel app versions, moving files across modules, or replacing existing flows.

- Read-only architecture mode
  - Treat `core/`, `ui/screens/`, `WtsApp.kt`, `MainActivity.kt`, `assets/fortunes.json` as read-only.
  - Any change outside the allowed modules requires an explicit issue and approval.

- Task boundaries (how to ask for changes)
  - Provide precise inputs, outputs, target files, and function names.
  - Use concrete verbs (add, update, refactor-in-place) and avoid vague prompts like "optimize".

- Acceptance checklist (must pass before commit)
  - [ ] Only allowed files changed (DeepSeekInterpreter or modules/*)
  - [ ] No changes to data models consumed by the rest of the app
  - [ ] Uses real data sources (fortunes.json, ZiweiCalculator) with logs
  - [ ] Builds locally: `./gradlew assembleDebug` success
  - [ ] Run in app and capture logs showing module execution

- Branch and diff requirements
  - Make changes on `feature/deepseek-fortune-analysis` (or a child feature branch) only.
  - Attach `git diff --name-only` and `git status` to every PR to prove scope compliance.
