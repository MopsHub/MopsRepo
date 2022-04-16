package ml.mopsbase;

import java.util.List;

public class Config {
	public Config() {}

	private List<String> subConfigs;
	private String configsFolder;
	private Translations translations;

	class Translations {
		public Translations() {}

		private List<String> languages;
	}
}
