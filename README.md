# MineStairBuilder Minecraft (Nukkit) Plugin

Build a lighted staircase descending to the specified depth coordinate.

## Prerequisites
- [Nukkit Minecraft Server](https://github.com/PetteriM1/NukkitPetteriM1Edition/releases)

## Installation 
- Place the `MineStairBuilder.jar` file in the `<Nukkit Installation Folder>/plugins/` folder.

## Usage

Ensure the area at least two blocks directly above the stairs is cleared of any blocks.   We do not do this automatically to avoid needlessly damaging any structures you may have already created.

- Create a stone-walled stairway to a depth of Y=20:

  `/minestair 20`

## Known Issues

- When water is directly adjacent to the stair area it can sometimes overwhelm the creation of the staircase.   This will result in some debris that needs to be cleaned up. 

## Building Project

Run `mvn clean package`.   The output will be saved to `/target/MineStairBuilder.jar`