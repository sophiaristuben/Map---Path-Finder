package onTheRoad;


import java.util.ArrayList;
import java.util.List;

//import autocomplete.Term;

/**
 * Class whose main method reads in description of graph and trip requests,
 * and then returns shortest paths (according to distance or time) from one
 * given vertex to another.  The input file is given by a command line argument.
 * @author ????
 * @date ????
 */
public class Optimizer {
	
	public static void main(String[] args) {
			
		FileParser fp = new FileParser(args[0]);
		
		List<String> vertices = fp.getVertices();
		List<TripRequest> tripRequest = fp.getTrips();
		int V = vertices.size();
		
		// build the graph(s) 
		// solve the trip requests in the file.
		EdgeWeightedDigraph distanceGraph = new EdgeWeightedDigraph(V);
		
		//boolean isDistance = tripRequest.get(i).isDistance();	
		distanceGraph = fp.makeGraph(true);
		
		EdgeWeightedDigraph timeGraph = new EdgeWeightedDigraph(V);
		timeGraph = fp.makeGraph(false);
		
		//checking to see if the map is connected
		if (!GraphAlgorithms.isStronglyConnected(distanceGraph)) {
			System.out.println("Disconnected Map");
		}
		//looping through all the trips
		else {
			for (int i = 0; i < tripRequest.size(); i ++) {
				int start = tripRequest.get(i).getStart();
				int end = tripRequest.get(i).getEnd();
				boolean isDistance = tripRequest.get(i).isDistance();
				
				
				//if done by distance, print the distance graph 
				if (isDistance == true) {
					ArrayList<DirectedEdge> result = GraphAlgorithms.getShortestPath(distanceGraph, start, end);	
					System.out.println("Shortest distance from " + vertices.get(result.get(0).from()) + " to " 
					+ vertices.get(result.get(result.size()-1).to()));
					GraphAlgorithms.printShortestPath(result, isDistance, vertices);
					
					
				}
				//else print the time graph 
				else {
					ArrayList<DirectedEdge> result = GraphAlgorithms.getShortestPath(timeGraph, start, end);	
					System.out.println("Shortest driving time from " + vertices.get(result.get(0).from()) + " to " 
					+ vertices.get(result.get(result.size()-1).to()));
					GraphAlgorithms.printShortestPath(result, isDistance, vertices);
				}
				
				
			}
		}
		
	}
}