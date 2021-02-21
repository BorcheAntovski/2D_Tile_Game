package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class SecretRoom {
    TETile[][] secretRoom;
    Random random;
    ArrayList<TETile> secretTiles = new ArrayList<>();


    public SecretRoom(Random random) {
        this.random = random;
        secretTiles.add(Tileset.APPLE);
        secretTiles.add(Tileset.POISON);
        secretTiles.add(Tileset.GOLD);
        makeSecretRoom();
    }

    public TETile[][] getSecretRoom() {
        return secretRoom;
    }

    /**
     * makes a secret room and returns it
     */
    private TETile[][] makeSecretRoom() {
        int width = RandomUtils.uniform(random, 8, 16);
        int height = RandomUtils.uniform(random, 8, 16);
        secretRoom = Room.fillIn(width, height, new Point(5, 5));
        TETile secretTile = secretTiles.get(RandomUtils.uniform(random, secretTiles.size()));
        for (int i = 1; i < width - 1; i += 1) {
            for (int j = 1; j < height - 1; j += 1) {
                secretRoom[i][j] = secretTile;
            }
        }
        return secretRoom;
    }

}
