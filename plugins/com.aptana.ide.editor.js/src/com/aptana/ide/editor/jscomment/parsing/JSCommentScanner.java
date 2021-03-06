/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.jscomment.parsing;

import java.text.ParseException;

import com.aptana.ide.editor.jscomment.lexing.JSCommentTokenTypes;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class JSCommentScanner extends JSCommentParserBase
{
	private static final String MULTI_LINE_GROUP = "multiline"; //$NON-NLS-1$
	private static final String SINGLE_LINE_GROUP = "singleline"; //$NON-NLS-1$
	
	/**
	 * JSCommentScanner
	 * 
	 * @throws ParserInitializationException
	 */
	public JSCommentScanner() throws ParserInitializationException
	{
		this(JSCommentMimeType.MimeType);
	}

	/**
	 * JSCommentScanner
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public JSCommentScanner(String language) throws ParserInitializationException
	{
		super(language);
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void parseAll(IParseNode parentNode) throws ParseException, LexerException
	{
		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), "default"); //$NON-NLS-1$

		this.advance();
		
		try
		{
			switch (this.currentLexeme.typeIndex)
			{
				case JSCommentTokenTypes.START_MULTILINE_COMMENT:
					this.getLexer().setGroup(MULTI_LINE_GROUP);
					break;
					
				case JSCommentTokenTypes.START_SINGLELINE_COMMENT:
					this.getLexer().setGroup(SINGLE_LINE_GROUP);
					break;
			}
		}
		catch (LexerException e)
		{
		}

		while (this.isEOS() == false)
		{
			this.advance();
		}
		
		this.getParseState().clearEditState();
	}
}
