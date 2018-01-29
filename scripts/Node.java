package scripts;

public abstract class Node {
	public abstract boolean validate();
	public abstract void execute();
	
	@Override
	public abstract String toString();
}
