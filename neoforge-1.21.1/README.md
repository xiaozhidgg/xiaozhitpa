# Xiaozhi TPA (NeoForge 1.21.1)

A server-side mod providing `/tpa`, `/home` and `/back` commands, ported and adapted from the
Forge 1.20.1 mod `xsytpa-1.3`. Built for **NeoForge `21.1.233`** on **Minecraft `1.21.1`**.

## Commands (all players, no OP required)

| Command | Description |
|---|---|
| `/tpa <player>` | Send a teleport request to a player. If the target has auto-accept on, teleport immediately. |
| `/tpaccept [player|all]` | Accept a pending request. Bare `/tpaccept` accepts the pending request. |
| `/tpdeny [player]` | Decline a pending request. |
| `/tpauto` | Toggle auto-accept of incoming requests. |
| `/sethome <name>` | Save a named home at your current position/dimension. |
| `/home [name]` | Teleport to a home (bare `/home` uses the default `home`). Suggests your names. |
| `/delhome <name>` | Delete a named home. |
| `/listhome` | List homes in your current dimension. |
| `/back` | Return to your last saved position (recorded on death and before a TPA/home teleport). |

Behavior notes (mirroring the original):
- TPA requests expire after **60 seconds**.
- A player may have **multiple named homes**, stored **per dimension**.
- Back position is recorded **on death** and **before** the player is teleported by `/home` or an accepted `/tpa`.
- Data persists in the world's saved data (overworld storage), key `xiaozhi_tpa_player_data`.

## Build

> ⚠️ **Important:** this sandbox/workspace has **no outbound network**, so `gradlew build`
> **cannot be run here** (it must download Gradle, NeoForge, Minecraft and mappings).
> Run the build on your own machine that has internet access.

Steps on your machine:

1. Install a JDK **21** and set `JAVA_HOME`.
2. Ensure you have Gradle **8.10+** (any recent Gradle works). The wrapper script is included
   but the binary `gradle/wrapper/gradle-wrapper.jar` is not (it is a standard binary you must
   obtain). Either:
   - Run `gradle wrapper` once to generate the jar, then use `gradlew.bat build`, **or**
   - Just run `gradle build` with your installed Gradle.
3. From the project root run:

   ```bat
   gradlew.bat build
   ```

   (or `gradle build`).

4. The mod jar is produced at `build/libs/xiaozhi_tpa-1.0.0-neoforge.jar`. Drop it into your server's `mods/`
   folder (NeoForge 1.21.1 server).

Optional — rebuild the wrapper if you have Gradle:
```bat
gradle wrapper --gradle-version 8.10
```

## Notes / porting specifics

- Uses **official Mojang mappings** (NeoForge default); no Parchment layer, to avoid a
  Parchment-version mismatch. Method names such as `getLevel()`, `getDataStorage()`,
  `teleportTo(...)` are the 1.21.1 Mojang names.
- `@EventBusSubscriber(..., bus = Bus.GAME)` wires `RegisterCommandsEvent` and
  `LivingDeathEvent` on the game bus.
- The original's odd "bare `/tpaccept` toggles auto-accept" behavior was replaced with a
  dedicated `/tpauto` toggle per your preference.
