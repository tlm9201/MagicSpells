package com.nisovin.magicspells.util.grammars;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.RecognitionException;

public class GrammarUtils {

	public static final ANTLRErrorListener LEXER_LISTENER = new BaseErrorListener() {

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
			throw new RuntimeException("Lexer error on line " + line + " position " + charPositionInLine + ": " + msg, e);
		}

	};

	public static final ANTLRErrorListener PARSER_LISTENER = new BaseErrorListener() {

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
			throw new RuntimeException("Parser error on line " + line + " position " + charPositionInLine + ": " + msg, e);
		}

	};

}
