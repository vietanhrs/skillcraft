# Skillcraft

A monorepo of Minecraft Forge mods for **1.21.11 / Forge 61.x**.

## Mods

| Mod | Description |
|-----|-------------|
| [enchantment-shrine](enchantment-shrine/) | Generates enchantment shrines with max-power bookshelves and loot chests across the overworld |

## Repo structure

```
skillcraft/
└── enchantment-shrine/     ← individual mod projects
    ├── build.gradle
    ├── gradle.properties
    ├── settings.gradle
    ├── gradlew
    └── src/
```

Each mod is a self-contained Gradle project. Build any mod independently:

```bash
cd enchantment-shrine
./gradlew build
```

## Setup notes (applies to all mods)

- **Java:** JDK 21 required (`org.gradle.java.home` set in each `gradle.properties`)
- **Gradle:** 8.8 via wrapper — do not use system Gradle 9.x (ForgeGradle 6 incompatible)
- **reobfJar:** disabled in all mods — Forge 61.x ships official Mojang names at runtime, SRG re-obfuscation causes `NoSuchFieldError`
- **IPv4:** `Djava.net.preferIPv4Stack=true` set to avoid Forge Maven 520 errors from broken IPv6
