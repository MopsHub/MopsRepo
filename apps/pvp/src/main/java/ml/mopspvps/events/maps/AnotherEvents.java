package ml.mopspvps.events.maps;

import ml.mopspvps.Dependencies;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class AnotherEvents {
	public void checkEffects(Player player) {
		// если игроку не разрешен флай, но он не движется и снизу него воздух - то его кикает (еще если он за бедроком_
		if (!player.getAllowFlight() && player.getVelocity().isNormalized() && player.getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
			TextComponent kickMessage = Component.text("вы были подозрены в абобе").color(NamedTextColor.AQUA);
			player.kick(kickMessage);
		}
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		Objective objective = board.getObjective("leave");
		Score score = objective != null ? objective.getScore(player) : null;
		if ((score != null ? score.getScore() : 0) == 1) {
			player.setAbsorptionAmount(0);
			player.setAbsorptionAmount(0);
			player.setAbsorptionAmount(0);

			player.setFreezeTicks(0);
			player.setFreezeTicks(0);
			player.setFreezeTicks(0);
			for (Player idiot : Bukkit.getOnlinePlayers()) {
				idiot.showPlayer(Dependencies.getPlugin(), player);
			}
		}
		if (player.getScoreboardTags().contains("snow")) {
			Objective objective2 = board.getObjective("cold");
			Score score2 = objective2 != null ? objective2.getScore(player) : null;
			if ((score2 != null ? score2.getScore() : 0) == 0) {
				player.setFreezeTicks(140);
			}
		}
	}
}
