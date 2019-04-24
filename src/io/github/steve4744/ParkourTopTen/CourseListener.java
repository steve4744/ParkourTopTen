package io.github.steve4744.ParkourTopTen;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import me.A5H73Y.Parkour.ParkourEvents.PlayerFinishCourseEvent;
import me.A5H73Y.Parkour.Other.TimeObject;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Utils;

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
        //plugin.getLogger().info("DEBUG: [CL] direction = " + direction);
        this.direction = direction;
        this.courseName = courseName;
        displayTopTen();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCourseCompletion(PlayerFinishCourseEvent event) {
    	String coursecompleted = event.getCourseName();
    	// Only update heads for course just completed
    	if (coursecompleted.equalsIgnoreCase(courseName)) {
    		displayTopTen();
    	}
    }

    public void displayTopTen() {
        if (topTenLocation == null) {
            plugin.getLogger().severe("The location of the top ten world does not exist. Maybe a world was deleted?");
            return;
        }
        // Get the top 10 times for the course
        List<TimeObject> topten = DatabaseMethods.getTopCourseResults(courseName.toLowerCase(), 10);

        // Display the top ten heads
        int i = 0;
        Block b = topTenLocation.getBlock();

        for (i = 0; i < topten.size(); ) {
            String name = topten.get(i).getPlayer(); 
            String time = Utils.displayCurrentTime(topten.get(i).getTime());
            i++;
            //plugin.getLogger().info("DEBUG: [dTT] " + i);
            //plugin.getLogger().info("DEBUG: [dTT] " + name);
            //plugin.getLogger().info("DEBUG: [dTT] " + time);

            // Get the block and move
            BlockFace directionFacing;
            if (b.getState() instanceof Sign) {
            //if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN) {
                Sign sign = (Sign)b.getState();
                org.bukkit.material.Sign s = (org.bukkit.material.Sign) sign.getData();
                directionFacing = s.getFacing();
                sign.setLine(0, "#" + i);

                //TODO if/when Parkour implements uuids then this can be simplified
                OfflinePlayer player = Bukkit.getOfflinePlayer(Bukkit.getOfflinePlayer(name).getUniqueId());
                sign.setLine(1, name);
                sign.setLine(2, "Time: " + time);
                sign.setLine(3, courseName);
                sign.update();

                // Place head
                BlockFace opp = directionFacing.getOppositeFace();

                // Get the block above the block that the sign is attached to
                Block attachToBlock = b.getRelative(BlockFace.UP).getRelative(opp);

                // Check if its already a SKULL
                if (attachToBlock.getType() != Material.PLAYER_HEAD) {
                	attachToBlock.setType(Material.PLAYER_HEAD);
                }
                // Set the rotation
                Rotatable skullData = (Rotatable)Material.PLAYER_HEAD.createBlockData();
                skullData.setRotation(opp);
                attachToBlock.setBlockData(skullData);

                // Set the owner
                Skull skull = (Skull)attachToBlock.getState();
                skull.setOwningPlayer(player);
                skull.update();

            } else {
            	// if there's no more signs then stop
            	return;
            }

            // Move to the next block
            b = b.getRelative(direction);
            if (i == 10) {
                break;
            }
        }
        // Less than 10 in the top ten
        if (i < 10) {
            for (int j = i+1; j < 11; j++) {
                BlockFace directionFacing;
                if (b.getState() instanceof Sign) {
                //if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN) {
                    Sign sign = (Sign)b.getState();
                    org.bukkit.material.Sign s = (org.bukkit.material.Sign) sign.getData();
                    directionFacing = s.getFacing();
                    sign.setLine(0, "#" + j);
                    sign.setLine(1, "");
                    sign.setLine(2, "");
                    sign.setLine(3, "");
                    sign.update();

                    // Remove head
                    BlockFace opp = directionFacing.getOppositeFace();
                    Block attachToBlock = b.getRelative(BlockFace.UP).getRelative(opp);
                    attachToBlock.setType(Material.AIR);
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
        for (int j = 0; j < 10; j++) {
            BlockFace directionFacing;
            if (b.getState() instanceof Sign) {
            //if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN) {
                Sign sign = (Sign)b.getState();
                org.bukkit.material.Sign s = (org.bukkit.material.Sign) sign.getData();
                directionFacing = s.getFacing();
                sign.setLine(0, "");
                sign.setLine(1, "");
                sign.setLine(2, "");
                sign.setLine(3, "");
                sign.update();

                // Remove head
                BlockFace opp = directionFacing.getOppositeFace();
                Block attachToBlock = b.getRelative(BlockFace.UP).getRelative(opp);
                attachToBlock.setType(Material.AIR);
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

    /**
     * @param course name to set
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

}
