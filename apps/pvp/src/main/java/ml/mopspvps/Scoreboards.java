package ml.mopspvps;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Set;

public class Scoreboards {
//    TODO: Доделать менеджер скорбоардов
	/**
	 * @param player Игрок, который использовал абилку (нажал ПКМ)
	 * @see ScoreboardManager
	 */
	public void createScoreboard(Player player) {
		ScoreboardManager manager = Dependencies.getManager();
		Scoreboard board = Dependencies.getBoard();
		Set<String> tags = player.getScoreboardTags();

		if(tags.contains("desert") || tags.contains("plains") || tags.contains("water") || tags.contains("forest")) {
			try {
				Dependencies.getLog().info("<=СОЗДАНИЕ СКОРБОАРДА=>");
				player.setScoreboard(Dependencies.getBattleScoreboards().generateBattleScoreboard(player));
			} catch (Exception e) {
				Dependencies.getLog().info("<=СОЗДАНИЕ СКОРБОАРДА=> ОШИБКА В generateBattleScoreboard:");
				Dependencies.getLog().warning(e.getMessage());
			}
		} else if (tags.contains("city")){
			try {
				player.setScoreboard(Dependencies.getCityScoreboards().generateCityScoreboard(player));
			} catch (Exception | Error e) {
				Dependencies.getLog().info("<=СОЗДАНИЕ СКОРБОАРДА=> ОШИБКА В generateCityScoreboard:");
				Dependencies.getLog().warning(e.getMessage());
			}
		}
		if(!tags.contains("desert") || !tags.contains("plains") || !tags.contains("water") || !tags.contains("forest") || !tags.contains("city") || !tags.contains("snow")) {
			try {
				player.setScoreboard(Dependencies.getLobbyScoreboards().generateLobbyScoreboard(player));
			} catch (Exception e) {
				Dependencies.getLog().info("<=СОЗДАНИЕ СКОРБОАРДА=> ОШИБКА В generateLobbyScoreboard:");
				Dependencies.getLog().warning(e.getMessage());
			}
		}
	}
}
