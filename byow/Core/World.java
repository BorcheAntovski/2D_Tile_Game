package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.*;

public class World {
    private int WIDTH;
    private int HEIGHT;
    private Random random;
    protected TETile[][] world;
    private int numerTilesFilled;
    private Avatar avatar;
    private TETile[][] withoutEmbel;

    public World(Random random) {
        WIDTH = 80;
        HEIGHT = 40;
        this.random = random;
        world = new TETile[WIDTH][HEIGHT];
        withoutEmbel = new TETile[WIDTH][HEIGHT];
        numerTilesFilled = 0;

        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Makes a random 2D world based on the member variable "random"
     *
     * @return A random 2D world
     */
    public TETile[][] generateWorld() {
        RectRoom firstRoom = (RectRoom) makeRandomRoom();
        LinkedList<RectRoom> rooms = new LinkedList<>();
        LinkedList<HallWay> halls = new LinkedList<>();
        LinkedList<Room> allRoomsMade = new LinkedList<>();
        allRoomsMade.add(firstRoom);
        rooms.add(firstRoom);
        while (!isTooFull()) {
            if (!rooms.isEmpty()) {
                RectRoom cur = rooms.removeFirst();
                if (cur.availableSides().size() == 0) {
                    continue;
                }
                int randNum = RandomUtils.uniform(random, 1, 4);
                for (HallWay hall : makeRandomHalls(cur, randNum)) {
                    halls.addLast(hall);
                }
            } else if (!halls.isEmpty()) {
                HallWay cur = halls.removeFirst();
                Room newRoom = null;
                int count = 0;
                while (newRoom == null && count < 20) {
                    newRoom = makeRandomRoom(cur);
                    count += 1;
                }
                if (newRoom != null) {
                    rooms.addLast((RectRoom) newRoom);
                    allRoomsMade.add(newRoom);
                }
            } else {
                Room prevMade = allRoomsMade.get(RandomUtils.uniform(random, allRoomsMade.size()));
                rooms.add((RectRoom) prevMade);
                for (HallWay hall : ((RectRoom) prevMade).getHallWays()) {
                    halls.addLast(hall);
                }
            }
        }
        updateWithoutEmbel();
        embellishWorld();
        return world;
    }

    private void updateWithoutEmbel() {
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                if (world[i][j].equals(Tileset.LOCKED_DOOR)) {
                    withoutEmbel[i][j] = Tileset.FLOOR;
                } else {
                    withoutEmbel[i][j] = world[i][j];
                }

            }
        }
    }

    public TETile[][] getWithoutEmbel() {
        return withoutEmbel;
    }

    /**
     * @return member variable "random"
     */
    public Random getRandom() {
        return random;
    }

    /**
     * @return whether the world is too full to add another room/hallway
     */
    boolean isTooFull() {
        return (double) numerTilesFilled / (WIDTH * HEIGHT) >= 0.45;
    }

    /**
     * Makes random parameters for a new room based on the given hallway
     *
     * @param hall : The hallway to whose closed end this function is to attach a new room
     *
     * @return A map of results as specified:
     * "height": height for the new room
     * "width": width for the new room
     * "point": bottom left corner for the new room
     */
    private HashMap<String, Object> randomRoomGenerator(HallWay hall) {
        HashMap<String, Object> parameters = new HashMap<>();
        int height = RandomUtils.uniform(random, 3, 9); // max 8 min 3
        int width = RandomUtils.uniform(random, 3, 9); // max 8 min 3

        int x;
        int y;
        if (hall == null) {
            x = RandomUtils.uniform(random, 8, 51);
            y = RandomUtils.uniform(random, 8, 31);
        } else {
            if (hall.vertical()) {
                if (hall.getSmallEnd() == null) {
                    y = hall.getBottomL().getY() - height;
                } else {
                    y = hall.getBottomL().getY() + hall.getLength();
                }
                if (width == 3) {
                    x = hall.getBottomL().getX();
                } else {
                    x = RandomUtils.uniform(random, hall.getBottomL().getX() - width + 3,
                            hall.getBottomL().getX());
                }
            } else {
                if (hall.getSmallEnd() == null) {
                    x = hall.getBottomL().getX() - width;
                } else {
                    x = hall.getBottomL().getX() + hall.getLength();
                }
                if (height == 3) {
                    y = hall.getBottomL().getY();
                } else {
                    y = RandomUtils.uniform(random, hall.getBottomL().getY() - height + 3,
                            hall.getBottomL().getY());

                }
            }
        }
        Point position = new Point(x, y);
        parameters.put("height", height);
        parameters.put("width", width);
        parameters.put("point", position);
        return parameters;
    }

    /**
     * Makes the first random room for the 2D world
     *
     * @return A map of results as specified:
     * "height": height of the newly generated room
     * "width": width of the newly generated room
     * "point": bottom left corner of the newly generated room
     */
    private Room makeRandomRoom() {
        HashMap<String, Object> parameters = randomRoomGenerator(null);
        int width = (int) parameters.get("width");
        int height = (int) parameters.get("height");
        Point point = (Point) parameters.get("point");
        while (!canBeAdded(width, height, world, point)) {
            parameters = randomRoomGenerator(null);
            width = (int) parameters.get("width");
            height = (int) parameters.get("height");
            point = (Point) parameters.get("point");
        }
        Room newRoom = new RectRoom(point, width, height, world, this);
        addToWorld(newRoom.getTileSet(), world, newRoom.getBottomL());
        return  newRoom;
    }

    /**
     * Makes a new room based on the given hallway
     *
     * @param hall : The hallway to whose closed end this function is to attach a new room
     *
     * @return a new random room if the random generation was successful. Otherwise null
     */
    private Room makeRandomRoom(HallWay hall) {
        RectRoom newRoom;
        HashMap<String, Object> parameters = randomRoomGenerator(hall);
        int width = (int) parameters.get("width");
        int height = (int) parameters.get("height");
        Point point = (Point) parameters.get("point");
        int count = 0;
        while (!canBeAdded(width, height, world, point) && count < 20) {
            parameters = randomRoomGenerator(hall);
            width = (int) parameters.get("width");
            height = (int) parameters.get("height");
            point = (Point) parameters.get("point");
            count += 1;
        }
        if (count == 20) {
            newRoom = null;
        } else {
            newRoom = new RectRoom(point, width, height, world, this);
            hall.addRoom(newRoom);
            hall.openToRoom(newRoom);
            newRoom.connectToHallway(hall);
            addToWorld(newRoom.getTileSet(), world, newRoom.getBottomL());
        }
        return newRoom;

    }

    /**
     * Makes a new hallway based on the given room
     *
     * @param room : The room based on whose available side this function is to make a new hallway
     *
     * @return The new random hallway. Null if the generation failed
     */
    private HallWay makeRandomHall(RectRoom room) {
        if (room.availableSides().size() == 0) {
            return null;
        }

        HashMap randomAvailablePt = room.randomAvailablePt();
        HallWay newHall;
        boolean vertical = (boolean) randomAvailablePt.get("vertical");
        Point openPt = (Point) randomAvailablePt.get("point");
        String side = (String) randomAvailablePt.get("side");
        Point bottomL = null;
        int ver;
        int hor;

        if (vertical) {
            hor = 3;
            ver = RandomUtils.uniform(random, 3, 5);
            if (side.equals("u")) {
                bottomL = new Point(openPt.getX() - 1, openPt.getY() + 1);
            } else if (side.equals("d")) {
                bottomL = new Point(openPt.getX() - 1, openPt.getY() - ver);
            }
        } else {
            ver = 3;
            hor = RandomUtils.uniform(random, 3, 5);
            if (side.equals("l")) {
                bottomL = new Point(openPt.getX() - hor, openPt.getY() - 1);
            } else if (side.equals("r")) {
                bottomL = new Point(openPt.getX() + 1, openPt.getY() - 1);
            }
        }

        if (canBeAdded(hor, ver, world, bottomL)) {
            if (side.equals("u") || side.equals("r")) {
                newHall = new HallWay(room, null, bottomL, hor, ver, vertical, world);
            } else {
                newHall = new HallWay(null, room, bottomL, hor, ver, vertical, world);
            }
            room.connectToHallway(newHall);
            addToWorld(newHall.getTileSet(), world, newHall.getBottomL());
            return newHall;
        } else {
            return null;
        }
    }

    /**
     * Makes a certain number of hallway based on the given room
     *
     * @param room : The room based on whose available side this function is to make new hallways
     * @param numHalls: The number of new hallways to make for the room
     *
     * @return A list of generated random hallway
     */
    private LinkedList<HallWay> makeRandomHalls(RectRoom room, int numHalls) {
        LinkedList<HallWay> halls = new LinkedList<>();
        for (int i = 0; i < numHalls; i += 1) {
            HallWay newHall = null;
            int count = 0;
            while (newHall == null && count <= 20) {
                newHall = makeRandomHall(room);
                count += 1;
            }
            if (newHall != null) {
                halls.addLast(newHall);
            }
        }
        return halls;
    }

    /**
     * Checks if a set of tiles with given location fits the 2D world (i.e., does not overlap)
     *
     * @param newSet : The set of tiles pending to be added to the 2D world
     * @param thisWorld: The 2D world that holds tiles
     * @param bottomL: the bottom left corner of the set of tiles to be added
     *
     * @return True if the set fits in the world. False otherwise.
     */
    private boolean canBeAdded(TETile[][] newSet, TETile[][] thisWorld, Point bottomL) {
        int width = newSet.length;
        int height = newSet[0].length;
        return canBeAdded(width, height, thisWorld, bottomL);
    }

    /**
     * Checks if a particular square space with given location fits the 2D world
     * (i.e., does not overlap)
     *
     * @param width : The width of a square space pending to be added to the 2D world
     * @param height : The height of a square space pending to be added to the 2D world
     * @param thisWorld: The 2D world that holds tiles
     * @param bottomL: the bottom left corner of the set of tiles to be added
     *
     * @return True if the square space fits in the world. False otherwise.
     */
    private boolean canBeAdded(int width, int height, TETile[][] thisWorld, Point bottomL) {
        HashSet<TETile> emptySpace = new HashSet<>();
        emptySpace.add(Tileset.NOTHING);
        emptySpace.add(Tileset.SAND);
        int bX = bottomL.getX();
        int bY = bottomL.getY();
        if (bX < 0 || bY < 0) {
            return false;
        }
        if (bX + width >= thisWorld.length || bY + height >= thisWorld[0].length) {
            return false;
        }
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                if (!emptySpace.contains(thisWorld[x + bX][y + bY])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Add the set of tiles with given location to the 2D world, assuming that it
     *      fits the 2D world (i.e., does not overlap)
     *
     * @param newSet : The set of tiles pending to be added to the 2D world
     * @param thisWorld: The 2D world that holds tiles
     * @param bottomL: the bottom left corner of the set of tiles to be added
     */
    public void addToWorld(TETile[][] newSet, TETile[][] thisWorld, Point bottomL) {
        int width = newSet.length;
        int height = newSet[0].length;
        int bX = bottomL.getX();
        int bY = bottomL.getY();
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                thisWorld[x + bX][y + bY] = newSet[x][y];
                numerTilesFilled += 1;
            }
        }
    }

    /**
     * adds random objects to the world (apples, keys, doors, secret doors)
     * also makes a main avatar for the world
     * (to be called as the last step of world generation)
     */
    public void embellishWorld() {
        HashSet<TETile> accessable = new HashSet<>();
        accessable.add(Tileset.FLOOR);
        accessable.add(Tileset.NOTHING);
        int x;
        int y;
        int numSecretDoor = 15;
        int numUnlockedDoor = 18;
        int numKeys = 10;
        int keyCount = 0;
        int doorCount = 0;
        int secretDoorCount = 0;
        boolean avatarExists = false;
        while (keyCount < numKeys || secretDoorCount < numSecretDoor
                || doorCount < numUnlockedDoor || !avatarExists) {
            x = RandomUtils.uniform(random, WIDTH);
            y = RandomUtils.uniform(random, HEIGHT);
            if (world[x][y].equals(Tileset.FLOOR) && keyCount < numKeys) {
                world[x][y] = Tileset.KEY;
                keyCount += 1;
            } else if (world[x][y].equals(Tileset.FLOOR) && !avatarExists) {
                world[x][y] = Tileset.AVATAR;
                avatar = new Avatar(Tileset.AVATAR, new Point(x, y));
                avatarExists = true;
            } else if (world[x][y].equals(Tileset.WALL) && doorCount < numUnlockedDoor) {
                world[x][y] = Tileset.LOCKED_DOOR;
                withoutEmbel[x][y] = Tileset.FLOOR;
                doorCount += 1;
            } else if (accessable.contains(world[x][y]) && secretDoorCount < numSecretDoor) {
                world[x][y] = Tileset.SECRETDOOR;
                secretDoorCount += 1;
            }
        }

        ArrayList<TETile> randomTiles = new ArrayList<>();
        randomTiles.add(Tileset.APPLE);
        randomTiles.add(Tileset.GOLD);
        randomTiles.add(Tileset.POISON);
        for (int i = 0; i <= 100; i += 1) {
            x = RandomUtils.uniform(random, WIDTH);
            y = RandomUtils.uniform(random, HEIGHT);
            if (accessable.contains(world[x][y])) {
                world[x][y] = randomTiles.get(RandomUtils.uniform(random, randomTiles.size()));
            }

        }
    }

    /**
     * @return: returns the main avatar of the world
     */
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * @return: returns the 2D world
     */
    public TETile[][] getWorld() {
        return world;
    }

    /**
     * setter for the world attribute
     */
    public void setWorld(TETile[][] newWorld) {
        this.world = newWorld;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

}
