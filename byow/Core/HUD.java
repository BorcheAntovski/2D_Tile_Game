package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class HUD {
    private String textTopLeft = "TileType";
    private String textTopRight = "Date";
    private String textTopCenter = "";
    private String textBottomCenter = "";
    private TERenderer ter;

    /**
     * HUD collects info about game status and draws in the corresponding region
     * health, when mouse in some location, display info
     * should constantly update and refresh (since mouse can be moving anytime)
     */
    public HUD(TERenderer ter) {
        this.ter = ter;
    }

    public void defaultInfo(Avatar a) {
        textTopCenter = "H: " + a.getHealth() + "     G: " + a.getGold()
                + "/100" + "     K: " + a.getKeys() + "     N: " + a.getName();
        textBottomCenter = "Health (H)     Gold (G)     Keys (K)     Name (N)";
    }

    public String getTextTopCenter() {
        return textTopCenter;
    }

    public String getTextBottom() {
        return textBottomCenter;
    }

    public String getTextLeft() {
        return textTopLeft;
    }

    public String getTextRight() {
        return textTopRight;
    }

    public void setTileType(TETile tile) {
        textTopLeft = "Tile Type: " + tile.description();
    }

    /**
     * @source
     * https://beginnersbook.com/2013/05/current-date-time-in-java/ was used for
     * date formatting
     */
    public void setTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        textTopRight = dateFormat.format(date);
    }
}
