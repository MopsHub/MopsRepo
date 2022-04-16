package ml.mopsexception;

public class MopsTranslationException extends Exception {
	public MopsTranslationException() {
		super("Translation error.");
	}
	public MopsTranslationException(String str) {
		super(str);
	}
}
