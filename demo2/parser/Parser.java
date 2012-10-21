package demo2.parser;
import java.util.ArrayList;
import java.util.List;

import demo2.exp.BooleanNode;
import demo2.exp.Node;
import demo2.exp.Num;
import demo2.exp.StringNode;
import demo2.exp.Symbol;
import demo2.lexer.Lexer;
import demo2.lexer.ScmTokenType;
import demo2.lexer.Token;


public class Parser {
	private Token currtoken;
	private Lexer lexer;

	public Parser(Lexer lexer){
		this.lexer  = lexer;
		currtoken = this.lexer.getNextToken(); 
	}
	
	public Node parse(){
		
		Node node = null;
		
		try{
			
			node = exp();
			
		}catch(Exception e){
			//语法缺少
			return null;
		}
		
		//语法多余
		if(currtoken.getType() != ScmTokenType.EOF){
			//如何处理？
		}
		 
		 
		
		return node;
	}
	
	public Node exp(){
		
		Node node = new Node(1);
		
		match(ScmTokenType.LBRACK); 
		
		node.setChild(items());
		
		match(ScmTokenType.RBRACK);
		
		return node;
		
	}
	
	private List <Node> items(){
		
	 //quotes = {"'":_quote, "`":_quasiquote, ",":_unquote, ",@":_unquotesplicing}

		List <Node> nodes = new ArrayList<Node>();
		
		Object type  ;
		
		do{	
			
		     nodes.add(item());
			
			 type = currtoken.getType() ;
			
		}while(type != ScmTokenType.RBRACK && type != ScmTokenType.EOF);	
		
		return nodes;
			
	}
	private Node item(){
		
		Object type =  currtoken.getType() ;
		
		if(type == ScmTokenType.LBRACK){
			
			return exp();
		
		}else if(type == ScmTokenType.VARCHAR ){
			
			return varchar();
			
		
		}else if(type == ScmTokenType.NUM){
			
			 Node r = new Num(currtoken);
			 comsume(); 
			 return r;
		} 
		
		throw new Error("未知token类型");
		
	}
	
	
	//修改解析树，支持更多的Node节点类型
	
	private Node varchar(){
		String id = currtoken.getText();
		Node result = null;
		
		
		if(id.equalsIgnoreCase("#t"))
			result = new BooleanNode(Boolean.TRUE,currtoken);
		else if(id.equalsIgnoreCase("#f"))
			result = new BooleanNode(Boolean.FALSE,currtoken);
		else if(id.charAt(0) == '"')
			result = new StringNode(id.substring(1));
		 
		else if(isQuote(id)){   
			Node n =  new Node(2); 
			n.addChild(new Symbol(currtoken));
			comsume();
			if(currtoken.isType(ScmTokenType.LBRACK))
				 n.addChild(exp()); 
			else
				n.addChild(item());
			return n;
		}
			
		if(result == null)	
			result = new Symbol(currtoken);
		
		comsume();
		
		return result;
	}
 
	//引用字符
	private static String quotes[] = "' ` ,@ ,".split(" ");
	
	private boolean isQuote(String id){
		for(String q : quotes){
			if(id.equals(q))
				return true;
		}
		return false;
	}
	
	private void match(ScmTokenType wantedtype){
	
		if(currtoken.getType() != wantedtype){
			throw new RuntimeException(String.format("错误：位置%d,%d，期望%s,但得到%s",
					lexer.getCurrentPos()[0],lexer.getCurrentPos()[1],
					wantedtype,currtoken));
		}else
			comsume();
			
	}
	
	private void comsume(){
		currtoken = this.lexer.getNextToken();   
	}
	

	
}
