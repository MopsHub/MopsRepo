package ml.mopspvps;

import ml.mopspvps.commands.AdminUtils;
import ml.mopspvps.commands.PlayerEssentials;
import ml.mopspvps.events.BattleEvents;
import ml.mopspvps.events.ItemEvents;
import ml.mopspvps.events.LobbyEvents;
import ml.mopspvps.events.QuestEvents;
import ml.mopspvps.events.maps.AnotherEvents;
import ml.mopspvps.events.maps.AquaEvents;
import ml.mopspvps.events.maps.CityEvents;
import ml.mopspvps.menus.*;
import ml.mopspvps.scoreboards.BattleScoreboards;
import ml.mopspvps.scoreboards.CityScoreboards;
import ml.mopspvps.scoreboards.LobbyScoreboards;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Dependencies {
	private static HashMap<Player, String> name = new HashMap<>();
	private final static HashMap<Player, String> rank = new HashMap<>();
	private static List<ArmorStand> armorstandlist = new ArrayList<>();
	static Plugin plugin;
	static Events events;
	private final static LobbyEvents lobbyEvents = new LobbyEvents();
	private final static QuestEvents questEvents = new QuestEvents();

	private final static BattleEvents battleEvents = new BattleEvents();
	private final static ItemEvents itemEvents = new ItemEvents();
	private final static CityEvents cityEvents = new CityEvents();
	private final static AquaEvents aquaEvents = new AquaEvents();
	private final static AnotherEvents anotherEvents = new AnotherEvents();

	private static Commands commands = null;
	private final static AdminUtils adminUtils = new AdminUtils();
	private final static PlayerEssentials playerEssentials = new PlayerEssentials();

	private static Scoreboards scoreboards = null;
	private final static BattleScoreboards battlescoreboards = new BattleScoreboards();
	private final static LobbyScoreboards lobbyScoreboards = new LobbyScoreboards();
	private final static CityScoreboards cityScoreboards = new CityScoreboards();

	private static Menus menus = null;

	private static Inventory customgive;
	private static Inventory simpledimple;
	private static Inventory kits;
	private static Inventory popit;

	private static Logger log;

	static ScoreboardManager manager;
	static Scoreboard board;

	static private Timestamp pluginEnabledTimestamp;


	public Dependencies(Plugin plugin) {
		Dependencies.plugin = plugin;
		Dependencies.events = new Events(this);
		Dependencies.commands = new Commands();
		Dependencies.menus = new Menus();
		Dependencies.scoreboards = new Scoreboards();
		Dependencies.manager = plugin.getServer().getScoreboardManager();
		Dependencies.board = manager.getMainScoreboard();
		Dependencies.customgive = Dependencies.menus.getMenu(MENUS.CUSTOMGIVE);
		Dependencies.simpledimple = Dependencies.menus.getMenu(MENUS.SIMPLEDIMPLE);
		Dependencies.kits = Dependencies.menus.getMenu(MENUS.KITS);
		Dependencies.popit = Dependencies.menus.getMenu(MENUS.POPIT);
		Dependencies.log = plugin.getLogger();

	}

	public static Timestamp getPluginEnabledTimestamp() {
		return Dependencies.pluginEnabledTimestamp;
	}

	public static void setPluginEnabledTimestamp(Timestamp pluginEnabledTimestamp) {
		Dependencies.pluginEnabledTimestamp = pluginEnabledTimestamp;
	}

	public static List<ArmorStand> getArmorstandlist() {
		return armorstandlist;
	}

	public static String getTimeSincePluginEnable(SimpleDateFormat sdf) {
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
		if (sdf != null) {
			simpleTimeFormat = sdf;
		}
		return simpleTimeFormat.format(pluginEnabledTimestamp.compareTo(new Timestamp(System.currentTimeMillis())));
	}

	public static String getTimeSincePluginEnable() {
		final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
		return simpleTimeFormat.format(pluginEnabledTimestamp.compareTo(new Timestamp(System.currentTimeMillis())));
	}

	public static void setArmorstandlist(List<ArmorStand> armorstandlist) {
		Dependencies.armorstandlist = armorstandlist;
	}

	public static void addArmorstandList(ArmorStand armorstand) {
		armorstandlist.add(armorstand);
	}

	public HashMap<Player, String> getMopsRank() {
		return rank;
	}

	public static HashMap<Player, String> getMopsName() {
		return name;
	}

	public static String getMopsRank(Player player) {
		return rank.get(player);
	}

	public static String getMopsName(Player player) {
		return name.get(player);
	}

	public static void setMopsRank(HashMap<Player, String> newRank) {
		Dependencies.name = newRank;
	}

	public static void setMopsName(HashMap<Player, String> newName) {
		Dependencies.name = newName;
	}

	public static HashMap<Player, String> getName() {
		return name;
	}

	public static HashMap<Player, String> getRank() {
		return rank;
	}

	public static Inventory getCustomgive() {
		return customgive;
	}

	public static Inventory getSimpledimple() {
		return simpledimple;
	}

	public static Inventory getPopit() {
		return popit;
	}

	public static void putMopsRank(Player keyPlayer, String newRank) {
		Dependencies.name.put(keyPlayer, newRank);
	}

	public static void putMopsName(Player keyPlayer, String newName) {
		Dependencies.name.put(keyPlayer, newName);
	}


	/**
	 * Кастомные инвентари - получаются через класс Menus
	 *
	 * @see ml.mopspvps.menus.MENUS
	 * "куегкт
	 */

	public static Inventory getCustomGive() {
		return customgive;
	}

	public static Inventory getSimpleDimple() {
		return simpledimple;
	}

	public static ItemStack getSimpleDimple(int i) {
		return simpledimple.getItem(i);
	}

	public static Inventory getKits() {
		return kits;
	}

	public static Inventory getPopIt() {
		return popit;
	}

	public static ItemStack getPopIt(int i) {
		return popit.getItem(i);
	}

	/**
	 * Установить customgive
	 *
	 * @see CustomGive
	 */
	public static void setCustomGive(Inventory customgive) {
		Dependencies.customgive = customgive;
	}

	/**
	 * Установить simpledimple
	 *
	 * @see SimpleDimple
	 */
	public static void setSimpledimple(Inventory simpledimple) {
		Dependencies.simpledimple = simpledimple;
	}

	/**
	 * Установить simpledimple
	 *
	 * @param i         Слот
	 * @param popitItem Предмет
	 * @see SimpleDimple
	 */
	public static void setSimpleDimple(int i, Object popitItem) {
		Dependencies.popit.setItem(i, (ItemStack) popitItem);
	}


	/**
	 * Установить popit
	 *
	 * @see PopIt
	 */
	public static void setPopIt(Inventory popit) {
		Dependencies.popit = popit;
	}

	/**
	 * Установить предмет в popit
	 *
	 * @param i         Слот
	 * @param popitItem Предмет
	 * @see PopIt
	 */
	public static void setPopItItem(int i, Object popitItem) {
		Dependencies.popit.setItem(i, (ItemStack) popitItem);
	}

	public static Plugin getPlugin() {
		return plugin;
	}

	public static Events getEvents() {
		return events;
	}

	public static LobbyEvents getLobbyEvents() {
		return lobbyEvents;
	}

	public static QuestEvents getQuestEvents() {
		return questEvents;
	}


	public static BattleEvents getBattleEvents() {
		return battleEvents;
	}


	public static ItemEvents getItemEvents() {
		return itemEvents;
	}

	public static CityEvents getCityEvents() {
		return cityEvents;
	}

	public static Commands getCommands() {
		return commands;
	}

	public static AdminUtils getAdminUtils() {
		return adminUtils;
	}

	public static PlayerEssentials getPlayerEssentials() {
		return playerEssentials;
	}


	public static Menus getMenus() {
		return menus;
	}

	public static void setMenus(Menus menus) {
		Dependencies.menus = menus;
	}

	public static void setKits(Inventory kits) {
		Dependencies.kits = kits;
	}

	public static Scoreboards getScoreboards() {
		return scoreboards;
	}

	public static BattleScoreboards getBattleScoreboards() {
		return battlescoreboards;
	}

	public static void setCustomgive(Inventory customgive) {
		Dependencies.customgive = customgive;
	}

	public static void setPopit(Inventory popit) {
		Dependencies.popit = popit;
	}

	public static BattleScoreboards getBattlescoreboards() {
		return battlescoreboards;
	}

	public static ScoreboardManager getManager() {
		return manager;
	}

	public static void setManager(ScoreboardManager manager) {
		Dependencies.manager = manager;
	}

	public static Scoreboard getBoard() {
		return board;
	}

	public static void setBoard(Scoreboard board) {
		Dependencies.board = board;
	}


	public static LobbyScoreboards getLobbyScoreboards() {
		return lobbyScoreboards;
	}


	public static CityScoreboards getCityScoreboards() {
		return cityScoreboards;
	}

	public static Logger getLog() {
		return log;
	}

	public static AquaEvents getAquaEvents() {
		return aquaEvents;
	}

	public static AnotherEvents getAnotherEvents() {
		return anotherEvents;
	}
}
