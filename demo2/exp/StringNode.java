package demo2.exp;

import demo2.lexer.Token;


public class StringNode extends Node{
	String value;
	public StringNode(String value){
		this.value = value;
		this.token = new Token(null,value);
	}
	public String toString(){
		return '"'+value;
	}
 
}
