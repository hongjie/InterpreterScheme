package demo2;

import java.util.Scanner; 

import demo2.compiler.Interpreter;
import demo2.compiler.filter.ParserAgain;
import demo2.env.Env;
import demo2.exp.Node;
import demo2.lexer.Code;
import demo2.lexer.Lexer;
import demo2.lexer.ScmLexer;
import demo2.parser.Parser;

public class Repl {
	static StringBuilder buffer = new StringBuilder();
	static ParserAgain expand = new  ParserAgain();
	static Interpreter i  = new Interpreter();
	static Env globalenv	=	new Env(i);
	
	
	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in); 
		
		while(true){
			onetime(input);
			
		}
	}
	
	public static void onetime(Scanner input){
		
		Node node = null; 
		
		System.out.println("scheme demo");
		
		do{
			System.out.print("=> ");
			
			buffer.append(input.nextLine()).append(" ");
			
			Code code = new Code(buffer.toString());
			Lexer lexer = new ScmLexer(code); 
			node = new Parser(lexer).parse(); 
			
		}while(node==null);//直到输入简单语法完整或者多余。
		 
		 
		try{
			Node o = expand.filter(node); 
			
			Node r = i.eval(o,globalenv);
			
			System.out.println(r);
			
		}catch(RuntimeException e){
			
			System.out.println("\nError: "+ e.getMessage()+"\n");
		}
		
		buffer.delete(0, buffer.length());
	}
}
