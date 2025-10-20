# Pull Request Template

## Change Summary
- What changed and why (plain language):

## Scope Confirmation
- [ ] Changes are limited to DeepSeek modules
- [ ] No core architecture/navigation/data-structure changes

## Files Changed (auto-attach output)
```
# paste output of:
# git diff --name-only
```

## Acceptance Checklist
- [ ] Only allowed files changed (DeepSeekInterpreter or modules/*)
- [ ] No changes to app-wide data models (`ZiweiAnalysis`, `QuestionAnalysis`, `UserProfile`, `MonthlyAnalysis`)
- [ ] Uses real data sources (fortunes.json, ZiweiCalculator) with logs (tag: `DeepSeekExecution`)
- [ ] Builds locally: `./gradlew assembleDebug` success; no new warnings/errors
- [ ] Run in app and attached logs/screenshots proving module execution

## Validation Artifacts
- Screenshots / log excerpts:

## Notes for Reviewers
- Risks, limitations, follow-ups:
