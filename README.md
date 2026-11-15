# ReachHUD

ReachHUD is a client-side Fabric mod that displays reach information while aiming at entities. It also includes projectile trajectory prediction for bows and crossbows.

## Features

### Melee Reach

When aiming at an entity, ReachHUD displays:

- Target name
- Distance between the player and target
- Whether the target is within interaction reach

The distance is calculated from the player's eye position to the nearest point of the target's hitbox.

### Projectile Prediction

ReachHUD can predict whether your projectile will hit an entity.

Supported weapons and mechanics:

- **Bow** - Calculates arrow speed based on draw time.
- **Crossbow** - Detects loaded projectiles and simulates their trajectory.
- **Multishot** - Checks all three projectile directions.
- **Firework Rockets** - Uses separate physics calculations for rocket movement.

The simulation checks whether the projectile's path intersects the target's hitbox and accounts for blocks that may obstruct the shot.

### HUD

The HUD appears below the crosshair and displays relevant target information.

It includes:
- Smooth distance updates
- Fade-in and fade-out animations
- Different target name colors
- Reach and hit indicators

## Controls

Press **Right Control** to toggle the HUD.

You can also change the keybind through Minecraft's Controls settings under the ReachHUD category.

The HUD's enabled state is saved in the configuration file:

`config/reachhud.properties`

## Installation

1. Install Fabric Loader.
2. Download the ReachHUD `.jar` file.
3. Place it inside your Minecraft `mods` folder.
4. Launch the game.

## Compatibility

- Minecraft: 26.3
- Mod Loader: Fabric
- Java: 25

## Notes

Projectile prediction is based on local trajectory simulation. Actual results may vary depending on server behavior and network latency.

ReachHUD is a client-side mod. It does not modify server reach or change Minecraft's combat mechanics.

## Links

- [GitHub](https://github.com/jaylgrs/ReachHUD-MultiVersion-PublicRepo)
- [Modrinth](https://modrinth.com/mod/reachhud)

Not affiliated with Mojang or Microsoft.