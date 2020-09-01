package io.github.steve4744.parkourtopten;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import io.github.a5h73y.parkour.type.course.CourseInfo;

public class AutoTabCompleter implements TabCompleter {

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
				list.add("help");
				list.add("create");
				list.add("remove");
				list.add("reload");
			}
		} else if (args.length == 2 && player.hasPermission("parkourtopten.admin")) {
			if (args[0].equalsIgnoreCase("create")) {
				list.addAll(CourseInfo.getAllCourses());
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
