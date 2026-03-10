# Skillcraft

A monorepo of Minecraft Forge mods for **1.21.11 / Forge 61.x**.

## Mods

| Mod | Description |
|-----|-------------|
| [enchantment-shrine](enchantment-shrine/) | Generates enchantment shrines with max-power bookshelves and loot chests across the overworld |
| [skillcraft-core](skillcraft-core/) | Adds Mana Castles, Secret Shop merchants, the Mana Potion, and the Lightning Book |

## Repo structure

```
skillcraft/
├── enchantment-shrine/     ← individual mod projects
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   └── src/
└── skillcraft-core/
    ├── build.gradle
    ├── settings.gradle
    ├── gradlew
    └── src/
```

Each mod is a self-contained Gradle project. Build any mod independently:

```bash
cd skillcraft-core
./gradlew build        # produces build/libs/skillcraft-1.0.0.jar
./gradlew runClient    # launch dev client
```

---

## skillcraft-core

### Features

#### Mana Castle
A chess-rook-shaped tower (~15 blocks tall) built from deepslate bricks. Spawns across all biomes at rarity 1200.

- Interior ladder, torches, and a loot chest at the top
- Chest contains: **1 Mana Potion** + **63 gold ingots**

#### Secret Shop
A market tent with a stationary NPC merchant. Spawns across all biomes at rarity 800.

- The merchant never moves or despawns
- Sells one trade: **63 gold ingots → 1 Lightning Book**

#### Mana Potion
- Drink to gain a persistent mana bar (100 max mana)
- Re-drinking refills the bar to full

#### Lightning Book
- Right-click to strike the nearest entity within 20 blocks with lightning
- Costs 30 mana per use; requires a mana bar (obtained from a Mana Potion)

#### Mana Bar
- Persists across death, respawn, and dimension changes
- Displayed in the bottom-left corner of the HUD when active

#### Debug mode
Set `debugSpawnNearby = true` in `run/config/skillcraft-common.toml` to force both structures to generate near world origin on every new chunk — useful for rapid in-dev testing.

---

## Setup notes (applies to all mods)

- **Java:** JDK 21 required (`org.gradle.java.home` set in each `gradle.properties`)
- **Gradle:** 8.8 via wrapper — do not use system Gradle 9.x (ForgeGradle 6 incompatible)
- **reobfJar:** disabled in all mods — Forge 61.x ships official Mojang names at runtime, SRG re-obfuscation causes `NoSuchFieldError`
- **IPv4:** `Djava.net.preferIPv4Stack=true` set to avoid Forge Maven 520 errors from broken IPv6
