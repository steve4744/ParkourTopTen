package io.github.steve4744.ParkourTopTen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.steve4744.ParkourTopTen.Metrics.Metrics;


public class ParkourTopTen extends JavaPlugin {
    private ParkourTopTenCommand commandListener;
    private String version;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        Plugin pkr = pm.getPlugin("Parkour");
        if (pkr == null) {
            getLogger().severe("Parkour not loaded. Disabling plugin");
            pm.disablePlugin(this);
            
        } else {
            getLogger().info("Found Parkour version " + pkr.getDescription().getVersion());
            // Load config
            saveDefaultConfig();
            
            // Register command
            commandListener = new ParkourTopTenCommand(this);
            getCommand("parkourtopten").setExecutor(commandListener);
            
            version = this.getDescription().getVersion();
            
            // check for new version
            checkForUpdate();
            
            // Metrics
            @SuppressWarnings("unused")
			Metrics metrics = new Metrics(this);
            
            // Load from config
            new BukkitRunnable() {
                @Override
            	public void run() {
                    reload();	
                }
            }.runTaskLater(this, 20L);
        }
    }

    @Override
    public void onDisable() {
        if (commandListener == null) {
            return;
        }
        // Save any top ten lists
        List<CourseListener> topTen = commandListener.getTopTen();
        List<String> serialize = new ArrayList<String>();
        for (CourseListener panel : topTen) {
            //getLogger().info("DEBUG: serializing");
            serialize.add(panel.getCourseName() + ":" + Util.getStringLocation(panel.getTopTenLocation()) + ":" + panel.getDirection().toString());
        }
        getConfig().set("panels", serialize);
        saveConfig();
    }

    public void reload() {
        // Load any existing panels
        List<String> serialize = getConfig().getStringList("panels");
        for (String panel : serialize) {
            try {
                String direction = panel.substring(panel.lastIndexOf(':')+1);
                //getLogger().info("DEBUG: direction = " + direction);
                BlockFace dir = BlockFace.valueOf(direction);
                
                String location = panel.substring(panel.indexOf(':')+1, panel.lastIndexOf(':'));
                //getLogger().info("DEBUG: location = " + location);
                Location loc = Util.getLocationString(location);
                //getLogger().info("DEBUG: loc = " + loc);
                
                String course = panel.substring(0, panel.indexOf(':'));
                //getLogger().info("DEBUG: course = " + course);
                
                CourseListener newTopTen = new CourseListener(this, loc, dir, course);
            
                commandListener.addTopTen(newTopTen);
                getServer().getPluginManager().registerEvents(newTopTen, this);
                //getLogger().info("DEBUG: new topten panel at " + loc + " heading " + dir + " for " + course);
                
            } catch(Exception e) {
                getLogger().severe("Problem loading panel " + panel + " skipping...");
                e.printStackTrace();
            }
        }
    }
    
	public void checkForUpdate() {
		if(!getConfig().getBoolean("Check_For_Update", true)){
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				String latestVersion = VersionChecker.getVersion();
				if (latestVersion == "error") {
					getLogger().info("Error attempting to check for new version. Please report it here: https://www.spigotmc.org/threads/parkour-top-ten.268403/");
				} else {
					if (!version.equals(latestVersion)) {
						getLogger().info("New version " + latestVersion + " available on Spigot: https://www.spigotmc.org/resources/parkour-top-ten.46268/");
					}
				}
			}
		}.runTaskLaterAsynchronously(this, 50L);
	}

}
