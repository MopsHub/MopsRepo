package ml.mopsbase;

import ml.mopsbase.game.GameSession;
import ml.mopsexception.MopsTranslationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.Callable;

public class MopsPlugin extends JavaPlugin {
	private List<GameSession> gameSessions;
	protected FileConfiguration config;
	public FileConfiguration translation = new YamlConfiguration();

	protected void loadTrainslation() throws MopsTranslationException {
	}

}
