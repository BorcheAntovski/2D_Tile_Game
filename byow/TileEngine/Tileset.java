package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you",
            "avatar.png");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", "wall.png");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor", "floor.png");
    public static final TETile PATHFLOOR = new TETile('·', Color.red, Color.black,
            "pathFloor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing",
            "nothing.png");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", "door.png");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.cyan, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile EXIT = new TETile('▲', Color.gray, Color.black, "exit",
            "secretDoor.png");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile APPLE = new TETile('\uF8FF', Color.magenta, Color.black, "apple",
            "apple.png" + "");
    public static final TETile GOLD = new TETile('★', Color.orange, Color.black, "gold",
            "gold.png");
    public static final TETile POISON = new TETile('!', Color.red, Color.black, "poison",
            "poison.png");
    public static final TETile KEY = new TETile('k', Color.blue, Color.black, "key");
    public static final TETile SECRETDOOR = new TETile('?', Color.white, Color.black, "secretDoor",
            "secretDoor.png");

}


