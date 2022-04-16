package ml.mopspvps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ml.mopsbase.Config;
import ml.mopsbase.MopsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.http.WebSocket.Listener;
import java.sql.Timestamp;
import java.util.Arrays;
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

//		File cfgFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
//		String cfgText = "";
//
//		try {
//			Scanner myReader = new Scanner(cfgFile);
//			while (myReader.hasNextLine()) {
//				String data = myReader.nextLine();
//				cfgText = cfgText + data;
//			}
//			myReader.close();
//		} catch (FileNotFoundException e) {
//			logger.info("Custom config not found. Loading default one");
//			try {
//				Scanner myReader = new Scanner(cfgFile);
//				while (myReader.hasNextLine()) {
//					String data = myReader.nextLine();
//					cfgText = cfgText + data;
//				}
//				myReader.close();
//			}
//			catch (Exception ex) {
//				logger.warning("default config not found.");
//				logger.warning(Arrays.toString(ex.getStackTrace()));
//			}
//		}
//
//		if (cfgText.isBlank()) {
//			logger.warning("Both default and custom configs are not found/empty/blank");
//			this.setEnabled(false);
//			return;
//		}

		File cfgFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
		boolean isCustomConfigOk = false;
		boolean isDefaultConfigOk;
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		try {
			if (cfgFile.exists()) {
				logger.info("Trying to parse config at path: " + cfgFile.getAbsolutePath());
				this.config = objectMapper.readValue(cfgFile, Config.class);
				try {
					logger.info("Custom config: \n" + this.config.parseToString());
					isCustomConfigOk = true;
					if (this.config.parseToString().isBlank()) {
						isCustomConfigOk = false;
						logger.info("Custom config is empty.");
					}
				} catch (Exception e) {
					logger.warning("Unable to turn config into string due to:\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
					isCustomConfigOk = false;
				}
			}
		} catch (Exception e) {
			logger.warning("Exception while parsing custom config: " + e.getMessage() + "\n " + Arrays.toString(e.getStackTrace()));
		}

		try {
			logger.info("Trying to parse default config");

			defaultConfig = objectMapper.readValue(this.getResource("config.yml"), Config.class);
			try {
				logger.info("Default config: \n" + this.defaultConfig.parseToString());
				isDefaultConfigOk = true;
				if (this.defaultConfig.parseToString().isBlank()) {
					isDefaultConfigOk = false;
					logger.warning("Default config is empty");
				}
				} catch (Exception e) {
					logger.warning("Unable to turn config into string due to:\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
					isDefaultConfigOk = false;
				}
			} catch (Exception e) {
			logger.warning("Exception while parsing default config: " + e.getMessage() + "\n " + Arrays.toString(e.getStackTrace()));
			this.setEnabled(false);
			return;
		}

		if (!isCustomConfigOk) {
			if (isDefaultConfigOk) {
				this.config = this.defaultConfig;
			} else {
				logger.warning("Both custom and default configs are blank/corrupted/not working/not found");
				this.setEnabled(false);
				return;
			}
		}


		dependencies = new Dependencies(Plugin.this);

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

