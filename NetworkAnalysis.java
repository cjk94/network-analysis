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
		}
		scanny.close();
		System.out.println("Interface:");
		while(true)//Interface
		{
			System.out.println("\nPlease type: \n1 To find the lowest latency path between two vertices\n2 To check if the graph is copper connected\n3 Determine if the graph will remain connected if any two vertices fail\n4 To exit the system");
			Scanner input = new Scanner(System.in);
			int choice = input.nextInt();
			switch(choice) 
			{
			case 1:
				System.out.println("Please enter the starting point (integer)");
				int vertexOne = input.nextInt();
				System.out.println("Please enter the end point (integer)");
				int vertexTwo = input.nextInt();
				if(vertexOne == vertexTwo)
				{
					System.out.println("The start and end vertices are the same");
				}
				else
				{
					LinkedList<Integer> wsp = weightedShortestPath(vertexOne,vertexTwo);
					resetEdges(AdjacencyList);
					if(wsp == null)
						System.out.println("There is no path from " + vertexOne + " to " + vertexTwo);
					else
					{
						System.out.println("the path is as follows: " + wsp.toString());
						//System.out.println("the bandwidth along the path is: " + getBandwidth(wsp));
					}
				}
				break;
			case 2:
				boolean copperConnection = checkConnection(CopperList,-1,-1);
				resetEdges(CopperList);
				if(copperConnection == true)
					System.out.println("The graph IS copper connected");
				else
					System.out.println("The graph IS NOT copper connected");
				break;
			case 3:
				ArrayList<Boolean> b = new ArrayList<Boolean>();
				PriorityQueue<Edge> [] network = new PriorityQueue[size];
				for(int i = 0; i < size; i++)
				{
					PriorityQueue<Edge> c = new PriorityQueue<Edge>(AdjacencyList[i]);
					network[i] = c;
				}
				for(int i = 0; i < size; i++)
				{
					for(int j = 0; j < size; j++)
					{
						resetEdges(AdjacencyList);
						PriorityQueue<Edge>[] n = new PriorityQueue[size];
						n = getNetwork(network,i,j);
						if(checkConnection(n,i,j))
							b.add(true);
						else
							b.add(false);
					}
				}
				if(b.contains(false))
					System.out.println("Removal of a pair of vertices disconnects the graph");
				else
					System.out.println("Removal of any pair of vertices does not disconnect the graph");
				resetEdges(AdjacencyList);
				break;
			case 4:
				System.exit(0);
			}
		}
	}
	public static PriorityQueue<Edge>[] getNetwork(PriorityQueue<Edge>[] network, int r1, int r2)//returns network with edges removed that connect to vertices r1 or r2
	{
		PriorityQueue<Edge>[] returnN = new PriorityQueue[size];
		for(int i = 0; i < size; i++)
		{
			PriorityQueue<Edge> c = new PriorityQueue<Edge>(network[i]);
			returnN[i] = c;
		}
		for(int i = 0; i < size; i++)
		{
			PriorityQueue<Edge> curr = returnN[i];
			LinkedList<Edge> currL = new LinkedList<Edge>(curr);
				for(int j = 0; j < currL.size(); j++)
				{
					Edge e = currL.get(j);
					if(e.getFrom() == r1 || e.getVNum() == r1)
						currL.remove(j);
					else if(e.getFrom() == r2 || e.getVNum() == r2)
						currL.remove(j);
				}
				returnN[i] = new PriorityQueue<Edge>(currL);
		}
		return returnN;
	}
	public static LinkedList<Integer> weightedShortestPath(int startPoint, int endPoint)
	{
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
			q.add(curr.poll());
		}
		
		while(!q.isEmpty())
		{
			Edge e = q.poll();
			current = e.getFrom();
			e.visited = true;
			int nextNode = e.getVNum();
			if(Distance[nextNode] == MAX_INT)
			{
				if(current == startPoint)
				{
					Distance[nextNode] = e.getLatency();
					Bandwidth[nextNode] = e.getBandwidth();
				}
				else 
				{
					Distance[nextNode] = e.getLatency() + Distance[current];
					Bandwidth[nextNode] = e.getBandwidth();
				}
				Via[nextNode] = current;
			}
			else if(Distance[current] + e.getLatency() < Distance[nextNode])
			{
				Distance[nextNode] = Distance[current] + e.getLatency();
				Via[nextNode] = current;
				Bandwidth[nextNode] = e.getBandwidth();
			}
			curr = new PriorityQueue<Edge>(AdjacencyList[nextNode]);
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
	public static boolean checkConnection(PriorityQueue<Edge>[] network, int r1, int r2)
	{
		//NOTHING TO DO WITH COPPER CONNECTION
		//simply returns true if all vertices are connected, and false if at least one isn't
		int current = -1;
		int x = 0;
		while(current == -1)
		{
			if(x == r1 || x == r2)
				x++;
			else
				current = x;
		}
		PriorityQueue<Edge> curr = new PriorityQueue<Edge>(network[current]);
		LinkedList<Edge> q = new LinkedList<Edge>();
		boolean [] Connected = new boolean [size];
		for(int i = 0; i < size; i++)
		{
			Connected[i] = false;
		}
		while(!curr.isEmpty())
		{
			q.add(curr.poll());
		}
		while(!q.isEmpty())
		{
			Connected[current] = true;
			Edge e = q.poll();
			current = e.getFrom();
			e.setVisited(true);
			//System.out.println("edge is from " + current + " to " + e.getVNum());
			int nextNode = e.getVNum();
			if(e.getFrom() != r1 && e.getVNum() != r1 && e.getFrom() != r2 && e.getVNum() != r2)
			{
				//System.out.println("connected [ ] " + nextNode + " set to true ");
				Connected[nextNode] = true;
			}
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
		if(r1 == -1)
		{
			for(int i = 0; i < size; i++)
			{
				if(Connected[i] == false)
				{
					//System.out.println(i + " = " +Connected[i]);
					return false;
				}
			}
			return true;
		}
		else
		{
			for(int i = 0; i < size; i ++)
			{
				if(Connected[i] == false && (i != r1 && i != r2))
				{
					//System.out.println(i + " = " +Connected[i]);
					return false;
				}
			}
			return true;
		}
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
