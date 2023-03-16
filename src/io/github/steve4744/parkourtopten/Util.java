package io.github.steve4744.parkourtopten;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import net.md_5.bungee.api.ChatColor;

public class Util {

	private final static Pattern HEXCOLOUR = Pattern.compile("<#([A-Fa-f0-9]){6}>");

	public static String colourText(String message) {

		Matcher matcher = HEXCOLOUR.matcher(message);
		while (matcher.find()) {
			StringBuilder sb = new StringBuilder();
			final ChatColor hexColour = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
			sb.append(message.substring(0, matcher.start())).append(hexColour).append(message.substring(matcher.end()));
			message = sb.toString();
			matcher = HEXCOLOUR.matcher(message);
		}

		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
     * Converts a serialized location to a Location.
     * Returns null if string is empty.
     *
     * @author tastybento
     *
     * @param s serialized location in format "world:x:y:z"
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
     * Converts a location to a simple string representation.
     * If location is null, returns empty string.
     *
     * @author tastybento
     *
     * @param location
     * @return
     */
    static public String getStringLocation(final Location l) {
    	if (l == null || l.getWorld() == null) {
    		return "";
    	}
    	return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + Float.floatToIntBits(l.getYaw()) + ":" + Float.floatToIntBits(l.getPitch());
    }

}
