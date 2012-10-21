package demo2.env;
import java.util.HashMap;
import java.util.List;

import demo2.compiler.CallccException;
import demo2.compiler.Interpreter;
import demo2.exp.BooleanNode;
import demo2.exp.Function;
import demo2.exp.Lambda;
import demo2.exp.ListNode;
import demo2.exp.Node;
import demo2.exp.NoneNode;
import demo2.exp.Num;
import demo2.exp.Symbol;

//树结构 环境 wait test. 改写node，hash
public class Env extends HashMap<Node,Node>{
	 
	private Env parent;
	private Interpreter in;
	private static Env global_env ;
	
	//根节点初始化。
	public Env(Interpreter interpreter){
		super(0);//优化
		this.in = interpreter;
		iniEnvFun();
		global_env = this;
	}
	
	public Env(Node[] param,Node[] args,Env parent){
		
		super(param.length);//优化
		
		if(parent == null) parent = global_env;
		
		this.parent = parent;
		
		if(param == null && args == null) // 修改支持不定长实际参数，当长度不相等的时候，param是symbol时候，arg修改成list。
			return ; //?
 
	//未测试	
		 if(param.length!= args.length 
				 && param.length == 1 
				 && param[0] instanceof Symbol){
			 
			 ListNode ls = new ListNode();
			 ls.setChild(args);
			 this.put(param[0],ls);
		 }
		 
		 if(param.length == args.length)
			 for(int i = 0 ; i < param.length ; i++){//形式参数，实际参数，（符号，代码）加入环境
				 this.put(param[i], args[i]);
			 }
	}
	
	public Node findNode(Node name){
	//错误	this.containsKey(name)；
		Node item = this.get(name);
		
		if(item != null)
			return item;
		else if(parent != null)
			return parent.findNode(name);
		else 
			throw new RuntimeException(name+"未找到定义");
 
	}
	
	public Env findEnv(Node name){
		if(this.get(name)!=null)
			return this;
		else if(parent!=null)
			return parent.findEnv(name);
		throw new Error(name+"未找到定义");
	}
 
 
	 private  void iniEnvFun(){
		//二元
		 	String[] ops = "+ - * / % < > = <= >= eq? ".split(" ");
		 	
			for(String item : ops){
				Symbol symbol = new Symbol(item+"");
				this.put(symbol,new Function(symbol){
					@Override
					public Node body(Node[] args) { 
						//if(args.length != 2) throw new Error("需要两个参数");
						return additionFunc(this.name,args,Env.this);
					}
				});
			} 
		 
			String[] oneTruple = ("length list? Symbol? None? " +
					"boolean? pair? null? display read write eval ").split(" ");
			
			for(String item:oneTruple){
				Symbol symbol = new Symbol(item);
				this.put(symbol, new Function(symbol){
					@Override
					public Node body(Node[] args) { 
						if(args.length != 1) throw new Error("需要一个参数");
						return oneTrupleFunc(this.name,args[0],Env.this);
					}
				});
			}
			
			// call/cc append cons
		
			callcc();
			cons(); 
			append();
			car();
			cdr();
			map();
			list();
			
	 }
	 
	 
	 private void callcc(){
		 	Symbol symbol = new Symbol("call/cc");
			
			this.put(symbol, new Function(symbol){ 
			 
				@Override
				public Node body(Node[] args) { 
					
					if(args.length != 1) throw new Error("需要一个参数");
					
					Lambda f = (Lambda)args[0];
					
					String newFunName = f.getParam()[0].getToken().getText();
					
					CallccException e = new CallccException("UserException "+newFunName);
					
					try{
						
						
						updateThrowFun(newFunName,e,this.getInvokeTime_env());
						 
						return in.eval(f.getBodyNode(),this.getInvokeTime_env());
						
					}catch(CallccException innerE){
							if(innerE == e)
								return e.getReturnNode();
							throw innerE;
					}
					
					 
				}
			}); 
	 }
	 //在特定的执行时环境，创建call/cc的抛函数对象。这里直接放入执行环境的上下文中。不必在外部再更新环境。
	 private void updateThrowFun(String fname, final CallccException e ,Env env){
		 
		 	Symbol  symbol = new Symbol(fname);
			
			env.put(symbol, new Function(symbol){ 
				@Override
				public Node body(Node[] args) { 
					
					if(args.length != 1) throw new Error("需要一个参数");
					
					Node return_node = args[0];
					
					e.setReturnNode(return_node);  //外部已经eval
					
					throw e; 
				}
			}); 
			
	 }
	 
