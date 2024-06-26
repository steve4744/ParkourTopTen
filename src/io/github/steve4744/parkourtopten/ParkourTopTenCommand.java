package io.github.steve4744.parkourtopten;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.a5h73y.parkour.Parkour;

public class ParkourTopTenCommand implements CommandExecutor {
	private final ParkourTopTen plugin;
	private final String version;
	private List<CourseListener> topTen;
	/**
	 * @param plugin
	 * @param version
	 */
	public ParkourTopTenCommand(ParkourTopTen plugin, String version) {
		this.plugin = plugin;
		this.version = version;
		topTen = new ArrayList<CourseListener>();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg3) {
		if (arg3.length == 0 || arg3[0].equalsIgnoreCase("info")) {
			sender.sendMessage(ChatColor.GREEN + "[ParkourTopTen] " + ChatColor.WHITE + "Version " + version + " : plugin by "+ ChatColor.AQUA + "steve4744");
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Command must be used in-game while looking at a row of signs.");
			return true;
		}
		Player player = (Player)sender;
		if (!player.isOp() && !player.hasPermission("parkourtopten.admin")) {
			player.sendMessage(ChatColor.RED + "You must be OP or have parkourtopten.admin permission to use this command");
			return false;
		}
		if (arg3[0].equalsIgnoreCase("help")) {
			sendHelp(player);
			return true;
		}
		// Create a top ten display
		if (arg3[0].equalsIgnoreCase("create")) {
			if (arg3.length == 1) {
				player.sendMessage(ChatColor.RED + "You must specify a Parkour course to display");
				return false;
			}
			if (!Parkour.getInstance().getCourseManager().doesCourseExist(arg3[1])) {
				player.sendMessage(ChatColor.RED + "Parkour course " + ChatColor.AQUA + arg3[1] + ChatColor.RED + " does not exist");
				return false;
			}

			// Get whether to place the head above the sign or on the block behind the sign
			String position = "b";
			if (arg3.length > 2 && (arg3[2].equalsIgnoreCase("a") || arg3[2].equalsIgnoreCase("above"))) {
				position = "a";
			}

			Block lastBlock = player.getTargetBlock(null, 10);

			if (plugin.isDebug()) {
				plugin.getLogger().info("DEBUG: [pTTC] lastBlock = " + lastBlock);
			}
			if (!plugin.getSignHandler().isValidSign(lastBlock)) {
				player.sendMessage(ChatColor.RED + "You must be looking at a sign to start");
				return true;
			}

			BlockFace facing = plugin.getBlockHandler().getFacingDirection(lastBlock);

			// Get the direction to the right
			BlockFace right = switch(facing) {
				case EAST  -> BlockFace.NORTH;
				case NORTH -> BlockFace.WEST;
				case SOUTH -> BlockFace.EAST;
				case WEST  -> BlockFace.SOUTH;
				default -> null;
			};
			if (right == null) {
				player.sendMessage(ChatColor.RED + "Sign needs to face north, south, east or west");
				return true;
			}

			// Check if this panel already exists
			for (CourseListener panel : topTen) {
				if (panel.getTopTenLocation().equals(lastBlock.getLocation())) {
					player.sendMessage(ChatColor.RED + "ParkourTopTen display is already active");
					return true;
				}
			}

			CourseListener newTopTen = new CourseListener(plugin, lastBlock.getLocation(), right, arg3[1], position);
			topTen.add(newTopTen);
			plugin.getServer().getPluginManager().registerEvents(newTopTen, plugin);
			player.sendMessage(ChatColor.GREEN + "ParkourTopTen heads created for course " + ChatColor.AQUA + arg3[1]);
			plugin.saveDisplays();
			return true;

		} else if (arg3[0].equalsIgnoreCase("remove")) {
			if (arg3.length == 2 && arg3[1].equalsIgnoreCase("all")) {
				if (topTen.size() == 0) {
					player.sendMessage(ChatColor.RED + "There are no active ParkourTopTen displays");
					return true;
				} else if (topTen.size() == 1) {
					player.sendMessage(ChatColor.GREEN + "Removing " + topTen.size() + " ParkourTopTen display");
				} else {
					player.sendMessage(ChatColor.GREEN + "Removing " + topTen.size() + " ParkourTopTen displays");
				}
				for (CourseListener panel : topTen) {
					// Clear each panel
					panel.removeTopTen();
					panel.setTopTenLocation(null);
				}
				// Remove all from the list
				topTen.removeAll(topTen);
				plugin.saveDisplays();
				return true;
			}
			if (arg3.length == 2 && arg3[1].equalsIgnoreCase("help")) {
				sendHelp(player);
				return true;
			}

			Block lastBlock = player.getTargetBlock(null, 10);

			if (!plugin.getSignHandler().isValidSign(lastBlock)) {
				player.sendMessage(ChatColor.RED + "You must be looking at the #1 top ten sign");
				return true;
			}
			// Check if this panel  exists
			for (CourseListener panel : topTen) {
				if (panel.getTopTenLocation().equals(lastBlock.getLocation())) {
					// Remove the display
					panel.removeTopTen();
					// Remove from the list
					topTen.remove(panel);
					// Prevent the display from being recreated
					panel.setTopTenLocation(null);
					player.sendMessage(ChatColor.GREEN + "ParkourTopTen display removed");
					plugin.saveDisplays();
					return true;
				}
			}
			player.sendMessage(ChatColor.RED + "Could not find a ParkourTopTen display in that position");
			return true;
 
		} else if (arg3[0].equalsIgnoreCase("reload")) {
			player.sendMessage("Config reloaded");
			plugin.reloadConfig();
			return true;
		}
		player.sendMessage(ChatColor.RED + "Not a valid ParkourTopTen command");
		return false;
	}

	public void sendHelp(Player player) {
		player.sendMessage(ChatColor.WHITE + " === " + ChatColor.YELLOW + "ParkourTopTen Help " + ChatColor.WHITE + "===");
		player.sendMessage(ChatColor.AQUA + "To create a top ten display for a course:");
		player.sendMessage(ChatColor.AQUA + "  : build a row of signs");
		player.sendMessage(ChatColor.AQUA + "  : look at the left-most sign and type the create command");
		player.sendMessage(ChatColor.WHITE + "  /ptt create [course] [head position]");
		player.sendMessage(ChatColor.AQUA  + "     where [course] = a valid Parkour course");
		player.sendMessage(ChatColor.AQUA  + "	   where [head position] = above or behind (default) the sign");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "To remove a top ten display for a course:");
		player.sendMessage(ChatColor.AQUA + "  : look at the left-most sign and type the remove command");
		player.sendMessage(ChatColor.WHITE + "  /ptt remove " + ChatColor.AQUA + "- remove a ParkourTopTen display");
		player.sendMessage(ChatColor.WHITE + "  /ptt remove all " + ChatColor.AQUA + "- remove ALL ParkourTopTen displays");
		player.sendMessage(ChatColor.WHITE + "  /ptt reload " + ChatColor.AQUA + "- reload the ParkourTopTen config"); 
		return;
	}

	/**
	 * @return the topTen
	 */
	public List<CourseListener> getTopTen() {
		return topTen;
	}

	/**
	 * @param topTen the topTen to set
	 */
	public void setTopTen(List<CourseListener> topTen) {
		this.topTen = topTen;
	}

	/**
	 * Add one panel to the list
	 * @param topTen
	 */
	public void addTopTen(CourseListener topTen) {
		this.topTen.add(topTen);
	}
}
