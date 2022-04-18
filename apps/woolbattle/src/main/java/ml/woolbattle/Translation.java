package ml.woolbattle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

public class Translation {
    private final TextComponent invalidString = Component.text("Invalid String", NamedTextColor.RED);
    private final TextComponent invalidLanguage = Component.text("Invalid Language", NamedTextColor.RED);
    protected List<Map<?, ?>> woolbattleTranslation;
    public List<String> languages = new LinkedList<>(Arrays.asList("rus", "eng"));

    public Translation(FileConfiguration fc, Logger logger) {
        this.woolbattleTranslation = fc.getMapList("woolbattle");
        logger.info("woolbattle translation:\n" + Arrays.toString(woolbattleTranslation.toArray()));
    }

    public TextComponent getTranslation(String lang, String string, Logger logger) {
        if (languages.contains(lang.toLowerCase(Locale.ROOT))) {
            String str = string.replaceFirst("woolbattle.", "");
            String s = "";

            //TODO: нужно дробить строку на String[] по точки (что бы условный woolbattle.song.name) норм работал
            // Я наверное сам этим займусь

            logger.info("woolbattleTranslation: " + Arrays.toString(woolbattleTranslation.toArray()));
            for (int i = 0; i < woolbattleTranslation.size(); i++) {
                Map<?, ?> mp = woolbattleTranslation.get(i);
                logger.info(mp.toString());
                if (mp.containsKey(str)) {
                    s = ((Map<?, ?>) mp.get(str)).get(lang).toString();
                    logger.info(s);
                    break;
                }
            }
            TextComponent tc;
            if (s.isBlank()) {
                tc = invalidString;
                logger.warning("invalid string");
            } else {
                tc = legacyAmpersand().deserialize(s);
            }
            return tc;
        }
        else {
            logger.warning("invalidlanguage");
            return invalidLanguage;
        }


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