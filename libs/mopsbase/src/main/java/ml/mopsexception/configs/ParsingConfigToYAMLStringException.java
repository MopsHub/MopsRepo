package ml.mopsexception.configs;

import ml.mopsexception.MopsConfigException;

import java.util.Arrays;

public class ParsingConfigToYAMLStringException extends MopsConfigException {
	public ParsingConfigToYAMLStringException(Exception e) {
		super("Error while parsing Config into yaml string\nException:" + e.getLocalizedMessage() + "\nStacktrace:\n\n\n" + Arrays.toString(e.getStackTrace()));
	}
}