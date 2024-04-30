import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

/**
 *
 * @author yaw
 * 
 *Contributors: Emery Krulish, Brady Underwood
 */
public class GraphToolBox {
    //return an array containing the vertex numbers of an optimal VC.
    public static int[] exactVC(Graph inputGraph) {
    	
    	//initialize graph, and vertex cover array. minSize is initially the same size as the whole graph
    	int[][] g = inputGraph.getGraph();
        int minSize = g.length;
    	int[] vc = new int[minSize];
        
        //set vc as just all the verticies to start
        for(int vertex = 0; vertex < g.length; vertex++) {
            vc[vertex] = vertex;
        }
        
        //create a set of all the verticies to put into the powerset
        Set<Integer> verticies = new HashSet<>();
        for(int vertex = 0; vertex < g.length; vertex++) {
            verticies.add(vertex);
        }
        
        //create powerset (ps), loop through it
        Set<Set<Integer>> ps = powerSet(verticies);
        for(Set<Integer> set:ps){
            
        	//check to see if each set is in VC		
        	if(inVC(g, set)) {
                
                //check to see if size of set is smaller than current vc.
                if(set.size() < minSize) {
                	//if it is, create newVC, replace vc, update minSize
                    Object[] objArray = set.toArray();
                    int[] newVC = new int[objArray.length];
                    for(int c = 0; c < objArray.length; c++ ){
                        newVC[c] = (int)objArray[c];
                    }
                    vc = newVC;
                    minSize = set.size();
                }
            }
        }
        //at this point, all potential sets have been checked, and the smallest is vc, as an array
        
        return vc;
    }
    
    // return (in polynomial time) an array containing the vertex numbers of a VC.
    public static int[] inexactVC(Graph inputGraph) {
        int[][] g = inputGraph.getGraph();
        Set<Integer> potentialVC = new HashSet<>();
        Random r = new Random();
        
        //start with a basic vertex cover, ie all the verticies
        for (int i = 0; i < g.length; i++) {
			potentialVC.add(i);
		}
        
        //take off one random vertex at a time until the set is not in VC of the graph
        boolean tempInVC = true;
        while (tempInVC) {
			int vertexRemoved = g.length+2;
			//get a random vertex that hasn't been removed yet
        	while(!potentialVC.contains(vertexRemoved)) {
				vertexRemoved = r.nextInt(g.length);
        	}
        	//remove it
        	potentialVC.remove(vertexRemoved);
        	
        	//check if potential VC
			if(!inVC(g,potentialVC)) {
				//if it isn't, add back in the last vertex, and stop the loop
				potentialVC.add(vertexRemoved);
				tempInVC=false;
			}
		}
        //convert set to int array
        Object[] objArray = potentialVC.toArray();
        int[] returnArray = new int[objArray.length];
        for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = (int)objArray[i];
		}
        
        return returnArray;
    }
    
    // return an array containing the vertex numbers of an optimal IS.
    public static int[] optimalIS(Graph inputGraph) {
        int[][] g = inputGraph.getGraph();
    	int[] is;
        int[] allV = new int[g.length];
        
        int[] vc = exactVC(inputGraph);

        for(int i = 0; i < allV.length; i++) {
            allV[i] = i;
        }

        //finds all allV which are not apart of the ideal vertex cover, which is the definition of an optimal independent set
        is = findMissing(allV, vc, allV.length, vc.length);
        
        return is;
    }
    
    // return (in polynomial time) an array containing the vertex numbers of a IS.
    public static int[] inexactIS(Graph inputGraph) {
        int[][] g = inputGraph.getGraph();
    	int[] is;
        int[] allV = new int[g.length];
        
        int[] vc = inexactVC(inputGraph);

        for(int i = 0; i < allV.length; i++) {
            allV[i] = i;
        }

        is = findMissing(allV, vc, allV.length, vc.length);
        
        return is;
    }

    
    //determines whether a given set is a vertex cover of a given graph
    private static boolean inVC(int[][] g, Set<Integer> set) {

    	//create boolean array to "mark" each vertex
        boolean[] inVC = new boolean[g.length];

        for(int vertex = 0; vertex < g.length; vertex++) {
        	//g[vertex] are neighbors of vertex
        	int[] neighbors = g[vertex];
            if(!set.contains(vertex)) {
            //if vertex is not in the set, need to make sure all of its neighbors are, otherwise set is not VC
                if(neighbors.length == 0) {
                	//if a vertex has no neighbors, and it isn't in the set, the set is not VC
                    return false;
                }
                //now check each neighbor. create a new array to store markings of each neighbor
                boolean[] neighborMarkings = new boolean[neighbors.length];
                for(int neighbor = 0; neighbor < neighbors.length; neighbor++) {                	
                    //mark true for each neighbor in the set, mark false if not
                    if(set.contains(neighbors[neighbor])) {
                    	neighborMarkings[neighbor] = true;
                    }
                    else {
                    	neighborMarkings[neighbor] = false;
                    }
                }
                //make sure all neighbors are marked true
                boolean neighborsTrue = true;
                for(boolean b: neighborMarkings) {
                    if(b != true) {
                        neighborsTrue = false;
                    }
                }
                //if all the neighbors are true, vertex is marked true. if any neighbor is false, vertex is marked false
                inVC[vertex] = neighborsTrue;
            }
        	//if the vertex is in the set, don't have to look at its neighbors, just mark it true
            else {
                inVC[vertex] = true;
            }
        }
        //at this point, every vertex is marked: either true or false
        //if every vertex is marked true, return true. if any are marked false, return false
        for(int vertex = 0; vertex < inVC.length; vertex++){
            if(inVC[vertex] == false) {
                return false;
            }
        }
        return true;
    }

    // Source of findMissing method: https://www.geeksforgeeks.org/find-elements-present-first-array-not-second/

    //returns a list of elements which are in a[] but not in b[]
    private static int[] findMissing(int a[], int b[], int n, int m)
    {
        Set<Integer> set = new HashSet<>();

        for (int i = 0; i < n; i++)
        {
            int j;
             
            for (j = 0; j < m; j++)
                if (a[i] == b[j])
                    break;
 
            if (j == m)
                set.add(a[i]);
        }
        
        Object[] numArray = set.toArray();
        int[] tempOptimalIS = new int[numArray.length];
        for(int j = 0; j < numArray.length; j++ ){
            tempOptimalIS[j] = (int)numArray[j];
        }

        return tempOptimalIS;

    }

    
    //Source of powerSet method: https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java
    
    //creates a set of all possible subsets from a given set, aka a powerset
    private static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }       
        return sets;
    }
    
}
