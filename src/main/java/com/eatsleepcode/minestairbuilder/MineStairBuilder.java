package com.eatsleepcode.minestairbuilder;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

public class MineStairBuilder extends PluginBase {

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "MineStairBuilder Loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "MineStairBuilder Disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("minestair")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextFormat.RED + "This command can only be used by a player.");
                return true;
            }

            Player player = (Player) sender;
            int currentY = (int) player.getPosition().y;
            int targetY = 20;

            // Set default target Y to 20 if no argument is provided
            if (args.length > 0) {
                try {
                    targetY = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(TextFormat.RED + "Usage: /minestair [depthCoordinate]");
                    return true;
                }
            }

            // Ensure the target Y is below the player's current Y position
            if (targetY >= currentY) {
                player.sendMessage(TextFormat.RED + "Target depth must be below player's current position.");
                return true;
			}

			Level level = player.getLevel();
			Vector3 start = new Vector3(player.getFloorX(), player.getFloorY() - 1, player.getFloorZ());
			int flightWidth = 3;
			int landingLength = flightWidth;
			int stepsPerFlight = 10;
		
			player.sendMessage(TextFormat.YELLOW + "Building stairs at " + start.toString() + " down to depth " + targetY);
			buildStairwell(level, player, start, getPlayerDirection(player), ((flightWidth * 2) + 3), ((stepsPerFlight + (landingLength *2) + 2)), targetY);
           	// buildCenterWall()
			// buildLandings()
			// buildStairs()
			// buildOppositeStairs()
			// buildLighting()

			player.sendMessage(TextFormat.GREEN + "Stairs built successfully");
			return true;
        }
        return false;
    }



	public void buildStairwell(Level level, Player player, Vector3 start, String direction, int width, int length, int depth) {
		for (int y = (int) start.y; y >= depth; y--) { // Depth
			int halfWidth = (width - 1) / 2;
			for (int x = -halfWidth; x <= halfWidth; x++) { // Width (centered, left-to-right)
				for (int z = 0; z < length; z++) { // Length (front-to-back)
					int blockX = (int) start.x + x;
					int blockZ = (int) start.z + z;
					int chunkX = blockX >> 4;
					int chunkZ = blockZ >> 4;
	
					if (!level.isChunkLoaded(chunkX, chunkZ)) {
						level.loadChunk(chunkX, chunkZ, true);
					}
					
					String stairDirection = "away";
					if (y % 20 == 0) {
						stairDirection = "away";
					}
					else if ((y % 10 == 0) && !(y % 20 == 0)) {
						stairDirection = "toward";
					}

					// Clear the room
					level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.AIR), true);

					// Build walls and floor only
					if (y == depth || x == -halfWidth || x == halfWidth || z == 0 || z == length - 1) {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
					} 
					// Build dividing wall
					else if (x == 0 && (z > 3 && z < length -4)) {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
					}
					// Build near landings
					else if ((z < 3) && stairDirection.equals("away")) {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
					}
					// Build far landings
					else if ((z > length - 4) && stairDirection.equals("toward")) {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
					}
					// Fill with air
					else {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.AIR), true);
					}
				}
			}
		}
	}
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																															 

	

    

    private String getPlayerDirection(Player player) {
		float yaw = (float) player.getYaw(); // Cast to float for Nukkit
		if (yaw < 0) {
			yaw += 360;
		}
		yaw %= 360;

		if (yaw >= 315 || yaw < 45) {
			return "south";
		} else if (yaw >= 45 && yaw < 135) {
			return "west";
		} else if (yaw >= 135 && yaw < 225) {
			return "north";
		} else {
			return "east";
		}
	}
}