	 private void cons(){
		 
		 Symbol symbol = new Symbol("cons");
			
			this.put(symbol, new Function(symbol){ 
			 
				@Override
				public Node body(Node[] args) { 
					if(args.length != 2 )
						throw new Error("cons");
					
					Node r = new Node(1+args[1].getContent().size());
					
					r.addChild(args[0]);
					
					for(Node item : args[1].getContent()){
						r.addChild(item);
					}
					
					return r;
				}
			}); 
		 
	 } 
	 
	 //?
	 private void append(){
		 
		Symbol symbol = new Symbol("append");
			
		this.put(symbol, new Function(symbol){ 
			 
				@Override
				public Node body(Node[] args) { 
					 
					
				 /* 	Node r = new Node(1+args[1].getContent().size());
					
					r.addChild(args[0]);
					
					for(Node item : args[1].getContent()){
						r.addChild(item);
					}
					
					return r; */
					
					List<Node> a = args[0].getContent();
					List<Node> b = args[1].getContent();
					
					Node r = new Node(a.size()+b.size());
					
					for(Node item : a){
						r.addChild(item);
					}
					for(Node item : b){
						r.addChild(item);
					}
					
					return r; 
					
				}
		}); 
	 }
	 private void car(){
		 
			Symbol symbol = new Symbol("car");
				
			this.put(symbol, new Function(symbol){ 
				 
					@Override
					public Node body(Node[] args) { 
						
						if(args.length == 0 || args[0].isNoChild()) 
							throw new Error("car需要一个参数,no none list");
						
						return args[0].getContent().get(0);
						 
					}
			}); 
		 }
	 private void cdr(){
		 
			Symbol symbol = new Symbol("cdr");
				
			this.put(symbol, new Function(symbol){ 
				 
					@Override
					public Node body(Node[] args) { 
						
						if(args.length == 0 || args[0].isNoChild()) 
							throw new Error("car需要一个参数,no none list");
						
						List<Node> list = args[0].getContent();
						list = list.subList(1,list.size());
						Node r = new Node();
						r.setChild(list);
						return r;
						 
					}
			}); 
		 }
	 
	 private void map(){
		 
			Symbol symbol = new Symbol("map");
			
			this.put(symbol, new Function(symbol){ 
				 
					@Override
					public Node body(Node[] args) { 
						
						if(args.length != 2) 
							throw new Error("map需要2个参数  ");
						
						Function f = (Function) args[0];
						
						 
					 
						Node list = new Node(args[1].getContent().size());
						
						for(Node data: args[1].getContent()){
							   //data 未eval
							
							list.addChild(f.invoke(new Node[]{data} ,this.getInvokeTime_env()));
						}
						
						return list;
						 
					}
			}); 
	 }
	 
	 
	 
