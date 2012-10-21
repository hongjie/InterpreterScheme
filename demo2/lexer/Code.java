package demo2.lexer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//wait test
public class Code {
	
	private BufferedReader in ;
	
	private String buffer ;
	
	private int pos = -1 ;
 	private boolean isStream = false;
	private int line;
	
	public Code(InputStreamReader ins){
		if(ins == null )
			throw new Error("输入为空"); 
 		
		isStream = true;
		
		try {
			this.in = new BufferedReader(ins);
			buffer = in.readLine() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Error("输入为空");
		}
		
		
	}
	public Code(String src){
		this.buffer = src ;
	}
	
	// todo.查看。\r\n
	public char look(int next){
		return 1;
	}

	public char nextChar(){
		
		try{
			pos ++;
			
			if(pos >= buffer.length() && isStream){ 
				
				buffer = in.readLine() ;
				
				line++ ; 
				
				pos = 0;
			
			}else if(pos >= buffer.length() || buffer.length() == 0){
				return 	(char)-1;
			}
			
			char cur = buffer.charAt(pos);
			
			//?? warn
			if(isnewline(cur))
				line ++;
			
			return cur;
			
		} catch (IOException e) { 
 			e.printStackTrace();
			throw new Error("读取异常");
		}
	}
	
	private boolean isnewline(char cur) {
		// TODO Auto-generated method stub
		return cur == '\n';
	}
	
	public int getPos() {
		return pos;
	}
 
	public int getLine() {
		return line;
	}
 
	
}
