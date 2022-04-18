package ml.mopsbase;

import ml.mopsbase.game.GameSession;
import ml.mopsexception.MopsTranslationException;
import ml.mopsutils.Translation;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class MopsPlugin extends JavaPlugin {
	private List<GameSession> gameSessions;
	protected FileConfiguration config;
	public FileConfiguration translation = new YamlConfiguration();

	public TextComponent getByLang(String lang, String string) {
		return new Translation(translation, getLogger(), "woolbattle").getTranslation(lang, string.replaceFirst("woolbattle.", ""));
	}
	public List<TextComponent> getByLang(String lang, String string, Map<String, String> formatV, boolean notSingular) {
		return new Translation(translation, getLogger(), "woolbattle").getTranslation(lang, string.replaceFirst("woolbattle.", ""), Map.of("", ""), notSingular);
	}
	public TextComponent getByLang(String lang, String string, Map<String, String> formatV) {
		return new Translation(translation, getLogger(), "woolbattle").getTranslation(lang, string.replaceFirst("woolbattle.", ""));
	}

}
