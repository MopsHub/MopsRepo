package ml.mopspvps.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Класс для меню попита
 */
public class PopIt {
	/**
	 * Функция, которая возвращает меню попита
	 *
	 * @return Inventory
	 */
	public Inventory popIt() {
		// Инициализация кастомного инвентаря
		Inventory popIt = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("ПопИТ!").color(NamedTextColor.LIGHT_PURPLE));
		// Инициализация, добавление и настройка предметов
		ItemStack item = new ItemStack(Material.PINK_TERRACOTTA);
		ItemMeta meta = item.getItemMeta();
		assert meta != null;
		meta.displayName(Component.text("ПопИТ!").color(NamedTextColor.LIGHT_PURPLE));
		item.setItemMeta(meta);
		for (int i = 0; i < 27; i++) {
			popIt.setItem(i, item);

		}
		return popIt; // Метод возвращает объект класса Inventory
	}
}
