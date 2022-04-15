package ml.woolbattle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

public class Translation extends Plugin {
    private TextComponent invalidString = Component.text("Invalid String", NamedTextColor.RED);
    private TextComponent invalidLanguage = Component.text("Invalid Language", NamedTextColor.RED);

    public TextComponent getByLang(String lang, String string) {

        TextComponent translatedMessage = this.invalidString;

        if (lang.equals("en")) {

            switch (string) {
                case ("woolbattle.gotKilledBy"):
                    translatedMessage = Component.text("got killed by", NamedTextColor.WHITE);
                    break;
                case ("woolbattle.generator.uncaptured"):
                    translatedMessage = Component.text("UNCAPTURED", NamedTextColor.GRAY);
                    break;
                case ("woolbattle.redWool"):
                    translatedMessage = Component.text("Red Wool", NamedTextColor.RED);
                    break;
                case ("woolbattle.yellowWool"):
                    translatedMessage = Component.text("Yellow Wool", NamedTextColor.YELLOW);
                    break;
                case ("woolbattle.greenWool"):
                    translatedMessage = Component.text("Green Wool", NamedTextColor.GREEN);
                    break;
                case ("woolbattle.blueWool"):
                    translatedMessage = Component.text("Blue Wool", NamedTextColor.AQUA);
                    break;
            }
            return translatedMessage;
        }




        if (lang.equals("ru")) {
            translatedMessage = Component.text("Неправильная строка", NamedTextColor.RED);

            switch (string) {
                case ("woolbattle.gotKilledBy") :
                    translatedMessage = Component.text("был убит", NamedTextColor.WHITE);
                    break;
                case ("woolbattle.generator.uncaptured"):
                    translatedMessage = Component.text("НЕЗАХВАЧЕН", NamedTextColor.GRAY);
                    break;
                case ("woolbattle.redWool"):
                    translatedMessage = Component.text("Красная Шерсть", NamedTextColor.RED);
                    break;
                case ("woolbattle.yellowWool"):
                    translatedMessage = Component.text("Жёлтая Шерсть", NamedTextColor.YELLOW);
                    break;
                case ("woolbattle.greenWool"):
                    translatedMessage = Component.text("Зелёная Шерсть", NamedTextColor.GREEN);
                    break;
                case ("woolbattle.blueWool"):
                    translatedMessage = Component.text("Синяя Шерсть", NamedTextColor.AQUA);
                    break;
            }
            return translatedMessage;
        }

        return invalidLanguage;
    }
}