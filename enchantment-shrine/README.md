# Enchantment Shrine

A Minecraft Forge mod for **1.21.11** that generates enchantment shrine structures across the overworld.

## What it does

When you load a new world, the mod procedurally generates open stone-brick shrines containing:

- **Enchanting table** at the center with **15 bookshelves** arranged around it — guaranteeing max enchanting level (30)
- **Chest** stocked with:
  - 160× Bottles o' Enchanting (XP bottles)
  - 192× Lapis Lazuli
  - 160× Books
- **Corner pillars** topped with lanterns for lighting
- **Stone brick floor** with cobblestone fill-down to prevent floating

Shrines spawn in all overworld biomes at roughly village-level rarity (~1 per 32 chunks).

## Structure layout

```
  P · · · P
  · B · B ·     P = Stone brick pillar (4 tall) + lantern
  · B E B ·     B = Bookshelf (15 total, max enchanting power)
  · B · B ·     E = Enchanting table
  P · C · P     C = Chest (faces north)
```

## Requirements

- Minecraft **1.21.11**
- Forge **61.0.10**
- Java **21**

## Building from source

```bash
# Requires JDK 21 and Gradle 8.8
./gradlew build
```

Output jar: `build/libs/enchantmentshrine-1.0.0.jar`

## Installation

1. Install [Forge 1.21.11](https://files.minecraftforge.net) on your client
2. Copy the jar to `.minecraft/mods/`
3. Launch with the Forge 1.21.11 profile
4. Create a **new world** — shrines only generate in new chunks

## Customization

Edit `src/main/resources/data/enchantmentshrine/worldgen/placed_feature/enchantment_shrine.json`:

```json
{
  "type": "minecraft:rarity_filter",
  "chance": 32
}
```

- Lower `chance` → more common
- Higher `chance` → rarer (villages are ~32)

## Known notes

- Forge 61.x ships with official Mojang field names at runtime — `reobfJar` is disabled intentionally to prevent SRG name mismatches
- `/locate structure` does not work for features; just explore new chunks to find shrines
