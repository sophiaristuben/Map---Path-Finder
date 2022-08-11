package onTheRoad;

/**
 * Common algorithms for Graphs. 
 * They all assume working with a EdgeWeightedDirected graph.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

public class GraphAlgorithms {

	/**
	 * Reverses the edges of a graph
	 * 
	 * @param g
	 *            edge weighted directed graph
	 * @return graph like g except all edges are reversed
	 */
	public static EdgeWeightedDigraph graphEdgeReversal(EdgeWeightedDigraph g) {
		// create empty digraph same size as g
		EdgeWeightedDigraph revG = new EdgeWeightedDigraph(g.V());
		for (DirectedEdge var : g.edges()) {
			int previousTail = var.from();
			int prevousHead = var.to();
			double varWeight = var.weight();
			revG.addEdge(new DirectedEdge(prevousHead,previousTail, varWeight));
		}
		return revG;
	}

	/**
	 * Performs breadth-first search of g from vertex start.
	 * 
	 * @param g
	 *            directed edge weighted graph
	 * @param start
	 *            index of starting vertex for search
	 */
	public static void breadthFirstSearch(EdgeWeightedDigraph g, int start) {
		//reset graph so that no vertex starts marked as visited
		g.reset();
		// put unvisited vertices on queue
		
		//reset graph so that no vertex starts marked as visited
		Deque<Integer> toVisit = new ArrayDeque<Integer>();	
		// make distance = 0
		DirectedEdge startEdge = new DirectedEdge(start, start, 0.0);
		//and mark as visited & make distance = 0
		g.visit(startEdge, 0);

		// put s on a queue
		toVisit.add(start);
		// repeat until queue is empty
		while (!toVisit.isEmpty()) {
			// dequeue vertex v by grabbing edge at front of deque
			int v = toVisit.remove();
			for (DirectedEdge w : g.adj(v)) {
				// if destination of edge is not visited
				if (!(g.isVisited(w.to()))) {
					// enqueue and mark
					double newDist = g.getDist(v) + 1;
					g.visit(w, newDist);
					toVisit.add(w.to());
				}
			}
		}
	}

	/**
	 * Calculates whether the graph is strongly connected
	 * 
	 * @param g
	 *            directed edge weighted graph
	 * @return whether graph g is strongly connected.
	 */
	public static boolean isStronglyConnected(EdgeWeightedDigraph g) {
		// do breadthFirstSearch to see which vertices have been visited starting at 0
		breadthFirstSearch(g, 0);
		
		// loop through the vertices
		for (int v = 0; v < g.V(); v++) {
			if (!g.isVisited(v)) { // if it has not been visited 
				return false;
			}	
		}
		
		// reverse the graph
		EdgeWeightedDigraph reverseGraph = graphEdgeReversal(g);
		
		// do breadthFirstSearch to see which vertices have been visited starting at 0
		breadthFirstSearch(reverseGraph, 0);
		
		// loop through the vertices
		for (int v = 0; v < reverseGraph.V(); v++) {
			if (!reverseGraph.isVisited(v)) { // if it has not been visited 
				return false;
			}	
		}
		return true;
	}
	
	
	/**
	 * 
	 * @param g
	 * @param e
	 * @param pq
	 */
	public static void relax(EdgeWeightedDigraph g, DirectedEdge e, IndexMinPQ<Double> pq) {

		// defining start and finish
		int v = e.from(), w = e.to();
		
		// comparing the weights
		if (g.getDist(w) > g.getDist(v) + e.weight()) {
			g.setDist(w, g.getDist(v) + e.weight()); // setting the new distance
			g.setEdgeTo(e); // setting the new edge

			// update the priority queue since the priority queue's size has changed
			if (pq.contains(w)) {
				pq.decreaseKey(w, g.getDist(w)); 
			// adding to the priority queue
			} else {
				pq.insert(w, g.getDist(w));
			}
		}
	}
	
	
	/**
	 * Runs Dijkstra's algorithm on path to calculate the shortest path from
	 * starting vertex to every other vertex of the graph.
	 * 
	 * @param g
	 *            directed edge weighted graph
	 * @param s
	 *            starting vertex
	 * @return a hashmap where a key-value pair <i, path_i> corresponds to the i-th
	 *         vertex and path_i is an arraylist that contains the edges along the
	 *         shortest path from s to i.
	 */
	public static HashMap<Integer, ArrayList<DirectedEdge>> dijkstra(EdgeWeightedDigraph g, int s) {
		g.reset();
		IndexMinPQ<Double> pq;
		HashMap<Integer, ArrayList<DirectedEdge>> myHash = new HashMap<Integer, ArrayList<DirectedEdge>>();
		
		// priority queue of vertices
		pq = new IndexMinPQ<Double>(g.V());
		
		// set everything to positive infinity
		for (int v = 0; v < g.V(); v++) {
			g.setDist(v, Double.POSITIVE_INFINITY);
		}
		g.setDist(s, 0.0);

		// relax vertices in order of distance from s
		pq.insert(s, g.getDist(s));
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			for (DirectedEdge e : g.adj(v)) {
				relax(g, e, pq);
			}
		}
		// loop through vertices
		for (int i = 0; i < g.V(); i++) {
			ArrayList<DirectedEdge> myArray  = new ArrayList<DirectedEdge>();
			// check if the distance is less than infinity
			if (g.getDist(i) < Double.POSITIVE_INFINITY) {
				for (DirectedEdge edge = g.getEdgeTo(i); edge != null; edge = g.getEdgeTo(edge.from())) {
					myArray.add(edge);
				}
				// insert into the hashMap
				myHash.put(i, myArray);
			} else {
				myHash.put(i, null);
			}
		}
	return myHash;
	}

	/**
	 * Computes shortest path from start to end using Dijkstra's algorithm.
	 *
	 * @param g
	 *            directed graph
	 * @param start
	 *            starting node in search for shortest path
	 * @param end
	 *            ending node in search for shortest path
	 * @return a list of edges in that shortest path in correct order
	 */
	public static ArrayList<DirectedEdge> getShortestPath(EdgeWeightedDigraph g, int start, int end) {
		// run dijkstra
		HashMap<Integer, ArrayList<DirectedEdge>> hashDJ = dijkstra(g, start);
		
		// create a new ArrayList with edges running from start to end.
		ArrayList<DirectedEdge> edges = new ArrayList<DirectedEdge>();
		
		// get all the paths from start to end
		ArrayList<DirectedEdge> endPath = hashDJ.get(end);
		
		// add list paths in reverse order
		for(int i = endPath.size()-1; i>=0; i--) {
			edges.add(endPath.get(i));
		}
		return edges;
	}

	/**
	 * Using the output from getShortestPath, print the shortest path
	 * between two nodes
	 * 
	 * @param path shortest path from start to end
	 * @param isDistance prints it based on distance (true) or time (false)
	 */
	public static void printShortestPath(ArrayList<DirectedEdge> path, boolean isDistance, List<String> vertices) {
		// Hint: Look into TestGraphs for format of printout
		
		// if it is distance
		if (isDistance) {
			// keep a variable for the total distance
			double distance = 0;
			
			// print each step
			System.out.println("\tBegin at " + vertices.get(path.get(0).from()));
			for (int i = 0; i < path.size(); i++) {
				System.out.println("\tContinue to " + vertices.get(path.get(i).to()) + "(" + path.get(i).weight() + ")");
				distance += path.get(i).weight();
			}
			
			// print total distance
			System.out.println("Total distance: " + distance + " miles");
		} 
		// if it is time
		else {
			// keep a variable of the total time
			double time = 0;
			System.out.println("\tBegin at " + vertices.get(path.get(0).from()));
			
			//print each path
			for (int i = 0; i < path.size(); i++) {
				System.out.println("\tContinue to " + vertices.get(path.get(i).to()) + " (" + hoursToHMS(path.get(i).weight()) + ")");
				time += path.get(i).weight();
			}
			System.out.println("Total time: " + hoursToHMS(time));
		}
	}

	/**
	 * Converts hours (in decimal) to hours, minutes, and seconds
	 * 
	 * @param rawhours
	 *            time elapsed
	 * @return Equivalent of rawhours in hours, minutes, and seconds (to nearest
	 *         10th of a second)
	 */
	private static String hoursToHMS(double rawhours) {
		//translating hours into ints
		int numHours = (int)rawhours;
		
		//finding the fractional hours
		double fractionalHours = rawhours - numHours;
		
		//finding number of tenth seconds
		int tenthSeconds = (int)Math.round(fractionalHours * 36000);
		
		//calculating number of mins based of tenths of seconds
		int minutes = tenthSeconds/600;
		
		//finding remainder of tenth of seconds left after subtracting full minutes
		double tenthSecondsLeft = tenthSeconds - 600 * minutes;
		
		//calculating number of seconds with remaining tenth seconds
		double seconds = tenthSecondsLeft / 10;
		
		//final time output
		if (numHours == 0 && minutes == 0) {
			return seconds + " secs";
		}
		else if (numHours == 0) {
			return minutes + " mins " + seconds + " secs";
		}
		else {
			return numHours + " hrs " + minutes + " mins " + seconds + " secs";
		}
	}
}