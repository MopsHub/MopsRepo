package ml.mopsutils;

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
    protected Logger logger;
    public HashMap<String, String> colors = new HashMap<>();
    protected String base;

    public Translation(FileConfiguration fc, Logger logger, String base) {
        this.woolbattleTranslation = fc.getMapList("woolbattle");
        this.logger = logger;
        this.base = base;
        logger.info(base + " translation:\n" + Arrays.toString(woolbattleTranslation.toArray()));
    }

    public TextComponent getTranslation(String lang, String string, Map<String, String> formatValues) {
        logger.info("Translation.getTranslation (1): " + lang + "\n" + string + "\n" + formatValues.toString());
        TextComponent tc;
        if (languages.contains(lang.toLowerCase(Locale.ROOT))) {
            String s = "";

            //TODO: нужно дробить строку на String[] по точки (что бы условный woolbattle.song.name) норм работал
            // Я наверное сам этим займусь

            logger.info("woolbattleTranslation: " + Arrays.toString(woolbattleTranslation.toArray()));
            for (int i = 0; i < woolbattleTranslation.size(); i++) {
                Map<?, ?> mp = woolbattleTranslation.get(i);
                logger.info(mp.toString());
                if (mp.containsKey(string)) {
                    s = ((Map<?, ?>) mp.get(string)).get(lang).toString();
                    logger.info(string);
                    break;
                }
            }

            if (s.isBlank()) {
                tc = invalidString;
                logger.warning("invalid string");
            } else {
                for (String K : formatValues.keySet()) {
                    s = s.replaceAll("§" + K + "§", formatValues.get(K));
                }
                    tc = (legacyAmpersand().deserialize(s));

            }
            return tc;
        }

        return invalidLanguage;

    }

    public TextComponent getTranslation(String lang, String string) {
        logger.info("Translation.getTranslation (1): " + lang + "\n" + string + "\n" + colors);
        return getTranslation(lang, string, colors);
    }
}