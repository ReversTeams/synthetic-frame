# Synthetic Frame

Ever thought you were too fleshy for your tech pack? 

## Features

- Adds "Synthetic Frames", mechanical player bodies you can posess and transfer between
  - Frames replace hunger with an internal RF battery
  - Max health scales based on charge capacity
  - Internal battery can be charged by powered Magnifying Transmitters in range
- Adds Magnifying Transmitters to keep Frames charged and protected (since Frames regenerate using energy, an always-full battery is like over-max saturation)
- Adds the Frame Cabinet to hang up your Frame and transfer to another Frame (or your original body, if you left it in a cabinet).
  - Cabinets are connected through Frame data cables or an ME network. 
  - Cabinets will also charge a Frame if connected to power.
  - Body transfer moves your inventory with it, with support for curios. 
  - While in a cabinet, even if you're still posessing the Frame, you can insert battery units to increase that frame's max health, with diminishing returns.

## Details

### Frames

Frames regenerate extremely quickly by consuming power. If you're taking a lot of damage, you might cause a blackout! Conversely, if you have a nuclear reactor plugged into you, you probably won't die.

### Magnifying Transmitter

The Magnifying Transmitter is a multiblock composed of three parts:
1. The central, secondary coil
2. The primary coil rings surrounding the secondary coil
3. The resonator coil, on top of the secondary coil but not touching the primary coil

Each complete 8-block primary ring around the secondary pole can be independantly powered and contributes to the total throughput of the transmitter. A longer secondary compared to the primaries will extend distance at the cost of throughput. Adding more resonators to the secondary will reduce the dropoff with range at the cost of more input demand. The Transmitter consumes its appropriate pwoer draw regardless of if there's any Frame to receive it in range.

### Config

The amount of energy that counts towards hunger, the scaling of max health and max power per battery unit, and whether the player should spawn as a regular player or be immediately put into a frame can be configured.