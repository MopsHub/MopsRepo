package ml.mopsbase;

import ml.mopsbase.game.GameSession;
import ml.mopsutils.Translation;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class MopsPlugin extends JavaPlugin {
	private List<GameSession> gameSessions;
	protected FileConfiguration config;
	public final FileConfiguration translation = new YamlConfiguration();

	public TextComponent getByLang(String lang, String string) {
		getLogger().info("MopsPlugin | getByLang: \n" + lang + "\n" + string);
		return new Translation(translation, getLogger(), "mopsgeneral").getTranslation(lang, string.replaceFirst("mopsgeneral.", ""));
	}
	public TextComponent getByLang(String lang, String string, Map<String, String> formatV) {
		getLogger().info("MopsPlugin | getByLang: \n" + lang + "\n" + string + "\n" + formatV.toString());
		return new Translation(translation, getLogger(), "mopsgeneral").getTranslation(lang, string.replaceFirst("mopsgeneral.", ""));
	}

}
