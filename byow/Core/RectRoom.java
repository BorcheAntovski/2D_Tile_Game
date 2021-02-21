package byow.Core;

import byow.TileEngine.TETile;
import java.util.*;

public class RectRoom extends Room {
    private LinkedList<HallWay> hallWays = new LinkedList<>();
    World twoDWorld;

    public RectRoom(Point bottomL, int horLength, int verLength,
                    TETile[][] world, World twoDWorld) {
        this.bottomL = bottomL;
        this.horLength = horLength;
        this.verLength = verLength;
        this.tileSet = fillIn(horLength, verLength, bottomL);
        this.world = world;
        this.twoDWorld = twoDWorld;
    }

    public void connectToHallway(HallWay hall) {
        validateHallAddition(hall);
        hallWays.add(hall);
        openWallForHall(hall);
    }

    public LinkedList<HallWay> getHallWays() {
        return hallWays;
    }

    public HashMap randomAvailablePt() {
        String side = randomAvailableSide();
        HashMap<String, Object> result = new HashMap<>();
        boolean vertical;
        Point openPt;
        if (side.equals("u")) {
            openPt = new Point(bottomL.getX()
                    + RandomUtils.uniform(twoDWorld.getRandom(), 1, horLength - 1),
                    bottomL.getY() + verLength - 1);
            vertical = true;
        } else if (side.equals("d")) {
            openPt = new Point(bottomL.getX()
                    + RandomUtils.uniform(twoDWorld.getRandom(), 1, horLength - 1), bottomL.getY());
            vertical = true;
        } else if (side.equals("l")) {
            openPt = new Point(bottomL.getX(),
                    bottomL.getY() + RandomUtils.uniform(twoDWorld.getRandom(), 1, verLength - 1));
            vertical = false;
        } else {

            openPt = new Point(bottomL.getX() + horLength - 1,
                    bottomL.getY() + RandomUtils.uniform(twoDWorld.getRandom(), 1, verLength - 1));
            vertical = false;
        }

        result.put("vertical", vertical);
        result.put("point", openPt);
        result.put("side", side);
        return result;
    }

    public String randomAvailableSide() {
        ArrayList<String> availableSides = availableSides();
        int randIndex = RandomUtils.uniform(twoDWorld.getRandom(), 0, availableSides.size());
        return availableSides.get(randIndex);
    }

    public ArrayList<String> availableSides() {
        ArrayList<String> availableSides = new ArrayList<>(List.of("u", "d", "l", "r"));
        for (HallWay hall : hallWays) {
            if (hall.vertical()) {
                if (hall.getSmallEnd() == this) {
                    availableSides.remove("u");
                } else {
                    availableSides.remove("d");
                }
            } else {
                if (hall.getSmallEnd() == this) {
                    availableSides.remove("r");
                } else {
                    availableSides.remove("l");
                }
            }
        }
        return availableSides;
    }


    private void validateHallAddition(HallWay hall) {
    }

    private void openWallForHall(HallWay hall) {
        int xHall = hall.getStart().getX();
        int yHall = hall.getStart().getY();
        if (hall.vertical()) {
            if (hall.getSmallEnd() == this) {
                openWallAtPt(xHall - this.bottomL.getX() + 1, this.verLength - 1);
            } else {
                openWallAtPt(xHall - this.bottomL.getX() + 1, 0);
            }
        } else {
            if (hall.getSmallEnd() == this) {
                openWallAtPt(this.horLength - 1, yHall - this.bottomL.getY() + 1);
            } else {
                openWallAtPt(0, yHall - this.bottomL.getY() + 1);
            }
        }

    }
}
