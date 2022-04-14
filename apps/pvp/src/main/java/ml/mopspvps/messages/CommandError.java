package ml.mopspvps.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;

public enum CommandError implements Messages{
	NO_PERMS,
	NO_OP,
	WRONG_SERVER,
	DISABLED,
	SYNTAX,
	ARGUMENTS,
	ARGUMENT_NAN,
	ARGUMENT_NO,
	ARGUMENT_NOTALL,
	UNKNOWN_COMAND,
	UNKNOWN_PLAYER;

	public TextComponent errorCode() {
		TextComponent errorCode;
		switch (this) {
			case NO_PERMS -> errorCode = Component.text("У Вас нет прав.").color(NamedTextColor.RED);
			case NO_OP -> errorCode = Component.text("У Вас нет OP.").color(NamedTextColor.RED);
			case WRONG_SERVER -> errorCode = Component.text("Вы на неправильном сервере.").color(NamedTextColor.RED);
			case DISABLED -> errorCode = Component.text("Команда отключена").color(NamedTextColor.RED);
			case SYNTAX, ARGUMENTS -> errorCode = Component.text("Вы неправильно ввели параметры.").color(NamedTextColor.RED);
			case ARGUMENT_NAN -> errorCode = Component.text("Введите число.").color(NamedTextColor.RED);
			case ARGUMENT_NO -> errorCode = Component.text("Введите параметры.").color(NamedTextColor.RED);
			case ARGUMENT_NOTALL -> errorCode = Component.text("Вы не ввели все параметры").color(NamedTextColor.RED);
			case UNKNOWN_COMAND -> errorCode = Component.text("Неизвестная комманда. Проверьте правильность написания команды").color(NamedTextColor.RED);
			case UNKNOWN_PLAYER -> errorCode = Component.text("Введите никнейм игрока. Для обозначения себя можете воспользоваться me .").color(NamedTextColor.RED);
			default -> errorCode = Component.text("Неизвестная ошибка. Проверьте правильность написания команды").color(NamedTextColor.RED);
		}
		return errorCode;
	}

	public Sound errorSound() {
		Sound errorSound;
		switch (this) {
			case DISABLED, UNKNOWN_COMAND -> errorSound = Sound.BLOCK_LAVA_EXTINGUISH;
			default -> errorSound = Sound.ENTITY_PLAYER_HURT;
		}
		return errorSound;
	}

	@Override
	public Sound messageSound() { return errorSound(); }

	@Override
	public TextComponent messageComponent() { return errorCode(); }
}
