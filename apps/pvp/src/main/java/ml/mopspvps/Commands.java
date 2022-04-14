package ml.mopspvps;

import ml.mopspvps.commands.AdminUtils;
import ml.mopspvps.commands.PlayerEssentials;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Обработчик комманд
 *
 * @author Kofiy
 */

public class Commands {
	// Хандлер команд

	public boolean commandsExecutor(CommandSender sender, Command command, String label, String[] args) {
		if (new AdminUtils().commandsExecutor(sender, command, label, args)) {
			return true;
		} else return new PlayerEssentials().commandsExecutor(sender, command, label, args);
	}

}
