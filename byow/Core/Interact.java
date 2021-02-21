package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.LinkedList;
import java.util.Random;

public class Interact {
    TETile [][] curWorldArray;
    Avatar curAvatar;
    private World world;
    private LinkedList<Avatar> mainAvatars = new LinkedList<>();
    private TERenderer ter;
    private HUD hud;
    private Random random;
    private LinkedList<TETile[][]> savedWorld = new LinkedList<>();
    private boolean inSecretRoom = false;

    public Interact(World world, TERenderer ter, HUD hud, Random random) {
        this.world = world;
        this.ter = ter;
        this.hud = hud;
        this.random = random;
        if (world != null) {
            this.curWorldArray = world.getWorld();
            this.curAvatar = world.getAvatar();
            this.savedWorld.addFirst(curWorldArray);
            this.mainAvatars.addFirst(world.getAvatar());
        }
    }

    /**
     * updates the HUD to reflect the current position of the mouse cursor
     */
    public void visitingTile() {
        curWorldArray = savedWorld.getFirst();
        int x = (int) Math.floor(ter.getMouseX());
        int y = (int) Math.floor(ter.getMouseY());
        if (y > 1 && y < world.getHEIGHT() + 2 && x >= 0 && x < world.getWIDTH()) {
            if (valid(x, y - 2)) {
                hud.setTileType(curWorldArray[x][y - 2]);
            } else {
                hud.setTileType(Tileset.NOTHING);
            }
        }
    }

    /**
     * getter method for the main character
     */
    public Avatar getMainAvatar() {
        return mainAvatars.getFirst();
    }

    /**
     *
     * All following methods and helpers are for the movement of the main avatar:
     * All tiles except for WALL are walkable.
     * can collect keys and open doors.
     * if a wall, don't do anything.
     * if an apple, eat it, change some amount of health
     * if a gold, take it, increase some amount of gold
     * if a poison, eat it, decrease some amount of health
     */
    public void goUp() {
        curAvatar = mainAvatars.getFirst();
        curWorldArray = savedWorld.getFirst();
        int xPos = curAvatar.getX();
        int yPos = curAvatar.getY();
        makeNextMove(xPos, yPos + 1, xPos, yPos);
    }

    public void goDown() {
        curAvatar = mainAvatars.getFirst();
        curWorldArray = savedWorld.getFirst();
        int xPos = curAvatar.getX();
        int yPos = curAvatar.getY();
        makeNextMove(xPos, yPos - 1, xPos, yPos);
    }

    public void goLeft() {
        curAvatar = mainAvatars.getFirst();
        curWorldArray = savedWorld.getFirst();
        int xPos = curAvatar.getX();
        int yPos = curAvatar.getY();
        makeNextMove(xPos - 1, yPos, xPos, yPos);
    }

    public void goRight() {
        curAvatar = mainAvatars.getFirst();
        curWorldArray = savedWorld.getFirst();
        int xPos = curAvatar.getX();
        int yPos = curAvatar.getY();
        makeNextMove(xPos + 1, yPos, xPos, yPos);
    }

    private void makeNextMove(int x, int y, int fromX, int fromY) {
        if (!valid(x, y)) {
            return;
        }
        TETile target = curWorldArray[x][y];
        if (target.equals(Tileset.FLOOR) || target.equals(Tileset.NOTHING)
                || target.equals((Tileset.PATHFLOOR))) {
            moveToPos(x, y, fromX, fromY);
        } else if (target.equals(Tileset.APPLE)) {
            moveToPos(x, y, fromX, fromY);
            eatApple();
        } else if (target.equals(Tileset.POISON)) {
            moveToPos(x, y, fromX, fromY);
            eatPoison();
        } else if (target.equals(Tileset.LOCKED_DOOR)) {
            if (curAvatar.getKeys() > 0) {
                moveToPos(x, y, fromX, fromY);
                curAvatar.useKey();
            }
        } else if (target.equals(Tileset.GOLD)) {
            moveToPos(x, y, fromX, fromY);
            collectGold();
        } else if (target.equals(Tileset.SECRETDOOR)) {
            moveToPos(x, y, fromX, fromY);
            openSecretDoor();
        } else if (target.equals(Tileset.KEY)) {
            moveToPos(x, y, fromX, fromY);
            curAvatar.collectKey();
        } else if (target.equals(Tileset.EXIT)) {
            moveToPos(x, y, fromX, fromY);
            savedWorld.removeFirst();
            Avatar obsoleteAvatar = mainAvatars.removeFirst();
            curAvatar = mainAvatars.getFirst();
            curAvatar.setGold(obsoleteAvatar.getGold());
            curAvatar.setHealth(obsoleteAvatar.getHealth());
            curAvatar.setKeys(obsoleteAvatar.getKeys());
            inSecretRoom = false;
        }
        world.setWorld(savedWorld.getFirst());
    }

    private void moveToPos(int x, int y, int fromX, int fromY) {
        if (inSecretRoom) {
            curWorldArray[fromX][fromY] = Tileset.FLOOR;
        } else {
            curWorldArray[fromX][fromY] = world.getWithoutEmbel()[fromX][fromY];
        }
        curWorldArray[x][y] = curAvatar.getSign();
        curAvatar.changeLocation(new Point(x, y));
    }

    private boolean valid(int x, int y) {
        return x >= 0 && y >= 0 && x < curWorldArray.length && y < curWorldArray[0].length;
    }

    private void eatApple() {
        curAvatar.changeHealth(RandomUtils.uniform(random, 1, 30));
    }

    private void eatPoison() {
        curAvatar.changeHealth(RandomUtils.uniform(random, -100, 0));
        if (curAvatar.getGold() > 0) {
            curAvatar.changeGold(-1);
        }
    }

    private void collectGold() {
        curAvatar.changeGold(RandomUtils.uniform(random, 0, 4));
    }

    private void openSecretDoor() {
        SecretRoom newRoom = new SecretRoom(random);
        TETile[][] newSecretRoom = newRoom.getSecretRoom();
        int width = newSecretRoom.length;
        int height = newSecretRoom[0].length;
        inSecretRoom = true;

        Avatar newAvatar = new Avatar(Tileset.AVATAR, new Point(1, 1));
        newAvatar.setGold(curAvatar.getGold());
        newAvatar.setHealth(curAvatar.getHealth());
        newAvatar.setKeys(curAvatar.getKeys());
        mainAvatars.addFirst(newAvatar);
        newSecretRoom[1][1] = Tileset.AVATAR;
        int tempX = RandomUtils.uniform(random, 2, width - 1);
        int tempY = RandomUtils.uniform(random, 2, height - 1);
        newSecretRoom[tempX][tempY] = Tileset.EXIT;
        savedWorld.addFirst(newSecretRoom);
        curAvatar = newAvatar;
        curWorldArray = newSecretRoom;
    }

    public TETile[][] getCurWorldArray() {
        return savedWorld.getFirst();
    }

    public TETile[][] getWithoutEmbel() {
        return world.getWithoutEmbel();
    }

    public World getWorld() {
        return world;
    }

    public void setCurWorldArray(TETile[][] newWorldArray) {
        savedWorld.removeFirst();
        savedWorld.addFirst(newWorldArray);
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * assuming a world hasn't been initialized to the Interact class yet
     */
    public void setWorld(World world) {
        if (!savedWorld.isEmpty()) {
            System.out.println("World already exits!");
            return;
        }
        this.world = world;
        this.curWorldArray = world.getWorld();
        this.curAvatar = world.getAvatar();
        this.savedWorld.addFirst(curWorldArray);
        this.mainAvatars.addFirst(world.getAvatar());
    }
}
