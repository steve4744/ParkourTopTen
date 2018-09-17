package io.github.steve4744.ParkourTopTen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AutoTabCompleter implements TabCompleter {
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		Player player = (Player) sender;
		
		List<String> list = new ArrayList<String>();
		List<String> auto = new ArrayList<String>();
		
		if (args.length == 1 && player.hasPermission("parkourtopten.admin")) {
			list.add("help");
			list.add("create");
			list.add("remove");
		}
		for (String s : list) {
			if (s.startsWith(args[args.length - 1])) {
				auto.add(s);
			}
		}
		return auto.isEmpty() ? list : auto;	
	}

}
