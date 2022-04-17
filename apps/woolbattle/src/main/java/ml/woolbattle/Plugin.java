package ml.woolbattle;

import ml.mopsbase.MopsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class Plugin extends MopsPlugin implements Listener, CommandExecutor {

	String lang = "rus";
	// внимание добавьте адекватный выбор языка который распространяется
	// на все сервера, и скажите мне об этом
	// спасибо
	// пододжди два года и сделаю))))

	List<Block> ppbs = new ArrayList<>();
	World mainworld = Bukkit.getServer().getWorlds().get(0);
	boolean hardmode = false;
	boolean gameactive = false;

	int redkills = 0;
	int yellowkills = 0;
	int greenkills = 0;
	int bluekills = 0;

	String genAstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();
	String genBstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();
	String genCstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();
	String genDstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();

	List<Block> genAblocks = getBlox(new Location(mainworld, 46, 254, -28).getBlock(), 2);
	List<Block> genBblocks = getBlox(new Location(mainworld, -28, 254, -28).getBlock(), 2);
	List<Block> genCblocks = getBlox(new Location(mainworld, -28, 254, 46).getBlock(), 2);
	List<Block> genDblocks = getBlox(new Location(mainworld, 46, 254, 46).getBlock(), 2);

	List<Block> genAblocksLONG = getBlox(new Location(mainworld, 46, 254, -28).getBlock(), 3);
	List<Block> genBblocksLONG = getBlox(new Location(mainworld, -28, 254, -28).getBlock(), 3);
	List<Block> genCblocksLONG = getBlox(new Location(mainworld, -28, 254, 46).getBlock(), 3);
	List<Block> genDblocksLONG = getBlox(new Location(mainworld, 46, 254, 46).getBlock(), 3);

	private final HashMap<Player, Integer> combo = new HashMap<>();
	private final HashMap<Player, BukkitTask> deathmsg = new HashMap<>();

	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getMainScoreboard();
	Objective fakekills = board.getObjective("fakekills");

	List<Player> redTeamPlayers = new ArrayList<>();
	List<Player> yellowTeamPlayers = new ArrayList<>();
	List<Player> greenTeamPlayers = new ArrayList<>();
	List<Player> blueTeamPlayers = new ArrayList<>();

	int generatorTask;

	BukkitTask worldBorderTask;

	boolean testmode = false;



	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		mainworld = Bukkit.getServer().getWorlds().get(0);
		Logger logger = getLogger();

		this.saveDefaultConfig();
		this.config = this.getConfig();
		logger.info("config: \n" + config.saveToString() );
		logger.info("default config: \n" + ((FileConfiguration) Objects.requireNonNull(config.getDefaults())).saveToString());

		StringBuilder data;

		try (Scanner reader = new Scanner(Objects.requireNonNull(getResource("translations.yml")))) {
			data = new StringBuilder();
			while (reader.hasNextLine()) {
				data.append("\n").append(reader.nextLine());
			}
		}

		try {
			this.translation.loadFromString(data.toString());
		} catch (InvalidConfigurationException e) {
			logger.warning(Arrays.toString(e.getStackTrace()));
		}

		logger.info("Loaded translations: \n" + translation.saveToString());

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {

				Team team = board.getPlayerTeam(player);
				assert team != null;
				String teamname = team.getName();

				mainworld = player.getWorld();
				if (player.getScoreboardTags().contains("ingame")) {

					combo.putIfAbsent(player, 0);

					Location loc0 = player.getLocation();

					if (loc0.clone().add(0, -1, 0).getBlock().getType() == Material.SLIME_BLOCK) {
						player.setVelocity(new Vector(0, 10, 0));
					}

					if (loc0.getY() > 143 && loc0.getY() < 160) {

						if (teamname.contains("red")) {
							ItemStack woolitem = new ItemStack(Material.RED_WOOL);
							ItemMeta woolmeta = woolitem.getItemMeta();
							woolmeta.setDisplayName(getByLang(lang, "woolbattle.redWool").toString());
							woolitem.setItemMeta(woolmeta);
							woolitem.setAmount(200000);
							player.getInventory().removeItem(woolitem);
						}
						if (teamname.contains("yellow")) {
							ItemStack woolitem = new ItemStack(Material.YELLOW_WOOL);
							ItemMeta woolmeta = woolitem.getItemMeta();
							woolmeta.setDisplayName(getByLang(lang, "woolbattle.yellowWool").toString());
							woolitem.setItemMeta(woolmeta);
							woolitem.setAmount(200000);
							player.getInventory().removeItem(woolitem);
						}
						if (teamname.contains("green")) {
							ItemStack woolitem = new ItemStack(Material.LIME_WOOL);
							ItemMeta woolmeta = woolitem.getItemMeta();
							woolmeta.setDisplayName(getByLang(lang, "woolbattle.greenWool").toString());
							woolitem.setItemMeta(woolmeta);
							woolitem.setAmount(200000);
							player.getInventory().removeItem(woolitem);
						}
						if (teamname.contains("blue")) {
							ItemStack woolitem = new ItemStack(Material.LIGHT_BLUE_WOOL);
							ItemMeta woolmeta = woolitem.getItemMeta();
							woolmeta.setDisplayName(getByLang(lang, "woolbattle.blueWool").toString());
							woolitem.setItemMeta(woolmeta);
							woolitem.setAmount(200000);
							player.getInventory().removeItem(woolitem);
						}

						Objective lastdamage = board.getObjective("lastdamagedbyteam");

						switch (Objects.requireNonNull(lastdamage).getScore(player.getName()).getScore()) {
							case 1 -> {
								redkills = redkills + 1;
								broadcastDeath(player, getByLang(lang, "woolbattle.gotKilledBy") + " " + ChatColor.RED + "" + ChatColor.BOLD + "КРАСНЫМИ" + ChatColor.GRAY + ".");
							}
							case 2 -> {
								yellowkills = yellowkills + 1;
								broadcastDeath(player, getByLang(lang, "woolbattle.gotKilledBy") + " " + ChatColor.YELLOW + "" + ChatColor.BOLD + "ЖЁЛТЫМИ" + ChatColor.GRAY + ".");
							}
							case 3 -> {
								greenkills = greenkills + 1;
								broadcastDeath(player, getByLang(lang, "woolbattle.gotKilledBy") + " " + ChatColor.GREEN + "" + ChatColor.BOLD + "ЗЕЛЁНЫМИ" + ChatColor.GRAY + ".");
							}
							case 4 -> {
								bluekills = bluekills + 1;
								broadcastDeath(player, getByLang(lang, "woolbattle.gotKilledBy") + " " + ChatColor.AQUA + "" + ChatColor.BOLD + "СИНИМИ" + ChatColor.GRAY + ".");
							}
						}

						if(!hardmode) {
							player.setGameMode(GameMode.SPECTATOR);
							Location mid = new Location(player.getWorld(), 9, 270, 9);
							player.teleport(mid);
							lastdamage.getScore(player.getName()).setScore(0);

							player.sendTitle(ChatColor.RED + "3", "", 1, 10, 10);
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);

							deathmsg.put(player, Bukkit.getScheduler().runTaskLater(this, () -> {
								player.sendTitle(ChatColor.YELLOW + "2", "", 1, 10, 10);
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
								deathmsg.put(player, Bukkit.getScheduler().runTaskLater(this, () -> {
									player.sendTitle(ChatColor.GREEN + "1", "", 1, 10, 10);
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.5F);
									deathmsg.put(player, Bukkit.getScheduler().runTaskLater(this, () -> {
										player.setGameMode(GameMode.SURVIVAL);
										updateLevels(player);

										if (teamname.contains("red")) {
											Location loc = new Location(player.getWorld(), 9.5, 258, -27.5);
											player.teleport(loc);
										}
										if (teamname.contains("yellow")) {
											Location loc = new Location(player.getWorld(), -27.5, 258, 9.5);
											loc.setYaw(-90);
											player.teleport(loc);
										}
										if (teamname.contains("green")) {
											Location loc = new Location(player.getWorld(), 9.5, 258, 46.5);
											loc.setYaw(-180);
											player.teleport(loc);
										}
										if (teamname.contains("blue")) {
											Location loc = new Location(player.getWorld(), 46.5, 258, 9.5);
											loc.setYaw(90);
											player.teleport(loc);
										}
										player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);

									}, 20L));
								}, 20L));
							}, 20L));
						} else {
							simulateHardmodeDeath(player);
						}
					}
					if(hardmode && !player.getWorld().getWorldBorder().isInside(player.getLocation())) {
						simulateHardmodeDeath(player);
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_DEATH, 0.8F, 1);
					}
				} else if (player.getScoreboardTags().contains("onspawn")) {
					if (player.getLocation().add(0, -2, 0).getBlock().getType().equals(Material.STRIPPED_WARPED_HYPHAE)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 7, true, false));
					}
					if (player.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.STRIPPED_WARPED_HYPHAE)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 7, true, false));
					}
				}

				int requiredKills = (int) (Math.round(4 * (Bukkit.getOnlinePlayers().size() * 0.7)));

				recountTeamMembers();

				if(redkills >= requiredKills || (!redTeamPlayers.isEmpty() && yellowTeamPlayers.isEmpty() && greenTeamPlayers.isEmpty() && blueTeamPlayers.isEmpty() && gameactive && !testmode)) {
					winningBroadcast(1);
					resetEveryFuckingKillScoreboard();
					try {
						deathmsg.get(player).cancel(); } catch (Throwable ignored) {}
					stopGame();
				}
				if(yellowkills >= requiredKills || (redTeamPlayers.isEmpty() && !yellowTeamPlayers.isEmpty() && greenTeamPlayers.isEmpty() && blueTeamPlayers.isEmpty() && gameactive && !testmode)) {
					winningBroadcast(2);
					resetEveryFuckingKillScoreboard();
					try {
						deathmsg.get(player).cancel(); } catch (Throwable ignored) {}
					stopGame();
				}
				if(greenkills >= requiredKills || (redTeamPlayers.isEmpty() && yellowTeamPlayers.isEmpty() && !greenTeamPlayers.isEmpty() && blueTeamPlayers.isEmpty() && gameactive && !testmode)) {
					winningBroadcast(3);
					resetEveryFuckingKillScoreboard();
					try {
						deathmsg.get(player).cancel(); } catch (Throwable ignored) {}
					stopGame();
				}
				if(bluekills >= requiredKills || (redTeamPlayers.isEmpty() && yellowTeamPlayers.isEmpty() && greenTeamPlayers.isEmpty() && !blueTeamPlayers.isEmpty()  && gameactive && !testmode)) {
					winningBroadcast(4);
					resetEveryFuckingKillScoreboard();
					try {
						deathmsg.get(player).cancel(); } catch (Throwable ignored) {}
					stopGame();
				}

			}
			for (Entity entity : mainworld.getEntities()) {
				if (entity.getType() == EntityType.ENDERMITE) {
					entity.teleport(entity.getLocation().add(0, -1000, 0));
				}
				if(entity instanceof Projectile) {
					if (entity.getLocation().getY() > 143 && entity.getLocation().getY() < 160) {
						entity.teleport(entity.getLocation().add(0, -1000, 0));
					}
				}
			}
		}, 1L, 5L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Team team = board.getPlayerTeam(player);
				assert team != null;
				String teamname = team.getName();

				if (player.getScoreboardTags().contains("onspawn") || !hardmode) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7, 100, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 7, 100, true, false));
				} else if(hardmode) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 0, true, false));
				}

				if (player.getScoreboardTags().contains("onspawn")) {
					player.setAllowFlight(player.getGameMode() != GameMode.SURVIVAL);
				}

				ChatColor chatcolor = ChatColor.WHITE;
				Color color = Color.fromBGR(255, 255, 255);

				if (teamname.contains("red")) {
					chatcolor = ChatColor.RED;
					color = Color.fromRGB(255, 10, 10);
				} else if (teamname.contains("yellow")) {
					chatcolor = ChatColor.YELLOW;
					color = Color.fromRGB(255, 207, 36);
				} else if (teamname.contains("green")) {
					chatcolor = ChatColor.GREEN;
					color = Color.fromRGB(105, 255, 82);
				} else if (teamname.contains("blue")) {
					chatcolor = ChatColor.AQUA;
					color = Color.fromRGB(47, 247, 227);
				}

				ItemStack item6 = new ItemStack(Material.LEATHER_BOOTS);
				LeatherArmorMeta meta6 = (LeatherArmorMeta) item6.getItemMeta();
				meta6.setDisplayName(chatcolor + "Дабл-Джамп Ботинки");
				List<String> lore6 = new ArrayList<>();
				lore6.add(ChatColor.GRAY + "Позволяют прыгать в воздухе. ");
				lore6.add(ChatColor.DARK_GRAY + "(Нужно нажать пробел дважды в падении)");
				lore6.add(ChatColor.AQUA + "Стоимость: 16 шерсти");
				meta6.setUnbreakable(true);
				meta6.setColor(color);
				meta6.setLore(lore6);
				item6.setItemMeta(meta6);

				if (player.getScoreboardTags().contains("ingame")) {
					if (player.isOnGround() && !player.getAllowFlight()) {
						player.setAllowFlight(true);
					}
					if (player.isFlying() && player.getGameMode().equals(GameMode.SURVIVAL)) {
						player.setFlying(false);

						boolean hasItems = woolRemove(16, player, teamname);

						if (hasItems) {
							player.setVelocity((player.getEyeLocation().getDirection().multiply(0.9)).add(new Vector(0, 0.45, 0)));
							player.setAllowFlight(false);
						} else {
							player.sendActionBar(Component.text(ChatColor.RED + "Недостаточно шерсти!"));
						}
					}
				}
			}
		}, 1L, 1L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			genConquerChecks(genAblocks, genAblocksLONG, "A");
			genConquerChecks(genBblocks, genBblocksLONG, "B");
			genConquerChecks(genCblocks, genCblocksLONG, "C");
			genConquerChecks(genDblocks, genDblocksLONG, "D");
		}, 1L, 20L);
	}

	final int[] minutes = {0};
	final int[] seconds = {0};

	final int[] minutes0 = {0};
	final int[] seconds0 = {-1};

	final int[] actualgametime = {0};
	final int[] actualgametime0 = {-1};

	String nextevent = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";
	String nextevent0 = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";

	int scoreboardTask;

	@Override
	public boolean onCommand(CommandSender sender, Command command, @NotNull String label, String[] args) {
		boolean perms = sender.isOp();
		Player player = (Player) sender;
		String commandName = command.getName().toLowerCase(Locale.ROOT);

		if (perms) {
			if (commandName.equals("startgame")) {
				player.getWorld().getWorldBorder().setSize(200, 1);
				hardmode = false;
				for (Player player1 : Bukkit.getOnlinePlayers()) {
					if (!(board.getPlayerTeam(player1) == null)) {
						Team team = board.getPlayerTeam(player1);
						assert team != null;
						String teamname = team.getName();

						try {
							if (args[0].equals("instant")) {
								gameStartSequence(player1, teamname);
							} else {
								if(!testmode) {
									timedGameStart(player1, teamname);
								} else {
									player.sendMessage(ChatColor.RED + "В тестмоде игра работает только с моментальным запуском.");
								}
							}
						} catch (IndexOutOfBoundsException error) {
							if(!testmode) {
								timedGameStart(player1, teamname);
							} else {
								player.sendMessage(ChatColor.RED + "В тестмоде игра работает только с моментальным запуском.");
							}
						}
					}
				}

				lootGenerator();

				Bukkit.getScheduler().runTaskLater(this, () -> scoreboardTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

					if (gameactive) {

						seconds[0] = seconds[0] + 1;

						if (seconds[0] == 60) {
							minutes[0] = minutes[0] + 1;
							seconds[0] = 0;
						}

						seconds0[0] = seconds0[0] + 1;

						if (seconds0[0] == 60) {
							minutes0[0] = minutes0[0] + 1;
							seconds0[0] = 0;
						}

						actualgametime[0] = actualgametime[0] + 1;
						actualgametime0[0] = actualgametime0[0] + 1;


						if (actualgametime[0] < 240) {
							nextevent = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";
						}
						if (actualgametime[0] == 240) {
							lootGenerator();
							for (Player player0 : mainworld.getPlayers()) {
								player0.sendTitle(ChatColor.YELLOW + "Рефилл!", ChatColor.GRAY + "Лут восстановился", 1, 20, 20);
								player0.playSound(player0.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
							}
						}
						if (actualgametime[0] < 480 && actualgametime[0] > 240) {
							nextevent = ChatColor.DARK_GRAY + " (Рефилл | 8:00)";
						}
						if (actualgametime[0] == 480) {
							lootGenerator();
							for (Player player0 : mainworld.getPlayers()) {
								player0.sendTitle(ChatColor.YELLOW + "Рефилл!", ChatColor.GRAY + "Лут восстановился", 1, 20, 20);
								player0.playSound(player0.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
							}
						}
						if (actualgametime[0] < 720 && actualgametime[0] > 480) {
							nextevent = ChatColor.DARK_GRAY + " (Рефилл | 12:00)";
						}
						if (actualgametime[0] == 720) {
							lootGenerator();
							for (Player player0 : mainworld.getPlayers()) {
								player0.sendTitle(ChatColor.YELLOW + "Рефилл!", ChatColor.GRAY + "Лут восстановился", 1, 20, 20);
								player0.playSound(player0.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
							}
						}
						if (actualgametime[0] < 1200 && actualgametime[0] > 720) {
							nextevent = ChatColor.DARK_GRAY + " (" + ChatColor.DARK_RED + "Хардмод" + ChatColor.DARK_GRAY + " | 20:00)";
						}
						if (actualgametime[0] == 1200) {
							if (!hardmode) {
								activateHardmode();
							}
							nextevent = ChatColor.DARK_GRAY + " (Сражение до конца)";
						}


						if (actualgametime0[0] < 240) {
							nextevent0 = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";
						}
						if (actualgametime0[0] < 480 && actualgametime0[0] > 240) {
							nextevent0 = ChatColor.DARK_GRAY + " (Рефилл | 8:00)";
						}
						if (actualgametime0[0] < 720 && actualgametime0[0] > 480) {
							nextevent0 = ChatColor.DARK_GRAY + " (Рефилл | 12:00)";
						}
						if (actualgametime0[0] < 1200 && actualgametime0[0] > 720) {
							nextevent0 = ChatColor.DARK_GRAY + " (" + ChatColor.DARK_RED + "Хардмод" + ChatColor.DARK_GRAY + " | 20:00)";
						}
						if (actualgametime0[0] == 1200) {
							nextevent0 = ChatColor.DARK_GRAY + " (Сражение до конца)";
						}

						Objects.requireNonNull(fakekills.getScoreboard()).resetScores(ChatColor.RED + "Убито красной командой" + ChatColor.WHITE + ": " + ChatColor.RED + (redkills - 1));
						fakekills.getScoreboard().resetScores(ChatColor.YELLOW + "Убито жёлтой командой" + ChatColor.WHITE + ": " + ChatColor.YELLOW + (yellowkills - 1));
						fakekills.getScoreboard().resetScores(ChatColor.GREEN + "Убито зелёной командой" + ChatColor.WHITE + ": " + ChatColor.GREEN + (greenkills - 1));
						fakekills.getScoreboard().resetScores(ChatColor.AQUA + "Убито синей командой" + ChatColor.WHITE + ": " + ChatColor.AQUA + (bluekills - 1));

						if (seconds0[0] < 10) {
							fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes0[0] + ":" + "0" + seconds0[0] + nextevent0);
						} else {
							fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes0[0] + ":" + seconds0[0] + nextevent0);
						}

						fakekills.getScore(ChatColor.RED + "Убито красной командой" + ChatColor.WHITE + ": " + ChatColor.RED + redkills).setScore(12);
						fakekills.getScore(ChatColor.YELLOW + "Убито жёлтой командой" + ChatColor.WHITE + ": " + ChatColor.YELLOW + yellowkills).setScore(11);
						fakekills.getScore(ChatColor.GREEN + "Убито зелёной командой" + ChatColor.WHITE + ": " + ChatColor.GREEN + greenkills).setScore(10);
						fakekills.getScore(ChatColor.AQUA + "Убито синей командой" + ChatColor.WHITE + ": " + ChatColor.AQUA + bluekills).setScore(9);
						fakekills.getScore(ChatColor.RED + " ").setScore(8);
						if (seconds[0] < 10) {
							fakekills.getScore(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes[0] + ":" + "0" + seconds[0] + nextevent).setScore(7);
						} else {
							fakekills.getScore(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes[0] + ":" + seconds[0] + nextevent).setScore(7);
						}
						fakekills.getScore(ChatColor.GOLD + " ").setScore(6);

						resetGeneratorText();

						fakekills.getScore(ChatColor.WHITE + "Генератор A - " + genAstatus).setScore(5);
						fakekills.getScore(ChatColor.WHITE + "Генератор B - " + genBstatus).setScore(4);
						fakekills.getScore(ChatColor.WHITE + "Генератор C - " + genCstatus).setScore(3);
						fakekills.getScore(ChatColor.WHITE + "Генератор D - " + genDstatus).setScore(2);

						fakekills.getScore(ChatColor.YELLOW + " ").setScore(1);
						fakekills.getScore(ChatColor.DARK_GRAY + Bukkit.getServer().getIp() + ":" + Bukkit.getPort()).setScore(0);
					}
				}, 0L, 20L), 160L);

				Bukkit.getScheduler().cancelTask(generatorTask);

				generatorTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

					for (Player player1 : mainworld.getPlayers()) {
						Team team = board.getPlayerTeam(player1);
						assert team != null;
						String teamname = team.getName();

						List<String> genStatuses = new ArrayList<String>();
						genStatuses.add(genAstatus);
						genStatuses.add(genBstatus);
						genStatuses.add(genCstatus);
						genStatuses.add(genDstatus);

						for (String genStatus : genStatuses) {
							if(player1.getScoreboardTags().contains("ingame")) {
								if (teamname.contains("red")) {
									if (genStatus.contains(ChatColor.RED + "КРАСНЫЙ")) {
										if (!player1.getInventory().contains(Material.RED_WOOL, 512)) {
											ItemStack woolitem = new ItemStack(Material.RED_WOOL, 1);
											ItemMeta woolmeta = woolitem.getItemMeta();
											woolmeta.displayName(getByLang(lang, "woolbattle.redWool"));
											woolitem.setItemMeta(woolmeta);
											player1.getInventory().addItem(woolitem);
										}
									}
								}
								if (teamname.contains("yellow")) {
									if (genStatus.contains(ChatColor.YELLOW + "ЖЁЛТЫЙ")) {
										if (!player1.getInventory().contains(Material.YELLOW_WOOL, 512)) {
											ItemStack woolitem = new ItemStack(Material.YELLOW_WOOL, 1);
											ItemMeta woolmeta = woolitem.getItemMeta();
											woolmeta.displayName(getByLang(lang, "woolbattle.yellowWool"));
											woolitem.setItemMeta(woolmeta);
											player1.getInventory().addItem(woolitem);
										}
									}
								}
								if (teamname.contains("green")) {
									if (genStatus.contains(ChatColor.GREEN + "ЗЕЛЁНЫЙ")) {
										if (!player1.getInventory().contains(Material.LIME_WOOL, 512)) {
											ItemStack woolitem = new ItemStack(Material.LIME_WOOL, 1);
											ItemMeta woolmeta = woolitem.getItemMeta();
											woolmeta.displayName(getByLang(lang, "woolbattle.greenWool"));
											woolitem.setItemMeta(woolmeta);
											player1.getInventory().addItem(woolitem);
										}
									}
								}
								if (teamname.contains("blue")) {
									if (genStatus.contains(ChatColor.AQUA + "СИНИЙ")) {
										if (!player1.getInventory().contains(Material.LIGHT_BLUE_WOOL, 512)) {
											ItemStack woolitem = new ItemStack(Material.LIGHT_BLUE_WOOL, 1);
											ItemMeta woolmeta = woolitem.getItemMeta();
											woolmeta.displayName(getByLang(lang, "woolbattle.blueWool"));
											woolitem.setItemMeta(woolmeta);
											player1.getInventory().addItem(woolitem);
										}
									}
								}
							}
						}
						checkForWoolCap(player1);
						updateLevels(player1);
					}
				}, 0L, 20L);

				return true;
			}
			if (commandName.equals("clearblocks")) {
				for (Block block : ppbs) {
					block.setType(Material.AIR);
				}
				return true;
			}
			if (commandName.equals("stopgame")) {
				stopGame();
				return true;
			}
			if (commandName.equals("hardmode")) {
				activateHardmode();
				return true;
			}
			if (commandName.equals("refill")) {
				lootGenerator();

				for (Player player0 : mainworld.getPlayers()) {
					player0.sendTitle(ChatColor.YELLOW + "Рефилл!", ChatColor.GRAY + "Лут восстановился", 1, 20, 20);
					player0.playSound(player0.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
				}
				return true;
			}
			if (commandName.equals("clearscoreboard")) {
				try {
					if (args[0].toLowerCase(Locale.ROOT).equals("reload")) {
						fakekills = board.getObjective("fakekills");
					} else {
						clearScoreboard();
					}
				} catch (IndexOutOfBoundsException error) {
					clearScoreboard();
				}
				return true;
			}
			if(commandName.equals("testmode")) {
				try {
					if (args[0].toLowerCase(Locale.ROOT).equals("false")) {
						testmode = false;
					} else if (args[0].toLowerCase(Locale.ROOT).equals("true")) {
						testmode = true;
					}
				} catch (IndexOutOfBoundsException error) {
					testmode = !testmode;
				}
				player.sendMessage("testmode был изменён на " + String.valueOf(testmode));
				return true;
			}
			if (commandName.equals("cubicstuff")) {
				int radius = Integer.parseInt(args[0]);
				Material material = Material.valueOf(args[1]);

				List<Block> blocclist = getBlox(player.getLocation().getBlock(), radius);

				for (Block blocc : blocclist) {
					if (blocc.getType() == Material.AIR) {
						blocc.setType(material);
						ppbs.add(blocc);
					}
				}
				return true;
			}
		} else {
			player.sendTitle(" ", ChatColor.RED + "У вас нет прав!", 0, 20, 15);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 1);
			return true;
		}
		if (commandName.equals("spawn") || commandName.equals("lobby") || commandName.equals("l") || commandName.equals("hub")) {
			teleportToSpawn(player);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.4F, 1.5F);
			return true;
		}
		return false;
	}

	@EventHandler
	public void onBlockHitArrow(ProjectileHitEvent event) {
		try {
			if (event.getEntity() instanceof Arrow arrow) {
				Block block = event.getHitBlock();
				if (block.getType() != Material.OAK_LEAVES) {
					if (ppbs.contains(block)) {
						block.setType(Material.AIR);
						arrow.remove();
					}
				}
			}
		} catch (Exception | Error ignored) {
		}
	}

	private HashMap<Player, BukkitTask> damagetask0 = new HashMap<>();

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		try {
			Entity attacker0 = event.getDamager();
			Entity victim0 = event.getEntity();

			Objective obj = board.getObjective("lastdamagedbyteam");

			if (victim0 instanceof Player victim && attacker0 instanceof Player attacker) {

				if(!board.getPlayerTeam(attacker).getName().equals(board.getPlayerTeam(victim).getName())) {

					if(victim.getHealth()-1 <= 0) {
						simulateHardmodeDeath(victim);
						broadcastFinalDeath(victim);
						victim.playSound(victim.getLocation(), Sound.ENTITY_PLAYER_DEATH, 0.8F, 1);
					} else {
						if (hardmode) {
							event.setDamage(1);
						}
					}

					if (attacker.getScoreboardTags().contains("ingame")) {
						combo.put(attacker, (combo.get(attacker) + 1));
					} else {
						combo.put(attacker, 0);
					}

					Team team = board.getPlayerTeam(attacker);
					String teamname = team.getName();

					if (combo.get(attacker) >= 10) {
						victim.setVelocity(attacker.getEyeLocation().getDirection().multiply(0.7).add(victim.getVelocity()).add(new Vector(0, 0.7, 0)));
						attacker.sendTitle(ChatColor.YELLOW + "КОМБО!", ChatColor.YELLOW + "10 ударов подряд!", 0, 15, 10);
						attacker.playSound(attacker.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 2);
						combo.put(attacker, 0);
					}

					if(victim.getScoreboardTags().contains("ingame")) {
						if (teamname.contains("red")) {
							obj.getScore(victim.getName()).setScore(1);
						}
						if (teamname.contains("yellow")) {
							obj.getScore(victim.getName()).setScore(2);
						}
						if (teamname.contains("green")) {
							obj.getScore(victim.getName()).setScore(3);
						}
						if (teamname.contains("blue")) {
							obj.getScore(victim.getName()).setScore(4);
						}
					}

					if (damagetask0.get(victim) != null) {
						damagetask0.get(victim).cancel();
						damagetask0.put(victim, null);
					}

					damagetask0.put(victim, new BukkitRunnable() {
						@Override
						public void run() {
							obj.getScore(victim.getName()).setScore(0);
							combo.put(attacker, 0);
						}
					}.runTaskLater(this, 420L));
				}

			} else if(victim0 instanceof Player victim && attacker0 instanceof Projectile projectile) {

				//ELSE IF ARROW
				Player attacker = (Player) projectile.getShooter();

				if(!board.getPlayerTeam(attacker).getName().equals(board.getPlayerTeam(victim).getName())) {

					if(victim.getHealth()-1 <= 0) {
						simulateHardmodeDeath(victim);
						broadcastFinalDeath(victim);
						victim.playSound(victim.getLocation(), Sound.ENTITY_PLAYER_DEATH, 0.8F, 1);
					} else {
						if (hardmode) {
							event.setDamage(1);
						}
					}

					if (victim != attacker) {
						Team team = board.getPlayerTeam(attacker);
						String teamname = team.getName();

						if (teamname.contains("red")) {
							obj.getScore(victim.getName()).setScore(1);
						}
						if (teamname.contains("yellow")) {
							obj.getScore(victim.getName()).setScore(2);
						}
						if (teamname.contains("green")) {
							obj.getScore(victim.getName()).setScore(3);
						}
						if (teamname.contains("blue")) {
							obj.getScore(victim.getName()).setScore(4);
						}

						if (damagetask0.get(victim) != null) {
							damagetask0.get(victim).cancel();
							damagetask0.put(victim, null);
						}

						damagetask0.put(victim, new BukkitRunnable() {
							@Override
							public void run() {
								obj.getScore(victim.getName()).setScore(0);
							}
						}.runTaskLater(this, 420L));
					}
				}
			}

		} catch (Exception | Error e) {}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItemDrop().getItemStack();

		List<Material> matList = new ArrayList<Material>();
		matList.add(Material.WHITE_WOOL);
		matList.add(Material.RED_WOOL);
		matList.add(Material.YELLOW_WOOL);
		matList.add(Material.LIME_WOOL);
		matList.add(Material.LIGHT_BLUE_WOOL);

		if (player.getScoreboardTags().contains("ingame")) {
			if(matList.contains(item.getType())) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
		}
		updateLevels(player);
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();
		Material type = item.getType();

		Team team = board.getPlayerTeam(player);
		String teamname = team.getName();

		if((teamname.contains("red") && type == Material.RED_WOOL) || (teamname.contains("yellow") && type == Material.YELLOW_WOOL) ||
				(teamname.contains("green") && type == Material.LIME_WOOL) || (teamname.contains("blue") && type == Material.LIGHT_BLUE_WOOL)) {
			if (!(player.getInventory().contains(Material.RED_WOOL, 512) || player.getInventory().contains(Material.YELLOW_WOOL, 512) || player.getInventory().contains(Material.LIME_WOOL, 512) || player.getInventory().contains(Material.LIGHT_BLUE_WOOL, 512))) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
		updateLevels(player);
	}

	@EventHandler
	public void onPlayerJoining(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Team team = board.getPlayerTeam(player);
		String teamname = team.getName();

		event.setJoinMessage("");
		for(Player players : Bukkit.getOnlinePlayers()) {
			ChatColor color = ChatColor.WHITE;

			if(teamname.contains("red")) {
				color = ChatColor.RED;
			}
			if(teamname.contains("yellow")) {
				color = ChatColor.YELLOW;
			}
			if(teamname.contains("green")) {
				color = ChatColor.GREEN;
			}
			if(teamname.contains("blue")) {
				color = ChatColor.AQUA;
			}

			players.sendMessage(color + player.getName() + " зашёл на сервер.");
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Team team = board.getPlayerTeam(player);
		String teamname = team.getName();

		event.setQuitMessage("");
		for(Player players : Bukkit.getOnlinePlayers()) {
			ChatColor color = ChatColor.WHITE;

			if(teamname.contains("red")) {
				color = ChatColor.RED;
			}
			if(teamname.contains("yellow")) {
				color = ChatColor.YELLOW;
			}
			if(teamname.contains("green")) {
				color = ChatColor.GREEN;
			}
			if(teamname.contains("blue")) {
				color = ChatColor.AQUA;
			}

			players.sendMessage(color + player.getName() + " вышел с сервера.");
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Team team = board.getPlayerTeam(player);
		String teamname = team.getName();
		String msg = event.getMessage();

		event.setCancelled(true);

		ChatColor color = ChatColor.WHITE;

		if(teamname.contains("red")) {
			color = ChatColor.RED;
		}
		if(teamname.contains("yellow")) {
			color = ChatColor.YELLOW;
		}
		if(teamname.contains("green")) {
			color = ChatColor.GREEN;
		}
		if(teamname.contains("blue")) {
			color = ChatColor.AQUA;
		}

		if(player.getScoreboardTags().contains("ingame")) {
			if (msg.startsWith("!")) {
				for (Player players : Bukkit.getOnlinePlayers()) {
					players.sendMessage(ChatColor.AQUA + "[!] " + color + player.getName() + ChatColor.WHITE + ": " + msg.replaceFirst("!", ""));
				}
			} else {
				for (OfflinePlayer players0 : team.getPlayers()) {
					if (players0.isOnline()) {
						Player players = players0.getPlayer();

						players.sendMessage(ChatColor.DARK_GREEN + "[Команда] " + color + player.getName() + ChatColor.WHITE + ": " + color + msg);
					}
				}
			}
		} else {
			for (Player players : Bukkit.getOnlinePlayers()) {
				players.sendMessage(color + player.getName() + ChatColor.WHITE + ": " + msg);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		List<Block> genBlockList = new ArrayList<>(genAblocks);
		genBlockList.addAll(genBblocks);
		genBlockList.addAll(genCblocks);
		genBlockList.addAll(genDblocks);

		if (!genBlockList.contains(block)) {
			ppbs.add(block);
		}

		updateLevels(player);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Material type = block.getType();
		Player player = event.getPlayer();

		Team team = board.getPlayerTeam(player);
		String teamname = team.getName();

		List<Block> genBlockList = new ArrayList<>(genAblocks);
		genBlockList.addAll(genBblocks);
		genBlockList.addAll(genCblocks);
		genBlockList.addAll(genDblocks);

		if (!player.getScoreboardTags().contains("canbreak") && !genBlockList.contains(block)) {
			event.setCancelled(true);
		}

		if (player.getScoreboardTags().contains("ingame")) {

			boolean materialstuff = (type == Material.WHITE_WOOL);

			if (teamname.contains("red")) {
				materialstuff = (type == Material.WHITE_WOOL || type == Material.RED_WOOL);
			}
			if (teamname.contains("yellow")) {
				materialstuff = (type == Material.WHITE_WOOL || type == Material.YELLOW_WOOL);
			}
			if (teamname.contains("green")) {
				materialstuff = (type == Material.WHITE_WOOL || type == Material.LIME_WOOL);
			}
			if (teamname.contains("blue")) {
				materialstuff = (type == Material.WHITE_WOOL || type == Material.LIGHT_BLUE_WOOL);
			}

			if(!hardmode) {
				if (genBlockList.contains(block)) {
					if (block.getType() != Material.WHITE_CONCRETE && block.getType() != Material.RED_CONCRETE && block.getType() != Material.YELLOW_CONCRETE && block.getType() != Material.LIME_CONCRETE && block.getType() != Material.LIGHT_BLUE_CONCRETE) {
						if (!((teamname.contains("red") && block.getType() == Material.RED_WOOL) || (teamname.contains("yellow") && block.getType() == Material.YELLOW_WOOL) || (teamname.contains("green") && block.getType() == Material.LIME_WOOL) || (teamname.contains("blue") && block.getType() == Material.LIGHT_BLUE_WOOL))) {
							event.setCancelled(true);
							block.setType(Material.AIR);

							materialstuff = (type == Material.WHITE_WOOL || type == Material.RED_WOOL || type == Material.YELLOW_WOOL || type == Material.LIME_WOOL || type == Material.LIGHT_BLUE_WOOL);

						} else {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
			} else {
				event.setCancelled(true);
			}

			if (teamname.contains("red")) {
				if (materialstuff || ppbs.contains(block)) {
					if (!player.getInventory().contains(Material.RED_WOOL, 512)) {
						ItemStack woolitem = new ItemStack(Material.RED_WOOL, 1);
						ItemMeta woolmeta = woolitem.getItemMeta();
						woolmeta.displayName(getByLang(lang, "woolbattle.redWool"));
						woolitem.setItemMeta(woolmeta);
						player.getInventory().addItem(woolitem);
					} else {
						player.sendTitle(" ", ChatColor.RED + "Лимит шерсти достигнут", 0, 15, 10);
					}
				} else {
					player.sendTitle(" ", ChatColor.RED + "Вы не можете это ломать", 0, 15, 10);
				}
			}
			if (teamname.contains("yellow")) {
				if (materialstuff || ppbs.contains(block)) {
					if (!player.getInventory().contains(Material.YELLOW_WOOL, 512)) {
						ItemStack woolitem = new ItemStack(Material.YELLOW_WOOL, 1);
						ItemMeta woolmeta = woolitem.getItemMeta();
						woolmeta.displayName(getByLang(lang, "woolbattle.yellowWool"));
						woolitem.setItemMeta(woolmeta);
						player.getInventory().addItem(woolitem);
					} else {
						player.sendTitle(" ", ChatColor.RED + "Лимит шерсти достигнут", 0, 15, 10);
					}
				} else {
					player.sendTitle(" ", ChatColor.RED + "Вы не можете это ломать", 0, 15, 10);
				}
			}
			if (teamname.contains("green")) {
				if (materialstuff || ppbs.contains(block)) {
					if (!player.getInventory().contains(Material.LIME_WOOL, 512)) {
						ItemStack woolitem = new ItemStack(Material.LIME_WOOL, 1);
						ItemMeta woolmeta = woolitem.getItemMeta();
						woolmeta.displayName(getByLang(lang, "woolbattle.greenWool"));
						woolitem.setItemMeta(woolmeta);
						player.getInventory().addItem(woolitem);
					} else {
						player.sendTitle(" ", ChatColor.RED + "Лимит шерсти достигнут", 0, 15, 10);
					}
				} else {
					player.sendTitle(" ", ChatColor.RED + "Вы не можете это ломать", 0, 15, 10);
				}
			}
			if (teamname.contains("blue")) {
				if (materialstuff || ppbs.contains(block)) {
					if (!player.getInventory().contains(Material.LIGHT_BLUE_WOOL, 512)) {
						ItemStack woolitem = new ItemStack(Material.LIGHT_BLUE_WOOL, 1);
						ItemMeta woolmeta = woolitem.getItemMeta();
						woolmeta.displayName(getByLang(lang, "woolbattle.blueWool"));
						woolitem.setItemMeta(woolmeta);
						player.getInventory().addItem(woolitem);
					} else {
						player.sendTitle(" ", ChatColor.RED + "Лимит шерсти достигнут", 0, 15, 10);
					}
				} else {
					player.sendTitle(" ", ChatColor.RED + "Вы не можете это ломать", 0, 15, 10);
				}
			}
		}
		if (ppbs.contains(block)) {
			block.setType(Material.AIR);
		}

		updateLevels(player);
	}

	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event) {
		try {
			Player player = event.getPlayer();
			ItemStack item = Objects.requireNonNull(event.getItem());
			ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
			String displayname = Objects.requireNonNull(meta.getDisplayName());
			if (event.getAction() == Action.RIGHT_CLICK_AIR) {

				try {
					ItemStack item0 = player.getItemInHand();

				} catch (Throwable ignored) {}

				if (displayname.equals(ChatColor.GREEN + "Надувной Батут")) {
					Team team = board.getPlayerTeam(player);
					String teamname = team.getName();

					boolean hasItems = woolRemove(208, player, teamname);

					if (hasItems) {
						Location loc = player.getLocation();
						int height = (int) (loc.getY() - 30);
						height = Math.max(height, 170);
						loc.setY(height);

						List<Block> blocclist = new ArrayList<Block>();
						Arrays.stream(HorizontalFaces).map(loc.getBlock()::getRelative).forEach(blocclist::add);

						for (Block blocc : blocclist) {
							if (blocc.getType() == Material.AIR) {
								blocc.setType(Material.SLIME_BLOCK);
								player.playSound(player.getLocation(), Sound.ENTITY_SLIME_ATTACK, 0.25F, 1);
							}
							Bukkit.getScheduler().runTaskLater(this, () -> blocc.setType(Material.AIR), 120L);

							player.setVelocity(new Vector(0, -1, 0));
						}
					} else {
						player.sendActionBar(Component.text(ChatColor.RED + "Недостаточно шерсти!"));
					}
				}
				if (displayname.equals(ChatColor.RED + "Платформа!")) {
					Team team = board.getPlayerTeam(player);
					String teamname = team.getName();

					boolean hasItems = woolRemove(152, player, teamname);

					if (hasItems) {
						Location loc = player.getLocation();
						int height = (int) (loc.getY() - 10);
						height = Math.max(height, 170);
						loc.setY(height);

						Material mat = Material.WHITE_WOOL;

						if (teamname.contains("red")) {
							mat = Material.RED_WOOL;
						}
						if (teamname.contains("yellow")) {
							mat = Material.YELLOW_WOOL;
						}
						if (teamname.contains("green")) {
							mat = Material.LIME_WOOL;
						}
						if (teamname.contains("blue")) {
							mat = Material.LIGHT_BLUE_WOOL;
						}

						List<Block> blocclist = new ArrayList<Block>();
						Arrays.stream(HorizontalFaces).map(loc.getBlock()::getRelative).forEach(blocclist::add);

						for (Block blocc : blocclist) {
							if (blocc.getType() == Material.AIR) {
								blocc.setType(mat);
								ppbs.add(blocc);
								player.playSound(player.getLocation(), Sound.BLOCK_WOOL_PLACE, 0.5F, 1);
							}
						}
					} else {
						player.sendActionBar(Component.text(ChatColor.RED + "Недостаточно шерсти!"));
					}

				}
			}
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (displayname.equals(ChatColor.YELLOW + "Взрывная Палка")) {
					Team team = board.getPlayerTeam(player);
					String teamname = team.getName();

					boolean hasItems = woolRemove(28, player, teamname);

					if (hasItems) {
						double x = player.getEyeLocation().getDirection().getX();
						double z = player.getEyeLocation().getDirection().getZ();
						double y = 0.4;

						x = x * 1.9;
						z = z * 1.9;

						x = -x;
						z = -z;

						player.setVelocity(player.getVelocity().add((new Vector(x, y, z))));

						Location loc = event.getClickedBlock().getLocation();
						loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 2F);
						}
					} else {
						player.sendActionBar(Component.text(ChatColor.RED + "Недостаточно шерсти!"));
					}
				} else if (displayname.equals(ChatColor.YELLOW + "Взрывная Палка (T2)")) {
					Team team = board.getPlayerTeam(player);
					String teamname = team.getName();

					boolean hasItems = woolRemove(40, player, teamname);

					if (hasItems) {

						double x = player.getEyeLocation().getDirection().getX();
						double z = player.getEyeLocation().getDirection().getZ();
						double y = 0.5;

						x = x * 2.3;
						z = z * 2.3;

						x = -x;
						z = -z;

						player.setVelocity(player.getVelocity().add((new Vector(x, y, z))));

						Location loc = event.getClickedBlock().getLocation();
						loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1F, 2);
						}
					} else {
						player.sendActionBar(Component.text(ChatColor.RED + "Недостаточно шерсти!"));
					}
				} else if(displayname.equals(ChatColor.AQUA + "Ножницы")) {
					Block block = event.getClickedBlock();
					if(genAblocks.contains(block) || genBblocks.contains(block) || genCblocks.contains(block) || genDblocks.contains(block)) {
						Team team = board.getPlayerTeam(player);
						String teamname = team.getName();

						Material mat = Material.AIR;
						if(teamname.contains("red")) {
							mat = Material.RED_WOOL;
						}
						if(teamname.contains("yellow")) {
							mat = Material.YELLOW_WOOL;
						}
						if(teamname.contains("green")) {
							mat = Material.LIME_WOOL;
						}
						if(teamname.contains("blue")) {
							mat = Material.LIGHT_BLUE_WOOL;
						}

						if(block.getType() == mat) {
							boolean hasItems = woolRemove(6, player, teamname);
							if (hasItems) {
								block.setType(Material.AIR);
								player.playSound(block.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 2);
							} else {
								player.sendActionBar(Component.text(ChatColor.RED + "Недостаточно шерсти!"));
							}
						}
					}
				}
			}
		} catch (Exception | Error exception) {
		}


	}

	public boolean woolRemove(int itemcount, Player player, String teamname) {
		boolean hasItems = false;

		if (teamname.contains("red")) {
			if (player.getInventory().contains(Material.RED_WOOL, itemcount)) {
				ItemStack woolitem = new ItemStack(Material.RED_WOOL, itemcount);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.redWool"));
				woolitem.setItemMeta(woolmeta);
				player.getInventory().removeItem(woolitem);
				hasItems = true;
			}
		}
		if (teamname.contains("yellow")) {
			if (player.getInventory().contains(Material.YELLOW_WOOL, itemcount)) {
				ItemStack woolitem = new ItemStack(Material.YELLOW_WOOL, itemcount);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.yellowWool"));
				woolitem.setItemMeta(woolmeta);
				player.getInventory().removeItem(woolitem);
				hasItems = true;
			}
		}
		if (teamname.contains("green")) {
			if (player.getInventory().contains(Material.LIME_WOOL, itemcount)) {
				ItemStack woolitem = new ItemStack(Material.LIME_WOOL, itemcount);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.greenWool"));
				woolitem.setItemMeta(woolmeta);
				player.getInventory().removeItem(woolitem);
				hasItems = true;
			}
		}
		if (teamname.contains("blue")) {
			if (player.getInventory().contains(Material.LIGHT_BLUE_WOOL, itemcount)) {
				ItemStack woolitem = new ItemStack(Material.LIGHT_BLUE_WOOL, itemcount);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.blueWool"));
				woolitem.setItemMeta(woolmeta);
				player.getInventory().removeItem(woolitem);
				hasItems = true;
			}
		}

		updateLevels(player);

		return hasItems;
	}

	public ItemStack randomLoot(ItemStack loot1item) {
		int max = 14;
		int min = 1;
		int loot1 = (int) (Math.random() * (max - min + 1)) + min;
		ItemMeta loot1meta = loot1item.getItemMeta();
		List<String> loot1lore = new ArrayList<String>();

		switch (loot1) {
			case 1 -> {
				loot1item.setType(Material.SHIELD);
				loot1meta.setDisplayName(ChatColor.GRAY + "Щит");
				loot1lore.add(ChatColor.GRAY + "Щит для блокировки стрел.");
				loot1lore.add(ChatColor.RED + "Начальная прочность - 16");
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setDurability((short) 320);
			}
			case 2 -> {
				loot1item.setType(Material.OAK_LEAVES);
				loot1meta.setDisplayName(ChatColor.DARK_GREEN + "Листья");
				loot1lore.add(ChatColor.GRAY + "Не ломаются стрелами.");
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setAmount(16);
			}
			case 3, 11 -> {
				loot1item.setType(Material.STONE_AXE);
				loot1meta.setDisplayName(ChatColor.AQUA + "Топор");
				loot1meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
				loot1lore.add(ChatColor.GRAY + "Топор с большим кнокбеком.");
				loot1lore.add(ChatColor.RED + "Начальная прочность - 16");
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setDurability((short) 115);
			}
			case 4, 13 -> {
				ItemStack potion = new ItemStack(Material.POTION);
				PotionMeta potionmeta = (PotionMeta) potion.getItemMeta();
				potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 1800, 1), true);
				potionmeta.setDisplayName(ChatColor.GREEN + "Зелье Прыгучести (1:30)");
				potionmeta.setColor(Color.LIME);
				potion.setItemMeta(potionmeta);
				loot1item.setType(Material.POTION);
				loot1item = potion;
			}
			case 5 -> {
				loot1item.setType(Material.OAK_LEAVES);
				loot1meta.setDisplayName(ChatColor.DARK_GREEN + "Листья");
				loot1lore.add(ChatColor.GRAY + "Не ломаются стрелами.");
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setAmount(32);
			}
			case 6, 10 -> {
				loot1item.setType(Material.BRICK);
				loot1meta.setDisplayName(ChatColor.RED + "Платформа!");
				loot1lore.add(ChatColor.GRAY + "Ставит платформу из шерсти." + ChatColor.YELLOW + "" + ChatColor.BOLD + " ПКМ" + ChatColor.DARK_GRAY + " (Не пропадает)");
				loot1lore.add(ChatColor.AQUA + "Стоимость: 152 шерсти");
				loot1meta.addEnchant(Enchantment.DURABILITY, 1, true);
				loot1meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
			}
			case 7 -> {
				loot1item.setType(Material.ENDER_PEARL);
				loot1meta.setDisplayName(ChatColor.DARK_PURPLE + "Эндер Булочка");
				loot1lore.add(ChatColor.GRAY + "Телепортирует.");
				loot1meta.addEnchant(Enchantment.DURABILITY, 1, true);
				loot1meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setAmount(2);
			}
			case 8, 12 -> {
				loot1item.setType(Material.ENDER_PEARL);
				loot1meta.setDisplayName(ChatColor.DARK_PURPLE + "Эндер Булочка");
				loot1lore.add(ChatColor.GRAY + "Телепортирует.");
				loot1meta.addEnchant(Enchantment.DURABILITY, 1, true);
				loot1meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setAmount(1);
			}
			case 9 -> {
				loot1item.setType(Material.OAK_LEAVES);
				loot1meta.setDisplayName(ChatColor.DARK_GREEN + "Листья");
				loot1lore.add(ChatColor.GRAY + "Не ломаются стрелами.");
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setAmount(8);
			}
			case 14 -> {
				loot1item.setType(Material.BLAZE_ROD);
				loot1meta.setDisplayName(ChatColor.YELLOW + "Взрывная Палка (T2)");
				loot1lore.add(ChatColor.GRAY + "Палка откидывающая тебя назад." + ChatColor.YELLOW + "" + ChatColor.BOLD + " ПКМ" + ChatColor.DARK_GRAY + " (Нужно наводится на блок)");
				loot1lore.add(ChatColor.AQUA + "Стоимость: 40 шерсти");
				loot1meta.addEnchant(Enchantment.DURABILITY, 0, true);
				loot1meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				loot1meta.setLore(loot1lore);
				loot1item.setItemMeta(loot1meta);
				loot1item.setAmount(1);
			}
		}
		return loot1item;
	}

	private static final BlockFace[] HorizontalFaces = new BlockFace[]{
			BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST,
			BlockFace.WEST, BlockFace.SELF, BlockFace.EAST,
			BlockFace.SOUTH_WEST, BlockFace.SOUTH, BlockFace.SOUTH_EAST
	};

	public void activateHardmode() {
		hardmode = true;
		mainworld.getWorldBorder().setSize(90, 90);
		Bukkit.getScheduler().runTaskLater(this, () -> mainworld.getWorldBorder().setSize(17, 70), 2100L);
		for (Player player1 : mainworld.getPlayers()) {
			player1.sendTitle(ChatColor.RED + "Хардмод!", ChatColor.GRAY + "Постарайся выжить!", 5, 30, 15);
			player1.playSound(player1.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 0);
			player1.playSound(player1.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 0);

			worldBorderTask = Bukkit.getScheduler().runTaskLater(this, () -> {
				player1.sendTitle(ChatColor.RED + "Генераторы", ChatColor.GRAY + "Заблокированы!", 5, 30, 15);
				player1.playSound(player1.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.8F, 0);
				player1.playSound(player1.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.8F, 1);
				player1.playSound(player1.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.8F, 2);
			}, 50L);
		}
		genAstatus = genAstatus + ChatColor.GRAY + " ⚠";
		genBstatus = genBstatus + ChatColor.GRAY + " ⚠";
		genCstatus = genCstatus + ChatColor.GRAY + " ⚠";
		genDstatus = genDstatus + ChatColor.GRAY + " ⚠";
	}

	public void clearScoreboard() {
		fakekills.getScoreboard().resetScores(ChatColor.RED + "Убито красной командой" + ChatColor.WHITE + ": " + ChatColor.RED + redkills);
		fakekills.getScoreboard().resetScores(ChatColor.YELLOW + "Убито жёлтой командой" + ChatColor.WHITE + ": " + ChatColor.YELLOW + yellowkills);
		fakekills.getScoreboard().resetScores(ChatColor.GREEN + "Убито зелёной командой" + ChatColor.WHITE + ": " + ChatColor.GREEN + greenkills);
		fakekills.getScoreboard().resetScores(ChatColor.AQUA + "Убито синей командой" + ChatColor.WHITE + ": " + ChatColor.AQUA + bluekills);

		fakekills.getScoreboard().resetScores(ChatColor.RED + "Убито красной командой" + ChatColor.WHITE + ": " + ChatColor.RED + (redkills-1));
		fakekills.getScoreboard().resetScores(ChatColor.YELLOW + "Убито жёлтой командой" + ChatColor.WHITE + ": " + ChatColor.YELLOW + (yellowkills-1));
		fakekills.getScoreboard().resetScores(ChatColor.GREEN + "Убито зелёной командой" + ChatColor.WHITE + ": " + ChatColor.GREEN + (greenkills-1));
		fakekills.getScoreboard().resetScores(ChatColor.AQUA + "Убито синей командой" + ChatColor.WHITE + ": " + ChatColor.AQUA + (bluekills-1));

		fakekills.getScoreboard().resetScores(ChatColor.RED + " ");

		if (seconds0[0] < 10) {
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes0[0] + ":" + "0" + seconds0[0] + nextevent0);
		} else {
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes0[0] + ":" + seconds0[0] + nextevent0);
		}

		if (seconds[0] < 10) {
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes[0] + ":" + "0" + seconds[0] + nextevent0);
		} else {
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Время: " + ChatColor.YELLOW + minutes[0] + ":" + seconds[0] + nextevent0);
		}

		fakekills.getScoreboard().resetScores(ChatColor.GOLD + " ");

		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор A - " + genAstatus);
		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор B - " + genBstatus);
		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор C - " + genCstatus);
		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор D - " + genDstatus);

		Bukkit.getScheduler().cancelTask(scoreboardTask);

		actualgametime[0] = 0;
		actualgametime0[0] = -1;
		seconds[0] = 0;
		seconds0[0] = -1;
		minutes[0] = 0;
		minutes0[0] = 0;

		genAstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();
		genBstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();
		genCstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();
		genDstatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();

		String nextevent = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";
		String nextevent0 = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";
	}

	public void lootGenerator() {
		for (Entity entity : mainworld.getEntities()) {
			if (entity.getScoreboardTags().contains("lootstands")) {
				ArmorStand lootstand = (ArmorStand) entity;

				Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
					if (lootstand.getHelmet().hasItemMeta()) {
						lootstand.setCustomNameVisible(true);
						ItemStack helmet = lootstand.getHelmet();
						ItemMeta helmetmeta = lootstand.getHelmet().getItemMeta();
						lootstand.setCustomName(helmetmeta.getDisplayName() + " " + ChatColor.DARK_GRAY + "(" + helmet.getAmount() + ")");
						lootstand.setMarker(false);

						List<Entity> nearbyEntities = lootstand.getNearbyEntities(5, 5, 5);
						List<EntityType> nearbyEntityTypes = new ArrayList<EntityType>();
						for(Entity entity0 : nearbyEntities) {
							nearbyEntityTypes.add(entity0.getType());
						}
						if(nearbyEntityTypes.contains(EntityType.PLAYER)) {
							lootstand.getWorld().spawnParticle(Particle.WAX_OFF, lootstand.getLocation().clone().add(0, 1.55, 0), 5, 0.04, 0.04 ,0.04, 0);
						}
					} else {
						lootstand.setMarker(true);
						lootstand.setCustomNameVisible(false);
						lootstand.setCustomName("");
					}
				}, 1L, 10L);

				ItemStack lootitem = new ItemStack(Material.WHITE_CONCRETE);
				lootitem = randomLoot(lootitem);
				lootstand.setHelmet(lootitem);
			}
		}
	}

	public void updateLevels(Player player) {
		Team team = board.getPlayerTeam(player);
		String teamname = team.getName();

		int woolcount = 0;

		if (teamname.contains("red")) {
			for (ItemStack contents : player.getInventory().getContents()) {
				if (contents != null && contents.getType().equals(Material.RED_WOOL)) {
					woolcount += contents.getAmount();
				}
			}
		}
		if (teamname.contains("yellow")) {
			for (ItemStack contents : player.getInventory().getContents()) {
				if (contents != null && contents.getType().equals(Material.YELLOW_WOOL)) {
					woolcount += contents.getAmount();
				}
			}
		}
		if (teamname.contains("green")) {
			for (ItemStack contents : player.getInventory().getContents()) {
				if (contents != null && contents.getType().equals(Material.LIME_WOOL)) {
					woolcount += contents.getAmount();
				}
			}
		}
		if (teamname.contains("blue")) {
			for (ItemStack contents : player.getInventory().getContents()) {
				if (contents != null && contents.getType().equals(Material.LIGHT_BLUE_WOOL)) {
					woolcount += contents.getAmount();
				}
			}
		}
		player.setExp(0);
		player.setLevel(woolcount);
	}

	public ArrayList<Block> getBlox(Block start, int radius) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
			for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++) {
				for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
					Location loc = new Location(start.getWorld(), x, y, z);
					blocks.add(loc.getBlock());
				}
			}
		}
		return blocks;
	}

	public int countGenWool(int redcount, int yellowcount, int greencount, int bluecount, List<Block> blocklist) {
		int integer = 0;

		for (Block block : blocklist) {
			if (block.getType().equals(Material.RED_WOOL)) {
				redcount = redcount + 1;
			}
			if (block.getType().equals(Material.YELLOW_WOOL)) {
				yellowcount = yellowcount + 1;
			}
			if (block.getType().equals(Material.LIME_WOOL)) {
				greencount = greencount + 1;
			}
			if (block.getType().equals(Material.LIGHT_BLUE_WOOL)) {
				bluecount = bluecount + 1;
			}

			if (redcount == 98) {
				integer = 1;
			}
			if (yellowcount == 98) {
				integer = 2;
			}
			if (greencount == 98) {
				integer = 3;
			}
			if (bluecount == 98) {
				integer = 4;
			}

		}
		return integer;
	}


	public void genConquerChecks(List<Block> gen, List<Block> genLONG, String genLetter) {
		if(!hardmode) {
			String genStatus = getByLang(lang, "woolbattle.generator.uncaptured").toString();
			if (gen == genAblocks) {
				genStatus = genAstatus;
			}
			if (gen == genBblocks) {
				genStatus = genBstatus;
			}
			if (gen == genCblocks) {
				genStatus = genCstatus;
			}
			if (gen == genDblocks) {
				genStatus = genDstatus;
			}

			int redcount = 0;
			int yellowcount = 0;
			int greencount = 0;
			int bluecount = 0;

			for (Player player : Bukkit.getOnlinePlayers()) {
				for (Block block : gen) {

					int genowner = countGenWool(redcount, yellowcount, greencount, bluecount, gen);

					switch (genowner) {
						case 1:
							for (Block blocc : genLONG) {
								if (String.valueOf(blocc.getType()).contains("CONCRETE") && blocc.getType() != Material.AIR) {
									blocc.setType(Material.RED_CONCRETE);
								}
								if (!genStatus.equals(ChatColor.RED + "КРАСНЫЙ")) {
									genBroadcast(genLetter, 1);
									genStatus = ChatColor.RED + "КРАСНЫЙ";
								}
							}
							break;
						case 2:
							for (Block blocc : genLONG) {
								if (String.valueOf(blocc.getType()).contains("CONCRETE") && blocc.getType() != Material.AIR) {
									blocc.setType(Material.YELLOW_CONCRETE);
								}
								if (!genStatus.equals(ChatColor.YELLOW + "ЖЁЛТЫЙ")) {
									genBroadcast(genLetter, 2);
									genStatus = ChatColor.YELLOW + "ЖЁЛТЫЙ";
								}
							}
							break;
						case 3:
							for (Block blocc : genLONG) {
								if (String.valueOf(blocc.getType()).contains("CONCRETE") && blocc.getType() != Material.AIR) {
									blocc.setType(Material.LIME_CONCRETE);
								}
								if (!genStatus.equals(ChatColor.GREEN + "ЗЕЛЁНЫЙ")) {
									genBroadcast(genLetter, 3);
									genStatus = ChatColor.GREEN + "ЗЕЛЁНЫЙ";
								}
							}
							break;
						case 4:
							for (Block blocc : genLONG) {
								if (String.valueOf(blocc.getType()).contains("CONCRETE") && blocc.getType() != Material.AIR) {
									blocc.setType(Material.LIGHT_BLUE_CONCRETE);
								}
								if (!genStatus.equals(ChatColor.AQUA + "СИНИЙ")) {
									genBroadcast(genLetter, 4);
									genStatus = ChatColor.AQUA + "СИНИЙ";
								}
							}
							break;
					}
				}
			}
			if (gen == genAblocks) {
				genAstatus = genStatus;
			}
			if (gen == genBblocks) {
				genBstatus = genStatus;
			}
			if (gen == genCblocks) {
				genCstatus = genStatus;
			}
			if (gen == genDblocks) {
				genDstatus = genStatus;
			}
		}
	}

	public void genBroadcast(String genLetter, int genowner) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(genowner == 1) {
				player.sendTitle(ChatColor.WHITE + "Генератор " + genLetter + " захвачен", ChatColor.RED + "КРАСНЫМИ", 0, 60, 20);
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.65F, 2);
			}
			if(genowner == 2) {
				player.sendTitle(ChatColor.WHITE + "Генератор " + genLetter + " захвачен", ChatColor.YELLOW + "ЖЁЛТЫМИ", 0, 60, 20);
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.65F, 2);
			}
			if(genowner == 3) {
				player.sendTitle(ChatColor.WHITE + "Генератор " + genLetter + " захвачен", ChatColor.GREEN + "ЗЕЛЁНЫМИ", 0, 60, 20);
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.65F, 2);
			}
			if(genowner == 4) {
				player.sendTitle(ChatColor.WHITE + "Генератор " + genLetter + " захвачен", ChatColor.AQUA + "СИНИМИ", 0, 60, 20);
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.65F, 2);
			}
		}
	}

	public void recoloringGenerators(List<Block> genLONG, List<Block> gen) {
		for(Block block : genLONG) {
			if(String.valueOf(block.getType()).contains("CONCRETE")) {
				block.setType(Material.WHITE_CONCRETE);
			}
			if(String.valueOf(block.getType()).contains("WOOL")) {
				block.setType(Material.WHITE_WOOL);
			}
		}
		for(Block block : gen) {
			if(block.getType().equals(Material.AIR)) {
				block.setType(Material.WHITE_WOOL);
			}
		}
	}
	public void resetGeneratorText() {

		List<String> genStatuses = new ArrayList<String>();
		genStatuses.add(getByLang(lang, "woolbattle.generator.uncaptured").toString());
		genStatuses.add(ChatColor.RED + "КРАСНЫЙ");
		genStatuses.add(ChatColor.YELLOW + "ЖЁЛТЫЙ");
		genStatuses.add(ChatColor.GREEN + "ЗЕЛЁНЫЙ");
		genStatuses.add(ChatColor.AQUA + "СИНИЙ");
		for(String genStatus : genStatuses) {
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор A - " + genStatus);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор B - " + genStatus);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор C - " + genStatus);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор D - " + genStatus);
		}
	}

	public void stopGame() {
		for (Block block : ppbs) {
			block.setType(Material.AIR);
		}
		for (Player player1 : Bukkit.getOnlinePlayers()) {
			if(player1.getScoreboardTags().contains("ingame")) {
				player1.teleport(new Location(player1.getWorld(), 9, -34, 9));
				player1.getInventory().clear();
				player1.removePotionEffect(PotionEffectType.JUMP);
				player1.setGameMode(GameMode.SURVIVAL);
				player1.setHealth(player1.getMaxHealth());

				updateLevels(player1);
			}
		}
		recoloringGenerators(genAblocksLONG, genAblocks);
		recoloringGenerators(genBblocksLONG, genBblocks);
		recoloringGenerators(genCblocksLONG, genCblocks);
		recoloringGenerators(genDblocksLONG, genDblocks);

		mainworld.getWorldBorder().setSize(200, 1);
		hardmode = false;
		gameactive = false;

		try {
			worldBorderTask.cancel();
		} catch (Throwable ignored) {}

		clearScoreboard();
	}

	public void winningBroadcast(int winner) {

		int fadeIn = 5;
		int hold = 40;
		int fadeOut = 30;

		for(Player player : Bukkit.getOnlinePlayers()) {
			if(winner == 1) {
				player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "КРАСНЫЕ", ChatColor.RESET + "Победили!", fadeIn, hold, fadeOut);
			}
			if(winner == 2) {
				player.sendTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "ЖЁЛТЫЕ", ChatColor.RESET + "Победили!", fadeIn, hold, fadeOut);
			}
			if(winner == 3) {
				player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "ЗЕЛЁНЫЕ", ChatColor.RESET + "Победили!", fadeIn, hold, fadeOut);
			}
			if(winner == 4) {
				player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "СИНИЕ", ChatColor.RESET + "Победили!", fadeIn, hold, fadeOut);
			}
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.2F);
			Bukkit.getScheduler().runTaskLater(this, () -> {
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.25F);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
				Bukkit.getScheduler().runTaskLater(this, () -> {
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.6F);
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.8F);
				}, 3L);
			}, 3L);
		}
	}

	public void resetEveryFuckingKillScoreboard() {
		fakekills.getScoreboard().resetScores(ChatColor.RED + "Убито красной командой" + ChatColor.WHITE + ": " + ChatColor.RED + redkills);
		fakekills.getScoreboard().resetScores(ChatColor.RED + "Убито красной командой" + ChatColor.WHITE + ": " + ChatColor.RED + (redkills-1));
		redkills = 0;
		fakekills.getScoreboard().resetScores(ChatColor.YELLOW + "Убито жёлтой командой" + ChatColor.WHITE + ": " + ChatColor.YELLOW + yellowkills);
		fakekills.getScoreboard().resetScores(ChatColor.YELLOW + "Убито жёлтой командой" + ChatColor.WHITE + ": " + ChatColor.YELLOW + (yellowkills-1));
		yellowkills = 0;
		fakekills.getScoreboard().resetScores(ChatColor.GREEN + "Убито зелёной командой" + ChatColor.WHITE + ": " + ChatColor.GREEN + greenkills);
		fakekills.getScoreboard().resetScores(ChatColor.GREEN + "Убито зелёной командой" + ChatColor.WHITE + ": " + ChatColor.GREEN + (greenkills-1));
		greenkills = 0;
		fakekills.getScoreboard().resetScores(ChatColor.AQUA + "Убито синей командой" + ChatColor.WHITE + ": " + ChatColor.AQUA + bluekills);
		fakekills.getScoreboard().resetScores(ChatColor.AQUA + "Убито синей командой" + ChatColor.WHITE + ": " + ChatColor.AQUA + (bluekills-1));
		bluekills = 0;
	}

	public void teleportToSpawn(Player player) {
		player.teleport(new Location(player.getWorld(), 9, -34, 9));
		player.getInventory().clear();
		player.removePotionEffect(PotionEffectType.JUMP);
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(player.getMaxHealth());

		player.removeScoreboardTag("ingame");
		player.addScoreboardTag("onspawn");
	}

	public void simulateHardmodeDeath(Player player) {
		teleportToSpawn(player);
		broadcastFinalDeath(player);
	}

	public void broadcastDeath(Player dead, String deathcause) {
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(players.getScoreboardTags().contains("ingame") || players == dead) {
				Team team = board.getPlayerTeam(dead);
				String teamname = team.getName();

				ChatColor color = ChatColor.WHITE;
				if(teamname.contains("red")) {
					color = ChatColor.RED;
				}
				if(teamname.contains("yellow")) {
					color = ChatColor.YELLOW;
				}
				if(teamname.contains("green")) {
					color = ChatColor.GREEN;
				}
				if(teamname.contains("blue")) {
					color = ChatColor.AQUA;
				}

				players.sendMessage(ChatColor.RED + "[☠] " + color + dead.getName() + ChatColor.GRAY + " " + deathcause);
			}
		}
	}

	public void broadcastFinalDeath(Player dead) {
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(players.getScoreboardTags().contains("ingame") || players == dead) {
				Team team = board.getPlayerTeam(dead);
				String teamname = team.getName();

				ChatColor color = ChatColor.WHITE;
				if(teamname.contains("red")) {
					color = ChatColor.RED;
				}
				if(teamname.contains("yellow")) {
					color = ChatColor.YELLOW;
				}
				if(teamname.contains("green")) {
					color = ChatColor.GREEN;
				}
				if(teamname.contains("blue")) {
					color = ChatColor.AQUA;
				}

				players.sendMessage(ChatColor.RED + "[☠] " + color + dead.getName() + ChatColor.DARK_RED + " окончательно" + ChatColor.GRAY + " умер.");
			}
		}
	}

	public void recountTeamMembers() {
		List<Player> redTeamPlayers0 = new ArrayList<Player>();
		List<Player> yellowTeamPlayers0 = new ArrayList<Player>();
		List<Player> greenTeamPlayers0 = new ArrayList<Player>();
		List<Player> blueTeamPlayers0 = new ArrayList<Player>();

		for(Player player : Bukkit.getOnlinePlayers()) {
			Team team = board.getPlayerTeam(player);
			String teamname = team.getName();

			if(teamname.contains("red")) {
				redTeamPlayers0.add(player);
			}
			if(teamname.contains("yellow")) {
				yellowTeamPlayers0.add(player);
			}
			if(teamname.contains("green")) {
				greenTeamPlayers0.add(player);
			}
			if(teamname.contains("blue")) {
				blueTeamPlayers0.add(player);
			}

			redTeamPlayers = redTeamPlayers0;
			yellowTeamPlayers = yellowTeamPlayers0;
			greenTeamPlayers = greenTeamPlayers0;
			blueTeamPlayers = blueTeamPlayers0;
		}
	}

	public void gameStartingTitle(Player player1) {
		int max = 17;
		int min = 1;
		int randomtitle = (int) (Math.random() * (max - min + 1)) + min;

		int fadeIn = 1;
		int hold = 30;
		int fadeOut = 20;

		switch (randomtitle) {
			case 1 -> player1.sendTitle(ChatColor.WHITE + "виу виу", ChatColor.WHITE + "дурка приехала", fadeIn, hold, fadeOut);
			case 2 -> player1.sendTitle(ChatColor.WHITE + "ландер", ChatColor.WHITE + "привео", fadeIn, hold, fadeOut);
			case 3 -> player1.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "WoolBattle", ChatColor.WHITE + "Игра началась.", fadeIn, hold, fadeOut);
			case 4 -> player1.sendTitle(ChatColor.WHITE + "эта миниигра", ChatColor.WHITE + "спонсирована мопспвп", fadeIn, hold, fadeOut);
			case 5 -> player1.sendTitle(ChatColor.WHITE + "русский корабль", ChatColor.WHITE + "иди нахуй", fadeIn, hold, fadeOut);
			case 6 -> player1.sendTitle(ChatColor.WHITE + "не пускайте", ChatColor.WHITE + "сюда тапка", fadeIn, hold, fadeOut);
			case 7 -> player1.sendTitle(ChatColor.WHITE + "окей", ChatColor.WHITE + "летсгоу", fadeIn, hold, fadeOut);
			case 8 -> player1.sendTitle(ChatColor.WHITE + "кот", ChatColor.WHITE + "гей", fadeIn, hold, fadeOut);
			case 9 -> player1.sendTitle(ChatColor.WHITE + "инф ты нахуя", ChatColor.WHITE + "продал мои ресы", fadeIn, hold, fadeOut);
			case 10 -> player1.sendTitle(ChatColor.WHITE + "кот", ChatColor.WHITE + "скинь логи", fadeIn, hold, fadeOut);
			case 11 -> player1.sendTitle(ChatColor.WHITE + "тут нет", ChatColor.WHITE + "голубей", fadeIn, hold, fadeOut);
			case 12 -> player1.sendTitle(ChatColor.WHITE + "NO WAY", ChatColor.WHITE + "Крис фумо", fadeIn, hold, fadeOut);
			case 13 -> player1.sendTitle(ChatColor.WHITE + "Класс!", ChatColor.WHITE + "Я негр!", fadeIn, hold, fadeOut);
			case 14 -> player1.sendTitle(ChatColor.WHITE + "ео", ChatColor.WHITE + "плши", fadeIn, hold, fadeOut);
			case 15 -> player1.sendTitle(ChatColor.WHITE + "мяу, мяри мяри", ChatColor.WHITE + "мяу мяу мяу мяу", fadeIn, hold, fadeOut);
			case 16 -> player1.sendTitle(ChatColor.WHITE + "пустите торда", ChatColor.WHITE + "на КоИ", fadeIn, hold, fadeOut);
			case 17 -> player1.sendTitle(ChatColor.WHITE + "stay", ChatColor.WHITE + "safe", fadeIn, hold, fadeOut);
		}
	}

	public void gameStartSequence(Player player1, String teamname) {
		if (teamname.contains("red")) {
			Location loc = new Location(player1.getWorld(), 9.5, 258, -27.5);
			player1.teleport(loc);
		}
		if (teamname.contains("yellow")) {
			Location loc = new Location(player1.getWorld(), -27.5, 258, 9.5);
			loc.setYaw(-90);
			player1.teleport(loc);
		}
		if (teamname.contains("green")) {
			Location loc = new Location(player1.getWorld(), 9.5, 258, 46.5);
			loc.setYaw(-180);
			player1.teleport(loc);
		}
		if (teamname.contains("blue")) {
			Location loc = new Location(player1.getWorld(), 46.5, 258, 9.5);
			loc.setYaw(90);
			player1.teleport(loc);
		}

		gameStartingTitle(player1);

		Bukkit.getScheduler().runTaskLater(this, () -> {
			player1.sendTitle(ChatColor.WHITE + "Нужно сделать", ChatColor.WHITE + String.valueOf((int) (Math.round(4 * (Bukkit.getOnlinePlayers().size() * 0.7)))) + " киллов.", 1, 40, 25);

			player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.2F);
			Bukkit.getScheduler().runTaskLater(this, () -> {
				player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.25F);
				player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
				Bukkit.getScheduler().runTaskLater(this, () -> {
					player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.6F);
					player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.8F);
				}, 3L);
			}, 3L);

		}, 51L);

		player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
		player1.removeScoreboardTag("onspawn");
		player1.removeScoreboardTag("canbreak");
		player1.addScoreboardTag("ingame");
		player1.setHealth(player1.getMaxHealth());
		player1.setGameMode(GameMode.SURVIVAL);

		try {
			damagetask0.get(player1).cancel();
		} catch (Throwable ignored) {}

		gameactive = true;

		ItemStack item1 = new ItemStack(Material.SHEARS);
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName(ChatColor.AQUA + "Ножницы");
		meta1.addEnchant(Enchantment.DIG_SPEED, 5, true);
		meta1.addEnchant(Enchantment.KNOCKBACK, 2, true);
		meta1.setUnbreakable(true);
		List<String> lore1 = new ArrayList<String>();
		lore1.add(ChatColor.GRAY + "Ножницы для копания шерсти и");
		lore1.add(ChatColor.GRAY + "скидывания игроков в бездну.");
		lore1.add("");
		lore1.add(ChatColor.AQUA + "Способность: " + ChatColor.GRAY + "Убирает блоки");
		lore1.add(ChatColor.GRAY + "своего цвета на генераторах." + ChatColor.YELLOW + "" + ChatColor.BOLD + " ПКМ");
		lore1.add(ChatColor.AQUA + "Стоимость: 6 шерсти");
		meta1.setLore(lore1);
		item1.setItemMeta(meta1);
		player1.getInventory().setItem(0, item1);

		ItemStack item2 = new ItemStack(Material.STICK);
		ItemMeta meta2 = item2.getItemMeta();
		meta2.setDisplayName(ChatColor.YELLOW + "Взрывная Палка");
		List<String> lore2 = new ArrayList<String>();
		lore2.add(ChatColor.GRAY + "Палка откидывающая тебя назад." + ChatColor.YELLOW + "" + ChatColor.BOLD + " ПКМ" + ChatColor.DARK_GRAY + " (Нужно наводится на блок)");
		lore2.add(ChatColor.AQUA + "Стоимость: 28 шерсти");
		meta2.setLore(lore2);
		item2.setItemMeta(meta2);
		player1.getInventory().setItem(1, item2);

		ItemStack item3 = new ItemStack(Material.SLIME_BALL);
		ItemMeta meta3 = item3.getItemMeta();
		meta3.setDisplayName(ChatColor.GREEN + "Надувной Батут");
		List<String> lore3 = new ArrayList<String>();
		lore3.add(ChatColor.GRAY + "Спасёт тебя при падении." + ChatColor.YELLOW + "" + ChatColor.BOLD + " ПКМ" + ChatColor.DARK_GRAY + " (Шифт чтобы прыгнуть высоко)");
		lore3.add(ChatColor.AQUA + "Стоимость: 208 шерсти");
		meta3.addEnchant(Enchantment.DURABILITY, 1, true);
		meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta3.setLore(lore3);
		item3.setItemMeta(meta3);
		player1.getInventory().setItem(2, item3);

		ItemStack item4 = new ItemStack(Material.BOW);
		ItemMeta meta4 = item4.getItemMeta();
		meta4.setDisplayName(ChatColor.AQUA + "Лук");
		List<String> lore4 = new ArrayList<String>();
		lore4.add(ChatColor.GRAY + "Лук, может ломать шерсть при попадании.");
		meta4.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
		meta4.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta4.setUnbreakable(true);
		meta4.setLore(lore4);
		item4.setItemMeta(meta4);
		player1.getInventory().setItem(8, item4);

		ItemStack item5 = new ItemStack(Material.ARROW);
		ItemMeta meta5 = item5.getItemMeta();
		meta5.setDisplayName(ChatColor.GRAY + "Стрела");
		item5.setItemMeta(meta5);
		player1.getInventory().setItem(17, item5);

		ChatColor chatcolor = ChatColor.WHITE;
		Color color = Color.fromBGR(255, 255, 255);

		if(teamname.contains("red")) {
			chatcolor = ChatColor.RED;
			color = Color.fromRGB(255, 10, 10);
		} else if(teamname.contains("yellow")) {
			chatcolor = ChatColor.YELLOW;
			color = Color.fromRGB(255, 207, 36);
		} else if(teamname.contains("green")) {
			chatcolor = ChatColor.GREEN;
			color = Color.fromRGB(105, 255, 82);
		} else if(teamname.contains("blue")) {
			chatcolor = ChatColor.AQUA;
			color = Color.fromRGB(47, 247, 227);
		}

		ItemStack item6 = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta meta6 = (LeatherArmorMeta) item6.getItemMeta();
		meta6.setDisplayName(chatcolor + "Дабл-Джамп Ботинки");
		List<String> lore6 = new ArrayList<String>();
		lore6.add(ChatColor.GRAY + "Позволяют прыгать в воздухе. ");
		lore6.add(ChatColor.DARK_GRAY + "(Нужно нажать пробел дважды в падении)");
		lore6.add(ChatColor.AQUA + "Стоимость: 16 шерсти");
		meta6.setUnbreakable(true);
		meta6.setColor(color);
		meta6.setLore(lore6);
		item6.setItemMeta(meta6);
		player1.getInventory().setItem(36, item6);

	}

	public void timedGameStart(Player player1, String teamname) {
		Bukkit.getScheduler().runTaskLater(this, () -> {
			player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);
			player1.sendTitle(ChatColor.WHITE + "Игра начнётся через", ChatColor.RED + "3", 1, 20, 20);
			Bukkit.getScheduler().runTaskLater(this, () -> {
				player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
				player1.sendTitle(ChatColor.WHITE + "Игра начнётся через", ChatColor.YELLOW + "2", 1, 20, 20);
				Bukkit.getScheduler().runTaskLater(this, () -> {
					player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.5F);
					player1.sendTitle(ChatColor.WHITE + "Игра начнётся через", ChatColor.GREEN + "1", 1, 20, 20);
					Bukkit.getScheduler().runTaskLater(this, () -> gameStartSequence(player1, teamname), 40L);
				}, 40L);
			}, 40L);
		}, 40L);
	}

	public void checkForWoolCap(Player player) {
		Team team = board.getPlayerTeam(player);
		String teamname = team.getName();

		if (teamname.contains("red")) {
			if (player.getInventory().contains(Material.RED_WOOL, 512)) {
				ItemStack woolitem = new ItemStack(Material.RED_WOOL);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.redWool"));
				woolitem.setItemMeta(woolmeta);
				woolitem.setAmount(getAmount(player, woolitem)-512);
				player.getInventory().removeItem(woolitem);
			}
		}
		if (teamname.contains("yellow")) {
			if (player.getInventory().contains(Material.YELLOW_WOOL, 512)) {
				ItemStack woolitem = new ItemStack(Material.YELLOW_WOOL);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.yellowWool"));
				woolitem.setItemMeta(woolmeta);
				woolitem.setAmount(getAmount(player, woolitem)-512);
				player.getInventory().removeItem(woolitem);
			}
		}
		if (teamname.contains("green")) {
			if (player.getInventory().contains(Material.LIME_WOOL, 512)) {
				ItemStack woolitem = new ItemStack(Material.LIME_WOOL);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.greenWool"));
				woolitem.setItemMeta(woolmeta);
				woolitem.setAmount(getAmount(player, woolitem)-512);
				player.getInventory().removeItem(woolitem);
			}
		}
		if (teamname.contains("blue")) {
			if (player.getInventory().contains(Material.LIGHT_BLUE_WOOL, 512)) {
				ItemStack woolitem = new ItemStack(Material.LIGHT_BLUE_WOOL);
				ItemMeta woolmeta = woolitem.getItemMeta();
				woolmeta.displayName(getByLang(lang, "woolbattle.blueWool"));
				woolitem.setItemMeta(woolmeta);
				woolitem.setAmount(getAmount(player, woolitem)-512);
				player.getInventory().removeItem(woolitem);
			}
		}
	}

	public static int getAmount(Player arg0, ItemStack arg1) {
		if (arg1 == null)
			return 0;
		int amount = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack slot = arg0.getInventory().getItem(i);
			if (slot == null || !slot.isSimilar(arg1))
				continue;
			amount += slot.getAmount();
		}
		return amount;
	}

	public TextComponent getByLang(String lang, String string) {
		return new Translation(translation, getLogger()).getTranslation(lang, string.replaceFirst("woolbattle.", ""));
	}
}