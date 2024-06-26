package io.github.steve4744.parkourtopten;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import io.github.a5h73y.parkour.Parkour;

public class AutoTabCompleter implements TabCompleter {

	private static final List<String> ADMIN_COMMANDS = Arrays.asList(
			"help", "create", "remove", "reload");

	private static final List<String> CREATE_OPTIONS = Arrays.asList(
			"above", "behind");

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		Player player = (Player) sender;

		List<String> list = new ArrayList<String>();
		List<String> auto = new ArrayList<String>();

		if (args.length == 1) {
			list.add("info");
			if (player.hasPermission("parkourtopten.admin")) {
				list.addAll(ADMIN_COMMANDS);
			}
		} else if (args.length == 2 && player.hasPermission("parkourtopten.admin")) {
			if (args[0].equalsIgnoreCase("create")) {
				list.addAll(Parkour.getInstance().getCourseManager().getCourseNames());
			} else if (args[0].equalsIgnoreCase("remove")) {
				list.add("all");
			}
		} else if (args.length == 3 && player.hasPermission("parkourtopten.admin")) {
			if (args[0].equalsIgnoreCase("create")) {
				list.addAll(CREATE_OPTIONS);
			}
		}
		for (String s : list) {
			if (s.startsWith(args[args.length - 1])) {
				auto.add(s);
			}
		}
		return auto.isEmpty() ? list : auto;	
	}

}
