// Generated from uk/ac/uea/cmp/phybre/core/ds/tree/newick/parser/NewickTree.g4 by ANTLR 4.1
package uk.ac.uea.cmp.phybre.core.ds.tree.newick.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link NewickTreeParser}.
 */
public interface NewickTreeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(@NotNull NewickTreeParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(@NotNull NewickTreeParser.WeightContext ctx);

	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#subtree}.
	 * @param ctx the parse tree
	 */
	void enterSubtree(@NotNull NewickTreeParser.SubtreeContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#subtree}.
	 * @param ctx the parse tree
	 */
	void exitSubtree(@NotNull NewickTreeParser.SubtreeContext ctx);

	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(@NotNull NewickTreeParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(@NotNull NewickTreeParser.NameContext ctx);

	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#length}.
	 * @param ctx the parse tree
	 */
	void enterLength(@NotNull NewickTreeParser.LengthContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#length}.
	 * @param ctx the parse tree
	 */
	void exitLength(@NotNull NewickTreeParser.LengthContext ctx);

	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#branch}.
	 * @param ctx the parse tree
	 */
	void enterBranch(@NotNull NewickTreeParser.BranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#branch}.
	 * @param ctx the parse tree
	 */
	void exitBranch(@NotNull NewickTreeParser.BranchContext ctx);

	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#branchset}.
	 * @param ctx the parse tree
	 */
	void enterBranchset(@NotNull NewickTreeParser.BranchsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#branchset}.
	 * @param ctx the parse tree
	 */
	void exitBranchset(@NotNull NewickTreeParser.BranchsetContext ctx);

	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#leaf}.
	 * @param ctx the parse tree
	 */
	void enterLeaf(@NotNull NewickTreeParser.LeafContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#leaf}.
	 * @param ctx the parse tree
	 */
	void exitLeaf(@NotNull NewickTreeParser.LeafContext ctx);

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

	/**
	 * Enter a parse tree produced by {@link NewickTreeParser#internal}.
	 * @param ctx the parse tree
	 */
	void enterInternal(@NotNull NewickTreeParser.InternalContext ctx);
	/**
	 * Exit a parse tree produced by {@link NewickTreeParser#internal}.
	 * @param ctx the parse tree
	 */
	void exitInternal(@NotNull NewickTreeParser.InternalContext ctx);
}