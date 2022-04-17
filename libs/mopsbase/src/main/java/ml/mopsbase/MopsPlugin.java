package ml.mopsbase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ml.mopsbase.game.GameSession;
import ml.mopsexception.MopsConfigException;
import ml.mopsexception.MopsTranslationException;
import ml.mopsexception.configs.BlankConfigException;
import ml.mopsexception.configs.ParseCfgToYAMLException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class MopsPlugin extends JavaPlugin {
	private List<GameSession> gameSessions;
	protected FileConfiguration config;

	protected void loadTrainslation() throws MopsTranslationException {

	}

//	public void loadConfig(Logger logger) throws MopsConfigException {
//		InputStream configFile = this.getResource("config.yml");
//		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//		try {
//			this.config = objectMapper.readValue(configFile, Config.class);
//		} catch (IOException e) {
//			throw new MopsConfigException(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
//		}
//
//		Scanner reader = new Scanner(configFile);
//		String data = "";
//		while (reader.hasNextLine()) {
//			data = data + "\n" + reader.nextLine();
//			System.out.println(data);
//		}
//		if (data.isBlank()) {
//			throw new BlankConfigException();
//		}
//		logger.info("Loaded config.yml: \n" + data);
//	}
//
//	public void logConfig(Logger logger) throws NullPointerException, ParseCfgToYAMLException {
//		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//		String cfg = "";
//		try {
//			cfg = objectMapper.writeValueAsString(this.config);
//		} catch (JsonProcessingException e) {
//			throw new ParseCfgToYAMLException(e);
//		}
//		if (config.equals(new Config()) || cfg.isBlank()) {
//			logger.warning("Something wrong with config2yaml parser. String or Config object (hash: %s) are blank.".formatted(config.hashCode()));
//			throw new NullPointerException();
//		} else {
//			logger.info("Curently loaded config (parsed back2yaml): \n" + cfg);
//		}
//	}
//
//

		//		File cfgFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
//		String cfgText = "";
//
//		try {
//			Scanner myReader = new Scanner(cfgFile);
//			while (myReader.hasNextLine()) {
//				String data = myReader.nextLine();
//				cfgText = cfgText + data;
//			}
//			myReader.close();
//		} catch (FileNotFoundException e) {
//			logger.info("Custom config not found. Loading default one");
//			try {
//				Scanner myReader = new Scanner(cfgFile);
//				while (myReader.hasNextLine()) {
//					String data = myReader.nextLine();
//					cfgText = cfgText + data;
//				}
//				myReader.close();
//			}
//			catch (Exception ex) {
//				logger.warning("default config not found.");
//				logger.warning(Arrays.toString(ex.getStackTrace()));
//			}
//		}
//
//		if (cfgText.isBlank()) {
//			logger.warning("Both default and custom configs are not found/empty/blank");
//			this.setEnabled(false);
//			return;
//		}

//		File cfgFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
//		boolean isCustomConfigOk = false;
//		boolean isDefaultConfigOk;
//		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//
//		try {
//			if (cfgFile.exists()) {
//				logger.info("Trying to parse config at path: " + cfgFile.getAbsolutePath());
//				this.config = objectMapper.readValue(cfgFile, Config.class);
//				try {
//					logger.info("Custom config: \n" + this.config.parseToString());
//					isCustomConfigOk = true;
//					if (this.config.parseToString().isBlank()) {
//						isCustomConfigOk = false;
//						logger.info("Custom config is empty.");
//					}
//				} catch (Exception e) {
//					logger.warning("Unable to turn config into string due to:\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
//					isCustomConfigOk = false;
//				}
//			}
//		} catch (Exception e) {
//			logger.warning("Exception while parsing custom config: " + e.getMessage() + "\n " + Arrays.toString(e.getStackTrace()));
//		}
//
//		try {
//			logger.info("Trying to parse default config");
//
//			defaultConfig = objectMapper.readValue(this.getResource("config.yml"), Config.class);
//			try {
//				logger.info("Default config: \n" + this.defaultConfig.parseToString());
//				isDefaultConfigOk = true;
//				if (this.defaultConfig.parseToString().isBlank()) {
//					isDefaultConfigOk = false;
//					logger.warning("Default config is empty");
//				}
//				} catch (Exception e) {
//					logger.warning("Unable to turn config into string due to:\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
//					isDefaultConfigOk = false;
//				}
//			} catch (Exception e) {
//			logger.warning("Exception while parsing default config: " + e.getMessage() + "\n " + Arrays.toString(e.getStackTrace()));
//			this.setEnabled(false);
//			return;
//		}
//
//		if (!isCustomConfigOk) {
//			if (isDefaultConfigOk) {
//				this.config = this.defaultConfig;
//			} else {
//				logger.warning("Both custom and default configs are blank/corrupted/not working/not found");
//				this.setEnabled(false);
//				return;
//			}
//		}

}
