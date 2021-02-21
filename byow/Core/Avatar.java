package byow.Core;

import byow.TileEngine.TETile;

public class Avatar {
    private int health = 500;
    private int gold = 0;
    private Point location;
    private String name;
    private int keys = 1;
    private TETile sign;

    public Avatar(TETile sign, Point location) {
        this.sign = sign;
        this.location = location;
        name = "I am an avatar";
    }

    /**
     *The following are getters and setters
     * (The parameter "change" can be positive or negative)
     */
    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return location.getX();
    }

    public int getY() {
        return location.getY();
    }

    public Point getLocation() {
        return location;
    }

    public TETile getSign() {
        return sign;
    }

    public void changeHealth(int change) {
        health += change;
    }

    public void changeGold(int change) {
        gold += change;
    }

    public void changeLocation(Point p) {
        location = p;
    }

    public void collectKey() {
        keys++;
    }

    public void useKey() {
        keys--;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    public int getKeys() {
        return keys;
    }

    public int getGold() {
        return gold;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }
}
