package ml.mopsbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ml.mopsexception.configs.BlankConfigException;
import ml.mopsexception.configs.ParsingConfigToYAMLStringException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Config {
	public Config() {}

	private List<String> subConfigs;
	private String configsFolder;
	private Translations translations;

	class Translations {
		public Translations() {}

		private List<String> languages;

		public List<String> getLanguages() {
			return languages;
		}

		public void setLanguages(List<String> languages) {
			this.languages = languages;
		}
	}

	@NotNull
	public String parseToString() throws ParsingConfigToYAMLStringException, BlankConfigException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		try {
			String yml = objectMapper.writeValueAsString(this);
			if (yml.isBlank()) {
				throw new BlankConfigException();
			}
			return yml;
		} catch (JsonProcessingException e) {
			throw new ParsingConfigToYAMLStringException(e);
		}
	}

	@Override
	@Nullable
	public String toString() {
		try {
			return this.parseToString();
		} catch (Exception e) {
			return null;
		}
	}

	public List<String> getSubConfigs() {
		return subConfigs;
	}

	public void setSubConfigs(List<String> subConfigs) {
		this.subConfigs = subConfigs;
	}

	public String getConfigsFolder() {
		return configsFolder;
	}

	public void setConfigsFolder(String configsFolder) {
		this.configsFolder = configsFolder;
	}

	public Translations getTranslations() {
		return translations;
	}

	public void setTranslations(Translations translations) {
		this.translations = translations;
	}
}
