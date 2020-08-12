package lasers.ptui;

import lasers.model.Observer;
import lasers.model.Safe;
import lasers.model.Tile;

import java.io.*;
import java.util.Scanner;

/**
 * Plain test user interface for the lasers game.
 *
 * @author Gerald Galano
 * @author Quynh Duong
 */
public class LasersPTUI implements Observer<Safe, Tile> {

    /** the model */
    private Safe safe;
    /** for user output messages */
    private PrintWriter userOut;
    /** for user input messages */
    private BufferedReader userIn;

    /**
     * Creates the new Safe and model and establishes a connection
     * @param args String[] for the command line arguments
     * @throws FileNotFoundException if the file does not exist
     */
    public LasersPTUI(String[] args) throws FileNotFoundException {
            String filename = args[0];
            this.safe = new Safe(filename);
            System.out.println(this.getSafe());
            this.safe.addObserver(this);
    }

    /**
     * Gets the Safe model
     * @return Safe
     */
    public Safe getSafe() {
        return this.safe;
    }

    /**
     * The main loop for the Lasers game
     * Handles user inputs and outputs
     * @param inputFile String for the name of the input file
     * @throws IOException if a failure occurs reading the the line
     */
    public void go(String inputFile) throws IOException {
        this.userIn = new BufferedReader(new InputStreamReader(System.in));
        this.userOut = new PrintWriter(System.out, true);
        if (!(inputFile.equals("noFile"))) {
            Scanner scanner = new Scanner(new File(inputFile));
            while (scanner.hasNextLine()) {
                String[] input = scanner.nextLine().split(" ");
                this.safe.command(input);
            }
        }
        boolean flag = false;
        while (!flag) {
            System.out.print("> ");
            String cmd = this.userIn.readLine();
            String[] a = cmd.split("");
            String[] args = cmd.split(" ");
            switch (a[0]) {
                case "q":
                    flag = true;
                    break;
                case "d":
                    System.out.println(getSafe());
                    break;
                case "h":
                    System.out.println("a|add r c: Add laser to (r,c)");
                    System.out.println("d|display: Display safe");
                    System.out.println("h|help: Print this help message");
                    System.out.println("q|quit: Exit program");
                    System.out.println("r|remove r c: Remove laser from (r,c)");
                    System.out.println("v|verify: Verify safe correctness");
                    break;
                case "a":
                case "r":
                case "v":
                    this.safe.command(args);
                    break;
                default:
                    System.out.println("Unrecognized command: " + cmd);
                    break;
            }
        }
        this.close();
        System.exit(-1);
    }

    /**
     * Called by the Safe model to update the state changes in the game
     * @param safe Safe for the model
     * @param tile Tile instance to change
     */
    public void update(Safe safe, Tile tile) {
        if (tile == null) {
            System.out.println("Invalid coordinates");
        } else {
            Tile.Commands command = tile.getCommand();
            int row = tile.getRow();
            int col = tile.getCol();
            if (command == Tile.Commands.ADD) {
                System.out.println("Laser added at: (" + row + ", " + col + ")");
                System.out.println(this.getSafe());
            } else if (command == Tile.Commands.ERROR_ADD) {
                System.out.println("Error adding laser at: (" + row + ", " + col + ")");
                System.out.println(this.getSafe());
            } else if (command == Tile.Commands.REMOVE) {
                System.out.println("Laser removed at: (" + row + ", " + col + ")");
                System.out.println(this.getSafe());
            } else if (command == Tile.Commands.ERROR_REMOVE) {
                System.out.println("Error removing laser at: (" + row + ", " + col + ")");
                System.out.println(this.getSafe());
            } else if (command == Tile.Commands.VERIFY) {
                System.out.println("Safe is fully verified!");
                System.out.println(this.getSafe());
            } else if (command == Tile.Commands.ERROR_VERIFY) {
                System.out.println("Error verifying at: (" + row + ", " + col + ")");
                System.out.println(this.getSafe());
            }
        }
    }

    /** Closes all connections */
    public void close() {
        try {
            System.in.close();
            this.userIn.close();
            this.userOut.close();
        } catch (IOException e) {
            // squash
        }
    }

    /**
     * The main method
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java LasersPTUI safe-file [input]");
        } else if (args.length == 1) {
            try {
                LasersPTUI ptui = new LasersPTUI(args);
                ptui.go("noFile");
            } catch (FileNotFoundException f) {
                System.out.println("File cannot be found");
            }
        } else {
            try {
                LasersPTUI ptui = new LasersPTUI(args);
                ptui.go(args[1]);
            } catch (FileNotFoundException f) {
                System.out.println("File cannot be found");
            }
        }
    }
}
