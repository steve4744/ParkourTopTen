package io.github.steve4744.parkourtopten;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

public class SignHandler {

	private final ParkourTopTen plugin;

	public SignHandler(ParkourTopTen plugin) {
		this.plugin = plugin;
	}

	public boolean isValidSign(Block block) {
		return block.getBlockData() instanceof WallSign || block.getBlockData() instanceof org.bukkit.block.data.type.Sign;
	}

	public void resetSign(Block block) {
		Sign sign = (Sign)block.getState();
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}

	public void updateSign(Block block, int index, String playerName, String time, String courseName) {
		Sign sign = (Sign)block.getState();
		sign.setLine(0, Util.colourText(plugin.getConfig().getString("signs.line0", "#")) + index);
		sign.setLine(1, Util.colourText(plugin.getConfig().getString("signs.line1", "")) + playerName);
		sign.setLine(2, Util.colourText(plugin.getConfig().getString("signs.line2", "")) + time);
		sign.setLine(3, Util.colourText(plugin.getConfig().getString("signs.line3", "")) + courseName);
		sign.update();
	}
}
