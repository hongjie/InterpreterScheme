package demo2.exp;

import demo2.lexer.Token;

public class BooleanNode extends Node {
	public Boolean value;

	public BooleanNode(Boolean value,Token t){
		this.value = value;
		this.token = t;
	}
	
	public BooleanNode(Boolean value){
		this.value = value;
	}
	public String toString(){
		return ("#"+value).substring(0,2);
	}
}
