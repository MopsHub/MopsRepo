package ml.mopspvps;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ml.mopsutils.Resources;
import ml.mopsbase.MopsPlugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.WebSocket.Listener;
import java.sql.Timestamp;
import java.util.logging.Logger;

/**
 * Основа плагина. Держится на соплях по сей день
 *
 * @author InFTord, SirCat07, Kofiy
 * @see org.bukkit.plugin.java.JavaPlugin
 */

public class Plugin extends MopsPlugin implements Listener, CommandExecutor {
	static Dependencies dependencies = null;
	static Events events = null;

	final TextComponent restartMessage = Component.text("Сервер был перезагружен!").color(NamedTextColor.GREEN);

	@Override
	public void onEnable() {
		Logger logger = getLogger();
		Timestamp enableTimeStamp = new Timestamp(System.currentTimeMillis());
		Bukkit.broadcast(restartMessage);

		this.saveDefaultConfig();
		this.config = this.getConfig();

		logger.info("config: \n" + config.saveToString() );
		logger.info("default config: \n" + ((FileConfiguration) config.getDefaults()).saveToString());

		Plugin.dependencies = new Dependencies(Plugin.this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				Dependencies.getAnotherEvents().checkEffects(player);
			}
		}, 1L, 1L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				// создает для всех игроков скоарборд
				if (player != null) {
					Dependencies.getScoreboards().createScoreboard(player);
				}
			}
		}, 1L, 60L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				// проверка на то - умер ли игрок на карте "Город" - и отправляет ему тайтл
				if (player.getScoreboardTags().contains("citydead")) {
					Dependencies.getCityEvents().deathMessage(player);
				}
				if (player.getScoreboardTags().contains("water")) {
					Dependencies.getAquaEvents().checkEffects(player, dependencies);
				}
			}
		}, 1L, 1L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getServer().getOnlinePlayers())
				if (player.getScoreboardTags().contains("city")) {
					Dependencies.getCityEvents().checkEffects(player);
				}
		}, 1L, 40L);
		Plugin.events = Dependencies.getEvents();
		Dependencies.setPluginEnabledTimestamp(enableTimeStamp);
		Bukkit.getServer().getPluginManager().registerEvents(events, this);

		// проверка онлайн игроков, выдаётся ранг по тиме
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player != null) {
				events.setRank(player);
			}
		}
	}

	/**
	 * Выполнение команд
	 *
	 * @param sender  Игрок, который отправил команду.
	 * @param command Сама команда
	 * @param label   Строка (точно не знаю что это
	 * @param args    Аргументы
	 * @return True при успешном выполнении команды и False при неуспешном
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		return Dependencies.getCommands().commandsExecutor(sender, command, label, args);
	}

	static Dependencies getDependencies() {
		return dependencies;
	}
}

