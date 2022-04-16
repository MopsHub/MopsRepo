package ml.mopsbase;
import ml.mopsbase.game.GameSession;
import ml.mopsbase.game.GamePlayer;
import ml.mopsutils.Folder;
import ml.mopsutils.Resources;
import org.bukkit.plugin.java.JavaPlugin;
import com.fasterxml.jackson.core.*;

import java.util.List;

public class MopsPlugin extends JavaPlugin {
	private List<GameSession> gameSessions;
	protected Config config;
	protected Config defaultConfig;

}
