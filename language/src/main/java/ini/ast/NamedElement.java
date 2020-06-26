package ini.ast;

public abstract class NamedElement extends AstElement {
	public String name;

	public NamedElement(String name) {
		super();
		this.name = name;
	}
	
}
