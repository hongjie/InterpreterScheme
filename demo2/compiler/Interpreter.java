package demo2.compiler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import demo2.env.Env;
import demo2.exp.BooleanNode;
import demo2.exp.Function;
import demo2.exp.Lambda;
import demo2.exp.Node;
import demo2.exp.NoneNode;
import demo2.exp.Num;
import demo2.exp.StringNode;
import demo2.exp.Symbol;
import demo2.lexer.Token;

 public class Interpreter {
	
 
	public static Logger log = Logger.getLogger("Interpreter2");
	{
		log.setLevel(Level.OFF);
	}
	public int recusetimes = 0;
	
	public Node eval(Node node){
		return eval(node,new Env(this));
	}
	
	public Node eval(Node node , Env env){
		
		
		log.info("eval " + node +" : "+env);
		
		if(node == null) return new NoneNode();
		
		recusetimes++;
		
		
		while(true){
			
			log.info("while " + node +" : "+env);
			
			List<Node> children = node.getContent();//maybe null o.
			
			if(node instanceof Symbol)//w t  原子节点. warn: var_name , fun_name should add to env;
				return   env.findNode(node); 
			else if(node instanceof Num)// ok
				return   node ;
			else if(node instanceof BooleanNode)//ok
				return   node ;
			else if(node instanceof StringNode)
				return   node;
			else if(node.isNoChild())//下面都要使用子节点的。注意。
				throw new Error("node.isnochild");
			
			else if(node.hasFun("quote") || node.hasFun("'")){ // 字面值 
				
				return children.get(1);//这里不能继续eval子节点了。返回节点。
				
				
			}else if(node.hasFun("if")){//pass test 
				 
			 
				Node predicate  = children.get(1);
				Node consequent = children.get(2);
				Node aleter     = children.get(3);
				 
				Node r = eval(predicate,env);
				
				if(r instanceof BooleanNode && ((BooleanNode)r).value == Boolean.FALSE) 	// r is  always true, except #f
					 node = aleter; 
				else 
					node = consequent; 
				
			}else if(node.hasFun("set!")){//赋值
				
				Node var  = children.get(1);//假设是一个变量
				Node exp  = children.get(2); 
				exp = eval(exp,env);  
				env.findEnv(var).put(var,exp) ;//覆盖
				return NoneNode.instance;
				
			}else if(node.hasFun("define")){
				
				if(children.size() != 3) throw new Error("define_fun error");
				
				Node var  = children.get(1);//假设是一个变量，作为函数名 Symbol
				Node exp  = children.get(2);
				  
				exp = eval(exp,env);//得到lambda Function  或者 其他
					
				env.put(var, exp);//更新环境变量
					
				log.info("define " + node +" : "+env);
				 
				
				return NoneNode.instance;
	 
			}else if(node.hasFun("lambda")){ // wait test
				
				if(children.size() != 3) throw new Error("lambda_fun error");
				
				Token fun_token       = children.get(0).getToken();
				final Node[] param 	  = children.get(1).getContent().toArray(new Node[0]);//warm
				final Node   body     = children.get(2);
				final Env fun_out_env = env;
				
				log.info("返回 create Lambda "  );
				return createLambda(fun_token, param, body, fun_out_env);
				
			}else if(node.hasFun("begin")){ 
				
				for(int i=1;i < children.size()-1;i++){
					 eval(children.get(i),env);
				}
				node = children.get(children.size()-1);
				
			}else{// other function call.
				
				Node fun_name = children.get(0);
				
				Node[] args = new Node[children.size()-1];
				  
				Function fun;
				
				if(fun_name instanceof Function) //对付lambda定义后直接调用。
					fun = (Function)fun_name;
				else{
					Node result = eval(fun_name,env);//函数名字查找
					
					if(result == null || !(result instanceof Function))
						throw new Error(node+"没有定义");  //可以修改这里，支持list? 直接返回
					else 
						fun = (Function) result;
				}
					
			
 
				if(children.size() > 1) //eval 下各参数
					for(int i=1;i<children.size();i++){
						args[i-1] = eval(children.get(i),env);
					}
				
				if(fun instanceof Lambda){
					Lambda lambda = (Lambda)fun;
					node = lambda.getBodyNode();
					env  = new Env(lambda.getParam(),args,lambda.getEnv());
				}else
					return fun.invoke(args,env);
				 
			} 
			
		}//while end
		
		 
	}


	/**
	 * 构造出函数子
	 * @param env
	 * @param p  (funname param body)
	 * @return
	 */
 

	private Node createLambda(Token fun_token, final Node[] param,
			final Node body, final Env fun_out_env) {
		
			//Lambda 在定义body的时候，把执行时候环境（形式，实际参数），定义时环境的包裹工作，做了。
		
			Lambda lambda =  new Lambda(fun_token){ 
				@Override
				public Node body(Node[] args) {
					// TODO Auto-generated method stub 
					
					
					
					Env env = new Env(param,args,fun_out_env);//函数定义的新环境
					
					this.setEnv(env);
					
					log.info(" lambda 执行 "  +" 内部环境: " + env);
					
					Node result = eval(body,env);//币包？
					
					log.info("lambda 执行结果 " + result);
	 				 
					return result;
				} 
			};
		
			lambda.setBodyNode(body);
			lambda.setParam(param);
			lambda.setEnv(fun_out_env);
			
			log.info("创建的 lambda" + lambda +" 内部环境: "+lambda.getEnv());
			
			return lambda;
	}



		
	
	
	
 
		 
}
