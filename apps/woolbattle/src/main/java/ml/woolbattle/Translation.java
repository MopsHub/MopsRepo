package ml.woolbattle;

import org.bukkit.ChatColor;

public class Translation extends Plugin {
    public String getByLang(String lang, String string) {

        String string2 = ChatColor.RED + "Invalid String!";

        if (lang.equals("en")) {
            string2 = ChatColor.RED + "Invalid String!";

            switch (string) {
                case ("woolbattle.gotKilledBy"):
                    string2 = "got killed by";
                    break;
            }
            return string2;
        }
        if (lang.equals("ru")) {
            string2 = ChatColor.RED + "Invalid String!";

            switch (string) {
                case ("woolbattle.gotKilledBy") :
                    string2 = "был убит";
                    break;
            }
        }

        return string2;
    }
}

