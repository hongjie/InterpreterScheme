package demo2.test;

import demo2.compiler.Interpreter;
import demo2.compiler.filter.ParserAgain;
import demo2.exp.Node;
import demo2.lexer.Code;
import demo2.lexer.Lexer;
import demo2.lexer.ScmLexer;
import demo2.lexer.ScmTokenType;
import demo2.lexer.Token;
import demo2.parser.Parser;

 

public class Test {
	public static void testLexer(String[] args) {
		Code code = new Code("(define (fn x) (* 11 x))");
		Lexer lexer = new ScmLexer(code);
		Token t;
		do{
			t = lexer.getNextToken();
			System.out.println(t);
		}while( t.getType() != ScmTokenType.EOF);
	}
	
	public static void main2(String[] args) {
		Code code = new Code("(define fn (lambda (x) (*  x (+ x 1))))");
		Lexer lexer = new ScmLexer(code);
		Node node = new Parser(lexer).parse();
		node.getContent();
	}
	
	public static void main3(String[] args) {
		Code code = new Code("(if #t 1 (* 2 2))");
		Lexer lexer = new ScmLexer(code);
		Node node = new Parser(lexer).parse();
		Node o = new Interpreter().eval(node );
		System.out.println(o);
	}
	
	public static void main4(String[] args) {
		Code code = new Code("(lambda (x) x)");
		Lexer lexer = new ScmLexer(code);
		Node node = new Parser(lexer).parse();
		Node o = new Interpreter().eval(node);
		System.out.println(o);
	}

	
	public static void main6(String[] args) {
/*		Env env = new Env(null);
		env.put(new Node(new Token("type","text")), new Node(new Token("type","text")));
		Node n = env.get(new Node(new Token("type","text")));
		n.getToken();*/
	}
	public static void main7 (String[] args) {
		Code code = new Code("(begin (define x (lambda (x) (* x (* 2 x)))) (x 3))");
		Lexer lexer = new ScmLexer(code);
		Node node = new Parser(lexer).parse();
		Node o = new Interpreter().eval(node);
		System.out.println(o);
	}
	
	public static void test(String src){
		Code code = new Code(src);
		Lexer lexer = new ScmLexer(code);
		Node node = new Parser(lexer).parse();
		Interpreter i = new Interpreter();
		Node o = i.eval(node);
		 System.out.println("递归次数"+i.recusetimes);
		System.out.println(src + " => " +o);
	}
	
	public static void mainz(String[] args) {
		 
	//	test("(begin (define fn (lambda (x) (if (= x 1) 1 (fn (- x 1))))) (fn 3))");
	//	 test("( begin  ( define x 1 ) ( define y 1 ) ( define z 1 )  ( + x y z ) )");
	//	 test("(map (lambda (x) (= x 1)) (list 1 2 3))");
		
	//	 test("(call/cc (lambda (throw) (+ 10 (call/cc (lambda (esecap) (throw 3))))))");		
	//	 test("(call/cc (lambda (throw) (+ 10 (call/cc (lambda (esecap) (esecap 3))))))");
	 	 test("(call/cc (lambda (throw) (+ 5 (begin (define f (lambda (x) (if (> x 3) (throw x) (* x (* 2 x))))) (f 4)))))");
  	 /*	test("(begin (define x (lambda (x) (* x (* 2 x)))) (x 3))");
	  test("(begin (define y (lambda x (if #t (* x 2) (* x 1)))) (y 3))");
	  	test("(begin (define y (lambda (x) (if #f (* x 2) (* x 1)))) (y 3))"); 
				//test Lexer Report for EOF error: test("(begin (define a #t) (set! a #f))  a");
	  	test("(begin (define y (lambda (x) (lambda (z) (* x z)))) ((y 3) 2))"); //闭包
	 	test("(begin (define fn (lambda (x) (if (= x 1) 1 (fn (- x 1))))) (fn 12))");
			//test("(begin (define (fn x y) (+ x y)) (fn 1 2))");
	  	test("((if #t * -) 1 2)"); 
	  	test("(eq? \"s \"s)");
	 	test("(+ 1 2 3)");
	 	test("(if #t (quote (1 2)) 1)");*/
		
	}
	
