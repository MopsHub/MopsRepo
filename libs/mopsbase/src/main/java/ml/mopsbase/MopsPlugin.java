package ml.mopsbase;
import ml.mopsbase.game.GameSession;
import ml.mopsbase.game.GamePlayer;
import ml.mopsutils.Folder;
import ml.mopsutils.Resources;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.List;

public class MopsPlugin extends JavaPlugin {
	protected FileConfiguration config;
	private List<GameSession> gameSessions;

}
