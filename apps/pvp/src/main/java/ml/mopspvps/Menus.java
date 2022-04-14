package ml.mopspvps;

import ml.mopspvps.menus.*;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.inventory.Inventory;

/**
 * Класс Menus возвращает кастомный инвентарь.
 *
 * @author Kofiy
 */

public class Menus {
	/**
	 * Сама функция для полученя инвентаря
	 *
	 * @param menu Инвентарь, который необходимо получить
	 * @return Возвращает Inventory
	 * @throws NullArgumentException Если нету инвентаря - то происходит данная ошибка
	 */
	public Inventory getMenu(MENUS menu) throws NullArgumentException { // Основываясь на аргументе menu метод возврашает объект
		return switch (menu) {
			case CUSTOMGIVE -> new CustomGive().customGive();
			case POPIT -> new PopIt().popIt();
			case KITS -> new Kits().kits();
			case SIMPLEDIMPLE -> new SimpleDimple().simpleDimple();
		};
	}
}
