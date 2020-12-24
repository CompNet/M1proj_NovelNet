/**
 * 
 */
package pipeline;


/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Edge {
	
	protected String id;
	protected Node nodeLeft;
	protected Node nodeRight;
	protected float ponderation;
	
	public Edge(String id, Node nodeLeft, Node nodeRight)
	{
		this.id = id;
		this.nodeLeft = nodeLeft;
		this.nodeRight = nodeRight;
		ponderation = 1;
	}
	
	public Edge(String id, Node nodeLeft, Node nodeRight, float ponderation)
	{
		this.id = id;
		this.nodeLeft = nodeLeft;
		this.nodeRight = nodeRight;
		this.ponderation = ponderation;
	}

	public String getId()
	{
		return id;
	}
	
	public Node getNodeLeft()
	{
		return nodeLeft;
	}
	
	public Node getNodeRight()
	{
		return nodeRight;
	}
	
	public float getPonderation()
	{
		return ponderation;
	}
	
	public void setPonderation(float ponderation)
	{
		this.ponderation = ponderation;
	}
	
	public void addPonderation(float ponderation)
	{
		this.ponderation+=ponderation;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Edge))return false;
	    Edge otherMyClass = (Edge)other;
	    
	    return id.equals(otherMyClass.id);
	    
	}
	
	@Override
	public String toString()
	{
		String ret="";
		ret+="{ id: "+id+", NodeLeft: "+nodeLeft.toString()+", nodeRight: "+nodeRight.toString()+", Ponderation: "+ponderation+"} ";
		return ret;
	}

}
