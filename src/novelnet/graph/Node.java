package novelnet.graph;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Node {
	
	protected String id;
	protected String name;
	
	public Node()
	{
		id ="";
		name = "";
	}
	
	public Node(String name)
	{
		this.id = name; 
		this.name = name;
	}
	
	public Node(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public String getId()
	{
		return this.id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Node))return false;
	    Node otherMyClass = (Node)other;
	    
	    return this.id.equals(otherMyClass.id);
	    
	}
	
	@Override
	public String toString()
	{
		String ret = "";
		ret+="{id: "+this.id+", name: "+this.name+"} ";
		return ret;
		
	}
	

}
