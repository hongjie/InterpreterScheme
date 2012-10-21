package demo2.lexer;


public abstract class Lexer {
	
	protected Code code;
	
	protected char c ;
	
	public static final char EOF = (char)-1;
	
	public Lexer(Code code){
		this.code = code;
		comsume();
	}
	protected void comsume(){
		c = code.nextChar();
	}
	
	protected void match(char expected){
		char cur = code.nextChar();
		if(cur != expected){
			throw new Error(String.format("在第%i行，第%i个字符期望%s,但是输入是%s\n",code.getLine(),code.getPos(),expected,cur));
		}
	}
	
	public abstract Token getNextToken();
	

	public  boolean isNum(char c) {
		// TODO Auto-generated method stub 
		return c >= '0' && c <= '9';
	}

	public boolean isWhitespace(char c) {
		// TODO Auto-generated method stub
		for(char i : wspace){
			if(i == c )
				return true;
		}
		return false;
	}
	private char[] wspace = {' ','\r','\n','\t'};
	
	public int[] getCurrentPos(){
		return new int[]{code.getLine(),code.getPos()};
	}
}
