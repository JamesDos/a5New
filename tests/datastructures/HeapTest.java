package datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import datastructures.PQueue;
import datastructures.SlowPQueue;
import org.junit.jupiter.api.Test;

public class HeapTest {

    @Test
    void reversed() {
        PQueue<Integer> q = new Heap<>(true);
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        for (int i = 10; i >= 0; i--) {
            q.add(i, i);
        }
        assertEquals(11, q.size());
        for (int i = 0; i <= 10; i++) {
            int k = q.peek();
            int j = q.extractMin();
            assertEquals(i, j, k);
        }
        assertTrue(q.isEmpty());
    }

    @Test
    void inorder() {
        PQueue<Integer> q = new Heap<>(true);
        assertTrue(q.isEmpty());
        for (int i = 0; i < 10; i++) {
            q.add(i, i);
        }
        assertEquals(10, q.size());
        for (int i = 0; i < 10; i++) {
            int k = q.peek();
            int j = q.extractMin();
            assertEquals(i, j, k);
        }
        assertTrue(q.isEmpty());
    }

    @Test
    void throwTest() {
        PQueue<Integer> q = new Heap<>(true);
        q.add(1, 1);
        assertThrows(IllegalArgumentException.class, () -> q.add(1, 2));
    }

    // TODO: Add your own test cases here. Make sure to test your code with good
    // coverage, and with both in min and max modes. Some example test cases have been
    // provided for you.
    @Test
    void todo1to8Test() {
        // Test multiple resizes on minHeap and maxHeap (TODO 1)
        Heap<Integer> minHeap = new Heap<>(true);
        Heap<Integer> maxHeap = new Heap<>(false);
        for (int i = 0; i < 21; i++) {
            minHeap.add(i, i);
            maxHeap.add(i, i);
        }
        assertEquals(21, minHeap.size());
        assertEquals(21, maxHeap.size());

        //Testing add (TODO 2-4)
        // Tests if priority (order invariant) is kept after adding a pair
        minHeap = new Heap<>(true);
        maxHeap = new Heap<>(false);
        for (int i = 1; i < 24; i += 2) {
            minHeap.add(i, i);
            maxHeap.add(i, i);
        }
        // bubble up from leaf to root for minHeap; add to end of list for maxHeap
        assertEquals(1, minHeap.peek());
        assertEquals(23, maxHeap.peek());
        minHeap.add(0, 0);
        maxHeap.add(0, 0);
        assertEquals(0, minHeap.peek());
        assertEquals(23, maxHeap.peek());
        // bubble up from leaf to root for maxHeap; add to end of list for maxHeap
        minHeap.add(25, 25);
        maxHeap.add(25, 25);
        assertEquals(0, minHeap.peek());
        assertEquals(25, maxHeap.peek());

        // Testing extractMin (TODO 6-7)
        // extractMin should return root node and maintain heap order invariant for both heaps
        assertEquals(0, minHeap.extractMin());
        assertEquals(1, minHeap.peek());
        assertEquals(25, maxHeap.extractMin());
        assertEquals(23, maxHeap.peek());

        // Testing changPriority (TODO 8)
        // changing priority should cause bubbling up/down if needed
        // Bubbling up due to priority change
        maxHeap.changePriority(11, 50.0);
        assertEquals(11, maxHeap.peek());
        assertEquals(50.0, maxHeap.peekAtPriority());
        minHeap.changePriority(19, 0.0);
        assertEquals(19, minHeap.peek());
        assertEquals(19, minHeap.peek());
        // Bubbling down due to priority change
        maxHeap.changePriority(11, 11.0);
        assertEquals(23, maxHeap.peek());
        assertEquals(23.0, maxHeap.peekAtPriority());
        minHeap.changePriority(19, 19.0);
        assertEquals(1, minHeap.peek());
        assertEquals(1.0, minHeap.peekAtPriority());
        // No bubbling due to priority change
        maxHeap.changePriority(21, 22.0);
        assertEquals(23, maxHeap.peek());
        minHeap.changePriority(3, 2.0);
        assertEquals(1, minHeap.peek());

        // Changing priority of a node to be the same as priority of another node
        // Node with lower priority now has priority of parent
        String beforeMax = maxHeap.toStringValues();
        String beforeMin = minHeap.toStringValues();
        maxHeap.changePriority(17, 19.0);
        assertEquals(beforeMax, maxHeap.toStringValues());
        minHeap.changePriority(11, 5.0);
        assertEquals(beforeMin, minHeap.toStringValues());
        // Node with higher priority now has priority of higher priority child; No bubbling
        maxHeap.changePriority(13, 7.0);
        assertEquals(beforeMax, maxHeap.toStringValues());
        minHeap.changePriority(7, 15.0);
        assertEquals(beforeMin, minHeap.toStringValues());
        // Node with higher priority now has priority of lower priority child; Other child should
        // bubble up
        maxHeap.changePriority(21, 9.0);
        assertEquals("[23, 19, 11, 13, 17, 21, 9, 1, 7, 5, 15, 3, 0]", maxHeap.toStringValues());
        minHeap.changePriority(9, 21.0);
        assertEquals("[1, 3, 5, 7, 19, 11, 13, 15, 17, 9, 21, 23, 25]", minHeap.toStringValues());

        // Edge cases
        //Adding / Removing from heaps of size 0 and 1
        minHeap = new Heap<>(true);
        maxHeap = new Heap<>(false);
        minHeap.add(2, 2.0);
        maxHeap.add(2, 2.0);
        minHeap.extractMin();
        maxHeap.extractMin();
        minHeap.add(2, 2.0);
        maxHeap.add(2, 2.0);
        // Adding causes change in priority for heaps of size 1 (bubbling up);
        minHeap.add(1, 1.0);
        maxHeap.add(3, 3.0);
        assertEquals(1, minHeap.peek());
        assertEquals(3, maxHeap.peek());
        // Removing root node causes the leaf node to be new root for heap of size 2 (bubbling down)
        minHeap.extractMin();
        maxHeap.extractMin();
        assertEquals(2, minHeap.peek());
        assertEquals(2, maxHeap.peek());
        // Changing priority for heaps of size 1 (shouldn't cause bubbling up or down)
        minHeap.changePriority(2, 1);
        maxHeap.changePriority(2, 3);
        assertEquals(1, minHeap.peekAtPriority());
        assertEquals(3, maxHeap.peekAtPriority());
        // Changing priority to negative values
        minHeap.changePriority(2, -2.0);
        maxHeap.changePriority(2, -2.0);
        // Bubbling up/down should work with negative priorities
        minHeap.add(-3, -3.0);
        maxHeap.add(-1, -1.0);
        assertEquals(-3, minHeap.extractMin());
        assertEquals(-1, maxHeap.extractMin());
        assertEquals(2, minHeap.peek());
        assertEquals(2, maxHeap.peek());
    }
}