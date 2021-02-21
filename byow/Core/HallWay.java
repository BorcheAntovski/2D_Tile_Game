package byow.Core;

import byow.TileEngine.TETile;

public class HallWay extends Room {
    private Room smallEnd;
    private Room bigEnd;
    private boolean vertical;

    public HallWay(Room smallEnd, Room bigEnd, Point bottomL,
                   int hor, int ver, boolean vertical, TETile[][] world) {
        this.bottomL = bottomL;
        this.horLength = hor;
        this.verLength = ver;
        this.vertical = vertical;
        this.smallEnd = smallEnd;
        this.bigEnd = bigEnd;
        this.tileSet = fillIn(hor, ver, bottomL);
        this.world = world;
        if (smallEnd != null) {
            openWallForRoom(smallEnd);
        }
        if (bigEnd != null) {
            openWallForRoom(bigEnd);
        }
    }

    public void addRoom(Room room) {
        if (smallEnd == null) {
            smallEnd = room;
        } else {
            bigEnd = room;
        }
    }

    /**
     * Note that whenever a hallway is made, it is connected to one room
     * so we can assume the invariant that a hallway always connects to either 1 or 2 rooms
     */
    public void openToRoom(Room room) {
        openWallForRoom(room);
    }

    public Room getSmallEnd() {
        return smallEnd;
    }

    public Room getBigEnd() {
        return bigEnd;
    }

    public Room[] endSideRooms() {
        return new Room[]{smallEnd, bigEnd};
    }

    public boolean isOneEnded() {
        return !((smallEnd != null & bigEnd != null) || (smallEnd == null & bigEnd == null));
    }

    public boolean vertical() {
        return vertical;
    }

    public Point getStart() {
        return bottomL;
    }

    public int getLength() {
        if (vertical) {
            return verLength;
        } else {
            return horLength;
        }
    }

    private void openWallForRoom(Room room) {
        if (vertical) {
            if (smallEnd == room) {
                openWallAtPt(1, 0);
            } else {
                openWallAtPt(1, getLength() - 1);
            }
        } else {
            if (smallEnd == room) {
                openWallAtPt(0, 1);
            } else {
                openWallAtPt(getLength() - 1, 1);
            }
        }
    }
}
