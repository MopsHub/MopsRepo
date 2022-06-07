package ml.mopsutils;

import ml.mopsbase.MopsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class Utilities {
	MopsPlugin pl;

	public Utilities(MopsPlugin plugin) {
		this.pl = plugin;
	}

	public Duration ticks(int ticks) {
		return Duration.ofMillis(ticks * 50);
	}

	public TextComponent combineComponents(TextComponent[] tcs, TextComponent separator) {
		TextComponent fc = Component.empty();
		for (TextComponent tc : tcs) {
			fc.append(tc).append(separator);
		}
		return fc;
	}

	public Title createTitle(@NotNull String lang, @Nullable String id, @Nullable String id2nd, int i, int j, int k) {
		TextComponent c1;

		if (id2nd != null && id2nd.isBlank()) {
			c1 = Component.empty();
		} else {
			try {
				c1 = pl.getByLang(lang, id);

			} catch (Exception e) {
				c1 = Component.empty();
			}
		}

		TextComponent c2;
		if (id2nd != null && id2nd.isBlank()) {
			c2 = Component.empty();
		} else {
			try {
				c2 = pl.getByLang(lang, id2nd);

			} catch (Exception e) {
				c2 = Component.empty();
			}
		}

		final Title.Times times = Title.Times.times(ticks(i), ticks(k), ticks(j));

		return Title.title(c1, c2, times);
	}
}
