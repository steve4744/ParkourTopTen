package io.github.steve4744.ParkourTopTen;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;

public class BlockHandler {

	private final ParkourTopTen plugin;

	public BlockHandler(ParkourTopTen plugin) {
		this.plugin = plugin;
	}

	public BlockFace getFacingDirection(Block block) {
		BlockFace facing = null;
		if (block.getBlockData() instanceof WallSign) {
        	Directional dir = (Directional)block.getBlockData();
        	facing = dir.getFacing();
        } else if (block.getBlockData() instanceof Sign) {
        	Rotatable rot = (Rotatable)block.getBlockData();
        	facing = rot.getRotation();
        }
		return facing;
	}
}
