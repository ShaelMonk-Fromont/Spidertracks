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

public class Go_Fetch_Railways {
	
	static String[] stationArray = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"}; 
			// Array of Stations, the dataMatrix should be updated whenever this array is.
	
	static int[][] dataMatrix = new int[][]{
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
			// Weighted Adjacency Matrix representation of the graph, this is easily updatable.
	
	/*_______________________________________
	 * Task 1 
	 * Calculate Distances	
	 * To solve the calculate distance problem I traced the path, checking at each stage that there existed a valid connection. 
	 * At each step the distance weight was added to a count. 
	 * When the path trace was complete and confirmed to be valid the sum of the distances along the path that was stored in distanceCount was returned.
	 */
	private static int calculateDistance(List<String> path) {
		String previousStation = path.get(0);
		if(indexOf(previousStation, stationArray) < 0){
			System.err.println("ERROR - Invalid station: "+previousStation+".\n");
			return -1;
		}
		String currentStation;
		int distanceCount = 0;
		for(int i = 1; i < path.size(); i++){
			currentStation = path.get(i);
			if(indexOf(currentStation, stationArray) < 0){
				System.err.println("ERROR - Invalid station: "+currentStation+".\n");
				return -1;
			}
			if (dataMatrix[indexOf(previousStation, stationArray)][indexOf(currentStation, stationArray)]>0 || currentStation.equals(previousStation)){		
				distanceCount += dataMatrix[indexOf(previousStation, stationArray)][indexOf(currentStation, stationArray)];
			}else{
				System.err.println("ERROR - This route is not possible because there is no path from "+ previousStation +" to "+ currentStation +".\n");
				return -1;
			}
			previousStation = currentStation;
		}
		return distanceCount;
	}


	/*_______________________________________
	 * Task 2 
	 * Journey Planner	
	 * To solve this problem I wrote a Breadth First Search algorithm. 
	 * It takes in a list of paths, if the last element in a path was the end station the path was added to a list of complete paths which would be returned. 
	 * If the path was not complete then the algorithm finds the stations that the last station in the path has as destinations 
	 * and checks if they are already in the path, if not  new path with the destination added to the end of the old path was added to a list of paths 
	 * which would be recursed down until they either reached the end station or ran out of new, reachable stations. 
	 * 
	 */
	public static List<List<String>> getAllPaths(String startStation, String endStation) {
		List<List<String>> allPaths = new ArrayList<List<String>>();
		List<String> path = new ArrayList<String>();                  
		path.add(startStation);
		allPaths.add(path);
		Set<String> setOfVisitedStations = new HashSet<String>();                 
		setOfVisitedStations.add(startStation);
		return bredthFirstSearch(allPaths, endStation); 
	}

	private static List<List<String>>  bredthFirstSearch(List<List<String>> previousLevelsPaths, String endStation) {
		List<List<String>> thisLevelsPaths = new ArrayList<List<String>>();  //This list of paths will be filled with incomplete paths and then used in the recursion.
		List<List<String>> keepers = new ArrayList<List<String>>(); //This list of paths will be filled with complete paths and then returned.
		for(List<String> path : previousLevelsPaths){  // For each of the paths recursed from the previous level: 
			String lastStationInPath = path.get(path.size()-1);
			if (lastStationInPath.equals(endStation)){ 
				keepers.add(path); // this path has been added to the list of paths that are complete and ready to be returned
			}else{
				List<String> destinations = getDestinationsFromStation(lastStationInPath); // get a list of all the stations that can be reached from the station at the end of the path.          
				for(String destination : destinations) if (!path.contains(destination)) {  // For each station that can be reach from the last station of the path, and which isn't already in the path .               
					List<String> newPath = copyList(path); 
					newPath.add(destination); 
					thisLevelsPaths.add(newPath); // This path is not complete and has been added to the list to be recursed.
				}
			}
		}
		if(thisLevelsPaths.size() > 0){ // if there are any paths in the list to be recursed:
			keepers.addAll(bredthFirstSearch(thisLevelsPaths, endStation)); //do the recursion and add the returned list of paths to the list of keepers from this level
		}
		return keepers; // return all complete paths from this level and below
	}
	
	public static List<String> getDestinationsFromStation(String fromStation) {
		List<String> destinations = new ArrayList<String>();
		for (String station: stationArray){
			if(dataMatrix[indexOf(fromStation, stationArray)][indexOf(station, stationArray)] != 0 ){
				destinations.add(station);
			}
		}
		return destinations;
	}
	
	private static List<List<String>> journeyPlannerExact(String start, String end, int exact) {
		List<List<String>> candidatePaths = getAllPaths(start, end);
		List<List<String>> goodPaths = new ArrayList<List<String>>();
		for(List<String> path: candidatePaths){
			if(path.size() == exact){
				goodPaths.add(path);
			}
		}
		return goodPaths;
	}

	private static List<List<String>> journeyPlannerMax(String start, String end, int max) {
		List<List<String>> candidatePaths = getAllPaths(start, end);
		List<List<String>> goodPaths = new ArrayList<List<String>>();
		for(List<String> path: candidatePaths){
			if(path.size() <= max){
				goodPaths.add(path);
			}
		}
		return goodPaths;
	}

	
	/*_______________________________________
	 * Task 3
	 * Shortest Route	
	 * Here I used my answers to the previous two questions together to solve the problem. 
	 * I found all possible paths using the journey planner then chose from among them the shortest.
	 * 
	 */
	private static void findShortestRoute(String startStation, String endStation){
		List<List<String>> allPaths = getAllPaths(startStation,  endStation);
		List<String> shortestPath = allPaths.get(0);
		int shortestDistance = calculateDistance(shortestPath);
		for(List<String> path: allPaths){ // Unnecessarily rechecks first path.
			int distance = calculateDistance(path);
			if (distance < shortestDistance){
				shortestPath = path;
				shortestDistance = distance;
			}
		}
		System.out.println("The shortest route between "+ startStation +" and "+ endStation +" is "+ shortestPath +" whith a distance of "+ shortestDistance +".");		
	}



	/*________________________________________
	 * Main
	 * purpose: basic user interface
	 * 	 
	 */
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean quit = false;		
		while(quit == false){
			System.out.println( "-----\n"
					+ "-----\n"
					+ "To calculate a distance enter 'calc'.\n"
					+ "To plan a journey enter 'plan'.\n"
					+ "To find a shortest route enter 'short'.\n"
					+ "To exit this program enter 'quit'.\n"
					+ ": ");
			String userInput = scan.nextLine();
					
			//___Calculate__Distance___
			if (userInput.equals("c") 
					|| userInput.equals("calc") 
					|| userInput.equals("Calc") 
					|| userInput.equals("Calculate") 
					|| userInput.equals("calculate")){
				System.out.print( "-----\n"
						+ "You have chosen to calculate the distance along a given path.\n"
						+ "If you do not wish to this you may enter 'exit'.\n"
						+ "Please enter a series of stations seperated by spaces, e.g. 'A B C D'\n"
						+ ": ");
				userInput = scan.nextLine();
				if(!(userInput.equals("e") 
						|| userInput.equals("exit") 
						|| userInput.equals("Exit"))){
					List<String> path = stringToList(userInput);
					int distance = calculateDistance(path);
					if (distance != -1){
						System.out.println("The total distance along that path is "+ distance +".");
					} 
				}
				
			//___Journey__Planning___	
			}else if (userInput.equals("p") 
					|| userInput.equals("plan") 
					|| userInput.equals("Plan")){
				System.out.print( "-----\n"
						+ "You have chosen to find possible paths between two stations.\n"
						+ "If you do not wish to this you may enter 'exit'.\n"
						+ "Please enter a pair of stations seperated by a space, e.g. 'A B'.\n"
						+ "You may also specify a maximum or exact number of stops by adding aan intiger followed by 'max' or 'exact' seperated by spaces, e.g. 'A B 6 max' or 'A B 6 exact'.\n"
						+ ": ");
				userInput = scan.nextLine();
				if(!(userInput.equals("e") 
						|| userInput.equals("exit") 
						|| userInput.equals("Exit"))){
					List<String> inputArray = stringToList(userInput);					
					if (inputArray.size() == 4
							&& (inputArray.get(3).equals("m")
							|| inputArray.get(3).equals("max")
							|| inputArray.get(3).equals("Max"))){
						List<List<String>> paths = journeyPlannerMax(inputArray.get(0), inputArray.get(1), Integer.parseInt(inputArray.get(2)));
						System.out.println("The possible paths between "+ inputArray.get(0) +" and "+ inputArray.get(1) +" are ");
						for(List<String> path: paths){
							System.out.println(path.toString());
						}
					}else if (inputArray.size() == 4
							&& (inputArray.get(3).equals("e")
							|| inputArray.get(3).equals("exact")
							|| inputArray.get(3).equals("Exact"))){
						List<List<String>> paths = journeyPlannerExact(inputArray.get(0), inputArray.get(1), Integer.parseInt(inputArray.get(2)));
						System.out.println("The possible paths between "+ inputArray.get(0) +" and "+ inputArray.get(1) +" are ");
						for(List<String> path: paths){
							System.out.println(path.toString());
						}
					}else if(inputArray.size() == 2){
						List<List<String>> paths = getAllPaths(inputArray.get(0), inputArray.get(1));
						System.out.println("The possible paths between "+ inputArray.get(0) +" and "+ inputArray.get(1) +" are ");
						for(List<String> path: paths){
							System.out.println(path.toString());
						}
					}else{
						System.err.println("Error - Input did match the expected formatting");
					}
				}
						
			//___Shortest__Route___
			}else if (userInput.equals("s") 
					|| userInput.equals("short") 
					|| userInput.equals("Short")){
				
				System.out.print( "-----\n"
						+ "You have chosen to find the shortest route between two stations.\n"
						+ "If you do not wish to this you may enter 'exit'.\n"
						+ "Please enter a pair of stations seperated by a space, e.g. 'A B'.\n"
						+ ": ");
				userInput = scan.nextLine();
				if(!(userInput.equals("e") 
						|| userInput.equals("exit") 
						|| userInput.equals("Exit"))){ 
					List<String> inputArray = stringToList(userInput); // I should add something to catch invalid input here..
					findShortestRoute(inputArray.get(0),inputArray.get(1));
				}
			
			//___Quit___
			}else if (userInput.equals("q") 
					|| userInput.equals("quit") 
					|| userInput.equals("Quit")){
				quit = true;
			}else{
				System.err.println("ERROR - Invalid Command: \""+userInput+"\".\n");
			}			
		}		
		scan.close();		
	}

	
	// Helper method: find the index of an element in an array.
	public static <T> int indexOf(T element, T[] array){
		for (int i=0; i<array.length; i++){
			if (array[i] != null && array[i].equals(element) || element == null && array[i] == null) {
				return i;
			}
		}
		return -1;
	}
	
	// Helper method: converts a user input string into a list of the space separated substrings.
	//This method may be somewhat unstable when it comes to unpredictable users.
	private static List<String> stringToList(String input) {
		String userInput = input.trim();
		List<String> list = new ArrayList<String>();
		String temp = "";
		for(int i = 0; i < userInput.length(); i++){
			String c = "" + userInput.charAt(i);
			if(c.equals(" ")){
				list.add(temp);
				temp = "";
			}else{
				temp += c;
			}
		}
		list.add(temp);
		return list;
	}

	// Helper method: make a close of a list of strings from scratch.
	private static List<String> copyList(List<String> list){
		List<String> newList = new ArrayList<String>();
		for(String string: list){
			newList.add(string);
		}
		return newList;
	}
}
