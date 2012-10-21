package demo2.exp;

import demo2.lexer.Token;

public class Symbol extends Node {
	
	public String value ;
	
 	public Symbol (Token t){ 
 		super(t);
		this.token = t;
		value = token.getText();
	}
 	
	public Symbol (String op){
		
		 this(new Token(null,op));//一定要token吗？
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return value;
	} 
 	


 

}
