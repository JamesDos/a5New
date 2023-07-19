package diver;

import game.*;
import graph.ShortestPaths;
import graph.WeightedDigraph;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {

    /** See {@code SewerDriver} for specification. */
    @Override
    public void seek(SeekState state) {
        // TODO : Look for the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method (it may be recursive) elsewhere, with a
        // good specification, and call it from this one.
        //
        // Working this way provides you with flexibility. For example, write
        // one basic method, which always works. Then, make a method that is a
        // copy of the first one and try to optimize in that second one.
        // If you don't succeed, you can always use the first one.
        //
        // Use this same process on the second method, scram.
        dfsWalk(state, new HashSet<>());
    }

    /**
     * Helper method used by seek() that uses a dfs to walk McDiver to the ring (if possible)
     * McDiver is standing on node 'current' given by state 's'
     * Method ends with McDiver standing on node current; Ends on ring if current == ring
     * Requires current is unvisited
     */

    public static void dfsWalk(SeekState s, HashSet<Long> visited){
        if(s.distanceToRing() == 0){
            return;
        }
        long current = s.currentLocation();
        visited.add(current);
        // Create list of pointers for neighbors
        List<NodeStatus> sortedNode = new ArrayList<>(s.neighbors());
        Collections.sort(sortedNode);
        System.out.println("Current: " + current);
        System.out.println("Size: " + sortedNode.size());
        for(NodeStatus n: sortedNode){
            System.out.println(n.getId() + " " + n.getDistanceToRing());
        }
        for (NodeStatus neighbor : sortedNode) {
            if (!visited.contains(neighbor.getId())) {
                s.moveTo(neighbor.getId());
                // s is now the seek state of neighbor
                dfsWalk(s, visited);
                if(s.distanceToRing() == 0){
                    return;
                }
                s.moveTo(current);
            }
        }
    }

    /** See {@code SewerDriver} for specification. */
    @Override
    public void scram(ScramState state) {
        // TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        // with a good specification, and call it from this one.
        basicScram(state);
    }
    /**
     * Helper method used by scram() that uses dijkstra's to walk McDiver to exit along the
     * shortest possible path.
     */
    public void basicScram(ScramState s){
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
        Node start = s.currentNode();
        Node exit = s.exit();
        ssp.singleSourceDistances(start);
        List<Edge> bestPath = ssp.bestPath(exit);
        for(Edge e: bestPath){
            s.moveTo(e.destination());
        }
    }


}
