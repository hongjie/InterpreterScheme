package demo2.compiler.filter;

 
import java.util.HashMap;
import java.util.List; 
import java.util.logging.Level;
import java.util.logging.Logger;

import demo2.compiler.Interpreter;
import demo2.exp.Lambda;
import demo2.exp.Node;
import demo2.exp.NoneNode;
import demo2.exp.Symbol;
import demo2.lexer.ScmTokenType;
import demo2.lexer.Token;

public class ParserAgain {
	
	public static Logger log = Logger.getLogger("ParserAgain");
	{
		log.setLevel(Level.INFO);
	}
	
	//  仅仅是给宏使用的话，是可以的的。宏是全局下定义的。
	private Interpreter interpreter = new Interpreter() ;
	 
	
	public ParserAgain(){
		
	}
	
	
	//检查语法， 
	//展开语法.
	// 语法糖  库语法
	public Node filter(Node node){
		
		
		if(node.isNoChild() ){
			return node;
		}
		
		List<Node> children = node.getContent();
		
		assert_(children.size()>0,"节点不能为空",node);
		
		if(node.hasFun("quote") || node.hasFun("'")){
			
			assert_(children.size()==2,"quote 错误",node);
			return node;
			
		}else if(node.hasFun("if")){
			// alter为空，语法糖
			if(children.size()==3)
				children.add(new NoneNode());
			
			assert_(children.size() == 4 ,"if 错误 正确语法是<if> <pre> <con> [<alter>]",node);
			
			for (int i = 1; i < 4; i++) {
				children.set(i, filter(children.get(i)));
			}
			return node;
			
		} else if(node.hasFun("set!")){
			
			assert_(children.size() == 3, "set! 需要2个参数", node);
			assert_((children.get(1) instanceof Symbol),"set！ 第一个参数应该是符号",node); 
			
			return node;
			
		} else if(node.hasFun("define")){
			
			assert_(children.size() == 3, "define  需要2个参数", node); //? 3个
			
			Node namelist = children.get(1);
			Node body  = children.get(2);
			
			// 处理define (fn x y) () 语法糖
			if(  namelist.hasChild() ){ 
				Node fname = namelist.getContent().get(0);
				namelist.getContent().remove(0);
				Node params = namelist;
				
				Node newBody = new Node(3);
				newBody.addChild(new Node(new Token(ScmTokenType.VARCHAR,"lambda")));
				newBody.addChild( params);
				newBody.addChild(body);
				
				children.set(1,fname);
				children.set(2, newBody);
			}else { // define var exp
				assert_(namelist instanceof Symbol,"define var ",node); 
			}
			
			Node exp = filter(children.get(2));
			
			children.set(2, exp);
			
			return node;
			
		}else if(node.hasFun("lambda")){
			
			assert_(children.size() == 3,"lambda need 3 params",node);
			//param2 need list of symbol
			
		 
			for(Node parma : children.get(1).getContent()){
					if( !(parma instanceof Symbol))
						throw new Error("lamdba  形式参数要为符号");
			}
		 
			
			children.set(2,filter(children.get(2)));
			
			return node;
			
		}else if(node.hasFun("begin")){
			
			 assert_(children.size()>1,"Unspecified return value",node);
			
			 for (int i = 1; i < children.size(); i++) {
					children.set(i, filter(children.get(i)));
			 }
			 return node;
			 
		}else if(node.hasFun("define-macro")){//编译成宏转换器
			Node var = children.get(1);
			Node exp = children.get(2);
			
			exp = filter(exp); // 而且把反引用执行成值了。
			
			Node proc = interpreter.eval(exp);// 由interpreter生成lambda
			
			assert_(proc instanceof Lambda,"define-macro 函数体应该为lambda",node);
			
			macro_table.put(var, (Lambda) proc);
			
			log.warning("宏转换器 "+proc.toString());
			
			return null;// 该macro不进入语法树
			
		}else if(node.hasFun("`")){ // quasiquote
			//assert
			assert_(children.size()==2,"quasiquote size ",node);
		 
			return quasiquote_filter(children.get(1));
			
		}else if(children.get(0) instanceof Symbol 
				&& macro_table.containsKey(children.get(0))){		//执行宏转换器， 所有参数都变成了一个list
			
			Lambda macro = (Lambda) macro_table.get(children.get(0));
			 
		/*	Node r = macro.invoke( children.subList(1, children.size())
								.toArray(new Node[0]), macro.getEnv());*/
			
	//13时15分43秒 修改了宏转换器的Lambda函数调用，与解析器Lambda的不同。append方法修正		
			 
			Node args = new Node();
			
			args.setChild( children.subList(1, children.size()));//所有参数都变成了一个list
			
 
		  	Node r = macro.invoke(new Node[]{args}, macro.getEnv()); //该环境无视，应该是宏用最顶级环境
		 
 //------------------
		/* 	Node callnode = new Node(2);
			callnode.addChild(children.get(0));
			callnode.addChild(args); //error : 需要反引用 (and ((= x 1) (= x 1))
			Env env  = new Env(new Node[]{children.get(0)},
								new Node[]{macro},macro.getEnv());
			
			Node r = interpreter.eval(callnode,env); */
			
			
			
			return filter(r);
		}
		
		//自定义，环境方法
		
		else{ // ((x x) x)  
			
			 for (int i = 0; i < children.size(); i++) {
				children.set(i, filter(children.get(i)));
			 }
			 return node;
		}
	
		
	}
	
	//quotes = {"'":_quote, "`":_quasiquote, ",":_unquote, ",@":_unquotesplicing}
	
	// Expand `x => 'x; `,x => x; `(,@x y) => (append x y) 
	/**
	 * 传入的exp是取出引用符号的后的部分。
	 */
	private Node quasiquote_filter(Node exp){
		
		if(exp.isNoChild()){
			Node r = new Node(2);
			r.addChild(new Symbol("'"));
			r.addChild(exp);
			return r;
		}
		List<Node> child = exp.getContent();
		
		assert_(!exp.hasFun(",@")," " ,exp);
			
		// (,x) => x
		if(exp.hasFun(",")){ //_unquote
			assert_(child.size() == 2, "",exp);
			return child.get(1);
		}
		//((,@ x) y z..) => (append x y z..)   处理,@
		if(child.get(0).hasChild() && child.get(0).hasFun(",@")){ //2级
			assert_(child.get(0).getContent().size()== 2, "",child.get(0));
			Node r = new Node(child.size()+1);
			r.addChild(new Symbol("append"));
			r.addChild(child.get(0).getContent().get(1));
	 	
			Node left = new Node();//1级留下
			left.setChild(child.subList(1, child.size())); 
			
			r.addChild(quasiquote_filter (left));
			
			return r; 
		}
		else{ // (cons fiter(x1) filter(x2..))  处理，
			// 拼接 exp
			Node r = new Node(child.size()+1);
			r.addChild(new Symbol("cons"));
			r.addChild( quasiquote_filter(child.get(0)));
			
			Node left = new Node();
			left.setChild(child.subList(1, child.size())); 
			 
			r.addChild(quasiquote_filter (left));
			
			return r;
		}
		 
	}
	
	private void assert_(Boolean condition,String message,Node n ){
		if(condition == false)
			throw new RuntimeException(message + "\n" + n);
	}
	


 
	
	private HashMap<Node,Node>  macro_table = new HashMap<Node,Node> ();
	 
	 
}
