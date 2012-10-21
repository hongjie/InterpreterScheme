package demo2.lexer;


public class ScmLexer extends Lexer {

	public ScmLexer(Code code) {
		super(code);
	}

	@Override
	public Token getNextToken() {
		while(c != EOF) {
			switch( c ){
				case '(' : comsume();return LBrack;
				case ')' : comsume();return RBrack;
				case '\'': comsume();return quote;
				case '`' : comsume();return quasiquote;
				case ',' : {
					comsume();
					if(c != '@') return unquote;
					comsume();
					return unquotesplicing; 
				}
				
				default  : 
					if(isWhitespace(c)){
						wspace();
						continue;
					}
					if(isNum(c))  //todo 修改支持负数
						return num();
					else	
						return id();
			}
		}
		
		return new Token(ScmTokenType.EOF,"EOF");
	}
	
	private void wspace() {
		// TODO Auto-generated method stub
		do{
			comsume();
			
		}while(isWhitespace(c));
		
	}

	private Token id() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(20);
		
		do{
			sb.append(c);
			comsume();
		}while(! isWhitespace(c) &&  c!='(' && c!=')' && c!=EOF); 
		
		return new Token(ScmTokenType.VARCHAR,sb.toString());
	}
	
	
 	private Token num() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(20);
		
		do{
			sb.append(c);
			comsume();
		}while(isNum(c)); 
		
		return new Token(ScmTokenType.NUM,sb.toString());
	}


	private static Token LBrack = new Token(ScmTokenType.LBRACK,"(");
	private static Token RBrack = new Token(ScmTokenType.RBRACK,")");
	private static Token quote = new Token(ScmTokenType.VARCHAR,"'");
	private static Token quasiquote = new Token(ScmTokenType.VARCHAR,"`");
	private static Token unquote = new Token(ScmTokenType.VARCHAR,",");
	private static Token unquotesplicing = new Token(ScmTokenType.VARCHAR,",@");

}




