package ini.ast;

import ini.ast.at.At;
import ini.ast.expression.BinaryNode;
import ini.ast.expression.ShortCircuitNode;
import ini.ast.expression.UnaryNode;

//import ini.eval.function.BoundExecutable;

public interface Visitor {

	void visitAstElement(AstElement element);

	void visitArrayAccess(ArrayAccess arrayAccess);

	void visitAssignment(Assignment assignment);
	
	void visitAt(At at);
	
	void visitAtBinding(AtBinding atBinding);
	
	void visitAtPredicate(AtPredicate atPredicate);
	
	void visitBinaryOperator(BinaryOperator binaryOperator);
	
	void visitBinaryNode(BinaryNode binaryNode);
	
	void visitBinding(Binding binding);
	
	void visitBooleanLiteral(BooleanLiteral booleanLiteral);

//	void visitBoundExecutable(BoundExecutable boundExecutable);
	
	void visitCaseStatement(CaseStatement caseStatement);
	
	void visitChannel(ChannelDeclaration channel);
	
	void visitCharLiteral(CharLiteral charLiteral);
	
	void visitConditionalExpression(ConditionalExpression conditionalExpression);

	void visitConstructor(Constructor constructor);

	void visitConstructorMatchExpression(ConstructorMatchExpression constructorMatchExpression);

	void visitField(Field field);

	void visitFieldAccess(FieldAccess fieldAccess);

	void visitFunction(Function function);

	void visitImport(Import importStatement);

	void visitInvocation(Invocation invocation);

	void visitListExpression(ListExpression listExpression);

	void visitParameter(Parameter parameter);

	void visitLTLPredicate(LTLPredicate predicate);
	
	void visitNumberLiteral(NumberLiteral numberLiteral);

	void visitProcess(Process process);
	
	void visitProcessExecutor(ProcessExecutor processExecutor);
	
	void visitProcessReturnValue(ProcessReturnValue processReturnValue);

	void visitReadArgumentFromContextNode(ReadArgumentFromContextNode readArgumentFromContextNode);
	
	void visitReturnStatement(ReturnStatement returnStatement);

	void visitRule(Rule rule);

	void visitSetConstructor(SetConstructor constructor);

	void visitSetDeclaration(SetDeclaration setDeclaration);

	void visitSetExpression(SetExpression setExpression);

	void visitShortCiruitNode(ShortCircuitNode shortCircuitNode);
	
	void visitStringLiteral(StringLiteral stringLiteral);

	void visitSubArrayAccess(SubArrayAccess subArrayAccess);
	
	void visitThisLiteral(ThisLiteral thisLiteral);

	void visitTypeVariable(TypeVariable typeVariable);
	
	void visitUnaryOperator(UnaryOperator unaryOperator);
	
	void visitUnaryNode(UnaryNode unaryNode);
	
	void visitUserType(UserType userType);
	
	void visitVariable(Variable variable);

	


	
	
}

