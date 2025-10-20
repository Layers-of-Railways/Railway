<!-- Copilot instructions for the Railway (Steam 'n' Rails) codebase -->
# Quick guide for AI coding agents

Keep guidance concise and only suggest changes that are consistent with the project's structure and Gradle properties. This repository has been migrated to a single-loader NeoForge setup for Minecraft 1.21.x. The canonical game logic and most source remain under `common/`, while the runtime is NeoForge-only under `forge/`.

Repository goal (important): The active goal for this repository is to port and maintain the mod on Minecraft 1.21.1+ using NeoForge (single-loader). Do not propose Fabric or multi-loader changes unless explicitly asked — focus on NeoForge + `common` changes required for the 1.21.* line.

High-level architecture
- `common/` – shared game logic, registries, data generators and most source code. This is the canonical implementation of features (registries, block/item definitions, core mechanics).
- `forge/` – NeoForge platform glue: NeoForge entrypoints, NeoGradle run configs, mixin configs, and platform-specific implementations.
- `development_assets/` – tools, scripts, models and translation helpers used by developers.

Why the change: The Create mod and some dependencies for 1.21.x require NeoForge. The project simplified from a multi-loader setup to a NeoForge-only layout to avoid mapping and compatibility conflicts.

 - Build / common developer workflows
 - Build with Gradle wrapper from repo root. Prefer the wrapper: `./gradlew` (Windows: `gradlew.bat`).
 - Common tasks:
   - `gradlew build` — full build (from repository root).
   - `gradlew :forge:runClient` — run NeoForge dev environment.
   - `gradlew :forge:runServer` — run NeoForge server dev environment.
   - `gradlew :forge:datagen` — run data generators (set DATAGEN=TRUE when required). Prefer Forge datagen or `common` data hooks.

IMPORTANT (porting): When working on the 1.21.* port, prefer these commands and focus on Forge runs. Do not assume Fabric is available in the port environment. Always verify builds on the Forge side and the `:common` module.

If you do need to run datagen, prefer the Forge datagen or the `common` hooks where available. If using the Fabric datagen run for reference, clearly document why it's required and mark it as temporary.

 - Datagen notes (important)
 - Datagen writes generated resources into `common/src/generated/resources`. `common` sourceSets include that folder.
 - Always set DATAGEN=TRUE when running datagen locally (this repo checks this in code). Prefer the Forge datagen run or `common` generator hooks.

Emphasize build & error checks
- The port's priority is a clean Forge + common build on the 1.21.* line. Before suggesting changes or opening PRs, run the following checks and include results in PR descriptions:
  - `gradlew :common:build` and `gradlew :forge:build` (or `gradlew build` for a full run) to verify compilation and resource generation.
  - Use Gradle's `--scan` or `--stacktrace` flags when failures occur to capture detailed errors.
  - Run the repo error check tool (IDE problems view or `./gradlew :common:compileJava` / `:forge:compileJava`) and include the error output if proposing changes that affect build or runtime.

If a change causes a build or mapping error, stop and fix that error before proceeding. Suggest small, incremental changes that keep the build green; do not introduce broad refactors across platform-specific folders without ensuring the build remains successful.
- If generated resources become stale or datagen fails, removing `src/generated/resources/data` and re-running `runData` can help. If you do this, make sure to re-add `src/generated/resources/data` to git if necessary (see mixin README note).

Project-specific conventions and patterns
- Property access: Gradle scripts use a helper operator function operator fun String.invoke() to read values from `rootProject.ext`/`gradle.properties`. When suggesting changes to versions or build properties reference keys from `gradle.properties` (e.g. `neoforge_version`, `minecraft_version`).
 - Registrate & data-gen: The project uses Registrate heavily (see `common/registry/CRBlocks.java` and `CRItems.java`) and adds data generators in `Railways.gatherData`. When changing registration or data formats, update data generators in `common` and platform-specific generator adapters in `forge`.
 - Mixin/access-wideners: `common` contains `railways.accesswidener` (referenced by `common/build.gradle.kts`). `forge` configures access widener paths to reuse this file. Avoid duplicate AW edits; edit the single canonical file in `common/src/main/resources`.
- Conditional features: Many features depend on Gradle properties (eg. `enable_hexcasting`, `enable_byg`, `enable_simple_voice_chat`). Use the property keys in suggestions rather than hard-coding dependency changes.

Important files to reference when making code changes
- `common/src/main/java/com/railwayteam/railways/Railways.java` — core mod class and data gathering hooks.
- `common/src/main/java/com/railwayteam/railways/registry/CRBlocks.java` and `CRItems.java` — canonical registration and examples of Registrate usage and data-gen hooks.
 - `forge/build.gradle.kts` — platform run tasks and VM args (datagen, access widener wiring).
- `gradle.properties` — central place for versions and feature flags. Suggest changes here when bumping versions or toggling optional compat.
- `common/src/main/resources/railways.accesswidener` — single AW source for all platforms.

Integration points and external dependencies
 - Create mod (com.simibubi.create) is a required dependency; code assumes Create APIs and Registrate patterns. Be careful when importing Create classes into `common` — platform differences exist. Use platform adapters in `forge` where necessary.
- Registrate, Flywheel, and Ponder are used; Flywheel and Registrate usage must be cautious due to cross-platform differences.
- Several optional runtime compat deps are gated by Gradle properties (BYG, HexCasting, Sodium/Rubidium, JourneyMap). When proposing code that interacts with optional mods, check the property's default in `gradle.properties` and prefer guard checks (ModList/Mods) or compile-time conditional hooks.

Testing & debugging
 - Use the `:forge:runClient` task to test runtime behaviour in the NeoForge dev environment.
 - The project includes `run/options.txt` etc. The `run/` directory under `forge` may hold local run-state useful during debugging.
- Profiling: the team uses YourKit (README mentions it) for CPU/memory profiling — avoid adding heavy instrumentation unless asked.

What to avoid recommending
- Don’t suggest moving platform-agnostic code from `common` into platform modules — the project intentionally centralises logic.
- Don’t change or duplicate the access widener across platforms; edit `common/src/main/resources/railways.accesswidener` only.

If you modify registration, datagen, or resource generation
 - Update the corresponding data generator (in `common`), and ensure platform adapters (under `forge`) either forward or implement platform-specific provider code.
- Re-run datagen and include generated resources in `common/src/generated/resources`.

Examples to copy from the repo (when creating similar code)
- Use `CRBlocks.makeTrack(...)` patterns for creating block entries with Registrate + data-gen lambdas.
 - Use the `forge` datagen run config (in `forge/build.gradle.kts`) as canonical example for setting VM args and environment variable wiring when adding new runs.

When in doubt, prefer the `common/` implementation and update platform adapters only when the change touches platform APIs or requires platform-specific registries.

 If you need more context or a follow-up change (tests, build tweaks, or enabling a compat flag), ask which side (runtime or data-gen) and whether the change affects `common` or `forge`.

---
If anything above is unclear or you want more examples (specific files or a small PR to illustrate patterns), tell me which area to expand (datagen, registrate, access-wideners, or build properties).
