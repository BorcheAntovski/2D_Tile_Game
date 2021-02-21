package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class TGraph {
    HashMap<Point, LinkedList<Point>> adjList = new HashMap<>();
    HashSet<TETile> accessable = new HashSet<>();
    TETile[][] world;
    int WIDTH;
    int HEIGHT;

    public TGraph(TETile[][] world) {
        this.world = world;
        accessable.add(Tileset.FLOOR);
        accessable.add(Tileset.UNLOCKED_DOOR);
        accessable.add(Tileset.NOTHING);
        accessable.add(Tileset.AVATAR);
        accessable.add(Tileset.PATHFLOOR);
        WIDTH = world.length;
        HEIGHT = world[0].length;
        updateGraph();
    }

    /**
     * updates the graph based on the current world 2D array
     */
    public void updateGraph() {
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                if (accessable.contains(world[i][j]) && !world[i][j].equals(Tileset.NOTHING)) {
                    addFloorToGraph(i, j);
                }
            }
        }
    }

    /**
     * add a tile to the graph and update its neighbors.
     * (by the setup it is impossible for NOTHING to be adjacent to floor)
     * (so whenever the next is NOTHING, we must be at an unlocked door)
     */
    private void addFloorToGraph(int x, int y) {
        Point cur = new Point(x, y);
        if (adjList.containsKey(cur)) {
            return;
        }
        LinkedList<Point> neighbors = new LinkedList<>();
        adjList.put(cur, neighbors);

        for (Point curPoint : fourNeighbors(x, y)) {
            int curX = curPoint.getX();
            int curY = curPoint.getY();
            if (valid(curX, curY) && accessable.contains(world[curX][curY])) {
                neighbors.add(curPoint);
                if (world[curX][curY].equals(Tileset.UNLOCKED_DOOR)) {
                    addFloorToGraph(curX, curY);
                }
                if (world[curX][curY].equals(Tileset.NOTHING)) {
                    addFloorToGraph(curX, curY);
                }
            }
        }
    }

    /**
     * @param cur: the point whose neighbors are to be found
     * @return: null if no neighbors. Otherwise return a linked list of neighbor points
     */
    public LinkedList<Point> neighbors(Point cur) {
        return adjList.get(cur);
    }

    public LinkedList<Point> neighbors(int x, int y) {
        return neighbors(new Point(x, y));
    }

    /**
     * The following are some helper methods
     */
    private boolean valid(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    private LinkedList<Point> fourNeighbors(int x, int y) {
        LinkedList<Point> fourNeighbors = new LinkedList<>();
        fourNeighbors.add(new Point(x - 1, y));
        fourNeighbors.add(new Point(x + 1, y));
        fourNeighbors.add(new Point(x, y - 1));
        fourNeighbors.add(new Point(x, y + 1));
        return fourNeighbors;
    }
}
