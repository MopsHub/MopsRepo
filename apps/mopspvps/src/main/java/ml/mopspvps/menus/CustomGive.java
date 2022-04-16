package ml.mopspvps.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Инвентарь - который нужен для команды /customgive
 */
public class CustomGive {
	/**
	 * @return Возвращает org.bukkit.inventory
	 */
	public Inventory customGive() {
		try {
			// Инициализация, инвентаря
			Inventory customGive = Bukkit.createInventory(null, 18, Component.text("Выдача предметов").color(NamedTextColor.YELLOW));
			// Инициализация, добавление и настройка предметов
			ItemStack item = new ItemStack(Material.ACACIA_BOAT);
			ItemMeta meta = item.getItemMeta();
			ItemStack item2 = new ItemStack(Material.ACACIA_BOAT);
			ItemMeta meta2 = item.getItemMeta();
			//Item 1 - Предмет один = Молниеносная Палка
			item.setType(Material.LIGHTNING_ROD);
			assert meta != null;
			meta.setDisplayName(ChatColor.GOLD + "Молниеносная Палка");
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(ChatColor.YELLOW + "Способность Предмета: Молния!" + ChatColor.BOLD + " ПКМ");
			lore.add(ChatColor.GRAY + "Нажмите ПКМ чтобы запустить");
			lore.add(ChatColor.GRAY + "молнию.");
			meta.setLore(lore);
			item.setItemMeta(meta);
			customGive.setItem(0, item);
			//Item 2 - Предмет два = Слизень Мощи
			item2.setType(Material.SLIME_BALL);
			assert meta2 != null;
			meta2.setDisplayName(ChatColor.GREEN + "Слизень Мощи");
			List<String> lore2 = new ArrayList<>();
			meta2.addEnchant(Enchantment.KNOCKBACK, 10, true);
			lore2.add("");
			lore2.add(ChatColor.YELLOW + "Способность Предмета: Прыжки!" + ChatColor.BOLD + " ПКМ");
			lore2.add(ChatColor.GRAY + "Нажмите ПКМ чтобы очень");
			lore2.add(ChatColor.GRAY + "сильно прыгнуть.");
			meta2.setLore(lore2);
			item2.setItemMeta(meta2);
			customGive.setItem(1, item2);
			//Item 3 - Предмет три = Убийца крови воздуха фрагмента алюминия убейте меня пж в году 2046
			item.setType(Material.IRON_INGOT);
			meta.setDisplayName(ChatColor.GRAY + "Убийца Крови Воздуха Фрагмента Алюминия 2046");
			lore.clear();
			lore.add("");
			lore.add(ChatColor.YELLOW + "Способность Предмета: иди нахер" + ChatColor.BOLD + " ПКМ");
			lore.add(ChatColor.GRAY + "Нажмите ПКМ чтобы");
			meta.setLore(lore);
			item.setItemMeta(meta);
			customGive.setItem(2, item);
			return customGive;
		} catch (Exception e) {
			return null; // Метод возвращает объект класса Inventory
		}

	}
}
