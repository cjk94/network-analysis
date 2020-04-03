import java.io.*;
import java.util.*;
public class NetworkAnalysis {
	private static final int MAX_INT = 2147483647;//java max int
	static LinkedList<Edge> [] AdjacencyList;
	static int size;
	public static void main(String [] args)throws IOException
	{
		String inFile = args[0];
		Scanner scanny = new Scanner(new FileInputStream(inFile));
		String s = scanny.nextLine();
		size = Integer.parseInt(s);
		AdjacencyList = new LinkedList [size];
		for(int i = 0; i < size; i++)//populate adjacency list
		{
			System.out.println("adding: " + i);
			AdjacencyList[i] = new LinkedList<Edge>();
		}
		while(scanny.hasNext())
		{
			s = scanny.nextLine();
			int index = s.indexOf(" ");
			String sub = s.substring(0,index);
			int i = Integer.parseInt(sub);
			LinkedList<Edge> vertex = AdjacencyList[i];//get linked list for vertex 
			s = s.substring(index+1);
			index = s.indexOf(" ");
			sub = s.substring(0,index);
			int vNum = Integer.parseInt(sub); //vNum = vertex edge goes too
			s = s.substring(index+1);
			index = s.indexOf(" ");
			String type = s.substring(0,index);
			s = s.substring(index+1);
			index = s.indexOf(" ");
			sub = s.substring(0,index);
			int bandwidth = Integer.parseInt(sub);
			s = s.substring(index+1);
			int length = Integer.parseInt(s);
			System.out.println("adding " + vNum + " " + type + " " + bandwidth + " " + length + " to " + i);
			Edge e = new Edge(vNum,type,bandwidth,length);
			vertex.add(e);
			LinkedList<Edge> reverseV = AdjacencyList[vNum];
			Edge reverse = new Edge(i,type,bandwidth,length);
			reverseV.add(reverse);
			System.out.println("adding " + i + " " + type + " " + bandwidth + " " + length + " to " + vNum);
		}
		scanny.close();
		System.out.println("Interface:");
		while(true)//Interface
		{
			System.out.println("Please type: \n1 To find the lowest latency path between two vertices");
			Scanner input = new Scanner(System.in);
			int choice = input.nextInt();
			switch(choice) 
			{
			case 1:
				System.out.println("Please enter the starting point (integer)");
				int vertexOne = input.nextInt();
				System.out.println("Please enter the end point (integer)");
				int vertexTwo = input.nextInt();
				LinkedList<Edge> wsp = weightedShortestPath(vertexOne,vertexTwo);
				break;
			}
		}
	}
	public static LinkedList<Edge> weightedShortestPath(int startPoint, int endPoint)
	{
		LinkedList<Edge> result = new LinkedList<Edge>();//will contain edges to follow to end point
		LinkedList<Edge> curr = AdjacencyList[startPoint];//starting vertex's edges
		int [] Distance = new int [size];//Distance[i] contains lowest distance from startPoint to Vertex i
		int [] Via = new int [size];//Via[i] contains vertex index to use to get to vertex i (Via[2] = 1 means use 1 to get from starting vertex to vertex 2)
		boolean [] Visited = new boolean [size];
		for(int i = 0; i < size; i++)//initialize distance array
		{
			if(i == startPoint)
				Distance[i] = 0;//distance from startPoint to startPoint is always 0;
			else
				Distance[i] = MAX_INT;
		}
		int current = startPoint;
		while(Visited[endPoint] != true) //haven't visited end point yet
		{
			Visited[current] = true;
			for(int i = 0; i < curr.size(); i++)//i = index of vertex 1
			{
				Edge e = curr.get(i);
				Distance[e.getVNum()] = e.getLength();
				Via[e.getVNum()] = i;
			}
			//set current and loop
		}
		return result;
	}
}
