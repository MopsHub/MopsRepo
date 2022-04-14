package ml.mopspvps.commands;

import ml.mopspvps.Commands;
import ml.mopspvps.Dependencies;
import ml.mopspvps.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Locale;

/**
 * Обработчик комманд обычных игроков
 *
 * @author Kofiy
 */

public class PlayerEssentials extends Commands {
	@Override
	public boolean commandsExecutor(CommandSender sender, Command command, String label, String[] args) {
		Plugin plugin = Dependencies.getPlugin();
		Player player = (Player) sender;
		String commandName = command.getName().toLowerCase(Locale.ROOT);
		if (commandName.equals("pog")) {
			player.sendTitle(ChatColor.RED + "POGGERS", "", 1, 5, 20);
			return true;
		}
		if (commandName.equals("hub") || commandName.equals("lobby") || commandName.equals("l")) {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard board = manager.getMainScoreboard();
			Objective objective = board.getObjective("leave");
			Score score = objective.getScore(player);
			score.setScore(1);
			return true;
		}
		if (commandName.equals("kit")) {
			player.openInventory(Dependencies.getKits());
			return true;
		}
		if (commandName.equals("whoiam")) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);
				player.sendTitle(ChatColor.RED + "5", "", 0, 0, 20);
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.4F);
					player.sendTitle(ChatColor.GOLD + "4", "", 0, 0, 20);
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.8F);
						player.sendTitle(ChatColor.YELLOW + "3", "", 0, 0, 20);
						Bukkit.getScheduler().runTaskLater(plugin, () -> {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.2F);
							player.sendTitle(ChatColor.GREEN + "2", "", 0, 0, 20);
							Bukkit.getScheduler().runTaskLater(plugin, () -> {
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.6F);
								player.sendTitle(ChatColor.AQUA + "1", "", 0, 0, 20);
								Bukkit.getScheduler().runTaskLater(plugin, () -> {
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2F);
									player.sendTitle(ChatColor.BLUE + "YOU ARE:", ChatColor.BLUE + "АБОБИЙ", 0, 0, 60);
								}, 20L);
							}, 20L);
						}, 20L);
					}, 20L);
				}, 20L);
			}, 20L);
		}
		if (commandName.equals("calc")) {
			double number1 = Double.parseDouble(args[0]);
			double number2 = Double.parseDouble(args[2]);
			String usage = args[1];
			double result = 0;
			if (usage.equals("+")) {
				result = number1 + number2;
			}
			if (usage.equals("-")) {
				result = number1 - number2;
			}
			if (usage.equals("*")) {
				result = number1 * number2;
			}
			if (usage.equals("/")) {
				result = number1 / number2;
			}
			player.sendMessage("Результат: " + result + "");
			return true;
		}
		return false;
	}
}
