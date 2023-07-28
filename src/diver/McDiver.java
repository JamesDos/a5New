package diver;

import datastructures.Heap;
import datastructures.PQueue;
import datastructures.SlowPQueue;
import game.*;
import graph.ShortestPaths;
import graph.WeightedDigraph;
import java.nio.file.NotDirectoryException;
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


/**
 * This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {

    /**
     * See {@code SewerDriver} for specification.
     */
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
     * McDiver is standing on node 'current' given by state 's' Method ends with McDiver standing on
     * node current; Ends on ring if current == ring Requires current is unvisited
     */

    private static void dfsWalk(SeekState s, HashSet<Long> visited) {
        if (s.distanceToRing() == 0) {
            return;
        }
        long current = s.currentLocation();
        visited.add(current);
        // Create list of pointers for neighbors
        List<NodeStatus> sortedNode = new ArrayList<>(s.neighbors());
        Collections.sort(sortedNode);
        for (NodeStatus neighbor : sortedNode) {
            if (!visited.contains(neighbor.getId())) {
                s.moveTo(neighbor.getId());
                // s is now the seek state of neighbor
                dfsWalk(s, visited);
                if (s.distanceToRing() == 0) {
                    return;
                }
                s.moveTo(current);
            }
        }
    }

    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void scram(ScramState state) {
        // TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        // with a good specification, and call it from this one.
        //vanillaScram(state);
        maxScram(state);
        //aPath(state);
        //optimizedScram(state);
        //optimizedScram2(state, new HashSet<>());

    }

    /**
     * Helper method used by scram() that uses dijkstra's to walk McDiver to exit along the shortest
     * possible path. Most basic version of scram; does not take into consideration coin's values or
     * distances
     */
    private void vanillaScram(ScramState s) {
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
        Node start = s.currentNode();
        Node exit = s.exit();
        ssp.singleSourceDistances(start);
        List<Edge> bestPath = ssp.bestPath(exit);
        for (Edge e : bestPath) {
            s.moveTo(e.destination());
        }
    }

    /**
     * Helper method used by maxScramGreedy() that is an optimized version of basicScram() Returns a
     * priority queue of a list of edges representing the best paths from McDiver's current location
     * to any coin in the Maze. Path priorities are calculated based off of the value of the coin
     * divided by the distance McDiver would have to travel in order to collect the coin.
     */

    private PQueue<List<Edge>> pathToCoin(ScramState s) {
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node start = s.currentNode();
        Heap<List<Edge>> pQueue = new Heap<>(false);
        //PQueue<List<Edge>> pQueue = new SlowPQueue<>();
        ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances(start);
        for (Node n : s.allNodes()) {
            if (n.getTile().coins() > 0) {
                //ssp.getDistance(n)
                pQueue.add(ssp.bestPath(n), (n.getTile().coins() / ssp.getDistance(n)));
            }
        }
        return pQueue;
    }

    /**
     * Helper method used by scram() that walks McDiver from the start, around the maze to collect
     * coins, and finally to the exit. This method calls pathToCoin() to find the best path from
     * McDiver's current location to the coin with the highest priority and walks McDiver along that
     * path. If McDiver is about to run out of steps, McDiver will take the shortest path from his
     * current location to the exit.
     */

    private void maxScram(ScramState s) {
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node exit = s.exit();
        while (true) {
            // If maze has no coins, McDiver takes the shortest path to the exit
            if (pathToCoin(s).size() == 0) {
                ShortestPaths<Node, Edge> sspStart = new ShortestPaths<>(graph);
                sspStart.singleSourceDistances(s.currentNode());
                List<Edge> bestPathToExit = sspStart.bestPath(exit);
                for (Edge edge : bestPathToExit) {
                    s.moveTo(edge.destination());
                }
                return;
            }
            // If maze has coins, McDiver goes to the coin with the highest priority in each
            // iteration. If McDiver is about to run out of steps, McDiver takes the shortest path
            // from current location to the exit.
            List<Edge> bestPathToCoin = pathToCoin(s).extractMin();
            for (Edge e : bestPathToCoin) {
                ShortestPaths<Node, Edge> sspCurr = new ShortestPaths<>(graph);
                sspCurr.singleSourceDistances(e.source());
                List<Edge> bestPathToExit = sspCurr.bestPath(exit);
                // e.length()*2 guarantees that McDiver will have enough steps to the exit
                // since McDiver has to double back (go to the tile with the coin and then back
                // his original tile) if he were to collect the coin and go to the exit
                if ((double) s.stepsToGo() <= sspCurr.getDistance(exit) + e.length() * 2) {
                    for (Edge edge : bestPathToExit) {
                        s.moveTo(edge.destination());
                    }
                    return;
                }
                s.moveTo(e.destination());
            }
        }
    }

    private static void aPath(ScramState s) {
        // Initialize a map for 0(1) lookup of cost of each node
        Map<Node, Integer> aMap = new HashMap<>();

        // Initialize each node as key and its cost as the value
        for (Node node : s.allNodes()) {
            aMap.put(node, nodeCost(s.currentNode(), s.exit(), node));
        }

        Stack<Node> discovered = new Stack<>();

        boolean isDone = false;
        while (!isDone) {
            Node moveNode = s.currentNode();
            discovered.push(moveNode);
            int min = 0;
            // Look through list of available neighbors and compare their cost
            // picks the node with the lowest cost and not in discovered list
            for (Node node : s.currentNode().getNeighbors()) {
                if (min == 0) {
                    min = aMap.get(node);
                    moveNode = node;
                }
                if (aMap.get(node) < min && (!node.equals((discovered.peek())))) {
                    System.out.println(discovered);
                    moveNode = node;
                    System.out.println(moveNode);
                }
            }
            s.moveTo(moveNode);
            if (s.currentNode().getTile().type() == Tile.TileType.RING) {
                isDone = true;
            }
        }
    }

    private static Integer nodeCost(Node start, Node exit, Node currNode) {
        // Find manhattan distance from start to node
        int gCost = Math.abs((start.getTile().row() - currNode.getTile().row()) +
                (start.getTile().column() - currNode.getTile().column()));
        // Find manhattan distance from exit to node
        int hCost = Math.abs((exit.getTile().row() - currNode.getTile().row()) +
                (exit.getTile().column() - currNode.getTile().column()));

        return gCost + hCost;
    }

    /**
     * A version of scram optimized to for large mazes (100x100) with a coin density of 0.99
     * optimizedScram() should run noticeably faster than maxScram() on such mazes while scoring
     * more than vanillaScram() optimizedScram() walks McDiver randomly around the maze using an
     * iterative dfs (implemented using a stack) until he is about to run out of steps. If McDiver
     * is about to run out of steps, he takes the shortest path from his current location to the
     * exit; this path is calculated each time McDiver takes a step.
     */
    private void optimizedScram(ScramState s) {
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node start = s.currentNode();
        Node exit = s.exit();
        Set<Node> visited = new HashSet<>();
        Stack<Node> stk = new Stack<>();
        stk.push(start);
        visited.add(start);
        while (!stk.isEmpty()) {
            ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
            ssp.singleSourceDistances(s.currentNode());
            List<Edge> pathToExit = ssp.bestPath(exit);
            Node curr = stk.peek();
            boolean found = false;
            for (Node neighbor : curr.getNeighbors()) {
                if (s.stepsToGo() <= ssp.getDistance(exit) + curr.getEdge(neighbor).length() * 2) {
                    for (Edge e : pathToExit) {
                        s.moveTo(e.destination());
                    }
                    return;
                }
                if (!visited.contains(neighbor)) {
                    stk.push(neighbor);
                    visited.add(neighbor);
                    found = true;
                    s.moveTo(stk.peek());
                    break;
                }
            }
            if (!found) {
                stk.pop();
                s.moveTo(stk.peek());
            }
        }
    }

    /**
     * A second version of optimizedScram() that has the same behavior as optimizedScram() but uses
     * a recursive dfs to walk McDiver around the maze rather than an iterative dfs NOTE,
     * optimizedScram() was used to report final times in the handout since optimizedScram2() threw
     * a stack overflow error for larger mazes.
     */

    private void optimizedScram2(ScramState s, HashSet<Node> visited) {
        Set<Node> nodeSet = new HashSet<>(s.allNodes());
        Maze graph = new Maze(nodeSet);
        Node exit = s.exit();
        Node curr = s.currentNode();
        ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances(curr);
        List<Edge> pathToExit = ssp.bestPath(exit);
        visited.add(curr);
        for (Node neighbor : curr.getNeighbors()) {
            if (s.stepsToGo() <= ssp.getDistance(exit) + curr.getEdge(neighbor).length() * 2) {
                for (Edge e : pathToExit) {
                    s.moveTo(e.destination());
                }
                return;
            }
            if (!visited.contains(neighbor)) {
                s.moveTo(neighbor);
                optimizedScram2(s, visited);
                if (s.currentNode().equals(exit)) {
                    return;
                }
                s.moveTo(curr);
            }
        }
    }

}
