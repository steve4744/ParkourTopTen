package io.github.steve4744.ParkourTopTen;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


/**
 * A set of utility methods
 * 
 * @author tastybento
 * 
 */
public class Util {

    /**
     * Converts a serialized location to a Location. Returns null if string is
     * empty
     * 
     * @param s
     *            - serialized location in format "world:x:y:z"
     * @return Location
     */
    static public Location getLocationString(final String s) {
	if (s == null || s.trim() == "") {
	    return null;
	}
	final String[] parts = s.split(":");
	if (parts.length == 6) {
	    final World w = Bukkit.getServer().getWorld(parts[0]);
	
	    if (w == null) {
		return null;
	    }
	    final int x = Integer.parseInt(parts[1]);
	    final int y = Integer.parseInt(parts[2]);
	    final int z = Integer.parseInt(parts[3]);
	    final float yaw = Float.intBitsToFloat(Integer.parseInt(parts[4]));
	    final float pitch = Float.intBitsToFloat(Integer.parseInt(parts[5]));
	    return new Location(w, x, y, z, yaw, pitch);
	}
	
	return null;
    }

    /**
     * Converts a location to a simple string representation
     * If location is null, returns empty string
     * 
     * @param l
     * @return
     */
    static public String getStringLocation(final Location l) {
	if (l == null || l.getWorld() == null) {
	    return "";
	}
	return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + Float.floatToIntBits(l.getYaw()) + ":" + Float.floatToIntBits(l.getPitch());
    }

}
