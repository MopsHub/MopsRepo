package ml.mopspvps.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SimpleDimple {
	public Inventory simpleDimple() {
		// Инициализация инвентаря
		Inventory simpleDimple = Bukkit.createInventory(null, 9, Component.text("СимплДимпл!").color(NamedTextColor.GREEN));
		// Инициализация, добавление и настройка предметов
		ItemStack item = new ItemStack(Material.LIME_TERRACOTTA);
		ItemMeta meta = item.getItemMeta();
		assert meta != null;
		meta.displayName(Component.text("СимплДимпл!").color(NamedTextColor.GREEN));
		item.setItemMeta(meta);
		simpleDimple.setItem(2, item);
		simpleDimple.setItem(6, item);
		return simpleDimple; // Метод возвращает объект класса Inventory
	}
}
