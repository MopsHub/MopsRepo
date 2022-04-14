package ml.mopspvps.events;

import ml.mopspvps.Dependencies;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ItemEvents {
	/**
	 * @param event  Ивент PlayerInteractEvent, отвечает за абилки
	 * @param player Игрок, который использовал абилку (нажал ПКМ)
	 * @param item   Предмет - который был использован игроком
	 * @return EVENT_STAT
	 * @see PlayerInteractEvent
	 */
	public EVENT_STAT useAbilityWeaponEvent(PlayerInteractEvent event, Player player, ItemStack item) {
		try {
			String itemDisplayName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (itemDisplayName.equals(ChatColor.DARK_AQUA + "Скипетр Иллюзиониста")) {
					if (player.getLocation().getBlock().getType() != Material.BEDROCK || player.getLocation().getBlock().getType() != Material.BARRIER || player.getLocation().getBlock().getType() != Material.ANDESITE) {
						player.spawnParticle(Particle.GLOW, player.getLocation(), 150);
						List<Block> list = player.getLineOfSight(null, 12);
						Location loc = player.getLocation();
						if (list.size() > 5) {
							loc = list.get(list.size() - 5).getLocation();
						}
						loc.setYaw(player.getLocation().getYaw());
						loc.setPitch(player.getLocation().getPitch());
						player.teleport(loc);
					}
					return EVENT_STAT.HANDLED;
				} else if (itemDisplayName.equals(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Shadow Warp Scythe")) {
					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard board = Objects.requireNonNull(manager).getMainScoreboard();
					Objective objective = board.getObjective("shadowwarp");
					Score score = Objects.requireNonNull(objective).getScore(player);
					score.setScore(1);
					return EVENT_STAT.HANDLED;
				} else if (itemDisplayName.equals(ChatColor.AQUA + "Ледяной Посох")) {
					List<Entity> entities = player.getNearbyEntities(3, 3, 3);
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 2);
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 0);
					entities.remove(player);
					for (Entity entity : entities) {
						if (!entity.getScoreboardTags().contains("city")) {
							if (80 >= entity.getMaxFireTicks()) {
								entity.setFreezeTicks(entity.getMaxFreezeTicks());
							} else {
								entity.setFreezeTicks(entity.getMaxFreezeTicks());
							}
							String entityname = entity.getCustomName();
							if (entity.getCustomName() != null) {
								if (!entity.getScoreboardTags().contains("frozen")) {
									entity.setCustomName(entity.getCustomName() + ChatColor.RESET + ChatColor.AQUA + " [ЗАМОРОЖЕННЫЙ]");
								}
							} else {
								if (!entity.getScoreboardTags().contains("frozen")) {
									entity.setCustomName(entity.getType().toString() + ChatColor.RESET + ChatColor.AQUA + " [ЗАМОРОЖЕННЫЙ]");
								}
							}
							entity.addScoreboardTag("frozen");
							if (entity instanceof LivingEntity) {
								if (!((LivingEntity) entity).isInvisible()) {
									((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 3, true, false));
									((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 1, true, false, false));
								}
							}
							if (entity instanceof Player) {
								((Player) entity).playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 2);
								((Player) entity).playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 0);
							}
							Bukkit.getScheduler().runTaskLater(Dependencies.getPlugin(), () -> {
								player.playSound(entity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 2);
								if (entity instanceof Player) {
									((Player) entity).playSound(entity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 2);
								}
								entity.removeScoreboardTag("frozen");
								entity.setCustomName(entityname);
							}, 150L);
						}
					}
					return EVENT_STAT.HANDLED;


				} else if (itemDisplayName.equals(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Лопата Некроманта")) { //текст названия предмета абилки
					//код самой абилки, понадобится убрать некоторые if так как они уже были сделаны раньше
					if (!player.getScoreboardTags().contains("citydead")) {
						ScoreboardManager manager = Bukkit.getScoreboardManager();
						Scoreboard board = Objects.requireNonNull(manager).getMainScoreboard();
						Objective objective = board.getObjective("topaz");
						Score score = Objects.requireNonNull(objective).getScore(player);
						score.setScore(1);
						return EVENT_STAT.HANDLED;
					}
				} else if (itemDisplayName.equals(ChatColor.RED + "Борщ")) {
					player.setHealth(Math.min(player.getHealth() + 6, Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));
					if (player.getFoodLevel() + 5 > 24) {
						player.setFoodLevel(25);
					} else {
						player.setFoodLevel(player.getFoodLevel() + 5);
					}
					player.setSaturation(player.getSaturation() + 0.1F);
					item.setAmount(0);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
					return EVENT_STAT.HANDLED;


				} else if (itemDisplayName.equals(ChatColor.AQUA + "Водяной Пистолет")) {
					if (!player.getScoreboardTags().contains("citydead")) {
						if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							ArmorStand armorstand = (ArmorStand) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ARMOR_STAND);
							armorstand.setMarker(true);
							armorstand.addScoreboardTag("watergunblast");
							armorstand.setInvisible(true);
							armorstand.setInvulnerable(true);
							armorstand.setSmall(true);
							Dependencies.addArmorstandList(armorstand);
							Location eyeloc = player.getEyeLocation();
							Vector vector = eyeloc.getDirection().multiply(2);
							armorstand.setVelocity(vector);
							Bukkit.getScheduler().scheduleSyncRepeatingTask(Dependencies.getPlugin(), () -> {
								armorstand.getWorld().spawnParticle(Particle.DRIP_WATER, armorstand.getLocation(), 4);
								armorstand.getWorld().spawnParticle(Particle.FALLING_WATER, armorstand.getLocation(), 2);
								armorstand.getWorld().spawnParticle(Particle.WATER_DROP, armorstand.getLocation(), 4);
							}, 1L, 1L);
							Bukkit.getScheduler().runTaskLater(Dependencies.getPlugin(), () -> {
								armorstand.getWorld().spawnParticle(Particle.DRIP_WATER, armorstand.getLocation(), 7);
								armorstand.getWorld().spawnParticle(Particle.WATER_DROP, armorstand.getLocation(), 7);
								armorstand.getWorld().spawnParticle(Particle.WATER_BUBBLE, armorstand.getLocation(), 7);
								armorstand.getWorld().spawnParticle(Particle.WATER_SPLASH, armorstand.getLocation(), 7);
								List<Entity> entitis = armorstand.getNearbyEntities(5, 5, 5);
								entitis.remove(player);
								for (Entity entity : entitis) {
									entity.setVelocity(new Vector(0, 0.3, 0).add(player.getVelocity()).add(player.getEyeLocation().getDirection().multiply(0.1)));
									if (entity instanceof LivingEntity) {
										((LivingEntity) entity).damage(1);
										if (entity instanceof Player player1) {
											player1.playSound(armorstand.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1, 1);
										}
									}
								}
								player.playSound(armorstand.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1, 1);
								armorstand.teleport(armorstand.getLocation().add(0, -1000, 0));
								player.setVelocity(new Vector(0, 0.4, 0).add(player.getVelocity()));
							}, 10L);
						}
					}
				}
				return EVENT_STAT.NOT_HANDLED;
			}
		} catch (Exception exception) {
			return EVENT_STAT.THROWABLE;

		}
		return EVENT_STAT.NOT_HANDLED;
	}

	/**
	 * @param event  Ивент PlayerInteractEvent, отвечает за абилки
	 * @param player Игрок, который использовал абилку (нажал ПКМ)
	 * @param item   Предмет - который был использован игроком
	 * @return {@link EVENT_STAT}
	 * @see PlayerInteractEvent
	 */
	public EVENT_STAT useCustomItemEvent(PlayerInteractEvent event, Player player, @NotNull ItemStack item) {
		String itemDisplayName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
		try {

			if (itemDisplayName.equals(ChatColor.BLUE + "ПопИт!")) {
				player.openInventory(Dependencies.getPopIt());
			}
		} catch (Exception | Error exception) {
			return EVENT_STAT.THROWABLE;
		}
		try {
			if (itemDisplayName.equals(ChatColor.GREEN + "СимплДимпл!")) {
				player.openInventory(Dependencies.getSimpleDimple());
			}
		} catch (Exception | Error exception) {
			return EVENT_STAT.THROWABLE;
		}
		try {
			if ((item.getType() == Material.PLAYER_HEAD) || (item.getType() == Material.WITHER_SKELETON_SKULL)) {
				if (Objects.requireNonNull(player.getInventory().getHelmet()).getType() == Material.AIR) {
					player.getInventory().setHelmet(item);
				}
			}
		} catch (Exception | Error exception) {
			return EVENT_STAT.THROWABLE;
		}
		try {
			if (itemDisplayName.equals(ChatColor.GRAY + "Убийца Крови Воздуха Фрагмента Алюминия 2046")) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					player.sendMessage(ChatColor.GRAY + "По географическому районированию Украина находится в Палеарктиеской области, где выделяются Европейская и Средиземноморская подобласти. Первая подобласть, при этом, охватывает почти всю территорию страны, в то время как вторая (Средиземноморская) — локально затрагивает лишь небольшие по площади долинно-речные, водохранилищные и приморские районы.\n" +
							"""
									Европейская подобласть
																
									Лесная кошка
									В состав Европейской подобласти входят следующие зоогеографические провинции, для каждой из них присущ свой фаунистический комплекс:
																
									Смешанные леса являются ареалом таких животных как: гадюка обыкновенная, ящерица, тритон гребенчатый, уж, глухарь, рябчик, чёрный аист, бекас, лесной кулик, вальдшнеп, дятел, дрозд, синица, ястреб, полёвка, серый крот, бобёр речной, заяц, бурозубки обычная и малая, дикий кабан, выдра, благородный олень, волк, лесная кошка.
									Лесостепные районы — это среда обитания косули, благородного оленя, лесного полоза, степной гадюки, тритона гребенчатого, крапчатого суслика, серой куропатки, ястреба, лисицы, лесного жаворонка, барсука, куницы, дикого кабана, двухцветной летучей мыши, большого подковоноса, карася, щуки, леща, сома.
									В степях распространены: лягушка зелёная, водный уж, степная гадюка, полоз жёлтобрюхий, жаворонок, степной орёл, степной журавль, серая куропатка, земляной заяц, малый суслик, крыса, малый слепыш, нутрия, серый горностай, хомяк, полёвка, сурок европейский, муфлон, лань.
									В Карпатах можно встретить более редкие виды: альпийский и карпатский тритоны, пятнистая саламандра, чёрный дятел, орёл-беркут, белка, дикий кабан, выдра, благородный олень, волк, рысь, лесная кошка, лисица, бурый медведь, зубр. В крымских горах живут скальная ящерица, леопардовый полоз, морская чайка, сизый голубь, южный соловей, чёрный гриф, благородный олень, муфлон и другие виды.
									Средиземноморская подобласть
									Внезональные долинно-речные, водохранилищные и приморские участки относятся к Средиземноморской подобласти весьма условно — в них преобладает не средиземноморский климат, а природные условия степи и лесостепи, несколько трансформированные наличием водотоков и созданных на них водохранилищ. В эту категорию включаются прибрежные районы днепровских, днестровских и других водохранилищ, днепровско-бугские, дунайские, днестровские плавни. Характерной особенностью фауны этих территорий является их тесная связь с водными объектами, формирующими богатый и разнообразный животный мир, состоящий из рыбных ресурсов, водоплавающих птиц и других представителей водно-болотной фауны.""");

				}
			}
		} catch (Exception | Error exception) {
			return EVENT_STAT.THROWABLE;
		}
		try {
			if (itemDisplayName.equals(ChatColor.GOLD + "Молниеносная Палка")) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block blocky = player.getTargetBlock(null, 48);
					Location loc = blocky.getLocation();
					Objects.requireNonNull(loc.getWorld()).strikeLightning(loc);
				}
			}
		} catch (Exception | Error exception) {
			return EVENT_STAT.THROWABLE;
		}
		return EVENT_STAT.NOT_HANDLED;
	}
}


