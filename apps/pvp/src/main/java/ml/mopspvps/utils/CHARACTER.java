package ml.mopspvps.utils;

import javax.annotation.CheckForNull;

public enum CHARACTER {
	DOT,                // . ТОЧКА
	PERIOD,             // . ТОЧКА
	COMMA,              // , ЗАПЯТАЯ
	COLON,              // : ДВОЕТОЧИЕ
	COLON_WITH_DOT,     // ; ДВОЕТОЧИЕ С ЗАПЯТОЙ
	SEMICOLON,          // ; ДВОЕТОЧИЕ С ЗАПЯТОЙ
	VERTICAL_LINE,      // | ЛИНИЯ
	DASH,               // - ТИРЕ
	TILDE,              // ~ ТИЛ(ть)ДА
	SPACE,              //   ПРОБЕЛ
	SPACEBAR;           //   ПРОБЕЛ

	public char getCharacter() {
		char chr;
		switch (this) {
			case DOT, PERIOD -> chr = '.';
			case COMMA -> chr = ',';
			case COLON -> chr = ':';
			case COLON_WITH_DOT, SEMICOLON -> chr = ';';
			case VERTICAL_LINE -> chr = '|';
			case DASH -> chr = '-';
			case TILDE -> chr = '~';
			case SPACE, SPACEBAR -> chr = ' ';
			default -> chr = ' ';
		}
		return chr;
	}

	public String getSymbol() {
		String chr = null;
		switch (this) {
			case DOT, PERIOD -> chr = ".";
			case COMMA -> chr = ",";
			case COLON -> chr = ":";
			case COLON_WITH_DOT, SEMICOLON -> chr = ";";
			case VERTICAL_LINE -> chr = "|";
			case DASH -> chr = "-";
			case TILDE -> chr = "~";
			case SPACE, SPACEBAR -> chr = " ";
		}
		return chr;
	}
}
