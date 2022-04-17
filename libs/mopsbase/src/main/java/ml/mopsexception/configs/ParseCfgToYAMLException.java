package ml.mopsexception.configs;

import ml.mopsexception.MopsConfigException;

import java.util.Arrays;

public class ParseCfgToYAMLException extends MopsConfigException {
	public ParseCfgToYAMLException(Exception e) {
		super("Error while parsing Config into yaml string\nException:" + e.getLocalizedMessage() + "\nStacktrace:\n\n\n" + Arrays.toString(e.getStackTrace()));
	}
}
