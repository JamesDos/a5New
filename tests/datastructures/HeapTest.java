package datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import datastructures.PQueue;
import datastructures.SlowPQueue;
import org.junit.jupiter.api.Test;

public class HeapTest {
    @Test void reversed() {
        PQueue<Integer> q = new Heap<>(true);
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        for (int i = 10; i >= 0; i--) q.add(i, i);
        assertEquals(11, q.size());
        for (int i = 0; i <= 10; i++) {
            int k = q.peek();
            int j = q.extractMin();
            assertEquals(i, j, k);
        }
        assertTrue(q.isEmpty());
    }
    @Test void inorder() {
        PQueue<Integer> q = new Heap<>(true);
        assertTrue(q.isEmpty());
        for (int i = 0; i < 10; i++) q.add(i, i);
        assertEquals(10, q.size());
        for (int i = 0; i < 10; i++) {
            int k = q.peek();
            int j = q.extractMin();
            assertEquals(i, j, k);
        }
        assertTrue(q.isEmpty());
    }
    @Test void throwTest() {
        PQueue<Integer> q = new Heap<>(true);
        q.add(1,1);
        assertThrows(IllegalArgumentException.class, () -> q.add(1,2));
    }

    // TODO: Add your own test cases here. Make sure to test your code with good
    // coverage, and with both in min and max modes. Some example test cases have been
    // provided for you.
    @ Test void todo1to8Test(){
        // Test multiple resizes on minHeap and maxHeap
        PQueue<Integer> minHeap = new Heap<>(true);
        PQueue<Integer> maxHeap = new Heap<>(false);
        for (int i = 0; i < 21; i++){
            minHeap.add(i, i);
            maxHeap.add(i, i);
        }
        assertEquals(21, minHeap.size());
        assertEquals(21, maxHeap.size());

        // Tests if priority (order invariant) is kept after adding a pair
        minHeap = new Heap<>(true);
        maxHeap = new Heap<>(false);
        for (int i = 1; i < 24; i += 2){
            minHeap.add(i, i);
            maxHeap.add(i, i);
        }
        // bubble up from leaf to root for minHeap
        minHeap.add(0, 0);
        maxHeap.add(0, 0);
        assertEquals(0, minHeap.peek());
        assertEquals(23, maxHeap.peek());
        // bubble up from leaf to root for maxHeap
        minHeap.add(25, 25);
        maxHeap.add(25, 25);
        assertEquals(0, minHeap.peek());
        assertEquals(25, maxHeap.peek());
        // extractMin should return root node and maintain heap order invariant
        assertEquals(0, minHeap.extractMin());
        assertEquals(25, maxHeap.extractMin());
        // changing priority should cause bubbling up/down if needed
        maxHeap.changePriority(11, 50.0);
        assertEquals(11, maxHeap.extractMin());
        minHeap.changePriority(19, 0.0);
        assertEquals(19, minHeap.extractMin());





    }
}