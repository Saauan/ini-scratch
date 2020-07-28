package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName="case")
public class CaseStatement extends AstElement implements Statement {
	
	@Children public Rule[] cases;
	@Children public AstElement[] defaultStatements;

	public CaseStatement(List<Rule> cases, Sequence<AstElement> defaultStatements) {
		this.cases = cases.toArray(new Rule[0]);
		this.defaultStatements = defaultStatements != null ? (AstElement[]) ini.Utils.convertSequenceToArray(defaultStatements) : null;
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
	public void executeVoid(VirtualFrame frame) {
		frame.materialize();
		final int nbCases = cases.length;
		boolean foundRule = false;
		Rule currentRule;
		for(int i=0; i<nbCases && !foundRule; i++) {
			currentRule = cases[i];
			foundRule = currentRule.executeBoolean(frame);
		}
		if(!foundRule && this.defaultStatements != null) {
			// No caseRule was executed, executing the default statements
			final int nbStatements = this.defaultStatements.length;
			for(int i=0; i<nbStatements; i++) {
				defaultStatements[i].executeVoid(frame);
			}
		}
	}


	
}
