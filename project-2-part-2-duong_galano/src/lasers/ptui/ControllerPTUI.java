package lasers.ptui;

import lasers.model.LasersModel;

import java.io.*;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author RIT CS
 * @author Gerald Galano
 * @author Quynh Duong
 */
public class ControllerPTUI  {
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;
    /** for user output messages */
    private PrintWriter userOut;
    /** for user input messages */
    private BufferedReader userIn;

    /**
     * Constructs the PTUI and create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) throws IOException {
        this.userIn = new BufferedReader(new InputStreamReader(System.in));
        this.userOut = new PrintWriter(System.out, true);
        if (!(inputFile.equals("noFile"))) {
            Scanner scanner = new Scanner(new File(inputFile));
            while (scanner.hasNextLine()) {
                String[] input = scanner.nextLine().split(" ");
                this.model.command(input);
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
                case "h":
                case "a":
                case "r":
                case "v":
                    this.model.command(args);
                    break;
                default:
                    System.out.println("Unrecognized command: " + cmd);
                    break;
            }
        }
        this.close();
        System.exit(-1);
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
}
