package diver;

import datastructures.Heap;
import datastructures.PQueue;
import datastructures.SlowPQueue;
import game.*;
import graph.ShortestPaths;
import graph.WeightedDigraph;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
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
        //System.out.println("Current: " + current);
        //System.out.println("Size: " + sortedNode.size());
        for(NodeStatus n: sortedNode){
            //System.out.println(n.getId() + " " + n.getDistanceToRing());
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
        //basicScram(state);
        //maxScramGreedy(state);
        aPath(state);
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

    public void maxScram(ScramState s){
        // Uses bfs to search all possible paths from starting node any node in graph
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node exit = s.exit();
        /**
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node start = s.currentNode();
        Node exit = s.exit();
        Queue<Node> frontier = new LinkedList<>();
        Map<Node, List<Edge>> visited = new HashMap<>();
        Heap<List<Edge>> bestPaths = new Heap<>(false);
        frontier.add(start);
        visited.put(frontier.peek(), new ArrayList<>());
        while(!frontier.isEmpty()){
            Node curr = frontier.poll();
            int currVal = curr.getTile().coins();
            List<Edge> currPath = visited.get(curr);
            for(Node neighbor: curr.getNeighbors()){
                List<Edge> neighPath = new ArrayList<>(currPath);
                int nVal = currVal + neighbor.getTile().coins();
                neighPath.add(curr.getEdge(neighbor));
                if(!visited.containsKey(neighbor)){
                    visited.put(neighbor, neighPath);
                    frontier.add(neighbor);
                    bestPaths.add(neighPath, nVal);
                } else{
                    if(nVal > bestPaths.peekAtPriority() ){
                        bestPaths.changePriority(neighPath, nVal);
                    }
                }
            }
        }

        List<Edge> greatest = bestPaths.extractMin();
        System.out.println("size" + greatest.size());
        */
        while(true) {
            List<Edge> greatest = findBestPath(s);
            for (Edge e : greatest) {
                ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
                ssp.singleSourceDistances(e.source());
                if ((double) s.stepsToGo() == ssp.getDistance(exit)) {
                    System.out.println("in if");
                    List<Edge> bestPath = ssp.bestPath(exit);
                    for (Edge edge : bestPath) {
                        s.moveTo(edge.destination());
                    }
                    return;
                }
                s.moveTo(e.destination());
            }
        }
    }

    public List<Edge> findBestPath(ScramState s){
        Node start = s.currentNode();
        Queue<Node> frontier = new LinkedList<>();
        Map<Node, List<Edge>> visited = new HashMap<>();
        Heap<List<Edge>> bestPaths = new Heap<>(false);
        frontier.add(start);
        visited.put(frontier.peek(), new ArrayList<>());
        while(!frontier.isEmpty()){
            Node curr = frontier.poll();
            int currVal = curr.getTile().coins();
            List<Edge> currPath = visited.get(curr);
            for(Node neighbor: curr.getNeighbors()){
                List<Edge> neighPath = new ArrayList<>(currPath);
                int nVal = currVal + neighbor.getTile().coins();
                neighPath.add(curr.getEdge(neighbor));
                if(!visited.containsKey(neighbor)){
                    visited.put(neighbor, neighPath);
                    frontier.add(neighbor);
                    bestPaths.add(neighPath, nVal);
                } else{
                    if(nVal > bestPaths.peekAtPriority() ){
                        bestPaths.changePriority(neighPath, nVal);
                    }
                }
            }
        } return bestPaths.extractMin();
    }

    public  PQueue<List<Edge>> pathToCoin(ScramState s){
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node start = s.currentNode();
        PQueue<List<Edge>> pQueue = new Heap<>(false);
        for(Node n: s.allNodes()){
            if(n.getTile().coins() > 0){
                ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
                ssp.singleSourceDistances(start);
                //ssp.getDistance(n)
                pQueue.add(ssp.bestPath(n), (n.getTile().coins()/ ssp.getDistance(n)));
            }
        }
        return pQueue;
    }

    public void maxScramGreedy(ScramState s){
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node exit = s.exit();
        while(true){
            List<Edge> bestPathToCoin = pathToCoin(s).extractMin();
            for(Edge e: bestPathToCoin){
                ShortestPaths<Node, Edge> sspCurr = new ShortestPaths<>(graph);
                sspCurr.singleSourceDistances(e.source());
                List<Edge> bestPathToExit = sspCurr.bestPath(exit);
                if ((double) s.stepsToGo() <= sspCurr.getDistance(exit) + e.length()*2){
                    for (Edge edge : bestPathToExit) {
                        s.moveTo(edge.destination());
                    }
                    return;
                }
                s.moveTo(e.destination());
            }
        }
    }
    public static void aPath(ScramState s){
        // Initialize a map for 0(1) lookup of cost of each node
        Map<Node, Integer> aMap = new HashMap<>();

        // Initialize each node as key and its cost as the value
        for(Node node: s.allNodes()){
            aMap.put(node, nodeCost(s.currentNode(),s.exit(),node));
        }

        Stack<Node> discovered = new Stack<>();

        boolean isDone = false;
        while(!isDone){
            Node moveNode = s.currentNode();
            discovered.push(moveNode);
            int min = 0;
            // Look through list of available neighbors and compare their cost
            // picks the node with the lowest cost and not in discovered list
            for(Node node: s.currentNode().getNeighbors()){
                if(min == 0){
                    min = aMap.get(node);
                    moveNode = node;}
                if(aMap.get(node) < min && (!node.equals((discovered.peek())))){
                    System.out.println(discovered);
                    moveNode = node;
                    System.out.println(moveNode);
                }
            }
            s.moveTo(moveNode);
            if(s.currentNode().getTile().type() == Tile.TileType.RING){
                isDone = true;
            }
        }
    }

    public static Integer nodeCost(Node start, Node exit, Node currNode){
        // Find manhattan distance from start to node
        int gCost = Math.abs((start.getTile().row() - currNode.getTile().row()) +
                (start.getTile().column() - currNode.getTile().column()));
        // Find manhattan distance from exit to node
        int hCost = Math.abs((exit.getTile().row() - currNode.getTile().row()) +
                (exit.getTile().column() - currNode.getTile().column()));

        return gCost + hCost;
    }



}
