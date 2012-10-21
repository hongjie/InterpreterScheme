package demo2.exp;

import demo2.env.Env;
import demo2.lexer.Token;


public class Function extends Node {
	
	public String name = "default-lambda";
	
	protected Env invokeTime_env;//执行时候外部环境？
	
	
	
	/**
	 * 
	 * @param token
	 *  
	 */
	public Function(Token token){
		 this.token = token; 
		 name = token.getText();
	}
	
	public Function(Symbol symbol){
		 this(symbol.getToken()); 
		 name = symbol.getToken().getText();
	}
	
	/**
	 * 依靠重载该方法，得到函数体。能把它设置为抽象吗.
	 * Lambda 在定义body的时候，把执行时候环境（形式，实际参数），定义时环境的包裹工作，做了。
	 * Function 在定义的时候，无设置，是顶级环境。
	 * @param args
	 * @return
	 */
	public  Node body(Node[] args){
		return null;
	};
	
	/**
	 * 执行的函数的入口
	 * @param args
	 * @return
	 */
	public  Node invoke(Node[] args,Env env){
		this.invokeTime_env = env; 
		return body(args);
	};
	
	/**
	 * 依靠重载该方法，paser时候，判断函数调用是否有效。能把它设置为抽象吗。 未调用
	 * @param args
	 * @return
	 */
	public void validate(){
		
	}
	
 
	
	
	public String toString(){
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Env getInvokeTime_env() {
		return invokeTime_env;
	}

	public void setInvokeTime_env(Env invokeTime_env) {
		this.invokeTime_env = invokeTime_env;
	}

 
 
	 
}
