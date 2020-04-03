
public class Edge {
	int vNum;
	String type;
	int bandwidth;
	int length;
	boolean visited;
	public Edge(int v, String t, int bw, int l)
	{
		vNum = v;
		type = t;
		bandwidth = bw;
		length = l;
		visited = false;
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
}
