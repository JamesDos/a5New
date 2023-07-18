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
}