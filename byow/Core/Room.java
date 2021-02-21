package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Room {
    protected Point bottomL;
    protected int horLength;
    protected int verLength;
    protected TETile[][] tileSet;
    protected TETile[][] world;

    protected void openWallAtPt(int x, int y) {
        tileSet[x][y] = Tileset.FLOOR;
        world[x + bottomL.getX()][y + bottomL.getY()] = Tileset.FLOOR;

    }

    public Point getBottomL() {
        return bottomL;
    }

    public int getSize() {
        return horLength * verLength;
    }

    public TETile[][] getTileSet() {
        return tileSet;
    }

    public static TETile[][] fillIn(int width, int height, Point startL) {
        TETile[][] floor = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                if (x == 0 || x + 1 == width || y == 0 || y + 1 == height) {
                    floor[x][y] = Tileset.WALL;
                } else {
                    floor[x][y] = Tileset.FLOOR;
                }
            }
        }
        return floor;
    }
}
