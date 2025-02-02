package com.eatsleepcode.stairbuilder;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

public class StairBuilder extends PluginBase {

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "StairBuilder Loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "StairBuilder Disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("stairs")) {
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
                    player.sendMessage(TextFormat.RED + "Usage: /stairs [depthCoordinate]");
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
		
			//player.sendMessage(TextFormat.YELLOW + "Building stairs at " + start.toString() + " down to depth " + targetY);
			buildStairwell(level, player, start, getPlayerDirection(player), ((flightWidth * 2) + 3), ((stepsPerFlight + (landingLength *2) + 2)), targetY, stepsPerFlight, landingLength);
			buildStairs(level, player, start, getPlayerDirection(player), ((flightWidth * 2) + 3), ((stepsPerFlight + (landingLength *2) + 2)), targetY, stepsPerFlight, landingLength);
            // buildLighting()

			player.sendMessage(TextFormat.GREEN + "Stairs built successfully");
			return true;
        }
        return false;
    }



	public void buildStairwell(Level level, Player player, Vector3 start, String direction, int width, int length, int depth, int stepsPerFlight, int landingLength) {
		int overallDepthCount = 0;

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

					// Clear the room
					level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.AIR), true);

					
					// Build outer walls and floor
					if (y == depth || x == -halfWidth || x == halfWidth || z == 0 || z == length - 1) {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
					} 


					// Build dividing wall
					else if (x == 0 && (z > landingLength && z < (length - (landingLength + 1)))) {
						if ((overallDepthCount % stepsPerFlight == 0) && (z == landingLength + 1 || z == (landingLength + stepsPerFlight))) {
							level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.SEA_LANTERN), true);
						}
						else {
							level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
						}
					}


					// Build near landings
					else if ((z <= landingLength) && (overallDepthCount % (stepsPerFlight * 2) == 0)) {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
					}
					
					
					// Build far landings
					else if ((z >= (length - (landingLength + 1))) && (overallDepthCount % stepsPerFlight == 0) && !(overallDepthCount % (stepsPerFlight * 2) == 0)) {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.STONE), true);
					}


					// Fill with air
					else {
						level.setBlock(new Vector3(blockX, y, blockZ), Block.get(Block.AIR), true);
					}

				}
			}

			overallDepthCount++;
		}
	}
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																															 

	
	public void buildStairs(Level level, Player player, Vector3 start, String direction, int width, int length, int depth, int stepsPerFlight, int landingLength) {
		String stairDirection = "toward";
		int flightStepCount = 0;
		int leftExtent = -1 * ((width - 3) / 2);
		int rightExtent = -1;
		int overallStepCount = 0;

		for (int y = (int) start.y; y > depth; y--) { // Depth
			// Set stair direction and reset step count
			if (overallStepCount % (stepsPerFlight * 2) == 0 && stairDirection.equals("toward")) {
				stairDirection = "away";
				flightStepCount = 0;
				leftExtent = 1;
				rightExtent = ((width - 3) / 2);
			}
			else if ((overallStepCount % stepsPerFlight == 0) && !(overallStepCount % (stepsPerFlight * 2) == 0) && stairDirection.equals("away")) {
				stairDirection = "toward";
				flightStepCount = 0;
				leftExtent = -1 * ((width - 3) / 2);
				rightExtent = -1;
			}
			
			for (int x = leftExtent; x <= rightExtent; x++) { // Width (left-to-right)
				
				for (int z = 0; z < length; z++) { // Length (front-to-back)
					int blockX = (int) start.x + x;
					int blockZ = (int) start.z + z;
					int chunkX = blockX >> 4;
					int chunkZ = blockZ >> 4;
	
					if (!level.isChunkLoaded(chunkX, chunkZ)) {
						level.loadChunk(chunkX, chunkZ, true);
					}

					if (z == (landingLength + 1) + flightStepCount && stairDirection.equals("away")) {
						Block stepBlock = Block.get(Block.NORMAL_STONE_STAIRS);
						stepBlock.setDamage(3);
						level.setBlock(new Vector3(blockX, y, blockZ), stepBlock, true);
					}
					else if (z == (landingLength) + (stepsPerFlight - flightStepCount) && stairDirection.equals("toward")) {
						Block stepBlock = Block.get(Block.NORMAL_STONE_STAIRS);
						stepBlock.setDamage(2);
						level.setBlock(new Vector3(blockX, y, blockZ), stepBlock, true);
					}
				}
			}

			overallStepCount++;
			flightStepCount++;
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