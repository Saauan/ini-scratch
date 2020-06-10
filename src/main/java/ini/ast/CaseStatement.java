package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.LoopConditionProfile;

import ini.parser.IniParser;
import ini.runtime.IniException;

@NodeInfo(shortName="case")
public class CaseStatement extends AstElement implements Statement {
	
	@Children public Rule[] cases;
	@Children public AstElement[] defaultStatements;
	
	private final LoopConditionProfile condition = LoopConditionProfile.createCountingProfile();

	public CaseStatement(IniParser parser, Token token, List<Rule> cases, Sequence<AstElement> defaultStatements) {
		super(parser, token);
		this.cases = cases.toArray(new Rule[0]);
		this.defaultStatements = (AstElement[]) ini.Utils.convertSequenceToArray(defaultStatements);
		this.nodeTypeId = AstNode.CASE_STATEMENT;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.println("    case {");
		if (cases != null) {
			for (Rule rule : cases) {
				rule.prettyPrint(out);
			}
		}
		out.println("      default {");
		if (defaultStatements != null) {
			for(int i=0; i<defaultStatements.length; i++) {
				out.print("        ");
				defaultStatements[i].prettyPrint(out);
				out.println();
			}
		}
		out.println("      }");
		out.println("    }");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitCaseStatement(this);
	}

	@Override
	public void executeVoid(VirtualFrame virtualFrame) {
		
		final int nbCases = cases.length;
		boolean foundRule = false;
		Rule currentRule;
		for(int i=0; condition.profile(i<nbCases && !foundRule); i++) {
			currentRule = cases[i];
			if (currentRule.guard != null) {
				try {
					if (currentRule.guard.executeBoolean(virtualFrame)) {
						currentRule.executeVoid(virtualFrame);
						foundRule = true;
					}
				} catch (UnexpectedResultException e) {
					throw IniException.typeError(this, currentRule);
				}
			}
		}
		if(!foundRule) {
			// No caseRule was executed, executing the default statements
			final int nbStatements = this.defaultStatements.length;
			for(int i=0; i<nbStatements; i++) {
				defaultStatements[i].executeVoid(virtualFrame);
			}
		}
	}


	
}