	public static Node mod(String src){
		Code code = new Code(src);
		Lexer lexer = new ScmLexer(code);
		Node node = new Parser(lexer).parse();
		ParserAgain p = new  ParserAgain();
		Node o = p.filter(node);
		 
		System.out.println(src + " => " +o);
		return o;
	}
	public static Node evalNode(Node node){
		Interpreter i = new Interpreter();
		Node r = i.eval(node);
		System.out.println(r);
		return r;
	}
	public static void main (String[] args) {
		
		
		
			test("(begin (define fn (lambda (x) (if (= x 1) 1 (fn (- x 1))))) (fn 3))");
		 	 test("( begin  ( define x 1 ) ( define y 1 ) ( define z 1 )  ( + x y z ) )");
		 	 test("(map (lambda (x) (= x 1)) (list 1 2 3))");
			
		 	 test("(call/cc (lambda (throw) (+ 10 (call/cc (lambda (esecap) (throw 3))))))");		
		 	 test("(call/cc (lambda (throw) (+ 10 (call/cc (lambda (esecap) (esecap 3))))))");
		 	 test("(call/cc (lambda (throw) (+ 5 (begin (define f (lambda (x) (if (> x 3) (throw x) (* x (* 2 x))))) (f 4)))))");
		 	 test("(begin (define x (lambda (x) (* x (* 2 x)))) (x 3))");
		 	 test("(begin (define y (lambda x (if #t (* x 2) (* x 1)))) (y 3))");
		  	test("(begin (define y (lambda (x) (if #f (* x 2) (* x 1)))) (y 3))"); 
					//test Lexer Report for EOF error: test("(begin (define a #t) (set! a #f))  a");
		  	test("(begin (define y (lambda (x) (lambda (z) (* x z)))) ((y 3) 2))"); //闭包
		 	test("(begin (define fn (lambda (x) (if (= x 1) 1 (fn (- x 1))))) (fn 12))");
				 
		  	test("((if #t * -) 1 2)"); 
		  	test("(eq? \"s \"s)");
		 	test("(+ 1 2 3)");
		 	test("(if #t (quote (1 2)) 1)"); 
		 	 
		 	 
		 	 
		 	 
		 evalNode(mod("(begin (define x '(y 1 )) `(define ,@x))"));

		evalNode(mod("(begin (define x '(y 1 )) ``(define ,@x))"));
		
		evalNode(evalNode(mod("(begin (define x ''(1 2 )) ` (list? ,x))"))); 
		
	  	
		
		 
		
	 	 mod("(begin " +
	 		//error	"(define-macro def (lambda args (if (= 1 (length args)) `(define ,@(car args))  `((define ,@(car args)) (def  ,@(cdr args))))))" +
	 			 
	 			 "(define-macro def (lambda args (map (lambda exp `(define ,@exp)) args)))"+
	 		
	 			 
	 		 //   "(define-macro let (lambda args `(begin (def ,@(car args)) ,@(cdr args) )))"+
	 			
	 			 
	 		//  "(define-macro let (lambda args (cons 'begin  (def ,@(car args)))))"+
	 		  	
	 		//	"(define-macro let (lambda args `(begin (def ,@(car args)) ,@(cdr args) )))"+
	 		    
	 		   "(define-macro let (lambda args (append (append 'begin (map (lambda exp `(define ,@exp)) (car args)))  (cdr args))))"+
	 		 	 
	 		   "(let ((x 1) (y 1) (z 1)) (+ x y z))"+
	 			 	// "(def (x 1) (y 1) (z 1))"+
	 	")"); 
	 	 
	 	 
	 	/* mod("(begin " +
					"(define-macro and (lambda args   " +
		 			" (if (null? args) #t    " +env
		 			"    (if (= (length args) 1) (car args) " +
		 			"  `(if ,(car args) (and ,@(cdr args)) #f)))))"+
	 			
		 			"(and (= 1 1) (> 1 2) (< 1 2))" +
	 			")");  */
		
	 	mod("(begin (define y (lambda (x) (if #f (* x 2) (* x 1)))) (y 3))"); 

	 	mod("(define (fn x y) (+ x y) )");
	 	mod("(define f (lambda x x))");
	 	mod("(if (= 1 2) 1)");
	 	mod("(list? '(1 2 3))");//转义的作用是执行特殊的操作，非函数调用，被转部分不进行eval
	 //	mod("(if (= 1 2))");
	 	mod("(begin xa a)");
	 	mod("((if #t + ) 1 2)");
	 	mod("(list? '(1 2 3))");
	 	mod("('x)");
	 	mod("('(if #t 1 2))"); 
	 	
	}
}
