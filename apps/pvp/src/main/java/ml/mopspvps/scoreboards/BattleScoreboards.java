package ml.mopspvps.scoreboards;

import ml.mopspvps.Dependencies;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;
import java.util.Set;

/**
 * Класс для скорбордов, которые используются при ПВП
 */

public class BattleScoreboards {

	/**
	 * @param player Основной игрок
	 */
	public Scoreboard generateBattleScoreboard(Player player) {
		Set<String> tags = player.getScoreboardTags();

		Player enemy = null;

		if (tags.contains("desert1")) {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("desert2")) {
					enemy = online;
				}
			}
		} else {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("desert1")) {
					enemy = online;
				}
			}
		}
		if (tags.contains("plains1")) {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("plains2")) {
					enemy = online;
				}
			}
		} else {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("plains1")) {
					enemy = online;
				}
			}
		}
		if (tags.contains("water1")) {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("water2")) {
					enemy = online;
				}
			}
		} else {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("water1")) {
					enemy = online;
				}
			}
		}
		if (tags.contains("forest1")) {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("forest2")) {
					enemy = online;
				}
			}
		} else {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getScoreboardTags().contains("forest1")) {
					enemy = online;
				}
			}
		}

		String map = "";

		if (tags.contains("desert")) {
			map = ChatColor.YELLOW + "Пустыня";
		}
		if (tags.contains("plains")) {
			map = ChatColor.GREEN + "Равнины";
		}
		if (tags.contains("water")) {
			map = ChatColor.AQUA + "Аква";
		}
		if (tags.contains("forest")) {
			map = ChatColor.DARK_GREEN + "Тайга";
		}

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		assert manager != null;
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.getObjective(ChatColor.YELLOW + "" + ChatColor.BOLD + "MopsPVPs" + ChatColor.RED + " ");
		Objects.requireNonNull(obj).setDisplaySlot(DisplaySlot.SIDEBAR);

		int minutes = 0;
		int seconds = 0;

		Score score1 = obj.getScore(ChatColor.GRAY + "Время: " + minutes + "m" + seconds + "s");
		score1.setScore(10);

		Score score2 = obj.getScore(ChatColor.RED + " ");
		score2.setScore(9);
		var enemyRank = Dependencies.getMopsRank(enemy);
		if (enemyRank == null) {
			enemyRank = ChatColor.GRAY + "";
		}
		var enemyName = Dependencies.getMopsName(enemy);

		String c = ChatColor.WHITE + "Ваш противник: ";
		String d = ChatColor.WHITE + "Противник: ";
		String e = ChatColor.WHITE + "Ваш враг: ";
		String f = ChatColor.WHITE + "Враг: ";
		String g = " " + ChatColor.RED + (int) Objects.requireNonNull(enemy).getHealth() + "❤";
		Score score3 = obj.getScore("Ваш противник: " + ChatColor.DARK_AQUA + "Одинокий Голубь");
		if ((c + enemyRank + enemyName).length() < 35) {
			score3 = obj.getScore(c + g);
		} else if ((d + enemyRank + enemyName).length() < 35) {
			score3 = obj.getScore(d + g);
		} else if ((e + enemyRank + enemyName).length() < 35) {
			score3 = obj.getScore(e + g);
		} else if ((f + enemyRank + enemyName).length() < 35) {
			score3 = obj.getScore(f + g);
		}
		score3.setScore(8);
		Score score4 = obj.getScore(ChatColor.GOLD + " ");
		score4.setScore(7);

//		String turtletime = ChatColor.DARK_PURPLE + "Никогда";
//		if (1 == 2) {
//			//иф = если черепашкотаймер между 0 и 5 секундами
//			turtletime = ChatColor.GREEN + "Только что!!!";
//		}
//		if (1 == 2) {
//			//иф = если черепашкотаймер между 5 и 80 секундами
//			turtletime = ChatColor.RED + "Долго...";
//		}
//		if (1 == 2) {
//			//иф = если черепашкотаймер между 80 и 130 секундами
//			turtletime = ChatColor.GOLD + "Не скоро";
//		}
//		if (1 == 2) {
//			//иф = если черепашкотаймер между 130 и 150 (0) секундами
//			turtletime = ChatColor.AQUA + "Скоро!";
//		}

		int damagetaken = 0;

		Score score5 = obj.getScore(ChatColor.WHITE + "Дамаг: " + damagetaken);
		score5.setScore(6);
//		Score score6 = obj.getScore(ChatColor.WHITE + "До черепашки: " + turtletime);
//		score6.setScore(5);
		Score score7 = obj.getScore(ChatColor.YELLOW + " ");
		score7.setScore(4);
		Score score8 = obj.getScore(ChatColor.WHITE + "Вы в " + map + "| Бой");
		score8.setScore(3);
		return board;
	}
}
