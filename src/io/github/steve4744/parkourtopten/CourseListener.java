package io.github.steve4744.parkourtopten;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.event.ParkourFinishEvent;
import io.github.a5h73y.parkour.event.ParkourResetCourseEvent;
import io.github.a5h73y.parkour.event.ParkourResetLeaderboardEvent;
import io.github.a5h73y.parkour.event.ParkourResetPlayerEvent;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;

public class CourseListener implements Listener {

	private ParkourTopTen plugin;
	private Location topTenLocation;
	private BlockFace direction;
	private String courseName;
	private String position;

	/**
	 * @param plugin
	 * @param topTenLocation
	 * @param direction
	 * @param courseName
     */
	public CourseListener(ParkourTopTen plugin, Location topTenLocation, BlockFace direction, String courseName, String position) {
		this.plugin = plugin;
		this.topTenLocation = topTenLocation;
		this.direction = direction;
		this.courseName = courseName;
		this.position = position;
		displayTopTen();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCourseCompletion(ParkourFinishEvent event) {
		// Only update heads for course just completed
		if (event.getCourseName().equalsIgnoreCase(courseName)) {
			updateTopTen();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCourseReset(ParkourResetCourseEvent event) {
		if (event.getCourseName().equalsIgnoreCase(courseName)) {
			updateTopTen();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerReset(ParkourResetPlayerEvent event) {
		updateTopTen();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onLeaderboardReset(ParkourResetLeaderboardEvent event) {
		if (event.getCourseName().equalsIgnoreCase(courseName)) {
			updateTopTen();
		}
	}

	private void updateTopTen() {
		new BukkitRunnable() {
			@Override
			public void run() {
				displayTopTen();
			}
		}.runTaskLater(plugin, 20L);
	}

	public void displayTopTen() {
		if (topTenLocation == null) {
			plugin.getLogger().severe("The location of the top ten world does not exist. Maybe a world was deleted?");
			return;
		}
		// Ensure the chunk is loaded
		if (!topTenLocation.getWorld().getChunkAt(topTenLocation).isLoaded()) {
			if (plugin.isDebug()) {
				plugin.getLogger().info("DEBUG: [dTT] Loading chunk at " + topTenLocation);
			}
			topTenLocation.getWorld().getChunkAt(topTenLocation).load();
		}
		// Get the top 10 times for the course
		List<TimeEntry> topten = Parkour.getInstance().getDatabaseManager().getTopBestTimes(courseName.toLowerCase(), 10);

		int i = 0;
		Block b = topTenLocation.getBlock();
		Material signType = b.getType();
		BlockFace directionFacing = plugin.getBlockHandler().getFacingDirection(b);

		for (i = 0; i < Math.min(topten.size(), 10); i++) {
			if (plugin.isDebug()) {
				plugin.getLogger().info("DEBUG: [dTT] " + i);
				plugin.getLogger().info("DEBUG: [dTT] " + topten.get(i).getPlayerName());
				plugin.getLogger().info("DEBUG: [dTT] " + topten.get(i).getPlayerId());
				plugin.getLogger().info("DEBUG: [dTT] " + getUuid(topten.get(i).getPlayerId()));
				plugin.getLogger().info("DEBUG: [dTT] " + DateTimeUtils.displayCurrentTime(topten.get(i).getTime()));
			}

			// if there are no more valid signs then stop
			if (!plugin.getSignHandler().isValidSign(b)) {
				return;
			}
			if (!plugin.getSignHandler().isSameSignType(b, signType)) {
				return;
			}

			plugin.getSignHandler().updateSign(b, i+1, topten.get(i).getPlayerName(),
					DateTimeUtils.displayCurrentTime(topten.get(i).getTime()), courseName);

			// Get the block above the block that the sign is attached to
			Block headBlock = plugin.getBlockHandler().getHeadBlock(b, position);

			// Check if its already a SKULL
			if (headBlock.getType() != Material.PLAYER_HEAD) {
				headBlock.setType(Material.PLAYER_HEAD);
			}
			// Set the rotation
			Rotatable skullData = (Rotatable)Material.PLAYER_HEAD.createBlockData();
			skullData.setRotation(directionFacing.getOppositeFace());
			headBlock.setBlockData(skullData);

			// Get the player profile from the uuid
			Skull skull = (Skull) headBlock.getState();
			PlayerProfile profile = Bukkit.createPlayerProfile(getUuid(topten.get(i).getPlayerId()), topten.get(i).getPlayerName());

			profile.update().thenAcceptAsync(updatedProfile -> {
				if (plugin.isDebug()) {
					plugin.getLogger().info("DEBUG: [dTT] Texture = " + updatedProfile.getTextures());
				}
				// Update with the skin contained in the profile
				skull.setOwnerProfile(updatedProfile);
				skull.update();
			}, runnable -> Bukkit.getScheduler().runTask(plugin, runnable));

			// Move to the next block
			b = b.getRelative(direction);
		}
		// Less than 10 in the top ten
		if (i < 10) {
			for (int j = i+1; j < 11; j++) {
				if (!plugin.getSignHandler().isValidSign(b)) {
					// no more signs
					return;
				}
				if (!plugin.getSignHandler().isSameSignType(b, signType)) {
					return;
				}

				plugin.getSignHandler().updateSign(b, j, "", "", courseName);
				plugin.getBlockHandler().removeHead(b, position);
				b = b.getRelative(direction);
			}
		}
	}

	public void removeTopTen() {
		if (topTenLocation == null) {
			return;
		}
		Block b = topTenLocation.getBlock();
		Material signType = b.getType();
		for (int j = 0; j < 10; j++) {
			if (!plugin.getSignHandler().isValidSign(b)) {
				return;
			}
			if (!plugin.getSignHandler().isSameSignType(b, signType)) {
				return;
			}

			plugin.getSignHandler().resetSign(b);
			plugin.getBlockHandler().removeHead(b, position);
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
	 * @return the position of the head
	 */
	public String getPosition() {
		return position;
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

	/**
	 * @param playerId
	 * @return UUID
	 */
	private UUID getUuid (String playerId) {
		return UUID.fromString(PlayerUtils.padPlayerUuid(playerId));
	}

}
