public class Edge implements Comparable<Edge> {
	private final int COPPER_SPEED = 230000000;
	private final int OPTICAL_SPEED = 200000000;
	int from;
	int vNum;
	String type;
	int bandwidth;
	int length;
	boolean visited;
	double latency;
	public Edge(int f,int v, String t, int bw, int l)
	{
		from = f;
		vNum = v;
		type = t;
		bandwidth = bw;
		length = l;
		visited = false;
		if(t.equals("optical"))
			latency = (double) l / OPTICAL_SPEED;
		else
			latency = (double) l/COPPER_SPEED;
		System.out.println(latency);
	}
	public int getVNum()
	{
		return vNum;
	}
	public int getBandwidth()
	{
		return bandwidth;
	}
	public int getLength()
	{
		return length;
	}
	public boolean getVisited()
	{
		return visited;
	}
	public double getLatency()
	{
		return latency;
	}
	public int getFrom()
	{
		return from;
	}
	public String getType()
	{
		return type;
	}

	public void setVisited(boolean b)
	{
		visited = b;
	}
	public int compareTo(Edge e) {//compares edges by reverse length order
		if(latency == e.getLatency())
			return 0;
		else if(latency > e.getLatency())
			return 1;
		else 
			return -1;
	}
}
