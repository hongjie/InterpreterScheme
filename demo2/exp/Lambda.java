package demo2.exp;

import demo2.env.Env;
import demo2.lexer.Token;

public class Lambda extends Function {

	private Node bodyNode ;
	 
	private Node[] param;
	
	protected Env env ; //能全当作生成时环境吗？
	
	public Lambda(Symbol symbol) {
		super(symbol);
		// TODO Auto-generated constructor stub
	}

	public Lambda(Token fun_token) {
		// TODO Auto-generated constructor stub
		super(fun_token);
	}


	public Node getBodyNode() {
		return bodyNode;
	}

	/**
	 * 尾递归
	 * @param bodyNode
	 */
	public void setBodyNode(Node bodyNode) {
		this.bodyNode = bodyNode;
	}

	public Node[] getParam() {
		return param;
	}

	public void setParam(Node[] param) {
		this.param = param;
	}

	public Env getEnv() {
		return env;
	}

	public void setEnv(Env env) {
		this.env = env;
	}

	public String toString(){
		return "lambda:" + this.getBodyNode(); 
	}
 

}
