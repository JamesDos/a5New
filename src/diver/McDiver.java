package diver;

import game.*;
import java.util.HashSet;


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
        dfsWalk(state);
    }

    /**
     * Helper method used by seek() that uses a dfs to walk McDiver to the ring (if possible)
     * McDiver is standing on node 'current' given by state 's'
     * Method ends with McDiver standing on node current; Ends on ring if current == ring
     * Requires current is unvisited
     */

    public static void dfsWalk(SeekState s){
        if(s.distanceToRing() == 0) return;

        HashSet<Long> visited = new HashSet<>();
        long current = s.currentLocation();
        visited.add(current);
        for (NodeStatus neighbor : s.neighbors()) {
            if (!visited.contains(neighbor.getId())) {
                s.moveTo(neighbor.getId());
                // s is now the seek state of neighbor
                dfsWalk(s);
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
    }

}
