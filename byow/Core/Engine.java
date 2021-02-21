package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 44;
    private Interact interaction;
    private HashSet<Character> validInput;
    private String pathString;
    private HUD hud;
    private TERenderer ter;
    private File load;
    private Random random;

    public Engine() {
        validInput = new HashSet<>();
        validInput.add('w');
        validInput.add('a');
        validInput.add('s');
        validInput.add('d');
        validInput.add('r');
        ter = new TERenderer();
        hud = new HUD(ter);
        interaction = new Interact(null, ter, hud, null);

        load = new File("savedWorld.txt");
        try {
            load.createNewFile();
        } catch (IOException ioe) {
            System.out.println("IO Exception occurred!");
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        ter.displayMenu();
        char inputChar;

        while (true) {
            pathString = "";
            if (ter.hasNextIn()) {
                inputChar = Character.toLowerCase(ter.nextKeyTyped());
                pathString += inputChar;
                if (inputChar == 'n') {
                    inputSeed();
                    startGame();
                } else if (inputChar == 'l') {
                    pathString = renderSavedGame();
                    if (pathString == null) {
                        continue;
                    }
                    interactWithInputString(pathString);
                    startGame();
                } else if (inputChar == 'q') {
                    ter.closeWindow();
                    return;
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        input = input.toLowerCase();
        if (input.charAt(0) == 'q') {
            return null;
        } else if (input.charAt(0) == 'l') {
            input = renderSavedGame() + input.substring(1);
        }

        pathString = "n";
        int count = getSeedCount(input);
        String seedInput = input.substring(1, count);
        String remainingInput = input.substring(count + 1);
        pathString += (seedInput + 's' + remainingInput);

        long seed = Long.parseLong(seedInput);
        random = new Random(seed);
        World world = new World(random);
        world.generateWorld();
        interaction.setWorld(world);
        interaction.setRandom(random);

        doRemainingInput(remainingInput);
        return interaction.getCurWorldArray();
    }

    /**
     * take a seed as input and renders the world
     * filters any illegal input (nun-numeral inputs)
     * (the seed input ends as 's')
     */
    private void inputSeed() {
        String seedInput = ter.enterSeed();
        long seed = Long.parseLong(seedInput);
        pathString += (seedInput + 's');

        random = new Random(seed);
        World world = new World(random);
        world.generateWorld();

        interaction.setWorld(world);
        interaction.setRandom(random);
    }

    /**
     * This is where the game keeps updating the display, taking user inputs,
     * and carrying out corresponding actions
     */
    private void startGame() {
        Avatar avatar = interaction.getMainAvatar();
        ter.displayMenu();
        boolean colonPressed = false;
        char nextInput;
        while (avatar.getHealth() > 0 && avatar.getGold() < 100) {
            interaction.visitingTile();
            hud.setTime();
            ter.renderFrame(interaction.getCurWorldArray(), hud, interaction.getMainAvatar());

            if (ter.hasNextIn()) {
                nextInput = Character.toLowerCase(ter.nextKeyTyped());
                if (validInput.contains(nextInput)) {
                    pathString += nextInput;
                    validKeyPressed(nextInput);
                } else if (nextInput == ':') {
                    colonPressed = true;
                } else if (colonPressed && nextInput == 'q') {
                    saveGame();
                    ter.successfulSave();
                    interaction = new Interact(null, ter, hud, null);
                    return;
                } else if (colonPressed && nextInput == 'g') {
                    interaction.getMainAvatar().changeGold(20);
                    colonPressed = false;
                } else if (colonPressed && nextInput == 'h') {
                    interaction.getMainAvatar().changeHealth(-50);
                    colonPressed = false;
                }
                ter.renderFrame(interaction.getCurWorldArray(), hud, interaction.getMainAvatar());
            }
            if (ter.mousePressed()) {
                int x = (int) Math.floor(ter.getMouseX());
                int y = (int) Math.floor(ter.getMouseY());
                mousePressed(x, y);
            }
            if (interaction.getMainAvatar().getHealth() <= 0) {
                ter.gameOver();
                interaction = new Interact(null, ter, hud, null);
                return;
            } else if (interaction.getMainAvatar().getGold() >= 100) {
                ter.youWin();
                interaction = new Interact(null, ter, hud, null);
                return;
            }
        }
    }

    /**
     * Processes valid movement key presses
     */
    private void validKeyPressed(char key) {
        switch (key) {
            case 'w':
                interaction.goUp();
                break;
            case 's':
                interaction.goDown();
                break;
            case 'a':
                interaction.goLeft();
                break;
            case 'd':
                interaction.goRight();
                break;
            case 'r':
                rotateWorld();
                break;
            default:
                return;
        }
    }

    /**
     * When mouse is pressed on the given coordinate, display a shortest path to that
     * point from current position
     */
    private void mousePressed(int x, int y) {
        TGraph graph = new TGraph(interaction.getCurWorldArray());
        GraphTraversal traversal = new GraphTraversal();
        Point goal = new Point(x, y - 2);
        Point start = interaction.getMainAvatar().getLocation();
        LinkedList<Point> path = traversal.bfs(graph, start, goal);
        if (path == null) {
            return;
        } else {
            path.removeFirst();
            updateShortestPath(path);
            ter.renderFrame(interaction.getCurWorldArray(), hud, interaction.getMainAvatar());
        }
    }

    /**
     * Returns the length of the input seed
     */
    private int getSeedCount(String input) {
        int count = 1;
        while (input.charAt(count) != 's') {
            count++;
        }
        return count;
    }

    /**
     * Processes a given input string and updates the world accordingly
     */
    private void doRemainingInput(String movingInput) {
        Avatar avatar = interaction.getMainAvatar();
        boolean colonPressed = false;
        char nextInput;

        int i;
        for (i = 0; i < movingInput.length(); i++) {
            nextInput = movingInput.charAt(i);
            if (validInput.contains(nextInput)) {
                validKeyPressed(nextInput);
            } else if (nextInput == ':') {
                colonPressed = true;
                continue;
            } else if (colonPressed && nextInput == 'q') {
                pathString = pathString.substring(0, pathString.length() - 2);
                saveGame();
                return;
            }
            if (avatar.getHealth() <= 0 || avatar.getGold() >= 100) {
                pathString = "";
                saveGame();
                return;
            }
            colonPressed = false;
        }
    }

    /**
     * Saves the game the file "savedWorld.txt"
     */
    private void saveGame() {
        try {
            FileWriter myWriter = new FileWriter("savedWorld.txt");
            myWriter.write(pathString);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException ioe) {
            System.out.println("Error when saving file!");
        }

    }

    /**
     * returns loaded stringPATH from "savedWorld.txt"
     */
    private String renderSavedGame() {
        try {
            Scanner reader = new Scanner(load);
            if (reader.hasNextLine()) {
                String loadString = reader.nextLine();
                return loadString;
            }
            return null;
        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
            return null;
        }
    }

    /**
     * Updates the world 2D array to reflect shortest path as red dots
     */
    private void updateShortestPath(LinkedList<Point> path) {
        for (int i = 0; i < interaction.getCurWorldArray().length; i += 1) {
            for (int j = 0; j < interaction.getCurWorldArray()[0].length; j += 1) {
                if (interaction.getCurWorldArray()[i][j].equals(Tileset.PATHFLOOR)) {
                    interaction.getCurWorldArray()[i][j] = interaction.getWithoutEmbel()[i][j];
                }
            }
        }
        for (Point p : path) {
            interaction.getCurWorldArray()[p.getX()][p.getY()] = Tileset.PATHFLOOR;
        }
    }

    /**
     * Rotates the world. Not yet supported
     */
    private void rotateWorld() {
        return;
    }
}
