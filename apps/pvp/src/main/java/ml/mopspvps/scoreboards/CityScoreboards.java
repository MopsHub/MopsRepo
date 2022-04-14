package ml.mopspvps.scoreboards;

import ml.mopspvps.Dependencies;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class CityScoreboards {
	public Scoreboard generateCityScoreboard(Player player) {
		ScoreboardManager manager = Dependencies.getManager();
		Scoreboard board = Objects.requireNonNull(manager).getNewScoreboard();

		Scoreboard board2 = manager.getMainScoreboard();
		Objective obj2 = board2.getObjective("coins");
		Score coins = Objects.requireNonNull(obj2).getScore(player);

		Scoreboard board3 = manager.getMainScoreboard();
		Objective obj3 = board3.getObjective("cityticks");
		Score ticks = Objects.requireNonNull(obj3).getScore(Objects.requireNonNull(Bukkit.getPlayer("SirCat07")));

		Scoreboard board4 = manager.getMainScoreboard();
		Objective obj4 = board4.getObjective("mopsorisdone");
		Score mopsorisdone = Objects.requireNonNull(obj4).getScore(Objects.requireNonNull(Bukkit.getPlayer("SirCat07")));

		Scoreboard board5 = manager.getMainScoreboard();
		Objective obj5 = board5.getObjective("minutes");
		Score minutes = Objects.requireNonNull(obj5).getScore(Objects.requireNonNull(Bukkit.getPlayer("SirCat07")));

		Scoreboard board6 = manager.getMainScoreboard();
		Objective obj6 = board6.getObjective("seconds");
		Score seconds = Objects.requireNonNull(obj6).getScore(Objects.requireNonNull(Bukkit.getPlayer("SirCat07")));

		Scoreboard board7 = manager.getMainScoreboard();
		Objective obj7 = board7.getObjective("mopstokens");
		Score mopstoken = Objects.requireNonNull(obj7).getScore(player);

		if (ticks.getScore() == 20) {
			seconds.setScore(seconds.getScore() + 1);
			ticks.setScore(0);
		}
		if (seconds.getScore() == 60) {
			minutes.setScore(minutes.getScore() + 1);
			seconds.setScore(0);
		}
		if (mopsorisdone.getScore() == 1) {
			seconds.setScore(0);
			minutes.setScore(0);
		}


		Objective obj = board.getObjective(ChatColor.YELLOW + "" + ChatColor.BOLD + "MopsPVPs" + ChatColor.GREEN + " ");
		Objects.requireNonNull(obj).setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score0 = obj.getScore(ChatColor.WHITE + "Время: " + ChatColor.GRAY + minutes.getScore() + "m" + seconds.getScore() + "s");
		score0.setScore(10);
		Score score2 = obj.getScore(ChatColor.GRAY + " ");
		score2.setScore(9);
		Score score3 = obj.getScore(ChatColor.GRAY + "Твои коины: " + ChatColor.GOLD + ChatColor.BOLD + coins.getScore());
		score3.setScore(8);
		Score score4 = obj.getScore(ChatColor.GRAY + "Твои жетоны: " + ChatColor.GOLD + ChatColor.BOLD + mopstoken.getScore());
		score4.setScore(7);
		Score score5 = obj.getScore(ChatColor.YELLOW + " ");
		score5.setScore(6);
		Score score6 = obj.getScore(ChatColor.WHITE + "Ваша команда:");
		score6.setScore(5);
		Score score7 = obj.getScore(ChatColor.GRAY + "Нет тиммейта" + ChatColor.RED + " ");
		score7.setScore(4);
		Score score8 = obj.getScore(ChatColor.GRAY + "Нет тиммейта" + ChatColor.YELLOW + " ");
		score8.setScore(3);
		Score score9 = obj.getScore(ChatColor.GRAY + "Нет тиммейта" + ChatColor.GREEN + " ");
		score9.setScore(2);
		Score score10 = obj.getScore(ChatColor.WHITE + " ");
		score10.setScore(1);
		Score score11 = obj.getScore(ChatColor.WHITE + "Вы в " + ChatColor.GOLD + "Бое С Боссом | Город");
		score11.setScore(0);
		player.setScoreboard(board);

		for (Player player1 : Bukkit.getOnlinePlayers()) {
			if (player1.getScoreboardTags().contains("city1")) {
				obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.RED + " ");
				if (player1.getScoreboardTags().contains("citydead")) {
					obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.RED + " ");
					String str = Dependencies.getMopsName(player1) + ChatColor.RED + " УМЕР";
					if (str.length() > 39) {
						str = str.substring(0, 39);
					}
					score7 = obj.getScore(str);
					score7.setScore(4);
				} else {
					obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.RED + " ");
					String str = Dependencies.getMopsName(player1) + ChatColor.RED + " " + (int) player1.getHealth() + "❤";
					if (str.length() > 39) {
						str = str.substring(0, 39);
					}
					score7 = obj.getScore(str);
					score7.setScore(4);
				}
			} else {
				score7 = obj.getScore(ChatColor.GRAY + "Нет тиммейта" + ChatColor.RED + " ");
				score7.setScore(4);
			}

			if (player1.getScoreboardTags().contains("city2")) {
				obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.YELLOW + " ");
				if (player1.getScoreboardTags().contains("citydead")) {
					obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.YELLOW + " ");
					String str = Dependencies.getMopsName(player1) + ChatColor.RED + " УМЕР";
					if (str.length() > 39) {
						str = str.substring(0, 39);
					}
					score8 = obj.getScore(str);
					score8.setScore(3);
				} else {
					obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.YELLOW + " ");
					String str = Dependencies.getMopsName(player1) + ChatColor.RED + " " + (int) player1.getHealth() + "❤";
					if (str.length() > 39) {
						str = str.substring(0, 39);
					}
					score8 = obj.getScore(str);
					score8.setScore(3);
				}
			} else {
				score8 = obj.getScore(ChatColor.GRAY + "Нет тиммейта" + ChatColor.GREEN + " ");
				score8.setScore(3);
			}

			if (player1.getScoreboardTags().contains("city5")) {
				obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.GREEN + " ");
				if (player1.getScoreboardTags().contains("citydead")) {
					String str = Dependencies.getMopsName(player1) + ChatColor.RED + " УМЕР";
					if (str.length() > 39) {
						str = str.substring(0, 39);
					}
					score9 = obj.getScore(str);
					score9.setScore(2);
				} else {
					obj.getScoreboard().resetScores(ChatColor.GRAY + "Нет тиммейта" + ChatColor.GREEN + " ");
					String str = Dependencies.getMopsName(player1) + ChatColor.RED + " " + (int) player1.getHealth() + ":heart:";
					if (str.length() > 39) {
						str = str.substring(0, 39);
					}
					score9 = obj.getScore(str);
					score9.setScore(2);
				}
			}
		}

		return board;
	}
}
