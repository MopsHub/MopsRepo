package ml.woolbattle;

import org.bukkit.Color;

public enum Teams {
	RED(Color.fromRGB(255, 10, 10), "&4"),
	YELLOW(Color.fromRGB(255, 207, 36), "&e"),
	GREEN(Color.fromRGB(105, 255, 82), "&a"),
	BLUE(Color.fromRGB(47, 247, 227), "&b"),
	SPECTATOR(Color.fromRGB(170, 170, 170), "&7"); //на будущие

	final String getColorString;
	final Color getLeatherColor;

	Teams (Color getLeatherColor, String getColorString) {
		this.getColorString = getColorString;
		this.getLeatherColor = getLeatherColor;
	}
}
