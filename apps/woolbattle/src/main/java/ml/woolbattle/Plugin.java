package ml.woolbattle;

import ml.mopsbase.MopsPlugin;
import ml.mopsutils.Translation;
import ml.mopsutils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
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
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class Plugin extends MopsPlugin implements Listener, CommandExecutor {

	String lang = "rus";
	// внимание добавьте адекватный выбор языка который распространяется
	// на все сервера, и скажите мне об этом
	// спасибо
	// пододжди два года и сделаю))))

	Utilities utilities = new Utilities(this);
	Abilities abilities = new Abilities(this);
	Translation translator;

	List<Block> ppbs = new ArrayList<>();
	World mainworld;
	boolean hardmode = false;
	boolean gameactive = false;

	int redkills, yellowkills, bluekills, greenkills = 0;

	int requiredKills = 0;

	String genAstatus, genBstatus, genCstatus, genDstatus = "woolbattle.generator.uncaptured";

	boolean gensLocked = false;

	HashMap<Player, ItemStack[]> savedInventory = new HashMap<>();

	List<Block> genAblocks, genBblocks, genCblocks, genDblocks;


	List<Block> genAblocksLONG, genBblocksLONG, genCblocksLONG, genDblocksLONG;

	private final HashMap<Player, Integer> combo = new HashMap<>();
	private final HashMap<Player, BukkitTask> deathmsg = new HashMap<>();




	List<Player> redTeamPlayers, yellowTeamPlayers, greenTeamPlayers, blueTeamPlayers = new ArrayList<>();

	int generatorTask;

	BukkitTask worldBorderTask;

	boolean testmode = false;

	String connectToIP = "mops.ml";

	ScoreboardManager manager;
	Scoreboard mainboard;
	Scoreboard newboard;

	

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		Logger logger = getLogger();
		logger.info("1");
		this.saveDefaultConfig();
		this.config = this.getConfig();
		logger.info("config: \n" + config.saveToString() );
		logger.info("default config: \n" + ((FileConfiguration) Objects.requireNonNull(config.getDefaults())).saveToString());
		logger.info("2");
		StringBuilder data;

		try (Scanner reader = new Scanner(Objects.requireNonNull(getResource("translations.yml")))) {
			logger.info("3");
			data = new StringBuilder();
			while (reader.hasNextLine()) {
				data.append("\n").append(reader.nextLine());
				logger.info("4");
			}
			logger.info(data.toString());
		}

		try {
			logger.info("5");
			this.translation.loadFromString(data.toString());
		} catch (InvalidConfigurationException e) {
			logger.warning(Arrays.toString(e.getStackTrace()));
		}

		logger.info("Loaded translations: \n" + translation.saveToString());
		logger.info("6");

		try {
			lang = config.getString("lang").toLowerCase(Locale.ROOT);
			logger.info("lang string: " + lang);
			if (lang.isBlank()) {
				logger.warning("lang in blank");
				lang = "rus";
				throw new Exception("couldn't load custom lang");
			}
		} catch (Exception e) {
			logger.info(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
		}

		translator = new Translation(translation, getLogger(), "woolbattle");

		this.connectToIP = config.getString("ip");

		mainworld = Bukkit.getServer().getWorlds().get(0);
		manager = Bukkit.getScoreboardManager();
		mainboard = manager.getMainScoreboard();
		newboard = manager.getNewScoreboard();
		loadGenLocation();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {

				Team team = mainboard.getPlayerTeam(player);
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
						ItemStack woolItem;
						TextComponent woolName;

						if (teamname.contains("red")) {
							logger.info(player.getName() + "'s team: " + "red");
							woolItem = new ItemStack(Material.RED_WOOL, 1296);
							woolName = getByLang(lang, "woolbattle.redWool");
						}
						else if (teamname.contains("yellow")) {
							logger.info(player.getName() + "'s team: " + "yellow");
							woolItem = new ItemStack(Material.RED_WOOL, 1296);
							woolName = getByLang(lang, "yellowWool");
						}
						else if (teamname.contains("green")) {
							logger.info(player.getName() + "'s team: " + "green");
							woolItem = new ItemStack(Material.LIME_WOOL, 1296);
							woolName = getByLang(lang, "greenWool");
						}
						else if (teamname.contains("blue")) {
							logger.info(player.getName() + "'s team: " + "blue");
							woolItem = new ItemStack(Material.LIGHT_BLUE_WOOL, 1296);
							woolName = getByLang(lang, "blueWool");
						} else {
							logger.warning("No team found for " + player.getName() + "\ntags: " + player.getScoreboardTags() + "\nteam name: " + player.getScoreboard().getPlayerTeam(player).getName());
							player.removeScoreboardTag("ingame");
							woolItem = new ItemStack(Material.AIR);
							woolName = Component.empty();
						}

						Objective lastdamage = mainboard.getObjective("lastdamagedbyteam");

						ItemMeta woolMeta = woolItem.getItemMeta();
						woolMeta.displayName(woolName);
						woolItem.setItemMeta(woolMeta);

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
							ItemStack[] savedInventory = new ItemStack[0];

							if(!player.getScoreboardTags().contains("spectator")) {
								savedInventory = player.getInventory().getContents();
								player.getInventory().clear();
								player.getInventory().remove(woolItem);
							}

							ItemStack[] finalSavedInventory = savedInventory;

							player.addScoreboardTag("spectator");
							player.hidePlayer(player);
							player.setAllowFlight(true);
							player.setFlying(true);

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
										player.showPlayer(player);
										player.setAllowFlight(false);
										player.setFlying(false);
										player.removeScoreboardTag("spectator");


										player.getInventory().setContents(finalSavedInventory);
										player.updateInventory();


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

				recountTeamMembers();

				if((redkills >= requiredKills && gameactive) || (!redTeamPlayers.isEmpty() && yellowTeamPlayers.isEmpty() && greenTeamPlayers.isEmpty() && blueTeamPlayers.isEmpty() && gameactive && !testmode)) {
					winningBroadcast(1);
					resetEveryFuckingKillScoreboard(player);
					try {
						deathmsg.get(player).cancel(); } catch (Throwable ignored) {}
					stopGame();
				}
				if((yellowkills >= requiredKills && gameactive) || (redTeamPlayers.isEmpty() && !yellowTeamPlayers.isEmpty() && greenTeamPlayers.isEmpty() && blueTeamPlayers.isEmpty() && gameactive && !testmode)) {
					winningBroadcast(2);
					resetEveryFuckingKillScoreboard(player);
					try {
						deathmsg.get(player).cancel(); } catch (Throwable ignored) {}
					stopGame();
				}
				if((greenkills >= requiredKills && gameactive) || (redTeamPlayers.isEmpty() && yellowTeamPlayers.isEmpty() && !greenTeamPlayers.isEmpty() && blueTeamPlayers.isEmpty() && gameactive && !testmode)) {
					winningBroadcast(3);
					resetEveryFuckingKillScoreboard(player);
					try {
						deathmsg.get(player).cancel(); } catch (Throwable ignored) {}
					stopGame();
				}
				if((bluekills >= requiredKills && gameactive) || (redTeamPlayers.isEmpty() && yellowTeamPlayers.isEmpty() && greenTeamPlayers.isEmpty() && !blueTeamPlayers.isEmpty()  && gameactive && !testmode)) {
					winningBroadcast(4);
					resetEveryFuckingKillScoreboard(player);
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
		}, 80L, 5L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Team team = mainboard.getPlayerTeam(player);
				assert team != null;
				String teamname = team.getName();

				if (player.getScoreboardTags().contains("onspawn") || !hardmode) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7, 100, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 7, 100, true, false));
				} else if(hardmode) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1, true, false));
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
					if (!player.getScoreboardTags().contains("spectator")) {
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
			}
		}, 80L, 1L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			genConquerChecks(genAblocks, genAblocksLONG, "A");
			genConquerChecks(genBblocks, genBblocksLONG, "B");
			genConquerChecks(genCblocks, genCblocksLONG, "C");
			genConquerChecks(genDblocks, genDblocksLONG, "D");
		}, 80L, 20L);
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
					if (!(mainboard.getPlayerTeam(player1) == null)) {
						Team team = mainboard.getPlayerTeam(player1);
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

				scoreboardTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

					if (gameactive) {
						newboard = manager.getNewScoreboard();
						Objective fakekills = newboard.registerNewObjective("fakekills", "dummy", Component.text("WoolBattle", NamedTextColor.GOLD, TextDecoration.BOLD));
						fakekills.setDisplaySlot(DisplaySlot.SIDEBAR);

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

						resetGeneratorText(player);


						String Acopy = genAstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
						String Bcopy = genBstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
						String Ccopy = genCstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
						String Dcopy = genDstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА

						if(gensLocked) {
							Acopy = Acopy + ChatColor.GRAY + " ⚠";
							Bcopy = Bcopy + ChatColor.GRAY + " ⚠";
							Ccopy = Ccopy + ChatColor.GRAY + " ⚠";
							Dcopy = Dcopy + ChatColor.GRAY + " ⚠";
						}

						fakekills.getScore(ChatColor.WHITE + "Генератор A - " + Acopy).setScore(5);
						fakekills.getScore(ChatColor.WHITE + "Генератор B - " + Bcopy).setScore(4);
						fakekills.getScore(ChatColor.WHITE + "Генератор C - " + Ccopy).setScore(3);
						fakekills.getScore(ChatColor.WHITE + "Генератор D - " + Dcopy).setScore(2);


						fakekills.getScore(ChatColor.YELLOW + " ").setScore(1);
						fakekills.getScore(ChatColor.DARK_GRAY + connectToIP + ":" + Bukkit.getPort()).setScore(0);

						for (Player p : getServer().getOnlinePlayers()) {
							if (p.getScoreboardTags().contains("ingame")) {
								p.setScoreboard(fakekills.getScoreboard());
							}
						}

					}
				}, 160L, 20L);

				Bukkit.getScheduler().cancelTask(generatorTask);

				generatorTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

					for (Player player1 : mainworld.getPlayers()) {
						Team team = mainboard.getPlayerTeam(player1);
						assert team != null;
						String teamname = team.getName();

						List<String> genStatuses = new ArrayList<>();
						genStatuses.add(genAstatus);
						genStatuses.add(genBstatus);
						genStatuses.add(genCstatus);
						genStatuses.add(genDstatus);

						for (String genStatus : genStatuses) {
							if (genStatus == null || genStatus.isBlank()) {
								genStatus = "woolbattle.generator.uncaptured";
							}

							if(player1.getScoreboardTags().contains("ingame")) {
								if (teamname.contains("red")) {
									if (genStatus.contains("woolbattle.generator.red")) {
										if (!player1.getInventory().contains(Material.RED_WOOL, 512)) {
											ItemStack woolitem = new ItemStack(Material.RED_WOOL, 1);
											ItemMeta woolmeta = woolitem.getItemMeta();
											woolmeta.displayName(getByLang(lang, "woolbattle.redWool"));
											woolitem.setItemMeta(woolmeta);
											player1.getInventory().addItem(woolitem);
										}
									}
								} else if (teamname.contains("yellow")) {
									if (genStatus.contains("woolbattle.generator.yellow")) {
										if (!player1.getInventory().contains(Material.YELLOW_WOOL, 512)) {
											ItemStack woolitem = new ItemStack(Material.YELLOW_WOOL, 1);
											ItemMeta woolmeta = woolitem.getItemMeta();
											woolmeta.displayName(getByLang(lang, "woolbattle.yellowWool"));
											woolitem.setItemMeta(woolmeta);
											player1.getInventory().addItem(woolitem);
										}
									}
								} else if (teamname.contains("green")) {
									if (genStatus.contains("woolbattle.generator.green")) {
										if (!player1.getInventory().contains(Material.LIME_WOOL, 512)) {
											ItemStack woolitem = new ItemStack(Material.LIME_WOOL, 1);
											ItemMeta woolmeta = woolitem.getItemMeta();
											woolmeta.displayName(getByLang(lang, "woolbattle.greenWool"));
											woolitem.setItemMeta(woolmeta);
											player1.getInventory().addItem(woolitem);
										}
									}
								} else if (teamname.contains("blue")) {
									if (genStatus.contains("woolbattle.generator.blue")) {
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
				clearScoreboard(player);
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
				player.sendMessage("testmode был изменён на " + testmode);
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

			Objective obj = mainboard.getObjective("lastdamagedbyteam");

			if (victim0 instanceof Player victim && attacker0 instanceof Player attacker) {

				if(!mainboard.getPlayerTeam(attacker).getName().equals(mainboard.getPlayerTeam(victim).getName())) {

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

					Team team = mainboard.getPlayerTeam(attacker);
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

				if(!mainboard.getPlayerTeam(attacker).getName().equals(mainboard.getPlayerTeam(victim).getName())) {

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
						Team team = mainboard.getPlayerTeam(attacker);
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

		} catch (Exception | Error e) {

		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItemDrop().getItemStack();

		List<Material> matList = new ArrayList<>();
		matList.add(Material.WHITE_WOOL);
		matList.add(Material.RED_WOOL);
		matList.add(Material.YELLOW_WOOL);
		matList.add(Material.LIME_WOOL);
		matList.add(Material.LIGHT_BLUE_WOOL);

		if (player.getScoreboardTags().contains("ingame")) {
			if (!matList.contains(item.getType())) {
				event.setCancelled(true);
			} else {
				event.setCancelled(false);
			}
		}
		updateLevels(player);
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();
		Material type = item.getType();

		Team team = mainboard.getPlayerTeam(player);
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
		Team team = mainboard.getPlayerTeam(player);
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
		Team team = mainboard.getPlayerTeam(player);
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
		Team team = mainboard.getPlayerTeam(player);
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

		if (!player.getScoreboardTags().contains("spectator")) {
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
		for (Player players : Bukkit.getOnlinePlayers()) {
			if(players.getScoreboardTags().contains("spectator")) {
				players.sendMessage(ChatColor.GRAY + "[Зрители] " + color + player.getName() + ChatColor.WHITE + ": " + msg);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if(player.getScoreboardTags().contains("spectator")) {
			event.setCancelled(true);
		} else {

			List<Block> genBlockList = new ArrayList<>(genAblocks);
			genBlockList.addAll(genBblocks);
			genBlockList.addAll(genCblocks);
			genBlockList.addAll(genDblocks);

			if (!genBlockList.contains(block)) {
				ppbs.add(block);
			}

			updateLevels(player);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Material type = block.getType();
		Player player = event.getPlayer();

		Team team = mainboard.getPlayerTeam(player);
		String teamname = team.getName();

		if(player.getScoreboardTags().contains("spectator")) {
			event.setCancelled(true);
		} else {

			List<Block> genBlockList = new ArrayList<>(genAblocks);
			genBlockList.addAll(genBblocks);
			genBlockList.addAll(genCblocks);
			genBlockList.addAll(genDblocks);

			if (!player.getScoreboardTags().contains("canbreak") && !genBlockList.contains(block)) {
				event.setCancelled(true);
			}

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

				if (!hardmode) {
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
							player.showTitle(genTitle(lang, null, "woolLimit", 0, 15, 10));
						}
					} else {
						player.showTitle(genTitle(lang, null, "cantBreak", 0, 15, 10));
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
							player.showTitle(genTitle(lang, null, "woolLimit", 0, 15, 10));
						}
					} else {
						player.showTitle(genTitle(lang, null, "cantBreak", 0, 15, 10));
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
							player.showTitle(genTitle(lang, null, "woolLimit", 0, 15, 10));
						}
					} else {
						player.showTitle(genTitle(lang, null, "cantBreak", 0, 15, 10));
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
							player.showTitle(genTitle(lang, null, "woolLimit", 0, 15, 10));
						}
					} else {
						player.showTitle(genTitle(lang, null, "cantBreak", 0, 15, 10));
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
					Team team = mainboard.getPlayerTeam(player);
					String teamname = team.getName();

					boolean hasItems = woolRemove(208, player, teamname);

					if (hasItems) {
						Location loc = player.getLocation();
						int height = (int) (loc.getY() - 30);
						height = Math.max(height, 170);
						loc.setY(height);

						List<Block> blocclist = new ArrayList<>();
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
					Team team = mainboard.getPlayerTeam(player);
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

						List<Block> blocclist = new ArrayList<>();
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
					Team team = mainboard.getPlayerTeam(player);
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
					Team team = mainboard.getPlayerTeam(player);
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
						Team team = mainboard.getPlayerTeam(player);
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
		List<String> loot1lore = new ArrayList<>();

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

		gensLocked = true;
	}

	public void clearScoreboard(Player player) {
		Objective fakekills = player.getScoreboard().getObjective("fakekills");

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

		String Acopy = genAstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
		String Bcopy = genBstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
		String Ccopy = genCstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
		String Dcopy = genDstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА

		if(gensLocked) {
			Acopy = Acopy + ChatColor.GRAY + " ⚠";
			Bcopy = Bcopy + ChatColor.GRAY + " ⚠";
			Ccopy = Ccopy + ChatColor.GRAY + " ⚠";
			Dcopy = Dcopy + ChatColor.GRAY + " ⚠";
		}

		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор A - " + Acopy);
		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор B - " + Bcopy);
		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор C - " + Ccopy);
		fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор D - " + Dcopy);

		Bukkit.getScheduler().cancelTask(scoreboardTask);

		actualgametime[0] = 0;
		actualgametime0[0] = -1;
		seconds[0] = 0;
		seconds0[0] = -1;
		minutes[0] = 0;
		minutes0[0] = 0;

		genAstatus = "woolbattle.generator.uncaptured";
		genBstatus = "woolbattle.generator.uncaptured";
		genCstatus = "woolbattle.generator.uncaptured";
		genDstatus = "woolbattle.generator.uncaptured";

		String nextevent = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";
		String nextevent0 = ChatColor.DARK_GRAY + " (Рефилл | 4:00)";

		player.setScoreboard(fakekills.getScoreboard());
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
						List<EntityType> nearbyEntityTypes = new ArrayList<>();
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
		Team team = mainboard.getPlayerTeam(player);
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
		player.setLevel(Math.min(woolcount, 512));
	}

	public ArrayList<Block> getBlox(Block start, int radius) {
		ArrayList<Block> blocks = new ArrayList<>();
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
			String genStatus = "woolbattle.generator.uncaptured";

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
								if (!genStatus.equals("woolbattle.generator.red")) {
									genBroadcast(genLetter, 1);
									genStatus = "woolbattle.generator.red";
								}
							}
							break;
						case 2:
							for (Block blocc : genLONG) {
								if (String.valueOf(blocc.getType()).contains("CONCRETE") && blocc.getType() != Material.AIR) {
									blocc.setType(Material.YELLOW_CONCRETE);
								}
								if (!genStatus.equals("woolbattle.generator.yellow")) {
									genBroadcast(genLetter, 2);
									genStatus = "woolbattle.generator.yellow";
								}
							}
							break;
						case 3:
							for (Block blocc : genLONG) {
								if (String.valueOf(blocc.getType()).contains("CONCRETE") && blocc.getType() != Material.AIR) {
									blocc.setType(Material.LIME_CONCRETE);
								}
								if (!genStatus.equals("woolbattle.generator.green")) {
									genBroadcast(genLetter, 3);
									genStatus = "woolbattle.generator.green";
								}
							}
							break;
						case 4:
							for (Block blocc : genLONG) {
								if (String.valueOf(blocc.getType()).contains("CONCRETE") && blocc.getType() != Material.AIR) {
									blocc.setType(Material.LIGHT_BLUE_CONCRETE);
								}
								if (!genStatus.equals("woolbattle.generator.blue")) {
									genBroadcast(genLetter, 4);
									genStatus = "woolbattle.generator.blue";
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
	public void resetGeneratorText(Player player) {

		List<String> genStatuses = new ArrayList<>();

		//тут типа надо language.getPlayer() ну вы поняли

		genStatuses.add("woolbattle.generator.uncaptured");
		genStatuses.add("woolbattle.generator.red");
		genStatuses.add("woolbattle.generator.yellow");
		genStatuses.add("woolbattle.generator.green");
		genStatuses.add("woolbattle.generator.blue");
		for(String genStatus : genStatuses) {
			Objective fakekills = player.getScoreboard().getObjective("fakekills");

			String Acopy = genAstatus; //ТУТ ТИПО БЕРЁТСЯ ЯЗЫК ИГРКОА
			String Bcopy = genBstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
			String Ccopy = genCstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА
			String Dcopy = genDstatus; //ТУТ ТИПО ТОЖЕ БЕРЁТСЯ ЯЗЫК ИГРКОА

			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор A - " + Acopy);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор B - " + Bcopy);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор C - " + Ccopy);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор D - " + Dcopy);


			Acopy = Acopy + ChatColor.GRAY + " ⚠";
			Bcopy = Bcopy + ChatColor.GRAY + " ⚠";
			Ccopy = Ccopy + ChatColor.GRAY + " ⚠";
			Dcopy = Dcopy + ChatColor.GRAY + " ⚠";


			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор A - " + Acopy);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор B - " + Bcopy);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор C - " + Ccopy);
			fakekills.getScoreboard().resetScores(ChatColor.WHITE + "Генератор D - " + Dcopy);

			player.setScoreboard(fakekills.getScoreboard());
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

		requiredKills = 3;

		try {
			worldBorderTask.cancel();
		} catch (Throwable ignored) {}

		for (Player player1 : Bukkit.getOnlinePlayers()) {
			clearScoreboard(player1);
		}
	}

	public void winningBroadcast(int winner) {

//		int fadeIn = 5;
//		int hold = 40;
//		int fadeOut = 30;

		for(Player player : Bukkit.getOnlinePlayers()) {
			String colorWon;

			switch (winner) {
				case 1 -> colorWon = "RED";
				case 2 -> colorWon = "YELLOW";
				case 3 -> colorWon = "GREEN";
				case 4 -> colorWon = "BLUE";
				default -> colorWon = "Nobody";
			}

			player.showTitle(genTitle(lang, "team." + colorWon, "team.won", 5, 40, 30));

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

	public void resetEveryFuckingKillScoreboard(Player player) {
		Objective fakekills = player.getScoreboard().getObjective("fakekills");

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

		player.setScoreboard(fakekills.getScoreboard());
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
				Team team = mainboard.getPlayerTeam(dead);
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
				Team team = mainboard.getPlayerTeam(dead);
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
		List<Player> redTeamPlayers0 = new ArrayList<>();
		List<Player> yellowTeamPlayers0 = new ArrayList<>();
		List<Player> greenTeamPlayers0 = new ArrayList<>();
		List<Player> blueTeamPlayers0 = new ArrayList<>();

		for(Player player : Bukkit.getOnlinePlayers()) {
			Team team = mainboard.getPlayerTeam(player);
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
		Teams team;
		if (teamname.contains("red")) {
			Location loc = new Location(player1.getWorld(), 9.5, 258, -27.5);
			player1.teleport(loc);
			team = Teams.RED;
		} else if (teamname.contains("yellow")) {
			Location loc = new Location(player1.getWorld(), -27.5, 258, 9.5);
			loc.setYaw(-90);
			player1.teleport(loc);
			team = Teams.YELLOW;
		} else if (teamname.contains("green")) {
			Location loc = new Location(player1.getWorld(), 9.5, 258, 46.5);
			loc.setYaw(-180);
			player1.teleport(loc);
			team = Teams.GREEN;
		} else if (teamname.contains("blue")) {
			Location loc = new Location(player1.getWorld(), 46.5, 258, 9.5);
			loc.setYaw(90);
			player1.teleport(loc);
			team = Teams.BLUE;
		} else {
			team = Teams.SPECTATOR;
		}

		gameStartingTitle(player1);
		requiredKills = (int) (Math.round(4 * (Bukkit.getOnlinePlayers().size() * 0.7)));

		Bukkit.getScheduler().runTaskLater(this, () -> {
			player1.sendTitle(ChatColor.WHITE + "Нужно сделать", ChatColor.WHITE + String.valueOf(requiredKills) + " киллов.", 1, 40, 25);

			player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.2F);
			Bukkit.getScheduler().runTaskLater(this, () -> {
				player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.25F);
				player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
				Bukkit.getScheduler().runTaskLater(this, () -> {
					player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.6F);
					player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.8F);

					player1.sendMessage(getByLang(lang, "onStartMessage", Map.of("kills", String.valueOf(requiredKills))));
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

		this.gameactive = true;

		abilities.startGame(lang, team, player1);

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
		Team team = mainboard.getPlayerTeam(player);
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
				woolmeta.displayName(getByLang(lang, "blueWool"));
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
	
	protected void loadGenLocation() {
		genAblocks = getBlox(new Location(mainworld, 46, 254, -28).getBlock(), 2);
		genBblocks = getBlox(new Location(mainworld, -28, 254, -28).getBlock(), 2);
		genCblocks = getBlox(new Location(mainworld, -28, 254, 46).getBlock(), 2);
		genDblocks = getBlox(new Location(mainworld, 46, 254, 46).getBlock(), 2);

		genAblocksLONG = getBlox(new Location(mainworld, 46, 254, -28).getBlock(), 3);
		genBblocksLONG = getBlox(new Location(mainworld, -28, 254, -28).getBlock(), 3);
		genCblocksLONG = getBlox(new Location(mainworld, -28, 254, 46).getBlock(), 3);
		genDblocksLONG = getBlox(new Location(mainworld, 46, 254, 46).getBlock(), 3);
	}

	@Override
	public TextComponent getByLang(String lang, String string) {
		getLogger().info("WoolBattle:Plugin | getByLang: \n" + lang + "\n" + string);
		return translator.getTranslation(lang, string.replaceFirst("woolbattle.", "")).decoration(TextDecoration.ITALIC, false);
	}
	@Override
	public TextComponent getByLang(String lang, String string, Map<String, String> formatV) {
		getLogger().info("WoolBattle:Plugin | getByLang: \n" + lang + "\n" + string + "\n" + formatV.toString());
		return translator.getTranslation(lang, string.replaceFirst("woolbattle.", ""), formatV).decoration(TextDecoration.ITALIC, false);

	}
	public Title genTitle(@NotNull String lang, @Nullable String id, @Nullable String id2nd, int i, int j, int k) {
		return utilities.createTitle(lang, id, id2nd, i, j, k);
	}
	public TextComponent uniteTC(TextComponent[] tcs) {
		return utilities.combineComponents(tcs, Component.empty());
	}
	public TextComponent uniteTCspace(TextComponent[] tcs) {
		return utilities.combineComponents(tcs, Component.space());
	}

}
