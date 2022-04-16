package ml.mopspvps.commands;

import ml.mopspvps.Commands;
import ml.mopspvps.Dependencies;
import ml.mopspvps.messages.CommandError;
import ml.mopspvps.utils.CHARACTER;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;
import ml.mopspvps.utils.Utils;
import java.util.*;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

/**
 * Обработчик комманд администрации (требуют права оператора)
 *
 * @author Kofiy
 */

public class AdminUtils extends Commands {
	@Override
	public boolean commandsExecutor(CommandSender sender, Command command, String label, String[] args) {
		boolean perms = sender.isOp(); //проверка прав
		if (args == null) { //проверка аргументов на нуль
			args = new String[] {""};
		}

		String arguments = Utils.combineStrings(args, CHARACTER.SPACE); //Объеденение всех аргументов в строку в стиле "arg0 arg1 arg2"
		String commandName = command.getName().toLowerCase(Locale.ROOT); //Получение названия команды

		if (sender instanceof Player && perms) { //Проверка на права и игрока
			Player player = (Player) sender;
			Player target;
			switch (commandName) { //Проверка команды
				case "crank": {
					try {
						target = Bukkit.getServer().getPlayer(args[0]); //Попытка получить игрока
					} catch (ArrayIndexOutOfBoundsException expection) {
						if (args[0].toLowerCase(Locale.ROOT).equals("me")) { //Проверка на использование me
							target = player;
						} else { //Ошибка, и всё к ней присущее
							TextComponent errorCode = CommandError.UNKNOWN_PLAYER.errorCode();
							Sound errorSound = CommandError.UNKNOWN_PLAYER.errorSound();

							player.sendMessage(errorCode);
							player.playSound(player.getLocation(), errorSound, 1, 2);

							Dependencies.getLog().info("<=КОМАНДЫ AU=> Игрок " + player.getName() + " ввёл команду ,," + commandName + arguments + "'' и вызвал ошибку:");
							Dependencies.getLog().info("<=КОМАНДЫ AU=> " + ChatColor.RED + expection.getMessage());
							Dependencies.getLog().info("<=КОМАНДЫ AU=> Игроку " + player.getName() + " было отправленно сообщение о ошибке: " + legacyAmpersand().serialize(errorCode));
							return true;
						}
					}
					Integer[] array0 = {0}; //Обозначает, что в строке nextArgument будет пропущен аргумент arg[0]
					String nextArguments = Utils.combineStrings(args, CHARACTER.SPACE, array0);

					if (nextArguments.isEmpty() || nextArguments.isBlank() || nextArguments == null) { //Если последующих аргументов нет
						String rank = ChatColor.GRAY + ""; //стандартный ранг
						String name = target.getName();
						Dependencies.putMopsRank(target, rank);
						Dependencies.putMopsName(target, name);
						Dependencies.getLog().info("<=РАНГИ=> Записано имя " + name + "с рангом " + rank); //логги

						Utils.updateDisplayName(target); //Обновление имени

						if (target.equals(player)) { //Сообщения о успешном ресете ранга
							target.sendMessage(ChatColor.GREEN + "Вы ресетнули свой ранг.");
						} else {
							final TextComponent c1 = Component.text("Вы ресетнули ранг игроку ").color(NamedTextColor.GREEN).append(target.name().color(NamedTextColor.GREEN));
							player.sendMessage(c1);
							final TextComponent c2 = Component.empty().append(player.name().color(NamedTextColor.GREEN)).append(Component.text(" ресетнул Ваш ранг."));
							target.sendMessage(c2);

							player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 2);
							target.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 2);
						}

						player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 2);
						return true;
					}

					String rank = nextArguments; //Установка ранга
					Dependencies.putMopsRank(target, rank);

					Utils.updateDisplayName(target); //Обновление имени

					if (target.equals(player)) { //Сообщение о успешном выполнении команды
						target.sendMessage(ChatColor.GREEN + "Вы изменили свой ранг.");
					} else {
						final TextComponent c1 = Component.text("Вы изменили ранг игроку ").color(NamedTextColor.GREEN).append(target.name().color(NamedTextColor.GREEN));
						player.sendMessage(c1);
						final TextComponent c2 = Component.empty().append(player.name().color(NamedTextColor.GREEN)).append(Component.text(" изменил Ваш ранг."));
						target.sendMessage(c2);

						player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 2);
						target.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 2);
					}


				}
				case "cname": {

				}
				case "cnameitem": {

				}
				default: { //Сообщение о том что комманда не найдена
					TextComponent errorCode = CommandError.ARGUMENT_NAN.errorCode();
					Sound errorSound = CommandError.ARGUMENT_NAN.errorSound();

					player.sendMessage(errorCode);
					player.playSound(player.getLocation(), errorSound, 1, 2);

					Dependencies.getLog().info("<=КОМАНДЫ AU=> Игрок " + player.getName() + " ввёл неизвестную команду ,," + commandName + arguments + "''");
					Dependencies.getLog().info("<=КОМАНДЫ AU=> Игроку " + player.getName() + " было отправленно сообщение о ошибке: " + legacyAmpersand().serialize(errorCode));
				}
			}
		} else {
			TextComponent errorCode = CommandError.NO_OP.errorCode(); //Логи
			Sound errorSound = CommandError.NO_OP.errorSound();

			sender.sendMessage(errorCode);

			Dependencies.getLog().info("<=КОМАНДЫ AU=> Существо/Игрок С/БЕЗ оп " + sender.getName() + " ввёл(о) команду ,," + commandName + arguments + "''");
			Dependencies.getLog().info("<=КОМАНДЫ AU=> Существу " + sender.getName() + " было отправленно сообщение о ошибке: " + legacyAmpersand().serialize(errorCode));
		}
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (commandName.equals("customgive")) {
				if (perms) {
					player.openInventory(Dependencies.getCustomGive());
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
				}
				return true;
			}

			if (commandName.equals("slimetest")) {
				if (perms) {
					Slime slime = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME);

					int integer;
					if (args.length == 0 || args[0].equals("")) {
						player.sendMessage(ChatColor.RED + "Напишите цифру.");
						return true;
					} else {
						String string = args[0];
						try {
							integer = Integer.parseInt(string);
						} catch (Exception e) {
							TextComponent errorCode = CommandError.ARGUMENT_NAN.errorCode();
							Sound errorSound = CommandError.ARGUMENT_NAN.errorSound();

							player.sendMessage(errorCode);
							player.playSound(player.getLocation(), errorSound, 1, 2);

							Dependencies.getLog().info("<=КОМАНДЫ AU=> Игрок " + player.getName() + " ввёл команду ,," + commandName + arguments + "'' и вызвал ошибку:");
							Dependencies.getLog().info("<=КОМАНДЫ AU=> " + ChatColor.RED + e.getMessage());
							Dependencies.getLog().info("<=КОМАНДЫ AU=> Игроку " + player.getName() + " было отправленно сообщение о ошибке: " + legacyAmpersand().serialize(errorCode));
							return true;
						}

						slime.setSize(integer);
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
				}
				return true;
			}

			if (commandName.equals("name")) {
				if (perms) {
					if (args.length == 0 || args[0].equals("")) {
						Dependencies.putMopsName(player, player.getName());
						player.sendMessage(ChatColor.GREEN + "Вы ресетнули свой ник.");
						player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 2);
					} else {
						String string = args[0];
						String string1 = string.replaceAll("_", " ").replaceAll("&0", ChatColor.BLACK + "").replaceAll("&1", ChatColor.DARK_BLUE + "").replaceAll("&2", ChatColor.DARK_GREEN + "").replaceAll("&3", ChatColor.DARK_AQUA + "").replaceAll("&4", ChatColor.DARK_RED + "").replaceAll("&5", ChatColor.DARK_PURPLE + "").replaceAll("&6", ChatColor.GOLD + "").replaceAll("&7", ChatColor.GRAY + "").replaceAll("&8", ChatColor.DARK_GRAY + "").replaceAll("&9", ChatColor.BLUE + "").replaceAll("&a", ChatColor.GREEN + "").replaceAll("&b", ChatColor.AQUA + "").replaceAll("&c", ChatColor.RED + "").replaceAll("&d", ChatColor.LIGHT_PURPLE + "").replaceAll("&e", ChatColor.YELLOW + "").replaceAll("&f", ChatColor.WHITE + "").replaceAll("&k", ChatColor.MAGIC + "").replaceAll("&l", ChatColor.BOLD + "").replaceAll("&m", ChatColor.STRIKETHROUGH + "").replaceAll("&n", ChatColor.UNDERLINE + "").replaceAll("&o", ChatColor.ITALIC + "").replaceAll("&r", ChatColor.RESET + "");
						Dependencies.putMopsName(player, string1.substring(0, 16));
						player.sendMessage(ChatColor.GREEN + "Вы изменили свой ник на " + ChatColor.RESET + string1);
					}
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
					player.setPlayerListName(Dependencies.getMopsRank(player) + Dependencies.getMopsName(player));
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("rank")) {
				if (perms) {
					if (args.length == 0 || args[0].equals("")) {
						Dependencies.putMopsRank(player, ChatColor.GRAY + "");
						player.sendMessage(ChatColor.GREEN + "Вы ресетнули свой ранг.");
						player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 2);
					} else {
						String string = args[0];
						String string1 = string.replaceAll("_", " ").replaceAll("&0", ChatColor.BLACK + "").replaceAll("&1", ChatColor.DARK_BLUE + "").replaceAll("&2", ChatColor.DARK_GREEN + "").replaceAll("&3", ChatColor.DARK_AQUA + "").replaceAll("&4", ChatColor.DARK_RED + "").replaceAll("&5", ChatColor.DARK_PURPLE + "").replaceAll("&6", ChatColor.GOLD + "").replaceAll("&7", ChatColor.GRAY + "").replaceAll("&8", ChatColor.DARK_GRAY + "").replaceAll("&9", ChatColor.BLUE + "").replaceAll("&a", ChatColor.GREEN + "").replaceAll("&b", ChatColor.AQUA + "").replaceAll("&c", ChatColor.RED + "").replaceAll("&d", ChatColor.LIGHT_PURPLE + "").replaceAll("&e", ChatColor.YELLOW + "").replaceAll("&f", ChatColor.WHITE + "").replaceAll("&k", ChatColor.MAGIC + "").replaceAll("&l", ChatColor.BOLD + "").replaceAll("&m", ChatColor.STRIKETHROUGH + "").replaceAll("&n", ChatColor.UNDERLINE + "").replaceAll("&o", ChatColor.ITALIC + "").replaceAll("&r", ChatColor.RESET + "");
						Dependencies.putMopsRank(player, string1.substring(0, 16) + " ");
						player.sendMessage(ChatColor.GREEN + "Вы изменили свой ранг на " + ChatColor.RESET + string1);
					}
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
					player.setPlayerListName(Dependencies.getMopsRank(player) + Dependencies.getMopsName());
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
				}
				return true;
			}
			if (commandName.equals("kickall")) {

				if (perms) {

					String string = args[0];
					String string1 = string.replaceAll("_", " ").replaceAll("&0", ChatColor.BLACK + "").replaceAll("&1", ChatColor.DARK_BLUE + "").replaceAll("&2", ChatColor.DARK_GREEN + "").replaceAll("&3", ChatColor.DARK_AQUA + "").replaceAll("&4", ChatColor.DARK_RED + "").replaceAll("&5", ChatColor.DARK_PURPLE + "").replaceAll("&6", ChatColor.GOLD + "").replaceAll("&7", ChatColor.GRAY + "").replaceAll("&8", ChatColor.DARK_GRAY + "").replaceAll("&9", ChatColor.BLUE + "").replaceAll("&a", ChatColor.GREEN + "").replaceAll("&b", ChatColor.AQUA + "").replaceAll("&c", ChatColor.RED + "").replaceAll("&d", ChatColor.LIGHT_PURPLE + "").replaceAll("&e", ChatColor.YELLOW + "").replaceAll("&f", ChatColor.WHITE + "").replaceAll("&k", ChatColor.MAGIC + "").replaceAll("&l", ChatColor.BOLD + "").replaceAll("&m", ChatColor.STRIKETHROUGH + "").replaceAll("&n", ChatColor.UNDERLINE + "").replaceAll("&o", ChatColor.ITALIC + "").replaceAll("&r", ChatColor.RESET + "");

					for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
						if (!player1.isOp()) {
							player1.kickPlayer(string1);
						} else {
							player1.sendMessage(ChatColor.RED + "Возможно вам стоить выйти по причине " + ChatColor.RESET + string1);
						}
					}

				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
				}
				return true;
			}
			if (commandName.equals("test")) {
				if (perms) {
					try {

					} catch (ArrayIndexOutOfBoundsException event) {
						player.sendMessage("ало ты какой то там эррей не написал");
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
				}
				return true;
			}
			if (commandName.equals("vector")) {
				if (perms) {
					double x = 0;
					double y = 0;
					double z = 0;
					try {
						x = Double.parseDouble(args[0]);
						y = Double.parseDouble(args[1]);
						z = Double.parseDouble(args[2]);
					} catch (ArrayIndexOutOfBoundsException ignored) {
					}
					Player player1;
					try {
						player = Bukkit.getServer().getPlayer(args[3]);
					} catch (ArrayIndexOutOfBoundsException expection) {

						player1 = player;
					}
					assert player != null;
					player.setVelocity(new Vector(x, y, z).multiply(2));
				}
				return true;
			}
			if (commandName.equals("coins")) {
				if (perms) {
					Player player1;
					try {
						player1 = Bukkit.getPlayer(args[1]);
					} catch (ArrayIndexOutOfBoundsException event) {
						player1 = player;
					}
					assert player1 != null;
					Score score = Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective("coins")).getScore(player1);
					score.setScore(Integer.parseInt(args[0]));
				}
				return true;
			}

			if (commandName.equals("loreadd")) {
				if (perms) {
					String string = args[0];
					String string2 = string.replaceAll("_", " ").replaceAll("&0", ChatColor.BLACK + "").replaceAll("&1", ChatColor.DARK_BLUE + "").replaceAll("&2", ChatColor.DARK_GREEN + "").replaceAll("&3", ChatColor.DARK_AQUA + "").replaceAll("&4", ChatColor.DARK_RED + "").replaceAll("&5", ChatColor.DARK_PURPLE + "").replaceAll("&6", ChatColor.GOLD + "").replaceAll("&7", ChatColor.GRAY + "").replaceAll("&8", ChatColor.DARK_GRAY + "").replaceAll("&9", ChatColor.BLUE + "").replaceAll("&a", ChatColor.GREEN + "").replaceAll("&b", ChatColor.AQUA + "").replaceAll("&c", ChatColor.RED + "").replaceAll("&d", ChatColor.LIGHT_PURPLE + "").replaceAll("&e", ChatColor.YELLOW + "").replaceAll("&f", ChatColor.WHITE + "").replaceAll("&k", ChatColor.MAGIC + "").replaceAll("&l", ChatColor.BOLD + "").replaceAll("&m", ChatColor.STRIKETHROUGH + "").replaceAll("&n", ChatColor.UNDERLINE + "").replaceAll("&o", ChatColor.ITALIC + "").replaceAll("&r", ChatColor.RESET + "" + ChatColor.WHITE);
					try {
						ItemStack item = player.getInventory().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();

						assert meta != null;
						if (meta.hasLore()) {
							List<String> lore = meta.getLore();
							assert lore != null;
							lore.add(string2);
							meta.setLore(lore);
						} else {
							List<String> lore = new ArrayList<>();
							lore.add(string2);
							meta.setLore(lore);
						}
						item.setItemMeta(meta);
						player.sendMessage(ChatColor.GREEN + "Вы добавили " + ChatColor.DARK_PURPLE + ChatColor.ITALIC + string2 + ChatColor.RESET + ChatColor.GREEN + " в описание предмета.");
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
						return true;
					} catch (NullPointerException event) {
						player.sendMessage(ChatColor.RED + "Вы не имеете предмета в руке!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("loreclear")) {
				if (perms) {
					try {
						ItemStack item = player.getInventory().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();
						assert meta != null;
						if (meta.hasLore()) {
							List<String> lore = meta.getLore();
							assert lore != null;
							lore.clear();
							meta.setLore(lore);
							item.setItemMeta(meta);
							player.sendMessage(ChatColor.GREEN + "Вы очистили лор предмета.");
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
						} else {
							player.sendMessage(ChatColor.RED + "У предмета нет лора!");
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						}
						return true;
					} catch (NullPointerException event) {
						player.sendMessage(ChatColor.RED + "Вы не имеете предмета в руке!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("nameitem")) {
				if (perms) {
					String string = args[0];
					String string2 = string.replaceAll("_", " ").replaceAll("&0", ChatColor.BLACK + "").replaceAll("&1", ChatColor.DARK_BLUE + "").replaceAll("&2", ChatColor.DARK_GREEN + "").replaceAll("&3", ChatColor.DARK_AQUA + "").replaceAll("&4", ChatColor.DARK_RED + "").replaceAll("&5", ChatColor.DARK_PURPLE + "").replaceAll("&6", ChatColor.GOLD + "").replaceAll("&7", ChatColor.GRAY + "").replaceAll("&8", ChatColor.DARK_GRAY + "").replaceAll("&9", ChatColor.BLUE + "").replaceAll("&a", ChatColor.GREEN + "").replaceAll("&b", ChatColor.AQUA + "").replaceAll("&c", ChatColor.RED + "").replaceAll("&d", ChatColor.LIGHT_PURPLE + "").replaceAll("&e", ChatColor.YELLOW + "").replaceAll("&f", ChatColor.WHITE + "").replaceAll("&k", ChatColor.MAGIC + "").replaceAll("&l", ChatColor.BOLD + "").replaceAll("&m", ChatColor.STRIKETHROUGH + "").replaceAll("&n", ChatColor.UNDERLINE + "").replaceAll("&o", ChatColor.ITALIC + "").replaceAll("&r", ChatColor.RESET + "" + ChatColor.WHITE);
					try {
						ItemStack item = player.getInventory().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();
						assert meta != null;
						meta.setDisplayName(string2);
						item.setItemMeta(meta);
						player.sendMessage(ChatColor.GREEN + "Вы назвали предмет " + ChatColor.RESET + string2 + ChatColor.GREEN + ".");
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
						return true;
					} catch (NullPointerException event) {
						player.sendMessage(ChatColor.RED + "Вы не имеете предмета в руке!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("enchantitem")) {
				if (perms) {
					try {
						String ench = args[0];
						String lvl0 = args[1];
						try {
							int lvl = Integer.parseInt(lvl0);
							try {
								try {
									ItemStack item = player.getInventory().getItemInMainHand();
									ItemMeta meta = item.getItemMeta();
									assert meta != null;
									meta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(ench))), lvl, true);
									item.setItemMeta(meta);
									player.sendMessage(ChatColor.GREEN + "Вы дали предмету зачарование " + ChatColor.RESET + ench + " " + lvl + ChatColor.GREEN + " уровня.");
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
									return true;
								} catch (NullPointerException event) {
									player.sendMessage(ChatColor.RED + "Вы не имеете предмета в руке!");
									player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
									return true;
								}
							} catch (IllegalArgumentException event) {
								player.sendMessage(ChatColor.RED + "Это не найдено в базе зачарований.");
								player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
								return true;
							}
						} catch (NumberFormatException event) {
							player.sendMessage(ChatColor.RED + "Комманду нужно использовать как: /enchantitem " + ChatColor.AQUA + ench + lvl0);
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
							return true;
						}
					} catch (ArrayIndexOutOfBoundsException event) {
						player.sendMessage(ChatColor.RED + "Комманду нужно использовать как: /enchantitem " + ChatColor.AQUA + "<ЗАЧАРОВАНИЕ> " + "<УРОЕНЬ>");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("enchantclear")) {
				if (perms) {
					try {
						ItemStack item = player.getInventory().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();
						assert meta != null;
						if (meta.getEnchants().isEmpty()) {
							player.sendMessage(ChatColor.RED + "На предмете нет зачарований.");
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						} else {
							for (Enchantment e : item.getEnchantments().keySet()) {
								item.removeEnchantment(e);
							}
							player.sendMessage(ChatColor.GREEN + "Вы стёрли зачарования предмета.");
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
						}
						return true;
					} catch (NullPointerException event) {
						player.sendMessage(ChatColor.RED + "Вы не имеете предмета в руке!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("unbreak")) {
				if (perms) {
					try {
						ItemStack item = player.getInventory().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();
						assert meta != null;
						if (meta.isUnbreakable()) {
							player.sendMessage(ChatColor.YELLOW + "Вы " + ChatColor.RED + ChatColor.BOLD + "ВЫКЛЮЧИЛИ" + ChatColor.RESET + ChatColor.YELLOW + " неломаемость");
							meta.setUnbreakable(false);
						} else {
							player.sendMessage(ChatColor.YELLOW + "Вы " + ChatColor.GREEN + ChatColor.BOLD + "ВКЛЮЧИЛИ" + ChatColor.RESET + ChatColor.YELLOW + " неломаемость");
							meta.setUnbreakable(true);
						}
						item.setItemMeta(meta);
						return true;
					} catch (NullPointerException event) {
						player.sendMessage(ChatColor.RED + "Вы не имеете предмета в руке!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("fly")) {
				if (perms) {
					if (player.getAllowFlight()) {
						player.setAllowFlight(false);
						player.sendMessage(ChatColor.YELLOW + "Вы " + ChatColor.RED + ChatColor.BOLD + "ВЫКЛЮЧИЛИ" + ChatColor.RESET + ChatColor.YELLOW + " флай");
					} else {
						player.setAllowFlight(true);
						player.sendMessage(ChatColor.YELLOW + "Вы " + ChatColor.GREEN + ChatColor.BOLD + "ВКЛЮЧИЛИ" + ChatColor.RESET + ChatColor.YELLOW + " флай");
					}
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
				}
				return true;
			}
			if (commandName.equals("food")) {
				if (player.isOp()) {
					player.setFoodLevel(Integer.parseInt(args[0]));
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					return true;
				}
			}
			if (commandName.equals("announce")) {
				if (perms) {
					if (args.length == 0 || args[0].equals("")) {
						player.sendMessage(ChatColor.RED + "Вам нужно написать хоть что то.");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
					} else {
						String string = args[0];
						String string1 = string.replaceAll("_", " ").replaceAll("&0", ChatColor.BLACK + "").replaceAll("&1", ChatColor.DARK_BLUE + "").replaceAll("&2", ChatColor.DARK_GREEN + "").replaceAll("&3", ChatColor.DARK_AQUA + "").replaceAll("&4", ChatColor.DARK_RED + "").replaceAll("&5", ChatColor.DARK_PURPLE + "").replaceAll("&6", ChatColor.GOLD + "").replaceAll("&7", ChatColor.GRAY + "").replaceAll("&8", ChatColor.DARK_GRAY + "").replaceAll("&9", ChatColor.BLUE + "").replaceAll("&a", ChatColor.GREEN + "").replaceAll("&b", ChatColor.AQUA + "").replaceAll("&c", ChatColor.RED + "").replaceAll("&d", ChatColor.LIGHT_PURPLE + "").replaceAll("&e", ChatColor.YELLOW + "").replaceAll("&f", ChatColor.WHITE + "").replaceAll("&k", ChatColor.MAGIC + "").replaceAll("&l", ChatColor.BOLD + "").replaceAll("&m", ChatColor.STRIKETHROUGH + "").replaceAll("&n", ChatColor.UNDERLINE + "").replaceAll("&o", ChatColor.ITALIC + "").replaceAll("&r", ChatColor.RESET + "");
						for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
							player1.sendMessage(string1);
							player1.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "У вас нет OP!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 2);
				}
				return true;
			}
			return false;
		} else {
			if (sender instanceof Pig) { //мем небаньте пж я очень уважаю граждан украины героям слава
				Dependencies.getPlugin().getServer().broadcast(Component.text("ХОХЛА СПРО").color(NamedTextColor.AQUA).append(Component.text("СИТЬ ЗАБЫЛИ").color(NamedTextColor.YELLOW)));
			}
			return true;
		}

	}
}
