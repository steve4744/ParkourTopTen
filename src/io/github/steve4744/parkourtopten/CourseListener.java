package io.github.steve4744.parkourtopten;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.event.PlayerFinishCourseEvent;
import io.github.a5h73y.parkour.utility.DateTimeUtils;

public class CourseListener implements Listener {

	private ParkourTopTen plugin;
	private Location topTenLocation;
	private BlockFace direction;
	private String courseName;

	/**
	 * @param plugin
	 * @param topTenLocation
	 * @param direction
	 * @param courseName
     */
	public CourseListener(ParkourTopTen plugin, Location topTenLocation, BlockFace direction, String courseName) {
		this.plugin = plugin;
		this.topTenLocation = topTenLocation;
		this.direction = direction;
		this.courseName = courseName;
		displayTopTen();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCourseCompletion(PlayerFinishCourseEvent event) {
		String coursecompleted = event.getCourseName();
		// Only update heads for course just completed
		if (coursecompleted.equalsIgnoreCase(courseName)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					displayTopTen();
				}
			}.runTaskLater(plugin, 20L);
		}
	}

	public void displayTopTen() {
		if (topTenLocation == null) {
			plugin.getLogger().severe("The location of the top ten world does not exist. Maybe a world was deleted?");
			return;
		}
		// Ensure the chunk is loaded
		topTenLocation.getWorld().getChunkAt(topTenLocation).load();
        
		// Get the top 10 times for the course
		List<TimeEntry> topten = Parkour.getInstance().getDatabase().getTopBestTimes(courseName.toLowerCase(), 10);

		int i = 0;
		Block b = topTenLocation.getBlock();
		Material signType = b.getType();
		BlockFace directionFacing = plugin.getBlockHandler().getFacingDirection(b);

		for (i = 0; i < Math.min(topten.size(), 10); i++) {
			if (plugin.isDebug()) {
				plugin.getLogger().info("DEBUG: [dTT] " + i);
				plugin.getLogger().info("DEBUG: [dTT] " + topten.get(i).getPlayerName());
				plugin.getLogger().info("DEBUG: [dTT] " + topten.get(i).getPlayerId());
				plugin.getLogger().info("DEBUG: [dTT] " + topten.get(i).getPlayerUuid());
				plugin.getLogger().info("DEBUG: [dTT] " + DateTimeUtils.displayCurrentTime(topten.get(i).getTime()));
			}

			// Get the block and move
			if (b.getBlockData() instanceof WallSign || b.getBlockData() instanceof org.bukkit.block.data.type.Sign) {
				if (plugin.getConfig().getBoolean("enforceSameSignType") && b.getType() != signType) {
					return;
				}

				Sign sign = (Sign)b.getState();
				sign.setLine(0, "#" + (i + 1));
				sign.setLine(1, topten.get(i).getPlayerName());
				sign.setLine(2, "Time: " + DateTimeUtils.displayCurrentTime(topten.get(i).getTime()));
				sign.setLine(3, courseName);
				sign.update();

				// Get the block above the block that the sign is attached to
				Block headBlock = plugin.getBlockHandler().getHeadBlock(b);

				// Check if its already a SKULL
				if (headBlock.getType() != Material.PLAYER_HEAD) {
					headBlock.setType(Material.PLAYER_HEAD);
				}
				// Set the rotation
				Rotatable skullData = (Rotatable)Material.PLAYER_HEAD.createBlockData();
				skullData.setRotation(directionFacing.getOppositeFace());
				headBlock.setBlockData(skullData);

				// Set the owner
				Skull skull = (Skull)headBlock.getState();
				skull.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(topten.get(i).getPlayerUuid())));
				skull.update();

			} else {
				// if there's no more signs then stop
				return;
			}
			// Move to the next block
			b = b.getRelative(direction);
		}
		// Less than 10 in the top ten
		if (i < 10) {
			for (int j = i+1; j < 11; j++) {
				if (b.getBlockData() instanceof WallSign || b.getBlockData() instanceof org.bukkit.block.data.type.Sign) {
					if (plugin.getConfig().getBoolean("enforceSameSignType") && b.getType() != signType) {
						return;
					}
					Sign sign = (Sign)b.getState();
					sign.setLine(0, "#" + j);
					sign.setLine(1, "");
					sign.setLine(2, "");
					sign.setLine(3, courseName);
					sign.update();

					plugin.getBlockHandler().removeHead(b);

				} else {
					// no more signs
					return;
				}
				b = b.getRelative(direction);
			}
		}
	}

	public void removeTopTen() {
		Block b = topTenLocation.getBlock();
		Material signType = b.getType();
		for (int j = 0; j < 10; j++) {
			if (b.getBlockData() instanceof WallSign || b.getBlockData() instanceof org.bukkit.block.data.type.Sign) {
				if (plugin.getConfig().getBoolean("enforceSameSignType") && b.getType() != signType) {
					return;
				}
				Sign sign = (Sign)b.getState();
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();

				plugin.getBlockHandler().removeHead(b);

			} else {
				return;
			}
			b = b.getRelative(direction);
		}
	}

	/**
	 * @return the topTenLocation
	 */
	public Location getTopTenLocation() {
		return topTenLocation;
	}

	/**
	 * @return the direction
	 */
	public BlockFace getDirection() {
		return direction;
	}

	/**
	 * @return the course name
	 */
	public String getCourseName() {
		return courseName;
	}

	/**
	 * @param topTenLocation the topTenLocation to set
	 */
	public void setTopTenLocation(Location topTenLocation) {
		this.topTenLocation = topTenLocation;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(BlockFace direction) {
		this.direction = direction;
	}

}
