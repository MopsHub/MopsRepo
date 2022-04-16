package ml.mopsexception.configs;

import ml.mopsexception.MopsConfigException;

public class BlankConfigException extends MopsConfigException {
	public BlankConfigException() {
		super("Config is blank");
	}
}
