package demo2.compiler;

import demo2.exp.Node;

public class CallccException extends RuntimeException {

	private Node returnNode;

	public CallccException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CallccException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public Node getReturnNode() {
		return returnNode;
	}

	public void setReturnNode(Node returnNode) {
		this.returnNode = returnNode;
	}
	
 

}
