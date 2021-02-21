package byow.Core;

import java.util.*;

public class GraphTraversal {
    private TGraph graph;
    private HashMap<Point, Point> edgeTo = new HashMap<>();
    private LinkedList<Point> queue = new LinkedList<>();

    /**
     * uses Breath First Search to find shortest path
     * @return a linked list of the shortest path starting from startPt to goal
     */
    public LinkedList<Point> bfs(TGraph tGraph, Point startPt, Point goal) {
        this.graph = tGraph;
        Point cur = startPt;
        edgeTo.put(cur, null);
        queue.addLast(cur);

        while (!queue.isEmpty() && !edgeTo.containsKey(goal)) {
            cur = queue.removeFirst();
            if (graph.neighbors(cur) == null) {
                continue;
            }
            for (Point p : graph.neighbors(cur)) {
                if (!edgeTo.containsKey(p)) {
                    queue.addLast(p);
                    edgeTo.put(p, cur);
                }
            }
        }

        if (!edgeTo.containsKey(goal)) {
            return null;
        } else {
            LinkedList<Point> result = new LinkedList<>();
            result.addFirst(goal);
            Point prev = edgeTo.get(goal);
            while (prev != null) {
                result.addFirst(prev);
                prev = edgeTo.get(prev);
            }
            return result;
        }
    }
}
