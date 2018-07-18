package io.github.steve4744.ParkourTopTen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Sign;
import org.bukkit.util.BlockIterator;

import me.A5H73Y.Parkour.Course.CourseMethods;


public class ParkourTopTenCommand implements CommandExecutor {
    private ParkourTopTen plugin;
    private List<CourseListener> topTen;
    /**
     * @param plugin
     */
    public ParkourTopTenCommand(ParkourTopTen plugin) {
        this.plugin = plugin;
        topTen = new ArrayList<CourseListener>();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] arg3) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command must be used in-game while looking at a row of signs.");
            return true;
        }
        Player player = (Player)sender;
        if (!player.isOp() && !player.hasPermission("parkourtopten.admin")) {
            sender.sendMessage(ChatColor.RED + "You must be OP or have parkourtopten.admin permission to use this command");
            return true;
        }
        if (command.getName().equalsIgnoreCase("parkourtopten")) {
            if (arg3.length == 0 || arg3[0].equalsIgnoreCase("help")){
                sendHelp(player);
                return true;
            }
            // Create a top ten display
            if (arg3[0].equalsIgnoreCase("create")) {
            	if (arg3.length == 1) {
            		sender.sendMessage(ChatColor.RED + "You must specify a Parkour course to display");
            		return true;
            	}
                if (!CourseMethods.exist(arg3[1])) {
                	sender.sendMessage(ChatColor.RED + "Parkour course " + ChatColor.AQUA + arg3[1] + ChatColor.RED + " does not exist");
                	return true;
                }
            
                // Get the block that the player is looking at
                BlockIterator iter = new BlockIterator(player, 10);
                Block lastBlock = iter.next();
                while (iter.hasNext()) {
                	lastBlock = iter.next();
                	if (lastBlock.getType() == Material.AIR)
                		continue;
                	break;
                }
                //plugin.getLogger().info("DEBUG: " + lastBlock);
                if (lastBlock.getType() != Material.WALL_SIGN && lastBlock.getType() != Material.SIGN) {
                	sender.sendMessage(ChatColor.RED + "You must be looking at a sign to start");
                	return true;
                }
            
                // Get the direction that the sign is facing
                Sign sign = (Sign)lastBlock.getState().getData();
                BlockFace facing = sign.getFacing();
                BlockFace right = null;
            
                // Get the direction to the right
                switch (facing) {
                case EAST:
                	right = BlockFace.NORTH;
                	break;
                case NORTH:
                	right = BlockFace.WEST;
                	break;
                case SOUTH:
                	right = BlockFace.EAST;
                	break;
                case WEST:
                	right = BlockFace.SOUTH;
                	break;
                default:
                	break;

                }
                if (right == null) {
                	sender.sendMessage(ChatColor.RED + "Sign needs to face north, south, east or west");
                	return true; 
                }
            
                // Check if this panel already exists
                for (CourseListener panel : topTen) {
                	if (panel.getTopTenLocation().equals(lastBlock.getLocation())) {
                		panel.setDirection(right);
                		sender.sendMessage(ChatColor.RED + "ParkourTopTen display is already active");
                		return true;
                	}
                }
            
                CourseListener newTopTen = new CourseListener(plugin, lastBlock.getLocation(), right, arg3[1]);
                topTen.add(newTopTen);
                plugin.getServer().getPluginManager().registerEvents(newTopTen, plugin);
                sender.sendMessage(ChatColor.GREEN + "ParkourTopTen heads created for course " + ChatColor.AQUA + arg3[1]);
                return true;
            
            } else if (arg3[0].equalsIgnoreCase("remove")) {
            	if (arg3.length == 2 && arg3[1].equalsIgnoreCase("all")) {
            		if (topTen.size() == 0) {
            			sender.sendMessage(ChatColor.RED + "There are no active ParkourTopTen displays");
            			return true;
            		} else if (topTen.size() == 1) {
            			sender.sendMessage(ChatColor.GREEN + "Removing " + topTen.size() + " ParkourTopTen display");
            		} else {
            			sender.sendMessage(ChatColor.GREEN + "Removing " + topTen.size() + " ParkourTopTen displays");
            		}
            		for (CourseListener panel : topTen) {
            			// Clear each panel
            			panel.removeTopTen();
            		}
            		// Remove all from the list
            		topTen.removeAll(topTen);
            		return true;
            	}
            	if (arg3.length == 2 && arg3[1].equalsIgnoreCase("help")) {
            		sendHelp(player);
            		return true;
            	} 
            
            	// Look at what the player was looking at
            	BlockIterator iter = new BlockIterator(player, 10);
            	Block lastBlock = iter.next();
            	while (iter.hasNext()) {
            		lastBlock = iter.next();
            		if (lastBlock.getType() == Material.AIR)
            			continue;
            		break;
            	}
            	//plugin.getLogger().info("DEBUG: " + lastBlock);
            	if (lastBlock.getType() != Material.WALL_SIGN && lastBlock.getType() != Material.SIGN) {
            		sender.sendMessage(ChatColor.RED + "You must be looking at the #1 top ten sign");
            		return true;
            	}
            	// Check if this panel  exists
            	for (CourseListener panel : topTen) {
            		if (panel.getTopTenLocation().equals(lastBlock.getLocation())) {
            			// Clear
            			panel.removeTopTen();
            			// Remove from the list
            			topTen.remove(panel);
            			sender.sendMessage(ChatColor.GREEN + "ParkourTopTen display removed");
            			return true;
            		}
            	}
            	sender.sendMessage(ChatColor.RED + "Could not find a ParkourTopTen display in that position");
            	return true;
            } else {
            	sender.sendMessage(ChatColor.RED + "Not a valid ParkourTopTen command");
            	return true;
            }
        }
		return false;
    }
    
    public void sendHelp(Player player) {
    	//player.sendMessage(ChatColor.WHITE + " ==================");
    	player.sendMessage(ChatColor.WHITE + " === " + ChatColor.YELLOW + "ParkourTopTen Help " + ChatColor.WHITE + "===");
    	//player.sendMessage(ChatColor.WHITE + " ==================");
        player.sendMessage(ChatColor.AQUA + "To create a top ten display for a course:");
        player.sendMessage(ChatColor.AQUA + "  : build a row of signs");
        player.sendMessage(ChatColor.AQUA + "  : look at the left-most sign and type the create command");
        //player.sendMessage("");
        player.sendMessage(ChatColor.WHITE + "  /ptt create [course] " + ChatColor.AQUA + "- specifying a valid Parkour course");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "To remove a top ten display for a course:");
        player.sendMessage(ChatColor.AQUA + "  : look at the left-most sign and type the remove command");
        //player.sendMessage("");
        player.sendMessage(ChatColor.WHITE + "  /ptt remove " + ChatColor.AQUA + "- remove a ParkourTopTen display");
        player.sendMessage(ChatColor.WHITE + "  /ptt remove all " + ChatColor.AQUA + "- remove ALL ParkourTopTen displays"); 
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
