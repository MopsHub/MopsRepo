package ml.woolbattle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;

public class Translation extends Plugin {
    private TextComponent invalidString = Component.text("Invalid String", NamedTextColor.RED);
    private TextComponent invalidLanguage = Component.text("Invalid Language", NamedTextColor.RED);

    public TextComponent getByLang(String lang, String string) {

        TextComponent translatedMessage = this.invalidString;

        if (lang.equals("en")) {

            switch (string) {
                case ("woolbattle.gotKilledBy"):
                    translatedMessage = Component.text("got killed by");
                    break;
            }
            return translatedMessage;
        }
        if (lang.equals("ru")) {
            translatedMessage = Component.text("Неправильная строка", NamedTextColor.RED);

            switch (string) {
                case ("woolbattle.gotKilledBy") :
                    translatedMessage = Component.text("был убит");
                    break;
            }
            return translatedMessage;
        }

        return invalidLanguage;
    }
}

