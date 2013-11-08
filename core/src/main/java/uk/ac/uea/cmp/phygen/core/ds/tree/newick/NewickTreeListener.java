/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.phygen.core.ds.tree.newick;// Generated from NewickTree.g4 by ANTLR 4.1
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link NewickTreeParser}.
 */
public interface NewickTreeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(@NotNull NewickTreeParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(@NotNull NewickTreeParser.ParseContext ctx);
}