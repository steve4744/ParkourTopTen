package io.github.steve4744.parkourtopten;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

public class SignHandler {

	private final ParkourTopTen plugin;

	public SignHandler(ParkourTopTen plugin) {
		this.plugin = plugin;
	}

	public boolean isValidSign(Block block) {
		return Tag.ALL_SIGNS.isTagged(block.getType());
	}

	public boolean isSameSignType(Block block, Material signType) {
		if (plugin.getConfig().getBoolean("enforceSameSignType") && block.getType() != signType) {
			return false;
		}
		return true;
	}

	public void resetSign(Block block) {
		Sign sign = (Sign)block.getState();
		sign.getSide(Side.FRONT).setLine(0, "");
		sign.getSide(Side.FRONT).setLine(1, "");
		sign.getSide(Side.FRONT).setLine(2, "");
		sign.getSide(Side.FRONT).setLine(3, "");
		sign.update();
	}

	public void updateSign(Block block, int index, String playerName, String time, String courseName) {
		Sign sign = (Sign)block.getState();
		sign.getSide(Side.FRONT).setLine(0, Util.colourText(plugin.getConfig().getString("signs.line0", "#")) + index);
		sign.getSide(Side.FRONT).setLine(1, Util.colourText(plugin.getConfig().getString("signs.line1", "")) + playerName);
		sign.getSide(Side.FRONT).setLine(2, Util.colourText(plugin.getConfig().getString("signs.line2", "")) + time);
		sign.getSide(Side.FRONT).setLine(3, Util.colourText(plugin.getConfig().getString("signs.line3", "")) + courseName);
		sign.update();
	}
}
