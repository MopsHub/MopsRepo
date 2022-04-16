package ml.mopsexception.config;

import ml.mopsexception.MopsConfigException;

public class NoCustomConfig extends MopsConfigException {
	public NoCustomConfig() {
		super("No custom config found. Loading default one.");
	}
}
