package org.lazygamerz.Lexer;

import java.io.IOException;

/**
 * A lexer should implement this interface.
 */
public interface Lexer {

	/**
	 * Returns the next token.
	 * 
	 * @return the next token
	 */
	public Token getNextToken() throws java.io.IOException;

	/**
	 * Closes the current input stream, and resets the scanner to read from a
	 * new input stream. All internal variables are reset, the old input stream
	 * cannot be reused (content of the internal buffer is discarded and lost).
	 * The lexical state is set to the initial state. Subsequent tokens read
	 * from the lexer will start with the line, char, and column values given
	 * here.
	 * 
	 * @param reader
	 *            The new input.
	 * @param yyline
	 *            The line number of the first token.
	 * @param yychar
	 *            The position (relative to the start of the stream) of the
	 *            first token.
	 * @param yycolumn
	 *            The position (relative to the line) of the first token.
	 * @throws IOException
	 *             if an IOExecption occurs while switching readers.
	 */
	public void reset(java.io.Reader reader, int yyline, int yychar,
			int yycolumn) throws java.io.IOException;
}
