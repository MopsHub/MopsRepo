package ml.mopspvps.scoreboards;

import ml.mopspvps.Dependencies;
import ml.mopspvps.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

public class LobbyScoreboards {
	public Scoreboard generateLobbyScoreboard(Player player) {
		ScoreboardManager manager = Dependencies.getManager();
		Scoreboard board = manager.getNewScoreboard();
		Scoreboard board2 = Objects.requireNonNull(manager).getMainScoreboard();
		Objective obj2 = board2.getObjective("coins");
		Score coins = Objects.requireNonNull(obj2).getScore(player);
		boolean noRank = false;

		String rank = Dependencies.getMopsRank(player);
		if (rank == null) {
			rank = ChatColor.GRAY + "";
		}

		try {
			if (board.getPlayerTeam(player) == null) {
				try {
					if (Dependencies.getMopsRank(player) == null) {
						Dependencies.putMopsRank(player, ChatColor.GRAY + "");
						noRank = true;
						rank = ChatColor.GRAY + "Никакой";
					}
					else {
						noRank = false;
						rank = Dependencies.getMopsRank(player);
					}
				}
				catch (Exception e) {
					Dependencies.getLog().warning("<=СОЗДАНИЕ СКОРБОАРДА=> LobbyScoreboards1 - ОШИБКА: " + ChatColor.RED + e.getMessage());
					noRank = true;
					rank = ChatColor.GRAY + "Никакой";
				}

			} else {
				Dependencies.getEvents().setRank(player);
				try {
					rank = Dependencies.getMopsRank(player);
					noRank = false;
				} catch (Exception e) {
					Dependencies.getLog().warning("<=СОЗДАНИЕ СКОРБОАРДА=> LobbyScoreboards3 - ОШИБКА: " + ChatColor.RED + e.getMessage());
					noRank = true;
					rank = ChatColor.GRAY + "Никакой";
				}
			}
		} catch (Exception e) {
			Dependencies.getLog().warning("<=СОЗДАНИЕ СКОРБОАРДА=> LobbyScoreboards0 - ОШИБКА: " + ChatColor.RED + e.getMessage());
			noRank = true;
			rank = ChatColor.GRAY + "Никакой";
		}



		try {
			if (rank.length() > 27) {
				rank = rank.substring(0, 27);
			}
		}
		catch (Exception e) {
			Dependencies.getLog().warning("<=СОЗДАНИЕ СКОРБОАРДА=> LobbyScoreboards2 - ОШИБКА: " + ChatColor.RED + e.getMessage());
			if (noRank) {
				rank = ChatColor.GRAY + "Никакой";
			} else {
				rank = ChatColor.GOLD + "ТЕХ. ШОКОЛАДКИ";
			}
		}

		TextComponent scoreTitle = Component.text("MopsPVPs", NamedTextColor.YELLOW, TextDecoration.BOLD);
		TextComponent scoreWelcome = Component.text("Добро пожаловать!", NamedTextColor.GRAY);
		TextComponent scoreSpace = Component.space().color(NamedTextColor.GRAY);
		TextComponent scoreCoins = Component.text("Твои коины:", NamedTextColor.WHITE).append(Component.space()).append(Component.text(coins.getScore(), NamedTextColor.GOLD, TextDecoration.BOLD));
		TextComponent scoreTokens = Component.text("Твои жетоны:", NamedTextColor.WHITE).append(Component.space()).append(Component.text(coins.getScore(), NamedTextColor.GOLD, TextDecoration.BOLD));
		TextComponent scoreRank = Component.text("Твой ранг:", NamedTextColor.WHITE).append(Component.space()).append(legacyAmpersand().deserialize(rank));
		TextComponent scoreWhere = Component.text("Вы в", NamedTextColor.WHITE).append(Component.text("Лобби", NamedTextColor.GRAY));


//		Scoreboard board4 = manager.getMainScoreboard();
//		Objective obj4 = board4.getObjective("mopstokens");
//		Score mopstoken = Objects.requireNonNull(obj4).getScore(player);

		Objective obj = board.registerNewObjective("MopsPVPs", "dummy", scoreTitle);
		Utils.addScore(obj, 10, scoreSpace);
		Utils.addScore(obj, 9, scoreWelcome);
		Utils.addScore(obj, 7, scoreCoins);
		Utils.addScore(obj, 6, scoreTokens);
		Utils.addScore(obj, 5, scoreRank);
		Utils.addScore(obj, 4, scoreSpace);
		Utils.addScore(obj, 3, scoreWhere);

//		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
//		Score score0 = obj.getScore(" ");
//		score0.setScore(10);
//		Score score = obj.getScore(ChatColor.GRAY + "Добро пожаловать!");
//		score.setScore(9);
//		Score score2 = obj.getScore(ChatColor.GRAY + " ");
//		score2.setScore(8);
//		Score score3 = obj.getScore(ChatColor.WHITE + "Твои коины: " + ChatColor.GOLD + ChatColor.BOLD + coins.getScore());
//		score3.setScore(7);
//		Score score4 = obj.getScore(ChatColor.WHITE + "Твои жетоны: " + ChatColor.GOLD + ChatColor.BOLD + mopstoken.getScore());
//		score4.setScore(6);
//		Score score5 = obj.getScore(ChatColor.WHITE + "Твой ранг: " + rank);
//		score5.setScore(5);
//		Score score6 = obj.getScore(" ");
//		score6.setScore(4);
//		Score score7 = obj.getScore(ChatColor.WHITE + "Вы в " + ChatColor.GRAY + "Лобби");
//		score7.setScore(3);
		try {
			Dependencies.getEvents().setRank(player);
		} catch (Exception e) {
			Dependencies.getLog().warning("<=РАСТАВЛЕНИЕ РАНГА=> вызов из LobbyScoreboards - ОШИБКА: " + ChatColor.RED + e.getMessage());
		}

		return board;
	}
}
