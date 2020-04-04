import java.io.*;
import java.util.*;
public class NetworkAnalysis {
	private static final int MAX_INT = 2147483647;//java max int
	static PriorityQueue<Edge> [] AdjacencyList;
	static PriorityQueue<Edge> [] CopperList; //only add copper edges to this list
	static int size;
	public static void main(String [] args)throws IOException
	{
		String inFile = args[0];
		Scanner scanny = new Scanner(new FileInputStream(inFile));
		String s = scanny.nextLine();
		size = Integer.parseInt(s);
		AdjacencyList = new PriorityQueue[size];
		CopperList = new PriorityQueue[size];
		for(int i = 0; i < size; i++)//populate adjacency list
		{
			System.out.println("adding: " + i);
			AdjacencyList[i] = new PriorityQueue<Edge>();
			CopperList[i] = new PriorityQueue<Edge>();
		}
		while(scanny.hasNext())
		{
			s = scanny.nextLine();
			int index = s.indexOf(" ");
			String sub = s.substring(0,index);
			int i = Integer.parseInt(sub);
			PriorityQueue<Edge> vertex = AdjacencyList[i];//get linked list for vertex 
			PriorityQueue<Edge> copperVertex = CopperList[i];
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
			Edge e = new Edge(i,vNum,type,bandwidth,length);
			vertex.add(e);
			if(e.getType().equals("copper"))
				copperVertex.add(e);
			PriorityQueue<Edge> reverseV = AdjacencyList[vNum];
			PriorityQueue<Edge> reverseCv = CopperList[vNum];
			Edge reverse = new Edge(vNum,i,type,bandwidth,length);
			reverseV.add(reverse);
			if(e.getType().equals("copper"))
				reverseCv.add(e);
			System.out.println("adding " + i + " " + type + " " + bandwidth + " " + length + " to " + vNum);
		}
		scanny.close();
		System.out.println("Interface:");
		while(true)//Interface
		{
			System.out.println("Please type: \n1 To find the lowest latency path between two vertices\n2 To check if the graph is copper connected\n4 To exit the system");
			Scanner input = new Scanner(System.in);
			int choice = input.nextInt();
			switch(choice) 
			{
			case 1:
				System.out.println("Please enter the starting point (integer)");
				int vertexOne = input.nextInt();
				System.out.println("Please enter the end point (integer)");
				int vertexTwo = input.nextInt();
				LinkedList<Integer> wsp = weightedShortestPath(vertexOne,vertexTwo);
				resetEdges(AdjacencyList);
				if(wsp == null)
					System.out.println("There is no path from " + vertexOne + " to " + vertexTwo);
				else
				{
					System.out.println("the path is as follows: " + wsp.toString());
					//System.out.println("the bandwidth along the path is: " + getBandwidth(wsp));
				}
				break;
			case 2:
				boolean copperConnection = checkConnection(CopperList);
				resetEdges(CopperList);
				if(copperConnection == true)
					System.out.println("The graph IS copper connected");
				else
					System.out.println("The graph IS NOT copper connected");
				break;
			case 4:
				System.exit(0);
			}
		}
	}
	public static LinkedList<Integer> weightedShortestPath(int startPoint, int endPoint)
	{
		System.out.println("weighted shortest path of : " + startPoint + " to " + endPoint);
		LinkedList<Integer> result = new LinkedList<Integer>();//will contain edges to follow to end point
		LinkedList<Edge> q = new LinkedList<Edge>();
		PriorityQueue<Edge> curr = new PriorityQueue<Edge>(AdjacencyList[startPoint]);//creates a copy of the starting vertex's edges in order from shortest to longest
		double [] Distance = new double [size];//Distance[i] contains lowest distance from startPoint to Vertex i
		int [] Via = new int [size];//Via[i] contains vertex index to use to get to vertex i (Via[2] = 1 means use 1 to get from starting vertex to vertex 2)
		int [] Bandwidth = new int [size];
		for(int i = 0; i < size; i++)//initialize distance array
		{
			if(i == startPoint)
				Distance[i] = 0;//distance from startPoint to startPoint is always 0;
			else
				Distance[i] = MAX_INT;
		}
		int current = startPoint;
		while(!curr.isEmpty())
		{
			System.out.println("adding edge " + startPoint + " to " + curr.peek().getVNum() + " to queue");
			q.add(curr.poll());
		}
		
		while(!q.isEmpty())
		{
			Edge e = q.poll();
			current = e.getFrom();
			e.visited = true;
			System.out.println("edge is from " + current + " to " + e.getVNum());
			int nextNode = e.getVNum();
			if(Distance[nextNode] == MAX_INT)
			{
				System.out.println("max int");
				if(current == startPoint)
				{
					Distance[nextNode] = e.getLatency();
					Bandwidth[nextNode] = e.getBandwidth();
					System.out.println("current == startPoint so set latency to nextNode " + e.getLatency());
				}
				else 
				{
					System.out.println("latency to " + nextNode + " = MAXINT");
					Distance[nextNode] = e.getLatency() + Distance[current];
					Bandwidth[nextNode] = e.getBandwidth();
					System.out.println("so set Distance " + nextNode + " to " + e.getLatency() + " + " + Distance[current] + " (" + Distance[nextNode] + ")");
				}
				Via[nextNode] = current;
			}
			else if(Distance[current] + e.getLatency() < Distance[nextNode])
			{
				System.out.println("Distance [current] "  + Distance[current] + " " + e.getLatency() + " < " + "Distance[nextNode] "+ Distance[nextNode]);
				Distance[nextNode] = Distance[current] + e.getLatency();
				Via[nextNode] = current;
				Bandwidth[nextNode] = e.getBandwidth();
			}
			System.out.println("new curr queue");
			curr = new PriorityQueue<Edge>(AdjacencyList[nextNode]);
			while(!curr.isEmpty())
			{
				if(curr.peek().getVisited() == false)
				{
					System.out.println("adding edge " + nextNode + " to " + curr.peek().getVNum() + " to queue");
					q.add(curr.poll());
				}
				else
					curr.poll();
			}
			System.out.println("added all of curr to q");
		}
	
			//set current and loo
		int currPoint = endPoint;
		int minB = Bandwidth[currPoint];
		while(Via[currPoint] != startPoint)
		{
			if(minB > Bandwidth[currPoint])
				minB = Bandwidth[currPoint];
			result.add(0,currPoint);
			currPoint = Via[currPoint];
		}
		if(minB > Bandwidth[currPoint])
			minB = Bandwidth[currPoint];
		result.add(0,currPoint);
		result.add(0,Via[currPoint]);
		if(Distance[endPoint] == MAX_INT)
			result = null;
		else 
		{
			System.out.println("the lowest latency path from " + startPoint + " to " + endPoint + " takes " + Distance[endPoint] * Math.pow(10, 9)+ " nanoseconds. ");
			System.out.println("it has a bandwidth of: " + minB + " Mbps");
		}
		return result;
	}
	public static boolean checkConnection(PriorityQueue<Edge>[] network)
	{
		//NOTHING TO DO WITH COPPER CONNECTION
		//simply returns true if all vertices are connected, and false if at least one isn't
		int current = 0;
		PriorityQueue<Edge> curr = new PriorityQueue<Edge>(network[current]);
		LinkedList<Edge> q = new LinkedList<Edge>();
		boolean [] Connected = new boolean [size];
		for(int i = 0; i < size; i++)
		{
			Connected[i] = false;
		}
		while(!curr.isEmpty())
		{
			System.out.println("adding edge " + current + " to " + curr.peek().getVNum() + " to queue");
			q.add(curr.poll());
		}
		Connected[0] = true;
		while(!q.isEmpty())
		{
			Edge e = q.poll();
			current = e.getFrom();
			e.visited = true;
			//System.out.println("edge is from " + current + " to " + e.getVNum());
			int nextNode = e.getVNum();
			System.out.println("setting " + nextNode + " to true");
			Connected[nextNode] = true;
			curr = new PriorityQueue<Edge>(network[nextNode]);
			while(!curr.isEmpty())
			{
				if(curr.peek().getVisited() == false)
				{
					q.add(curr.poll());
				}
				else
					curr.poll();
			}
		}
		for(int i = 0; i < size; i++)
		{
			System.out.println(Connected[i]);
			if(Connected[i] == false)
				return false;
		}
		return true;
	}
	public static void resetEdges(PriorityQueue<Edge>[] al)//resets edges to unvisited
	{
		for(int i = 0; i < size; i++)
		{
			PriorityQueue<Edge> curr = new PriorityQueue<Edge>(al[i]);
			while(!curr.isEmpty())
			{
				Edge e = curr.poll();
				e.setVisited(false);
			}
		}
	}
}
