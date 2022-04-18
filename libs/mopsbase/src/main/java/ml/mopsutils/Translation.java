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
    public HashMap<String, String> colors = new HashMap();

    public Translation(FileConfiguration fc, Logger logger, String base) {
        this.woolbattleTranslation = fc.getMapList(base);
        this.logger = logger;
        logger.info("woolbattle translation:\n" + Arrays.toString(woolbattleTranslation.toArray()));
    }

    public List<TextComponent> getTranslation(String lang, String string, Map<String, String> formatValues, boolean notSingular) {
        LinkedList<TextComponent> tc = new LinkedList<>();
        if (languages.contains(lang.toLowerCase(Locale.ROOT))) {
            String s = "";

            //TODO: нужно дробить строку на String[] по точки (что бы условный woolbattle.song.name) норм работал
            // Я наверное сам этим займусь

            logger.info("woolbattleTranslation: " + Arrays.toString(woolbattleTranslation.toArray()));
            for (Map<?, ?> mp : woolbattleTranslation) {
                logger.info(mp.toString());
                if (mp.containsKey(string)) {
                    s = ((Map<?, ?>) mp.get(string)).get(lang).toString();
                    logger.info(s);
                    break;
                }
            }

            if (s.isBlank()) {
                tc.add(invalidString);
                logger.warning("invalid string");
            } else {
                for (String K : formatValues.keySet()) {
                    s = s.replaceAll(K, formatValues.get(K));
                }
                if (notSingular) {
                    String[] ses = s.split("\n");
                    for (String sus : ses) {
                        tc.add(legacyAmpersand().deserialize(sus));
                    }
                }
                else {
                    tc.add(legacyAmpersand().deserialize(s));
                }
            }
            return tc;
        }

        logger.warning("invalidlanguage: " + lang);
        LinkedList aaa = new LinkedList<>();
        aaa.add(invalidLanguage);
        return aaa;

    }

    public TextComponent getTranslation(String lang, String string) {
        return getTranslation(lang, string, colors, false).get(0);
    }
}