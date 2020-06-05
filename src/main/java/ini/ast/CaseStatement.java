package ini.ast;

import ini.parser.IniParser;
import ini.runtime.IniException;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeInfo(shortName="case")
public class CaseStatement extends AstElement implements Statement {
	@Children public Rule[] cases;
	@Children public AstElement[] defaultStatements;

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
	public Object executeGeneric(VirtualFrame virtualFrame) {
		for (Rule caseRule : cases) {
			try {
				if (caseRule.guard != null && caseRule.guard.executeBoolean(virtualFrame)) {
					caseRule.executeVoid(virtualFrame);
					return null;
				}
			} catch (UnexpectedResultException e) {
				throw IniException.typeError(this, caseRule);
			}
		}
		// No caseRule was executed, executing the default statements
		final int nbStatements = this.defaultStatements.length;
		for(int i=0; i<nbStatements; i++) {
			defaultStatements[i].executeVoid(virtualFrame);
		}
		return null;
	}
	
}
