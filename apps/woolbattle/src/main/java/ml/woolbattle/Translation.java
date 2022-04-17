package ml.woolbattle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

public class Translation {
    private TextComponent invalidString = Component.text("Invalid String", NamedTextColor.RED);
    private TextComponent invalidLanguage = Component.text("Invalid Language", NamedTextColor.RED);
    protected FileConfiguration woolbattleTranslation;
    public List<String> languages = new LinkedList<>(Arrays.asList(new String[]{"rus", "eng"}));

    public Translation(FileConfiguration fc, Logger logger) {
        this.woolbattleTranslation = ((FileConfiguration) fc.get("woolbattle"));
        logger.info("woolbattle translation:\n" + woolbattleTranslation.saveToString());
    }

    public TextComponent getTranslation(String lang, String string) {
        if (languages.contains(lang.toLowerCase(Locale.ROOT))) {
            //TODO: нужно дробить строку на String[] по точки (что бы условный woolbattle.song.name) норм работал
            // Я наверное сам этим займусь
            TextComponent tc;
            String s = (String) woolbattleTranslation.getConfigurationSection(string).get(lang);
            tc = legacyAmpersand().deserialize(s);
            if (s.isBlank()) {
                tc = invalidString;
            }
            return tc;
        }
        else return invalidLanguage;

    }
//    public TextComponent getByLang(String lang, String string) {
//
//        TextComponent translatedMessage = this.invalidString;
//
//        if (lang.equals("en")) {
//
//            switch (string) {
//                case ("woolbattle.gotKilledBy"):
//                    translatedMessage = Component.text("got killed by", NamedTextColor.WHITE);
//                    break;
//                case ("woolbattle.generator.uncaptured"):
//                    translatedMessage = Component.text("UNCAPTURED", NamedTextColor.GRAY);
//                    break;
//                case ("woolbattle.redWool"):
//                    translatedMessage = Component.text("Red Wool", NamedTextColor.RED);
//                    break;
//                case ("woolbattle.yellowWool"):
//                    translatedMessage = Component.text("Yellow Wool", NamedTextColor.YELLOW);
//                    break;
//                case ("woolbattle.greenWool"):
//                    translatedMessage = Component.text("Green Wool", NamedTextColor.GREEN);
//                    break;
//                case ("woolbattle.blueWool"):
//                    translatedMessage = Component.text("Blue Wool", NamedTextColor.AQUA);
//                    break;
//            }
//            return translatedMessage;
//        }
//
//
//
//
//        if (lang.equals("ru")) {
//            translatedMessage = Component.text("Неправильная строка", NamedTextColor.RED);
//
//            switch (string) {
//                case ("woolbattle.gotKilledBy") :
//                    translatedMessage = Component.text("был убит", NamedTextColor.WHITE);
//                    break;
//                case ("woolbattle.generator.uncaptured"):
//                    translatedMessage = Component.text("НЕЗАХВАЧЕН", NamedTextColor.GRAY);
//                    break;
//                case ("woolbattle.redWool"):
//                    translatedMessage = Component.text("Красная Шерсть", NamedTextColor.RED);
//                    break;
//                case ("woolbattle.yellowWool"):
//                    translatedMessage = Component.text("Жёлтая Шерсть", NamedTextColor.YELLOW);
//                    break;
//                case ("woolbattle.greenWool"):
//                    translatedMessage = Component.text("Зелёная Шерсть", NamedTextColor.GREEN);
//                    break;
//                case ("woolbattle.blueWool"):
//                    translatedMessage = Component.text("Синяя Шерсть", NamedTextColor.AQUA);
//                    break;
//            }
//            return translatedMessage;
//        }
//
//        return invalidLanguage;
//    }
}