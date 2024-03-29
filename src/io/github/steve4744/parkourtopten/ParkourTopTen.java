package io.github.steve4744.parkourtopten;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.steve4744.parkourtopten.metrics.Metrics;

public class ParkourTopTen extends JavaPlugin {
	private ParkourTopTenCommand commandListener;
	private String version;
	private BlockHandler blockHandler;
	private SignHandler signHandler;
	private static final int BSTATS_PLUGIN_ID = 2147;

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();

		Plugin pkr = pm.getPlugin("Parkour");
		if (pkr == null) {
			getLogger().severe("Parkour not loaded. Disabling plugin");
			pm.disablePlugin(this);

		} else {
			getLogger().info("Found Parkour version " + pkr.getDescription().getVersion());

			saveDefaultConfig();
			getConfig().options().copyDefaults(true);
			saveConfig();

			version = this.getDescription().getVersion();

			commandListener = new ParkourTopTenCommand(this, version);
			getCommand("parkourtopten").setExecutor(commandListener);
			getCommand("parkourtopten").setTabCompleter(new AutoTabCompleter());

			blockHandler = new BlockHandler(this);
			signHandler = new SignHandler(this);
			checkForUpdate();
			new Metrics(this, BSTATS_PLUGIN_ID);

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
	}

	private void reload() {
		// Load any existing panels
		List<String> serialize = getConfig().getStringList("panels");
		for (String panel : serialize) {
			try {
				String direction = panel.substring(panel.lastIndexOf(':')+1);
				BlockFace dir = BlockFace.valueOf(direction);

				String location = panel.substring(panel.indexOf(':')+1, panel.lastIndexOf(':'));
				Location loc = Util.getLocationString(location);
				if (loc == null) {
					getLogger().severe("The location of the top ten panel does not exist. Maybe a world was deleted?");
					continue;
				}

				String course = panel.substring(0, panel.indexOf(':'));

				if (isDebug()) {
					getLogger().info("DEBUG: [pTT] new top ten panel at " + loc + " heading " + dir + " for " + course);
				}

				CourseListener newTopTen = new CourseListener(this, loc, dir, course);

				commandListener.addTopTen(newTopTen);
				getServer().getPluginManager().registerEvents(newTopTen, this);

			} catch(Exception e) {
				getLogger().severe("Problem loading panel " + panel + " skipping...");
				e.printStackTrace();
			}
		}
		getLogger().info("Loaded " + commandListener.getTopTen().size() + " top ten displays");
	}

	public void saveDisplays() {
		if (isDebug()) {
			getLogger().info("DEBUG: [pTT] displays to save: " + commandListener.getTopTen().size());
		}
		List<String> serialize = new ArrayList<>();
		for (CourseListener panel : commandListener.getTopTen()) {
			serialize.add(panel.getCourseName() + ":" + Util.getStringLocation(panel.getTopTenLocation()) + ":" + panel.getDirection().toString());
		}
		if (isDebug()) {
			getLogger().info("DEBUG: [pTT] panels serialized: " + serialize.size());
		}
		getConfig().set("panels", serialize);
		saveConfig();
	}

	public BlockHandler getBlockHandler() {
		return blockHandler;
	}

	public SignHandler getSignHandler() {
		return signHandler;
	}

	public boolean isDebug() {
		return getConfig().getBoolean("debug");
	}

	private void checkForUpdate() {
		if(!getConfig().getBoolean("checkForUpdate", true)) {
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
