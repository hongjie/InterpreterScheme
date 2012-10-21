package demo2.exp;

import demo2.lexer.Token;

public class Num extends Node{
	
	public int value;

	/**
	 * 叶子节点
	 * @param t
	 */
 	public Num(Token t){ 
 		
		this.token = t;
		
		try{
			if(t != null)
			value = Integer.parseInt(token.getText());
		}catch(Exception e){
			e.printStackTrace();
		}
	} 
 	
 	public Num(int v){
 		this.value = v;
 	}
 	
 	public String toString(){
 		return   String.valueOf(value);
 	}
}
