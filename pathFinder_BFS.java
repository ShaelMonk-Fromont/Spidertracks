/*
 * Date: October 2014
 * Author: Shael Monk-Fromont
 * Purpose: Applicant Coding Problem 
 * 
 * Notes: 
 * I have not used Maven for this problem.. sorry?
 * I think the code I have written is very good in terms of robustness and scalability because 
 * it can easily be updated, the data can be quickly changed without needing to recode anything.
 * Most of the methods I have used are reasonably generic so can be reused in different ways 
 * and for different purposes within the program.
 * Where I believe my code is least robust is in dealing with user input, unexpected input can cause unusual behaviour.
 * 
 * 
 */


import java.util.*;

public class pathFinder_BFS {

	static String[] nodeArray = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"}; 	
	static int[][] weightMatrix = new int[][]{
		{0,	12, 0, 	19, 20, 0, 	16, 0, 	0, 	0},
		{0, 0, 	5, 	13, 0, 	0,	0, 	0, 	15, 0},
		{0, 0, 	0, 	5, 	0, 	0,	0, 	0, 	0, 	0},
		{0, 0, 	0, 	0, 	7, 	0, 	0, 	0, 	0, 	0},
		{0, 0, 	0, 	0, 	0, 	5, 	0, 	0, 	0, 	0},
		{5, 0, 	0, 	0, 	0, 	0, 	0, 	0, 	0, 	0},
		{0, 0, 	0, 	0, 	0, 	11,	0, 	0, 	0, 	0},
		{4, 19, 0, 	0, 	0, 	0, 	6, 	0, 	0, 	0},
		{0, 0, 	0, 	0, 	0, 	0, 	0, 	21, 0, 	10},
		{0, 7, 	15, 0, 	0, 	0, 	0, 	0, 	0, 	0}}; 

	//---------------------------------------
	//Calculate Distances
	//---------------------------------------

	private static int calculateDistanceOfPath(List<String> path) {
		String currentNode = path.get(0);
		if(indexOf(currentNode, nodeArray) < 0){
			System.err.println("ERROR - Invalid node: "+ currentNode +".\n");
			return -1;
		}
		String nextNode;
		int distanceCount = 0;
		for(int i = 0; i < path.size(); i++){
			nextNode = path.get(i);
			if(indexOf(nextNode, nodeArray) < 0){
				System.err.println("ERROR - Invalid node: "+nextNode+".\n");
				return -1;
			}
			int distance = weightMatrix[indexOf(currentNode, nodeArray)][indexOf(nextNode, nodeArray)];
			if (distance > 0 || nextNode.equals(currentNode)){	
				distanceCount += distance;//weightMatrix[indexOf(currentNode, nodeArray)][indexOf(nextNode, nodeArray)];
			}else{
				System.err.println("ERROR - This path is not possible because there is no path from "+ currentNode +" to "+ nextNode +".\n");
				return -1;
			}
			currentNode = nextNode;
		}
		return distanceCount;
	}


	//---------------------------------------
	//Find Paths Between Two Nodes - All, Max Length & Exact Length 
	//---------------------------------------
	
	private static List<List<String>> getPathsWithExactLength(String start, String end, int exactLength) {
		List<List<String>> candidatePaths = getAllPaths(start, end), selectedPaths = new ArrayList<List<String>>();
		for(List<String> path: candidatePaths){
			if(path.size() == exactLength){
				selectedPaths.add(path);
			}
		}
		return selectedPaths;
	}

	private static List<List<String>>  getPathsWithMaxLength(String start, String end, int maxLengths) {
		List<List<String>> candidatePaths = getAllPaths(start, end), selectedPaths = new ArrayList<List<String>>();
		for(List<String> path: candidatePaths){
			if(path.size() <= maxLengths){
				selectedPaths.add(path);
			}
		}
		return selectedPaths;
	}

	public static List<List<String>> getAllPaths(String startNode, String endNode) {
		List<String> path = new ArrayList<String>();                  
		path.add(startNode);
		List<List<String>> allPaths = new ArrayList<List<String>>();
		allPaths.add(path);
		return bredthFirstSearch(allPaths, endNode); 
	}

	private static List<List<String>>  bredthFirstSearch(List<List<String>> queriedPaths, String endNode) {  
		List<List<String>> 	partialPaths = new ArrayList<List<String>>(), completePaths = new ArrayList<List<String>>(); 
		for(List<String> path : queriedPaths){ 
			String lastInPath = path.get(path.size()-1);
			if (lastInPath.equals(endNode)){ 
				completePaths.add(path); 
			}else{
				for (String node: nodeArray){
					if(weightMatrix[indexOf(lastInPath, nodeArray)][indexOf(node, nodeArray)] != 0 && !path.contains(node)){
						List<String> newPath = new ArrayList<String>();
						for(String string: path){
							newPath.add(string);
						}
						newPath.add(node); 
						partialPaths.add(newPath);
					}
				}		

			}
		}
		if(partialPaths.size() > 0){ 
			completePaths.addAll(bredthFirstSearch(partialPaths, endNode));
		}
		return completePaths; 
	}


	//---------------------------------------
	//Find Shortest Path
	//---------------------------------------
	private static void findShortestPath(String startNode, String endNode){
		List<List<String>> allPaths = getAllPaths(startNode, endNode);
		List<String> shortestPath = allPaths.get(0);
		int shortestDistance = calculateDistanceOfPath(shortestPath);
		for(int i = 0; i < allPaths.size()-1; i ++){ 
			int distance = calculateDistanceOfPath(allPaths.get(0));
			if (distance < shortestDistance){
				shortestPath = allPaths.get(0);
				shortestDistance = distance;
			}
		}
		System.out.println("The shortest route between "+ startNode +" and "+ endNode +" is "+ shortestPath +" whith a distance of "+ shortestDistance +".");		
	}
	

	// 
	//Helper method: find the index of an element in an array.
	//
	public static <T> int indexOf(T element, T[] array){
		for (int i=0; i<array.length; i++){
			if (array[i] != null && array[i].equals(element) || element == null && array[i] == null) {
				return i;
			}
		}
		return -1;
	}

}
