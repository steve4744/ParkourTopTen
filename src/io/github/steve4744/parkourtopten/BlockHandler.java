package io.github.steve4744.parkourtopten;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;

public class BlockHandler {

	private final ParkourTopTen plugin;

	public BlockHandler(ParkourTopTen plugin) {
		this.plugin = plugin;
	}

	/**
	 * Get the facing direction of the block.
	 *
	 * @param block
	 * @return
	 */
	public BlockFace getFacingDirection(Block block) {
		BlockFace facing = null;
		if (Tag.WALL_SIGNS.isTagged(block.getType()) || Tag.WALL_HANGING_SIGNS.isTagged(block.getType())) {
			Directional dir = (Directional)block.getBlockData();
			facing = dir.getFacing();

		} else if (Tag.STANDING_SIGNS.isTagged(block.getType()) || Tag.CEILING_HANGING_SIGNS.isTagged(block.getType())) {
			Rotatable rot = (Rotatable)block.getBlockData();
			facing = rot.getRotation();
		}
		return facing;
	}

	/**
	 * Get the block above the sign, or the block above the block the sign is attached to.
	 *
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