	 private void list(){
		 
			Symbol symbol = new Symbol("list");
			
			this.put(symbol, new Function(symbol){ 
				 
					@Override
					public Node body(Node[] args) {  
					 
						Node list = new Node(); 
						
						list.setChild(args); 
						
						return list;
						 
					}
			}); 
	 }
	 
	 

	 
	 private Node additionFunc(String o,Node[] args, Env env){
			 
			if("+".equalsIgnoreCase(o)){
				int sum = ((Num)in.eval(args[0],env)).value ;
				for(int i = 1; i < args.length ;i++){
					sum += ((Num)in.eval(args[i],env)).value;
				}
				return new Num( sum );
			}
			if("-".equalsIgnoreCase(o)){
				int sum = ((Num)in.eval(args[0],env)).value ;
				for(int i = 1; i < args.length ;i++){
					sum -= ((Num)in.eval(args[i],env)).value;
				}
				return new Num( sum );
			}
			if("*".equalsIgnoreCase(o)){
				int sum = ((Num)in.eval(args[0],env)).value ;
				for(int i = 1; i < args.length ;i++){
					sum *= ((Num)in.eval(args[i],env)).value;
				}
				return new Num( sum );
			}
			if("/".equalsIgnoreCase(o)){
				int sum = ((Num)in.eval(args[0],env)).value ;
				for(int i = 1; i < args.length ;i++){
					sum /= ((Num)in.eval(args[i],env)).value;
				}
				return new Num( sum );
			}
				
			 
			if("%".equalsIgnoreCase(o))
				return new Num(((Num)in.eval(args[0],env)).value  
						%((Num)in.eval(args[1],env)).value);
			if("<".equalsIgnoreCase(o))
				return new BooleanNode(((Num)in.eval(args[0],env)).value  
						< ((Num)in.eval(args[1],env)).value);
			if(">".equalsIgnoreCase(o))
				return new BooleanNode(((Num)in.eval(args[0],env)).value  
						> ((Num)in.eval(args[1],env)).value);
			if("=".equalsIgnoreCase(o))
				return new BooleanNode(((Num)in.eval(args[0],env)).value  
						== ((Num)in.eval(args[1],env)).value);
			if("<=".equalsIgnoreCase(o))
				return new BooleanNode(((Num)in.eval(args[0],env)).value  
						<= ((Num)in.eval(args[1],env)).value);
			if(">=".equalsIgnoreCase(o))
				return new BooleanNode(((Num)in.eval(args[0],env)).value  
						>= ((Num)in.eval(args[1],env)).value);
			
			if("eq?".equalsIgnoreCase(o)){
				if((args[0].isLeaf() && args[1].isLeaf()
						&& args[0].getToken().getText().equalsIgnoreCase(args[1].getToken().getText()))
					|| ( args[0].equals(args[1])))
					return new BooleanNode(Boolean.TRUE);
				else
					return new BooleanNode(Boolean.FALSE);
			}
			
			return null;
		}

	 //未测试
	 private Node oneTrupleFunc(String o,Node arg, Env env){
		 if("length".equalsIgnoreCase(o)){
			 if(arg.hasChild())
				 return new Num(arg.getContent().size());
			 return new Num(0);
		 }
		 if("list?".equalsIgnoreCase(o)){
			 return new BooleanNode(arg.hasChild());
		 }
		 if("Symbol?".equalsIgnoreCase(o)){
			 return new BooleanNode((arg instanceof Symbol));
		 }
		 if("None?".equalsIgnoreCase(o)){
			 return new BooleanNode((arg instanceof NoneNode));
		 }
		 if("boolean?".equalsIgnoreCase(o)){
			 return new BooleanNode((arg instanceof BooleanNode));
		 }
		 if("pair?".equalsIgnoreCase(o)){
			 
			return new BooleanNode(arg.hasChild());
			 
		 }
		 if("null?".equalsIgnoreCase(o)){
			return new BooleanNode(arg instanceof NoneNode);
		 }
		 if("display".equalsIgnoreCase(o)){
			 System.out.println("\n"+arg);
		 }
		 if("read".equalsIgnoreCase(o)){
			 
		 }
		 if("write".equalsIgnoreCase(o)){
			 
		 }
		 if("eval".equalsIgnoreCase(o)){
			return  this.in.eval(arg,env);
		 }
		 
		 return null;
		 
	 }
	 public String toString(){
		 StringBuilder sb = new StringBuilder();
		 
		 sb.append("[ ") 
		 	.append(super.toString());
		 if(parent!=null)
		 	sb.append(parent.toString());
		 sb.append(" ]");
		 
		 return sb.toString();
	 } 
	
}
