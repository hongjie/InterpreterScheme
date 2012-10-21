package demo2.exp;
import java.util.ArrayList;
import java.util.List;

import demo2.lexer.Token;


public class Node {
	
	protected Token token ;
	
	protected List<Node> list  ;
	
	public Node(int count){
		list = new ArrayList<Node>(count); 
	}
	 
	public Node(){
		 
	}
	/**
	 * 叶子节点
	 * @param t
	 */
 	public Node(Token t){ 
		this.token = t;
	} 
	/**
	 * 只加入有意义的token
	 */
	public void setToken(Token t){
		this.token = t;
	}
	
	public void setChild(List <Node> nodes){
		list = nodes;
	}
	// for wei
	public void setChild(Node[] children){
		if(children == null) return;
		
		if(list==null) list = new ArrayList(children.length);
		
		for (int i = 0; i < children.length; i++) {
			list.add(children[i]);
		}
	}
	public void addChild(Node node){
		if(list==null) list = new ArrayList(5);
		list.add(node);
	}
	
	
	public Token getToken() {
		return token;
	}

	public List<Node> getContent() {
		if(list == null) {
			List a = new ArrayList<Node>(1);
			a.add(this);
			return a;
		}
		return list;
	}
	
	public boolean isLeaf(){
		return token != null;
	}
	public boolean isNoChild(){
		return list==null || list.size()==0;
	}
	public boolean hasChild(){
		return list!=null && list.size() !=0;
	}
	public boolean hasFun (String n){
		if(list==null || list.get(0).token == null)
			return false;
		return n.equals(list.get(0).token.getText() );
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj!=null && obj instanceof Node ){
			Node node = (Node)obj;
			if(node.isNoChild() && this.isNoChild() 
					&& node.getToken().getText().equalsIgnoreCase(this.getToken().getText()))
				return true;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		if(this.isNoChild() ){
			int h = this.getToken().getText().toLowerCase().hashCode();
			return h;
		}  
		return super.hashCode();
	}
	
	public String toString(){
		
		
		if(list!=null){
			StringBuilder sb = new StringBuilder();
			sb.append("( ");
			for (Node n : list) {
				if(n!=null)
					sb.append(n.toString()).append(" ");
			} 
			sb.append(")");
			return sb.toString();
		}else
			return token.getText();
	}
 
}

