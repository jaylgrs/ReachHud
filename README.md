# ReachHUD

**A real-time reach indicator for Minecraft.**

ReachHUD is a client-side Fabric mod designed to provide useful combat information through an in-game HUD. It helps players monitor target distance and reach information during gameplay.

> **BETA NOTICE:** ReachHUD for Minecraft 26.3 is currently in beta. Some features may be unstable, incomplete, or subject to change. Bugs and unexpected behavior are possible.

## Features

- **Real-time Reach Display** – Displays the distance between you and your current target.
- **Target Tracking** – Identifies and tracks the entity you are looking at.
- **Projectile Tracking** – Includes projectile trajectory calculations for more accurate distance and collision prediction.
- **Interaction Reach** – Provides information related to player interaction distance.
- **Movement Detection** – Tracks relevant movement information.
- **Jump Height** – Displays jump-related information.
- **HUD Toggle** – Enable or disable the HUD using a configurable keybind.
- **HUD Notifications** – Displays notifications when toggling the HUD.

## Compatibility

| Requirement | Version |
|---|---|
| Minecraft | 26.3 |
| Mod Loader | Fabric |
| Environment | Client-side |
| Java | Java 25 |

**Dependencies:**
- Fabric Loader
- Fabric API

## Installation

1. Install Fabric Loader for Minecraft 26.3.
2. Download the latest ReachHUD release.
3. Download the required dependencies.
4. Place the ReachHUD `.jar` file inside your Minecraft `mods` folder.
5. Launch Minecraft using your Fabric installation.

## Controls

| Keybind | Action |
|---|---|
| Right Control | Toggle ReachHUD |

The HUD toggle keybind is configurable through Minecraft's Controls settings.

## Development

ReachHUD is built using Java and the Fabric modding ecosystem.

### Building from Source

Clone the repository:

```bash
git clone YOUR_REPOSITORY_URL
cd ReachHUD
```

Build the project:

```bash
./gradlew build
```

For Windows:

```powershell
.\gradlew.bat build
```

The compiled JAR files can be found in:

```text
build/libs/
```

## Beta Status

ReachHUD 0.9.4 for Minecraft 26.3 is currently a beta release.

During this testing phase, the project may receive:
- Bug fixes and stability improvements.
- HUD behavior and visual adjustments.
- Improvements to target and projectile calculations.
- Performance optimizations.
- Compatibility updates.

Feedback and bug reports are welcome and will help improve future releases.

## License

ReachHUD is licensed under the **MIT License**.

See the `LICENSE` file for more information.

## Links

- [Modrinth](https://modrinth.com/mod/reachhud)

---

**ReachHUD** — Know your reach. Stay in control.