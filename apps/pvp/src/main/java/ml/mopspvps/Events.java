package ml.mopspvps;

import ml.mopspvps.events.ItemEvents;
import ml.mopspvps.events.maps.CityEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;
import ml.mopsutils.Pair;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

/**
 * Listener ивентов, который отвечает из их обработку
 *
 * @author InFTord, SirCat07, Kofiy
 */

public class Events implements Listener {
	//    TODO: базовый менеджер ивентов
	Dependencies dependencies;
	Plugin plugin;
	ItemEvents itemEvents;
	CityEvents cityEvents;
	Logger logger;

	public Events(Dependencies dependencies) {
		this.dependencies = dependencies;
		this.plugin = Dependencies.getPlugin();
		this.itemEvents = Dependencies.getItemEvents();
		this.cityEvents = Dependencies.getCityEvents();
		this.logger = plugin.getLogger();
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.joinMessage(Component.empty());
		Player player = event.getPlayer();

		//TODO: ранги нужно фиксить фиксить и фиксить
		setRank(player);
		Dependencies.getScoreboards().createScoreboard(player);

		// Версия компонента где ранк + имя
//          final TextComponent playerName = legacyAmpersand().deserialize(dependencies.getMopsRank(player)).append(legacyAmpersand().deserialize(dependencies.getMopsName(player)));
		// Версия компонента где нету ранка потому что они не работают вроде как я хз
		final TextComponent playerName = legacyAmpersand().deserialize(Dependencies.getMopsName(player));

		if (!player.isOp()) {
			final TextComponent joinMessage = playerName.append(Component.text(" зашёл на сервер.").color(NamedTextColor.AQUA));
			event.joinMessage(joinMessage);

		} else {
			for (Player receiver : Bukkit.getServer().getOnlinePlayers()) {
				if (receiver.isOp()) {
					final TextComponent joinMessage = playerName.append(Component.text(" тихо зашёл на сервер.").color(NamedTextColor.DARK_AQUA));
					receiver.sendMessage(joinMessage);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		event.quitMessage(Component.empty());
		Player player = event.getPlayer();

		// Версия компонента где ранк + имя
//          final TextComponent playerName = legacyAmpersand().deserialize(dependencies.getMopsRank(player)).append(legacyAmpersand().deserialize(dependencies.getMopsName(player)));
		// Версия компонента где нету ранка потому что они не работают вроде как я хз
		final TextComponent playerName = legacyAmpersand().deserialize(Dependencies.getMopsName(player));

		if (!player.isOp()) {
			final TextComponent quitMessage = playerName.append(Component.text(" вышел с сервера.").color(NamedTextColor.AQUA));
			event.quitMessage(quitMessage);

		} else {
			for (Player receiver : Bukkit.getServer().getOnlinePlayers()) {
				if (receiver.isOp()) {
					final TextComponent quitMessage = playerName.append(Component.text(" тихо вышел с сервера.").color(NamedTextColor.DARK_AQUA));
					receiver.sendMessage(quitMessage);
				}
			}
		}

	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (player.getScoreboardTags().contains("city")) {
				switch (Dependencies.getCityEvents().cityPLayerDamageEvent(event, player)) {
					case CANCELED -> {
						event.setCancelled(true);
					}
					case HANDLED, NOT_HANDLED -> {
					}
					case THROWABLE -> //тут должны быть логи
							this.plugin.getLogger().log(Level.SEVERE, "Произошел пиздец");
				}
			}
		}
	}

	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity victim = event.getEntity();
		if (damager instanceof Player) {
			Player playr = (((Player) damager).getPlayer());
			if (damager.getScoreboardTags().contains("citydead")) {
				event.setCancelled(true);
			}
			if (victim instanceof Player) {
				if (victim.getScoreboardTags().contains("city") && damager.getScoreboardTags().contains("city")) {
					event.setDamage(0);
				}
			}
			if (victim.getScoreboardTags().contains("arcticbear")) {
				Player player = (Player) damager;
				PolarBear bear = (PolarBear) victim;

				if (bear.getHealth() - event.getDamage() <= 0) {

					Location loc = victim.getLocation();
					ItemStack item = new ItemStack(Material.SLIME_BALL);
					ItemMeta meta = item.getItemMeta();
					assert meta != null;
					meta.setDisplayName(ChatColor.DARK_GREEN + "2-ПопИт апгрейд");
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.DARK_GRAY + "Устанавливается на: ПопИт!, СимплДимпл!");
					lore.add("");
					lore.add(ChatColor.GRAY + "Нужно чтобы добавить по 2 клика к каждому");
					lore.add(ChatColor.GRAY + "использованию ПопИтов.");
					lore.add(ChatColor.DARK_GRAY + "Одноразовая вещь, Сброс на перерождении.");
					meta.setLore(lore);
					item.setItemMeta(meta);

					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard board = manager.getMainScoreboard();
					Objective objective = board != null ? board.getObjective("luck") : null;
					Score score = objective != null ? objective.getScore(player) : null;

					int luck2 = (score != null ? score.getScore() : 0) / 100;
					Random rand = new Random();
					int maxchance;
					int damage = (int) event.getDamage() + 1;
					int damage1 = damage / 4 + 1;
					if (luck2 >= 1) {
						maxchance = rand.nextInt(128) % 128 / luck2 / damage1 + 1;
					} else {
						maxchance = rand.nextInt(128) % 128 / damage1 + 1;
					}
					Random rand2 = new Random();
					int chance = rand.nextInt(128) % maxchance + 1;
					if (chance == maxchance) {
						int score2 = (score != null ? score.getScore() : 0) / 8;
						if (score != null) {
							score.setScore(score.getScore() - score2);
						}
						Item item1 = victim.getWorld().dropItem(loc, item);
						item1.setGlowing(true);
						player.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "РЕДКИЙ ДРОП! " + ChatColor.RESET + ChatColor.DARK_GREEN + "2-ПопИт апгрейд");
					}
				}
			}
			try {
				if (playr != null && Objects.requireNonNull(Objects.requireNonNull(playr.getInventory().getItemInMainHand().getItemMeta()).getLore()).contains(ChatColor.GRAY + "Хилит тебя на 1/4 твоего урона.")) {
					double damage = event.getDamage() / 4;
					playr.setHealth(Math.min(playr.getHealth() + damage, playr.getMaxHealth()));
				}
			} catch (NullPointerException e) {
				logger.severe(e.getMessage());

			}
		}
	}


	//ЛОМАНИЯ БЛОКОВ И СТАВКА КОТ СМОТРИ
	//ЛОМАНИЯ БЛОКОВ И СТАВКА КОТ СМОТРИ
	//ЛОМАНИЯ БЛОКОВ И СТАВКА КОТ СМОТРИ
	//ЛОМАНИЯ БЛОКОВ И СТАВКА КОТ СМОТРИ
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (event.getBlock().getType() == Material.LEVER || event.getBlock().getType() == Material.RED_CONCRETE || event.getBlock().getType() == Material.REDSTONE_LAMP || event.getBlock().getType() == Material.POLISHED_BLACKSTONE_BUTTON || event.getBlock().getType() == Material.STONE_BUTTON || event.getBlock().getType() == Material.BIRCH_SIGN || event.getBlock().getType() == Material.BIRCH_WALL_SIGN || event.getBlock().getType() == Material.SEA_LANTERN || event.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {
			event.setCancelled(player.getGameMode() == GameMode.CREATIVE);
		}
		if (player.getScoreboardTags().contains("city")) {
			ItemStack item = player.getItemInHand();
			switch (Dependencies.getCityEvents().cityBlockBreakEvent(event, item)) {
				case HANDLED:
					return;
				case THROWABLE:
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (player.getScoreboardTags().contains("snow") || player.getScoreboardTags().contains("city") || player.getScoreboardTags().contains("desert") || player.getScoreboardTags().contains("plains") || player.getScoreboardTags().contains("water") || player.getScoreboardTags().contains("forest")) {
			if (event.getBlock().getState() instanceof ShulkerBox) {
				event.setCancelled(event.getBlockAgainst().getType() != Material.RED_CONCRETE);
			}
		}
		if (event.getBlock().getType() == Material.PLAYER_HEAD || event.getBlock().getType() == Material.WITHER_SKELETON_SKULL) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				Block block = event.getBlock();
				block.setType(Material.AIR);
//                if (player.getInventory().getHelmet() == null) {
//                    ItemStack head = new ItemStack(block.getType());
//                    player.getInventory().setHelmet(head);
//                    head.setAmount(0);
//                }
			}
		}
		if (player.getScoreboardTags().contains("city")) {
			Player dungstarted = Bukkit.getServer().getPlayer("SirCat07");
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard board = manager != null ? manager.getMainScoreboard() : null;
			Objective objective = board != null ? board.getObjective("dungeonstarted") : null;
			Score score = objective != null ? objective.getScore(dungstarted) : null;
			if ((score != null ? score.getScore() : 0) == 1) {
				event.setCancelled(event.getBlock().getType() == Material.DARK_OAK_PLANKS);
				if (event.getBlockAgainst().getType() == Material.CHAIN) {
					event.setCancelled(event.getBlock().getType() != Material.CHAIN);
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(event.getBlockAgainst().getType() != Material.RED_CONCRETE);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		try {
			if (Objects.requireNonNull(event.getClickedInventory()).getType() == InventoryType.ANVIL) {
				AnvilInventory anvil = (AnvilInventory) event.getClickedInventory();
				ItemStack previous = anvil.getItem(0);
				ItemStack after = anvil.getItem(2);
				assert after != null;
				ItemMeta meta = after.getItemMeta();
				assert meta != null;
				assert previous != null;
				meta.setDisplayName(Objects.requireNonNull(previous.getItemMeta()).getDisplayName());
				after.setItemMeta(meta);
			}
		} catch (NullPointerException e) {
			logger.severe(e.getMessage());

		}
		try {
			if (!player.getScoreboardTags().contains("snow") && !player.getScoreboardTags().contains("city") && !player.getScoreboardTags().contains("desert") && !player.getScoreboardTags().contains("plains") && !player.getScoreboardTags().contains("water") && !player.getScoreboardTags().contains("forest") && !player.getScoreboardTags().contains("admins")) {
				if (event.getClickedInventory() != player.getInventory() && event.getClickedInventory() != player.getEnderChest() && Objects.requireNonNull(event.getClickedInventory()).getType() != InventoryType.ANVIL) {
					event.setCancelled(true);
				}
			}
		} catch (NullPointerException e) {
			logger.severe(e.getMessage());

		}
		if (player.getScoreboardTags().contains("citydead")) {
			event.setCancelled(event.getClickedInventory() != player.getInventory());
		}
		if (event.getInventory() == Dependencies.getKits()) {
			event.setCancelled(true);
		}
		if (event.getInventory() == Dependencies.getPopIt()) {
			event.setCancelled(true);
			ItemStack pinkpopit = new ItemStack(Material.PINK_TERRACOTTA);
			ItemMeta meta = pinkpopit.getItemMeta();
			Objects.requireNonNull(meta).setDisplayName(ChatColor.LIGHT_PURPLE + "ПопИт!");
			pinkpopit.setItemMeta(meta);
			ItemStack bluepopit = new ItemStack(Material.LIGHT_BLUE_TERRACOTTA);
			ItemMeta meta2 = bluepopit.getItemMeta();
			Objects.requireNonNull(meta2).setDisplayName(ChatColor.BLUE + "ПопИт!");
			bluepopit.setItemMeta(meta2);

			int slot = event.getSlot();

			if (Dependencies.getPopIt(slot).getType() == Material.PINK_TERRACOTTA) {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					Dependencies.setPopItItem(slot, bluepopit);
					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard board = Objects.requireNonNull(manager).getMainScoreboard();
					Objective objective = board.getObjective("popituses");
					Score score = objective != null ? objective.getScore(player) : null;
					assert score != null;
					score.setScore(score.getScore() + 1);
				}, 1L);
			}
			if (Dependencies.getPopIt(slot).getType() == Material.LIGHT_BLUE_TERRACOTTA) {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 0);
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					Dependencies.setPopItItem(slot, pinkpopit);
					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard board = manager != null ? manager.getMainScoreboard() : null;
					Objective objective = board != null ? board.getObjective("popituses") : null;
					Score score = objective != null ? objective.getScore(player) : null;
					Objects.requireNonNull(score).setScore(score.getScore() + 1);
				}, 1L);
			}

		}
		if (event.getInventory() == Dependencies.getSimpleDimple()) {
			event.setCancelled(true);
			ItemStack greensimpledimple = new ItemStack(Material.LIME_TERRACOTTA);
			ItemMeta meta = greensimpledimple.getItemMeta();
			Objects.requireNonNull(meta).setDisplayName(ChatColor.GREEN + "СимплДимпл!");
			greensimpledimple.setItemMeta(meta);
			ItemStack goldsimpledimple = new ItemStack(Material.ORANGE_TERRACOTTA);
			ItemMeta meta2 = goldsimpledimple.getItemMeta();
			Objects.requireNonNull(meta2).setDisplayName(ChatColor.GOLD + "СимплДимпл!");
			goldsimpledimple.setItemMeta(meta2);

			int slot = event.getSlot();

			if (Dependencies.getSimpleDimple(slot).getType() == Material.LIME_TERRACOTTA) {
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, 1, 1);
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					Dependencies.setSimpleDimple(slot, goldsimpledimple);
					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard board = manager.getMainScoreboard();
					Objective objective = board.getObjective("popituses");
					Score score = objective != null ? objective.getScore(player) : null;
					Objects.requireNonNull(score).setScore(score.getScore() + 7);
				}, 1L);
			}

			if (Dependencies.getSimpleDimple(slot).getType() == Material.ORANGE_TERRACOTTA) {
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 1, 0);
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					Dependencies.setSimpleDimple(slot, greensimpledimple);
					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard board = manager.getMainScoreboard();
					Objective objective = board.getObjective("popituses");
					Score score = objective != null ? objective.getScore(player) : null;
					Objects.requireNonNull(score).setScore(score.getScore() + 7);
				}, 1L);
			}

		}
		if (event.getInventory() == Dependencies.getCustomGive()) {
			event.setCancelled(true);
			if (event.getSlot() == 0) {
				ItemStack item = new ItemStack(Material.LIGHTNING_ROD);
				ItemMeta meta = item.getItemMeta();
				Objects.requireNonNull(meta).setDisplayName(ChatColor.GOLD + "Молниеносная Палка");
				List<String> lore = new ArrayList<>();
				lore.add("");
				lore.add(ChatColor.YELLOW + "Способность Предмета: Молния!" + ChatColor.BOLD + " ПКМ");
				lore.add(ChatColor.GRAY + "Нажмите ПКМ чтобы запустить");
				lore.add(ChatColor.GRAY + "молнию.");
				meta.setLore(lore);
				item.setItemMeta(meta);
				player.getInventory().addItem(item);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
			}
			if (event.getSlot() == 1) {
				ItemStack item = new ItemStack(Material.SLIME_BALL);
				ItemMeta meta = item.getItemMeta();
				Objects.requireNonNull(meta).setDisplayName(ChatColor.GREEN + "Слизень Мощи");
				meta.addEnchant(Enchantment.KNOCKBACK, 10, true);
				List<String> lore2 = new ArrayList<>();
				lore2.add("");
				lore2.add(ChatColor.YELLOW + "Способность Предмета: Прыжки!" + ChatColor.BOLD + " ПКМ");
				lore2.add(ChatColor.GRAY + "Нажмите ПКМ чтобы очень");
				lore2.add(ChatColor.GRAY + "сильно прыгнуть.");
				meta.setLore(lore2);
				item.setItemMeta(meta);
				player.getInventory().addItem(item);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
			}
			if (event.getSlot() == 2) {
				ItemStack item = new ItemStack(Material.IRON_INGOT);
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<>();
				Objects.requireNonNull(meta).setDisplayName(ChatColor.GRAY + "Убийца Крови Воздуха Фрагмента Алюминия 2046");
				lore.add("");
				lore.add(ChatColor.YELLOW + "Способность Предмета: иди нахер" + ChatColor.BOLD + " ПКМ");
				lore.add(ChatColor.GRAY + "Нажмите ПКМ чтобы");
				meta.setLore(lore);
				item.setItemMeta(meta);
				player.getInventory().addItem(item);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
			}
		}
	}


	@EventHandler
	public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {

		ArmorStand armorstand = event.getRightClicked();
		if (Dependencies.getArmorstandlist().contains(armorstand)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = ((Player) event.getEntity()).getPlayer();
			try {
				if (Objects.requireNonNull(player).getInventory().getItemInMainHand() != null && (Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.GREEN + "Лук Лучника"))) {
					ItemStack item = event.getBow();
					Entity arrow = event.getProjectile();
				}
			} catch (NullPointerException e) {
				logger.severe("пиздец: ");

			}
		}
	}

	/**
	 * @param event - ивент который будет обрабатываться (в коде это PlayerInteractEvent) ((почему кста?))
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		try { //Ивенты для мёртвых горожян
			if (player.getScoreboardTags().contains("citydead")) {
				switch (cityEvents.cityDeadEvent(event, player, item)) {
					case CANCELED -> event.setCancelled(true); //Ивент был отменён
					case HANDLED -> { //Ивент был обработан
						return;
					}
					case NOT_HANDLED -> { //Ивент не был обработан (пока что)
					}
					case THROWABLE -> {
					}
				}
			}
		} catch (Throwable e) {
			logger.severe(e.getMessage());
		}
		if (item != null) { //Не сувать сюда ивенты которые не требуют предмета в руке
			try { //Ивенты для горожян (связанные с абилками)
				if (player.getScoreboardTags().contains("city")) {
					switch (cityEvents.cityItemAbilityEvent(event, player, item)) {
						case CANCELED -> event.setCancelled(true); //Ивент был отменён
						case HANDLED -> { //Ивент был обработан
							return;
						}
						case NOT_HANDLED -> { //Ивент не был обработан (пока что)
						}
						case THROWABLE -> {
						}
					}
				}
			} catch (Exception e) {
				logger.severe(e.getMessage());

			}
			try {
				switch (itemEvents.useAbilityWeaponEvent(event, player, item)) {
					case CANCELED -> event.setCancelled(true); //Ивент был отменён
					case HANDLED -> { //Ивент был обработан
						return;
					}
					case NOT_HANDLED -> { //Ивент не был обработан (пока что)
					}
					case THROWABLE -> {
					}
				}
			} catch (Exception e) {
				logger.severe(e.getMessage());

			}
			try {
				switch (itemEvents.useCustomItemEvent(event, player, item)) {
					case CANCELED -> event.setCancelled(true); //Ивент был отменён
					case HANDLED -> { //Ивент был обработан
					}
					case NOT_HANDLED -> { //Ивент не был обработан (пока что)
					}
					case THROWABLE -> {
					}
				}
			} catch (Exception e) {
				logger.severe(e.getMessage());

			}
		}
	}

	@Deprecated
	public void checkRank(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = Objects.requireNonNull(manager).getMainScoreboard();
		dependencies.getMopsRank().putIfAbsent(player, ChatColor.GRAY + "");
		dependencies.getMopsName().putIfAbsent(player, ChatColor.GRAY + player.getName());

		if (Objects.requireNonNull(board.getTeam("vip")).getEntries().contains(player)) {
			Dependencies.putMopsRank(player, ChatColor.GOLD + "[VIP] ");
			Dependencies.putMopsName(player, ChatColor.GOLD + player.getName());
		}
		if (Objects.requireNonNull(board.getTeam("developers")).getEntries().contains(player)) {
			Dependencies.putMopsRank(player, ChatColor.GREEN + "[DEV] ");
			Dependencies.putMopsName(player, ChatColor.GREEN + player.getName());
		}
		if (Objects.requireNonNull(board.getTeam("admin")).getEntries().contains(player)) {
			Dependencies.putMopsRank(player, ChatColor.RED + "[ADMIN] ");
			Dependencies.putMopsName(player, ChatColor.RED + player.getName());
		}
		if (Objects.requireNonNull(board.getTeam("mod")).getEntries().contains(player)) {
			Dependencies.putMopsRank(player, ChatColor.RED + "[ADMIN-] ");
			Dependencies.putMopsName(player, ChatColor.RED + player.getName());
		}
		if (Objects.requireNonNull(board.getTeam("builders")).getEntries().contains(player)) {
			Dependencies.putMopsRank(player, ChatColor.BLUE + "[BUILD] ");
			Dependencies.putMopsName(player, ChatColor.BLUE + player.getName());
		}
		if (Objects.requireNonNull(board.getTeam("YOUTUBE")).getEntries().contains(player)) {
			Dependencies.putMopsRank(player, ChatColor.RED + "[" + ChatColor.WHITE + "YOUTUBE" + ChatColor.RED + "] ");
			Dependencies.putMopsName(player, ChatColor.RED + player.getName());
		}

//		player.setPlayerListName(Dependencies.getMopsRank(player) + Dependencies.getMopsName(player));
		final TextComponent visibleName = legacyAmpersand().deserialize(Dependencies.getMopsRank(player) + Dependencies.getMopsName(player));
		player.playerListName(visibleName);
	}

	public void setRank(Player player) {
		Scoreboard board = Dependencies.getBoard();
		Team team = board.getPlayerTeam(player);
		if (team != null) {
			String teamName = team.getName();
			String rank;
			String name = player.getName();
			logger.info("<=РАНГИ=> обработка пользователя с тимой " + teamName);
			switch (teamName.toLowerCase()) {
				case "vip":
					rank = ChatColor.GOLD + "[VIP] ";
					name = ChatColor.GOLD + name;
				case "developers":
					rank = ChatColor.GREEN + "[DEV] ";
					name = ChatColor.GREEN + name;
				case "admin":
					rank = ChatColor.RED + "[ADMIN] ";
					name = ChatColor.RED + name;
				case "mod":
					rank = ChatColor.RED + "[MOD] ";
					name = ChatColor.RED + name;
				case "builders":
					rank = ChatColor.BLUE + "[BUILD] ";
					name = ChatColor.BLUE + name;
				case "youtube":
					rank = ChatColor.RED + "[" + ChatColor.WHITE + "YOUTUBE" + ChatColor.RED + "] ";
					name = ChatColor.RED + name;
				default:
					rank = ChatColor.GRAY + "";
			}
			Dependencies.putMopsRank(player, rank);
			Dependencies.putMopsName(player, name);
			logger.info("<=РАНГИ=> Записано имя " + name + "с рангом " + rank);
		} else {
			logger.info("<=РАНГИ=> обработка пользователя без тимы ");
			Dependencies.putMopsRank(player, ChatColor.GRAY + "");
			Dependencies.putMopsName(player, player.getName());
			logger.info("<=РАНГИ=> Записано имя " + player.getName() + "с рангом " + ChatColor.GRAY + "");
		}
	}


}
