package io.github.steve4744.ParkourTopTen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;

public class VersionChecker {
	
	public static String getVersion(){
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=46268").openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("GET");
			String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			con.disconnect();
			if (version.length() <= 7) {
				return version;
			}
		} catch (Exception ex) {
			Bukkit.getLogger().info("[ParkourTopTen] Failed to check for update on Spigot.");
		}
		return "error";
	}

}
