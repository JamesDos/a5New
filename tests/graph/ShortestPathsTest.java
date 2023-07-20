package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ShortestPathsTest {
    /** The graph example from Prof. Myers's notes. There are 7 vertices labeled a-g, as
     *  described by vertices1. 
     *  Edges are specified by edges1 as triples of the form {src, dest, weight}
     *  where src and dest are the indices of the source and destination
     *  vertices in vertices1. For example, there is an edge from a to d with
     *  weight 15.
     */
    static final String[] vertices1 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges1 = {
        {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
        {1, 4, 23},
        {2, 4, 17}, {2, 3, 5}, {2, 5, 30},
        {3, 5, 20}, {3, 6, 37},
        {4, 5, 3}, {4, 6, 20},
        {5, 6, 16}
    };
    static final String[] vertices2 = { "a", "b", "c", "d", "e"};
    static final int[][] edges2 = {
            {0, 1, 1}, {0, 4, 2},
            {1, 2, 1},
            {2, 3, 1},
            {3, 4, 1}
    };
/*               D
*              /   \
*       A --- B --- C
*              \   /
*                E
* directional cyclic graph i.e  B -> C -> D -> B
* best path is A -> B - E
* */
    static final String[] vertices3 = {"a", "b", "c", "d", "e"};
    static final int[][] edges3 = {
            {0,1,1},
            {1,2,1},{1,4,2},
            {2,3,1},{2,4,2},
            {3,1,1}
    };

    // Undirected graph from lecture
    static final String[] vertices4 = {"a", "s", "b", "c", "d"};
    static final int[][] edges4 = {
            {0,1,1}, {0,2,7}, {0,3,6},
            {1,0,1}, {1,2,2},
            {2,0,7}, {2,1,2}, {2,3,4}, {2,4,1},
            {3,0,6}, {3,2,4}, {3,4,1},
            {4,2,1},{4,3,1}
    };


    static class TestGraph implements WeightedDigraph<String, int[]> {
        int[][] edges;
        String[] vertices;
        Map<String, Set<int[]>> outgoing;

        TestGraph(String[] vertices, int[][] edges) {
            this.vertices = vertices;
            this.edges = edges;
            this.outgoing = new HashMap<>();
            for (String v : vertices) {
                outgoing.put(v, new HashSet<>());
            }
            for (int[] edge : edges) {
                outgoing.get(vertices[edge[0]]).add(edge);
            }
        }
        public Iterable<int[]> outgoingEdges(String vertex) { return outgoing.get(vertex); }
        public String source(int[] edge) { return vertices[edge[0]]; }
        public String dest(int[] edge) { return vertices[edge[1]]; }
        public double weight(int[] edge) { return edge[2]; }
    }
    static TestGraph testGraph1() {
        return new TestGraph(vertices1, edges1);
    }
    static TestGraph testGraph2() {
        return new TestGraph(vertices2, edges2);
    }
    static TestGraph testGraph3() {
        return new TestGraph(vertices3, edges3);
    }
    static TestGraph testGraph4() {
        return new TestGraph(vertices4, edges4);
    }

    @Test
    void lectureNotesTest() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(50, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a c e f g", sb.toString());
    }

    // TODO: Add 2 more tests

    @Test
    // Shortest path from 'a' to 'e' should be along shortest path from 'a' to 'g'
    void lectureNotesTest2() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(31, ssp.getDistance("e"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("e")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" e");
        assertEquals("best path: a c e", sb.toString());
    }

    // Greedy method shouldn't fail
    @ Test
    void graph2Test(){
        TestGraph graph = testGraph2();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(2, ssp.getDistance("e"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("e")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" e");
        assertEquals("best path: a e", sb.toString());
    }
    @Test
    void graph3Test(){
        TestGraph graph = testGraph3();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(3, ssp.getDistance("e"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("e")) {
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" e");
        assertEquals("best path: a b e", sb.toString());
    }

    @ Test
    void graph4Test(){
        TestGraph graph = testGraph4();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(3, ssp.getDistance("b"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("b")) {
            sb.append(" " + vertices4[e[0]]);
        }
        sb.append(" b");
        assertEquals("best path: a s b", sb.toString());
    }
}
