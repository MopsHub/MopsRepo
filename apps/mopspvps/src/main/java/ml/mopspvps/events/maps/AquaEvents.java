package ml.mopspvps.events.maps;

import ml.mopspvps.Dependencies;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class AquaEvents {
	//    TODO: ивенты для аквы
	public void checkEffects(Player player, Dependencies dependencies) {
		//???: чё за код это
		//кот: это код который отвечает за абилки на акве, снизу код с джамп падами
		Location loc = player.getLocation();
		Block block = loc.add(0, -1, 0).getBlock();
		Block blocky = loc.add(0, 1, 0).getBlock();
		if (block.getType() == Material.SLIME_BLOCK) {
			Location eyeloc = player.getEyeLocation();
			@NotNull Vector vec = eyeloc.getDirection();
			player.setVelocity((vec).multiply(2.2));
		}
		//???: проверка на тип блоков?
		//кот: а тут код с найт виженом в воде на акве
		if (blocky.getType() == Material.WATER) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 7, 255, true, false, false));
		}
	}
}
