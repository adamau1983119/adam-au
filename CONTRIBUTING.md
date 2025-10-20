
## AI-assisted edits: hard guardrails and acceptance checks

- Scope boundaries
  - Do NOT modify core architecture, navigation, or existing data structures.
  - Core architecture is defined as: `core/**`, `ui/screens/**`, `ui/chatnew/**`, `MainActivity.kt`, `WtsApp.kt`, `data/ServiceLocator.kt`, `assets/fortunes.json`, navigation graphs, and any files under `ziwei/**` except usage via public APIs.
  - All DeepSeek work MUST be confined to `app/src/main/java/com/example/wtsaskingforsignature/ai/DeepSeekInterpreter.kt` and `app/src/main/java/com/example/wtsaskingforsignature/modules/**`.
  - No new files may be created outside these allowed paths.
  - Forbidden: creating parallel app versions, moving files across modules, or replacing existing flows (e.g., replacing `MainActivity`, altering navigation destinations, overriding existing ViewModels, or swapping `LocalAIRepository` behavior outside approved interfaces).

- Read-only architecture mode
  - Treat `core/`, `ui/screens/`, `ui/chatnew/`, `WtsApp.kt`, `MainActivity.kt`, `data/ServiceLocator.kt`, `assets/fortunes.json` as read-only.
  - Any change outside the allowed modules requires an explicit issue and approval.

- Task boundaries (how to ask for changes)
  - Provide precise inputs, outputs, target files, and function names.
  - Use concrete verbs (add, update, refactor-in-place) and avoid vague prompts like "optimize".

- Acceptance checklist (must pass before commit)
  - [ ] Only allowed files changed (DeepSeekInterpreter or modules/*); attach `git diff --name-only`
  - [ ] No changes to app-wide data models: `EnhancedModels.kt` types (e.g., `ZiweiAnalysis`, `QuestionAnalysis`, `UserProfile`, `MonthlyAnalysis`) remain backward compatible
  - [ ] Uses real data sources (fortunes.json, ZiweiCalculator) with logs; include log tag `DeepSeekExecution`
  - [ ] Builds locally: `./gradlew assembleDebug` success; no new warnings/errors introduced
  - [ ] Run in app and capture logs showing module execution; attach screenshots or log excerpts in PR

- Branch and diff requirements
  - Make changes on `feature/deepseek-fortune-analysis` (or a child feature branch) only.
  - Attach `git diff --name-only` and `git status` to every PR to prove scope compliance.
