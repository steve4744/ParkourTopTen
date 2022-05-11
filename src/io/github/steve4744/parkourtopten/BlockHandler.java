package io.github.steve4744.parkourtopten;

import org.bukkit.Material;
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

	/**
	 * Get the facing direction of the block.
	 * @param block
	 * @return
	 */
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

	/**
	 * Get the block above the sign, or the block above the block the sign is attached to.
	 * @param block
	 * @return
	 */
	public Block getHeadBlock(Block block) {
		BlockFace directionFacing = getFacingDirection(block);
		boolean placeAboveSign = plugin.getConfig().getBoolean("placeHeadAboveSign");
		return placeAboveSign ? block.getRelative(BlockFace.UP) : block.getRelative(BlockFace.UP).getRelative(directionFacing.getOppositeFace());
	}

	public void removeHead(Block block) {
		getHeadBlock(block).setType(Material.AIR);
	}

}
