package io.github.steve4744.parkourtopten;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

public class VersionChecker {

	private ParkourTopTen plugin;
	private int resourceId;

	public VersionChecker(ParkourTopTen plugin, int resourceId) {
		this.plugin = plugin;
		this.resourceId = resourceId;
	}

	public void getVersion(final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
			try (InputStream is = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~").toURL().openStream();
					Scanner scann = new Scanner(is)) {
				if (scann.hasNext()) {
					consumer.accept(scann.next());
				}
			} catch (IOException | URISyntaxException e) {
				plugin.getLogger().info("Unable to check for update: " + e.getMessage());
			}
		}, 50L);
	}

}
