package demo2.lexer;

public class Token {
	//文本值
	private String text;
	//类型
	private Object type;
	
	private String pos;
	
	public Token( Object type,String text) {
		this.text = text;
		this.type = type;
	}
	
	public String toString(){
		return String.format("<%s  '%s'>",type,text);
	}

	public boolean isType(Object o){
		return o == type;
	}
	public Object getType() {
		return type;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getText() {
		return text;
	}
	

}
