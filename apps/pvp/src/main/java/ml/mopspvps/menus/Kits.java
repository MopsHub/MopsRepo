package ml.mopspvps.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Класс инвентаря для меню выбора китов
 */
public class Kits {
	/**
	 * Функция, которая возвращает инвентарь Kits
	 */

	public enum KitsEnum { //TODO: вроде как изменились киты нужно чекнуть КООООТТТТТ РАБОТАААЙ
		BLOODY,
		PALADIN,
		WEREWOLF,
		ARCHER,
		GOLEM,
		POSEIDON,
		HEALER,
		PIGLET,
		NECROMANCER,
		ILLUSIONIST,
		WITHERLORD,
		ASSASSIN,
		DIVER,
		PHOENIX,
		BOMBER,
		MAGE
	}

	public Inventory kits() {
		//Инициализация инвентаря
		Inventory kits = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("Выдача предметов").color(NamedTextColor.YELLOW));
		// Инициализация, добавление и настройка предметов
		ItemStack item = new ItemStack(Material.PINK_SHULKER_BOX);
		ItemMeta meta = item.getItemMeta();
		assert meta != null;
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Блади");
		item.setItemMeta(meta);
		kits.setItem(0, item);
		item.setType(Material.YELLOW_SHULKER_BOX);
		meta.setDisplayName(ChatColor.YELLOW + "Паладин");
		item.setItemMeta(meta);
		kits.setItem(1, item);
		item.setType(Material.GRAY_SHULKER_BOX);
		meta.setDisplayName(ChatColor.GRAY + "Оборотень");
		item.setItemMeta(meta);
		kits.setItem(2, item);
		item.setType(Material.GREEN_SHULKER_BOX);
		meta.setDisplayName(ChatColor.DARK_GREEN + "Лучник");
		item.setItemMeta(meta);
		kits.setItem(3, item);
		item.setType(Material.BROWN_SHULKER_BOX);
		meta.setDisplayName(ChatColor.GOLD + "Голем");
		item.setItemMeta(meta);
		kits.setItem(4, item);
		item.setType(Material.CYAN_SHULKER_BOX);
		meta.setDisplayName(ChatColor.DARK_AQUA + "Поссейдон");
		item.setItemMeta(meta);
		kits.setItem(5, item);
		item.setType(Material.WHITE_SHULKER_BOX);
		meta.setDisplayName(ChatColor.WHITE + "Хилер");
		item.setItemMeta(meta);
		kits.setItem(6, item);
		item.setType(Material.LIME_SHULKER_BOX);
		meta.setDisplayName(ChatColor.GREEN + "Свинья");
		item.setItemMeta(meta);
		kits.setItem(7, item);
		item.setType(Material.MAGENTA_SHULKER_BOX);
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Некромант");
		item.setItemMeta(meta);
		kits.setItem(8, item);
		item.setType(Material.BLUE_SHULKER_BOX);
		meta.setDisplayName(ChatColor.BLUE + "Иллюзионист");
		item.setItemMeta(meta);
		kits.setItem(9, item);
		item.setType(Material.RED_SHULKER_BOX);
		meta.setDisplayName(ChatColor.RED + "Визерлорд");
		item.setItemMeta(meta);
		kits.setItem(10, item);
		item.setType(Material.LIGHT_GRAY_SHULKER_BOX);
		meta.setDisplayName(ChatColor.GRAY + "Ассассин");
		item.setItemMeta(meta);
		kits.setItem(11, item);
		item.setType(Material.LIGHT_BLUE_SHULKER_BOX);
		meta.setDisplayName(ChatColor.AQUA + "Дайвер");
		item.setItemMeta(meta);
		kits.setItem(12, item);
		item.setType(Material.ORANGE_SHULKER_BOX);
		meta.setDisplayName(ChatColor.GOLD + "Феникс");
		item.setItemMeta(meta);
		kits.setItem(13, item);
		item.setType(Material.BLACK_SHULKER_BOX);
		meta.setDisplayName(ChatColor.GOLD + "Бомбер");
		item.setItemMeta(meta);
		kits.setItem(14, item);
		item.setType(Material.PURPLE_SHULKER_BOX);
		meta.setDisplayName(ChatColor.DARK_PURPLE + "Маг");
		item.setItemMeta(meta);
		kits.setItem(15, item);
		return kits; // Метод возвращает объект класса Inventory
	}
}


