package graph;


/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Edge {
	
	protected String id;
	protected Node nodeLeft;
	protected Node nodeRight;
	protected float ponderation;
	protected String type;
	
	public Edge(String id, Node nodeLeft, Node nodeRight, String type)
	{
		this.id = id;
		this.nodeLeft = nodeLeft;
		this.nodeRight = nodeRight;
		this.type = type;
		ponderation = 1;
	}
	
	public Edge(String id, Node nodeLeft, Node nodeRight, float ponderation, String type)
	{
		this.id = id;
		this.nodeLeft = nodeLeft;
		this.nodeRight = nodeRight;
		this.ponderation = ponderation;
		this.type = type;
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

	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setPonderation(float ponderation)
	{
		this.ponderation = ponderation;
	}
	
	public void addPonderation(float ponderation)
	{
		this.ponderation+=ponderation;
	}

	public void addPonderation()
	{
		this.ponderation+=1;
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
