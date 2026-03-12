# Skillcraft

A monorepo of Minecraft Forge mods for **1.21.11 / Forge 61.x**.

## Mods

| Mod | Description |
|-----|-------------|
| [enchantment-shrine](enchantment-shrine/) | Generates enchantment shrines with max-power bookshelves and loot chests across the overworld |
| [skillcraft-core](skillcraft-core/) | Adds Mana Castles, Secret Shop merchants, the Mana Potion, and the Lightning Book |
| [special-forces](special-forces/) | Adds military weapons and gadgets: Sniper Rifle, Night Vision Goggles, and Bullets |

## Repo structure

```
skillcraft/
├── build.gradle              ← root build file
├── settings.gradle           ← multi-project Gradle setup
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── enchantment-shrine/       ← mod subproject
│   ├── build.gradle
│   └── src/
├── skillcraft-core/          ← mod subproject
│   ├── build.gradle
│   └── src/
└── special-forces/           ← mod subproject
    ├── build.gradle
    └── src/
```

The repo is a **Gradle multi-project build**. You can build or run any mod from the root:

```bash
# Build all mods
./gradlew build

# Build a specific mod
./gradlew :enchantment-shrine:build
./gradlew :skillcraft-core:build
./gradlew :special-forces:build

# Launch dev client
./gradlew :skillcraft-core:runClient
./gradlew :enchantment-shrine:runClient
./gradlew :special-forces:runClient
```

Or build independently from each subproject's directory using its own `gradlew`.

---

## enchantment-shrine

Generates open stone-brick shrines containing an enchanting table surrounded by 15 bookshelves (max level 30), plus a chest stocked with XP bottles, lapis, and books. Spawns in all overworld biomes at ~1 per 32 chunks.

See [enchantment-shrine/README.md](enchantment-shrine/README.md) for full details.

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

## special-forces

### Features

#### Sniper Rifle
- Right-click to cycle zoom: **Normal → x2 → x4**
- Scoped view draws a black vignette with a circular cutout and crosshair overlay
- FOV adjusts with zoom level
- Left-click fires a **500-block raycast** shot
  - No-scope spread: ~20% miss chance
  - Scoped spread: ~0.5% miss chance
- Deals **20 damage** on hit
- **40-tick cooldown** after each shot; zoom resets to normal during cooldown and restores to the pre-shot level when cooldown ends
- Requires **Bullets** in inventory; consumes one per shot

#### Bullet
- Stackable to 64
- Crafted shapeless: **1 copper ingot + 1 gunpowder → 4 bullets**

#### Night Vision Goggles
- Equippable in the **head slot**
- Continuously applies **Night Vision** while worn
- Draws a green-tint vignette overlay on the HUD

#### Debug Chest
A chest is placed at **(8, surface, 8)** near spawn when a new world is created, stocked with one Sniper Rifle, 16 Bullets, and one pair of Night Vision Goggles.

---

## Setup notes (applies to all mods)

- **Java:** JDK 21 required (`org.gradle.java.home` set in each `gradle.properties`)
- **Gradle:** 8.8 via wrapper — do not use system Gradle 9.x (ForgeGradle 6 incompatible)
- **reobfJar:** disabled in all mods — Forge 61.x ships official Mojang names at runtime, SRG re-obfuscation causes `NoSuchFieldError`
- **IPv4:** `Djava.net.preferIPv4Stack=true` set to avoid Forge Maven 520 errors from broken IPv6
