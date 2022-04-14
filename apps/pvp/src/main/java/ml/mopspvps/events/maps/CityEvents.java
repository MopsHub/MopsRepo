package ml.mopspvps.events.maps;

import ml.mopspvps.Dependencies;
import ml.mopspvps.Plugin;
import ml.mopspvps.events.EVENT_STAT;
import ml.mopspvps.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CityEvents {
	/**
	 * @param event  Ивент PlayerInteractEvent, отвечает за абилки
	 * @param player Игрок, который использовал абилку (нажал ПКМ)
	 * @param item   Предмет - который был использован игроком
	 * @return EVENT_STAT
	 * @see PlayerInteractEvent
	 */
	public EVENT_STAT cityDeadEvent(PlayerInteractEvent event, Player player, ItemStack item) {
		try {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.STONE_BUTTON || event.getClickedBlock().getType() == Material.ACACIA_BUTTON || event.getClickedBlock().getType() == Material.WARPED_BUTTON || event.getClickedBlock().getType() == Material.POLISHED_BLACKSTONE_BUTTON || event.getClickedBlock().getType() == Material.LEVER) {
					return EVENT_STAT.CANCELED;
				}
			}
			return EVENT_STAT.NOT_HANDLED;
		} catch (Exception | Error exception) {
			Dependencies.getLog().warning(exception.getMessage());
			return EVENT_STAT.THROWABLE;
		}
	}

	/**
	 * @param event  Ивент PlayerInteractEvent, отвечает за абилки
	 * @param player Игрок, который использовал абилку (нажал ПКМ)
	 * @param item   Предмет - который был использован игроком
	 * @return EVENT_STAT
	 * @see PlayerInteractEvent
	 */
	public EVENT_STAT cityItemAbilityEvent(PlayerInteractEvent event, Player player, ItemStack item) {
		try {
			String itemDisplayName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (itemDisplayName.equals(ChatColor.DARK_PURPLE + "Магии Возрождения")) {
					List<Player> deadplayers = new ArrayList<>();
					if (player.getScoreboardTags().contains("city")) {
						for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
							if (player1.getScoreboardTags().contains("citydead")) {
								deadplayers.add(player1);
							}
						}
					}
					if (!deadplayers.isEmpty()) {
						Player player2 = deadplayers.get(0);
						player2.removeScoreboardTag("citydead");
						player2.addScoreboardTag("cityalive");
						for (Player idiot : Bukkit.getOnlinePlayers()) {
							idiot.showPlayer(Dependencies.getPlugin(), player2);
						}
						player2.setAllowFlight(false);
						player2.teleport(player.getLocation());
						player2.sendTitle(ChatColor.GREEN + "Вы возрождены!", "", 0, 1, 3);
						item.setAmount(0);
						player2.playSound(player2.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 2);
						player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 2);
					}
				}
			}
		} catch (Exception exception) {
			Dependencies.getLog().warning(exception.getMessage());
			return EVENT_STAT.THROWABLE;
		}
		return EVENT_STAT.NOT_HANDLED;
	}

	/**
	 * @param event  Ивент PlayerInteractEvent, отвечает за абилки
	 * @param player Игрок, который использовал абилку (нажал ПКМ)
	 * @return EVENT_STAT
	 * @see EntityDamageEvent
	 */
	public EVENT_STAT cityPLayerDamageEvent(EntityDamageEvent event, Player player) {
		try {
			Plugin plugin = Dependencies.getPlugin();
			if (player.getScoreboardTags().contains("cityalive")) {
				if (player.getHealth() - event.getFinalDamage() <= 0 && player.getAbsorptionAmount() - event.getDamage() <= 0) {
					if ((player.getInventory().getItemInMainHand().getType() != Material.TOTEM_OF_UNDYING && player.getInventory().getItemInOffHand().getType() != Material.TOTEM_OF_UNDYING)) {
						event.setCancelled(true);
						player.setHealth(player.getMaxHealth());
						player.addScoreboardTag("citydead");
						player.removeScoreboardTag("cityalive");
						player.setAllowFlight(true);
						for (Player idiot : Bukkit.getOnlinePlayers()) {
							idiot.hidePlayer(plugin, player);
						}
						Score score = Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective("citydeaths")).getScore(player);
						score.setScore(1);
						player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 2);
						player.getInventory().remove(Material.POPPED_CHORUS_FRUIT);
					}
					return EVENT_STAT.HANDLED;
				}
			}
			if (player.getScoreboardTags().contains("citydead")) {
				return EVENT_STAT.CANCELED;
			}
		} catch (Exception exception) {
			Dependencies.getLog().warning(exception.getMessage());
			return EVENT_STAT.THROWABLE;
		}
		return EVENT_STAT.NOT_HANDLED;
	}

	/**
	 * @param event Ивент PlayerInteractEvent, отвечает за абилки
	 * @param item  Предмет - который был использован игроком
	 * @return EVENT_STAT
	 * @see BlockBreakEvent
	 */
	public EVENT_STAT cityBlockBreakEvent(BlockBreakEvent event, ItemStack item) {
		try {
			if (event.getBlock().getType().equals(Material.RED_WOOL)) {
				event.setCancelled(Objects.requireNonNull(item.getItemMeta()).getDisplayName().equals(ChatColor.GOLD + "Ножницы Для Шерсти"));
			} else {
				event.setCancelled(true);
			}
			return EVENT_STAT.HANDLED;
		} catch (Exception exception) {
			Dependencies.getLog().warning(exception.getMessage());
			return EVENT_STAT.THROWABLE;
		}
	}

	public void checkEffects(Player player) {
		if (player.getLocation().getBlock().getType() == Material.WATER) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7, 3, true, false, false));
		}
		if (player.isGliding()) {
			player.setGliding(false);
		}
		try {
			if (player.getInventory().getHelmet() != null) {
				ItemStack item = player.getInventory().getHelmet();
				ItemMeta meta = item.getItemMeta();
				if ((meta != null && Utils.componentsToStrings(Objects.requireNonNull(meta.lore())).contains(ChatColor.GRAY + "Шлем всех шлемов всех хилов.")) || Utils.componentsToStrings(Objects.requireNonNull(Objects.requireNonNull(meta).lore())).contains(ChatColor.GRAY + "Просто шлем хилера.")) {
					for (Player player2 : Bukkit.getServer().getOnlinePlayers()) {
						if (player2.getScoreboardTags().contains("city")) {
							if (player.isSneaking()) {
								if (player2.getAbsorptionAmount() <= 20) {
									player2.setAbsorptionAmount(player2.getAbsorptionAmount() + 0.08);
								}
								player2.playSound(player2.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 1, 0);
								Random rand = new Random();
								int x = rand.nextInt(10) % 4 - 1;
								Random rand3 = new Random();
								int z = rand3.nextInt(10) % 4 - 1;

								player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(x, 1, z), 2);
							}
							ScoreboardManager manager = Bukkit.getScoreboardManager();
							Scoreboard board = manager != null ? manager.getMainScoreboard() : null;
							Objective objective = board != null ? board.getObjective("healertotemuse") : null;
							Score score = objective != null ? objective.getScore(player) : null;
							if ((score != null ? score.getScore() : 0) == 1) {
								player2.setAbsorptionAmount(player2.getAbsorptionAmount() + 8);
								player2.setHealth(player2.getMaxHealth());
								player2.getWorld().spawnParticle(Particle.TOTEM, player2.getLocation(), 20);
								player2.playSound(player2.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
							}
						}
					}
				}
			}
		} catch (NullPointerException e) {
			Dependencies.getLog().warning(e.getMessage());

		}
	}

	public void deathMessage(Player player) {
		Component g = Component.text("Вы умерли.").color(NamedTextColor.RED);
		Component g1 = Component.empty();
		player.showTitle(
				Title.title(g, g1)
		);
	}
}
