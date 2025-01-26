package com.eatsleepcode.minestairbuilder;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSeaLantern;
import cn.nukkit.block.BlockStairsStone;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

public class MineStairBuilder extends PluginBase {

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "MineStairBuilder plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "MineStairBuilder plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("minestair")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextFormat.RED + "This command can only be used by a player.");
                return true;
            }

            Player player = (Player) sender;
            int targetY = 20; // Default target Y value

            if (args.length > 0) {
                try {
                    targetY = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(TextFormat.RED + "Usage: /minestair [depthCoordinate]");
                }
            }

            Vector3 position = player.getPosition();
            if (targetY >= position.getFloorY()) {
                player.sendMessage(TextFormat.RED + "Target depth must be below player's current position.");
            } else {
                buildStairs(player, targetY);
                player.sendMessage(TextFormat.GREEN + "Mine stairway built to depth of Y=" + targetY);
            }
            
            return true;
        }
        return false;
    }

    private void buildStairs(Player player, int targetY) {
        Vector3 position = player.getPosition();
        String direction = getPlayerDirection(player);

        int xOffset = 0, zOffset = 0;
        switch (direction) {
            case "south": zOffset = 1; break;
            case "west": xOffset = -1; break;
            case "north": zOffset = -1; break;
            case "east": xOffset = 1; break;
        }

        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();
        

        while (y > targetY) {
            Level level = player.getLevel();
            level.loadChunk(x >> 4, z >> 4, true);
            // Build 10 steps
            for (int i = 0; i < 10 && y > targetY; i++) {
                buildStep(x, y, z, xOffset, zOffset);
                x += xOffset;
                z += zOffset;
                y--;
            }

            // Build landing
            buildLanding(x, y, z, xOffset, zOffset);

            // Reverse direction
            xOffset = -xOffset;
            zOffset = -zOffset;
        }

        // Fill the floor with stone
        fillFloor(x, targetY, z, xOffset, zOffset);
    }

    private void buildStep(int x, int y, int z, int xOffset, int zOffset) {
        for (int i = -1; i <= 1; i++) { // 3 blocks wide
            // Place stairs
            Server.getInstance().getDefaultLevel().setBlock(new Vector3(x + i * zOffset, y, z + i * xOffset), new BlockStairsStone());
            // Place walls
            Server.getInstance().getDefaultLevel().setBlock(new Vector3(x + i * zOffset, y + 1, z + i * xOffset), Block.get(Block.STONE));
            Server.getInstance().getDefaultLevel().setBlock(new Vector3(x + i * zOffset, y + 2, z + i * xOffset), Block.get(Block.STONE));
        }
    }

    private void buildLanding(int x, int y, int z, int xOffset, int zOffset) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = 0; dz < 6; dz++) {
                int bx = x + dx * zOffset + dz * xOffset;
                int bz = z + dx * xOffset + dz * zOffset;
                Server.getInstance().getDefaultLevel().setBlock(new Vector3(bx, y, bz), Block.get(Block.STONE));
            }
        }

        // Place sea lantern
        int lanternX = x + 2 * xOffset;
        int lanternZ = z + 2 * zOffset;
        Server.getInstance().getDefaultLevel().setBlock(new Vector3(lanternX, y + 2, lanternZ), new BlockSeaLantern());
    }

    private void fillFloor(int x, int targetY, int z, int xOffset, int zOffset) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = 0; dz < 6; dz++) {
                int bx = x + dx * zOffset + dz * xOffset;
                int bz = z + dx * xOffset + dz * zOffset;
                Server.getInstance().getDefaultLevel().setBlock(new Vector3(bx, targetY, bz), Block.get(Block.STONE));
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
