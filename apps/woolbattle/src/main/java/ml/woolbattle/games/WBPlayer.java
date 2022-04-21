package ml.woolbattle.games;

import ml.mopsbase.game.GamePlayer;
import ml.woolbattle.Items;
import ml.woolbattle.Teams;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

public class WBPlayer implements GamePlayer {
	protected Player player;
	protected Teams team;
	protected HashMap<Items, HashMap<String, ?>> items;
	protected boolean alive;
	protected boolean inGame;

	public WBPlayer(Player player, Logger logger) {
		this.player = player;
		try {
			identifyTeam();
		} catch (Exception exception) {
			logger.warning(exception.getMessage());
		}
	}

	public void identifyTeam() throws Exception {
		Team t = player.getScoreboard().getPlayerTeam(player);
		switch (t.getName().toLowerCase(Locale.ROOT)) {
			case "red" -> this.team = Teams.RED;
			case "blue" -> this.team = Teams.BLUE;
			case "green" -> this.team = Teams.GREEN;
			case "yellow" -> this.team = Teams.YELLOW;
			case "spectator" -> this.team = Teams.SPECTATOR;
			default -> throw new Exception("can't identify team for player " + player.getName() + " with team name: " + t.getName());
		}

	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Teams getTeam() {
		return team;
	}

	public void setTeam(Teams team) {
		this.team = team;
	}

	public HashMap<Items, HashMap<String, ?>> getItems() {
		return items;
	}

	public void setItems(HashMap<Items, HashMap<String, ?>> items) {
		this.items = items;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
}
