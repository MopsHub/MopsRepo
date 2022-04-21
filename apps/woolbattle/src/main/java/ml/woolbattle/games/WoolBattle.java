package ml.woolbattle.games;

import ml.mopsbase.MopsPlugin;
import ml.mopsbase.game.GameSession;
import ml.woolbattle.Plugin;
import ml.woolbattle.Teams;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WoolBattle extends GameSession {
	protected ArrayList<WBPlayer> players;
	protected Teams[] aliveTeams = {Teams.RED, Teams.BLUE, Teams.GREEN, Teams.YELLOW};
	protected boolean areSpectators = false;
	protected HashMap<String, WoolGenerator> generators = new HashMap<String, WoolGenerator>();
	protected MopsPlugin plugin;
	protected Logger logger;
	protected boolean isActive;
	protected ScoreboardManager manager;
	protected Scoreboard board;

	class WoolGenerator {
		public WGStatus status = WGStatus.UNCAPTURED;
		public boolean locked = false;
	}

	enum WGStatus {
		RED,
		BLUE,
		GREEN,
		YELLOW,
		UNCAPTURED
	}

	public WoolBattle(MopsPlugin plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		this.isActive = false;
		this.manager = plugin.getServer().getScoreboardManager();
		this.board = manager.getNewScoreboard();
		String[] genIDs = {"a", "b", "c", "d"};
		for (String i : genIDs) {
			generators.put(i, new WoolGenerator());
		}

		prepareGame();
	}

	protected void prepareGame() {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (p.getScoreboardTags().contains("ingame") || p.getScoreboardTags().contains("spectators")) {
				break;
			}

			players.add(new WBPlayer(p, this.logger));
		}
	}
}
