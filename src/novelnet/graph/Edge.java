package novelnet.graph;

import novelnet.util.ImpUtils;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Edge {
	
	protected String id;
	protected Node nodeLeft;
	protected Node nodeRight;
	protected double ponderation;
	protected String type;
	
	public Edge(String id, Node nodeLeft, Node nodeRight, String type)
	{
		this.id = id;
		this.nodeLeft = nodeLeft;
		this.nodeRight = nodeRight;
		this.type = type;
		ponderation = 1;
	}
	
	public Edge(String id, Node nodeLeft, Node nodeRight, double ponderation, String type)
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
	
	public double getPonderation()
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
	
	public void setPonderation(double ponderation)
	{
		this.ponderation = ImpUtils.round(ponderation, 3);
	}
	
	/**
	 * add the specified ponderation to the edge
	*/
	public void addPonderation(double ponderation)
	{
		if (Double.isInfinite(ponderation)) ponderation =1;
		if (Double.isInfinite(this.ponderation)) this.ponderation = 0;
		this.ponderation+=ponderation;
		this.ponderation = ImpUtils.round(this.ponderation, 3);
	}

	/**
	 * add the 1.0 to the edge ponderation
	*/
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
		return "{ id: "+id+", NodeLeft: "+nodeLeft.toString()+", nodeRight: "+nodeRight.toString()+", Ponderation: "+ponderation+" }";
	}

}
