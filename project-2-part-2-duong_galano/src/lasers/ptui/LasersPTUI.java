package lasers.ptui;

import java.io.*;

import lasers.model.LasersModel;
import lasers.model.ModelData;
import lasers.model.Observer;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author Gerald Galano
 * @author Quynh Duong
 */
public class LasersPTUI implements Observer<LasersModel, ModelData> {
    /** The UI's connection to the model */
    private LasersModel model;
    /** The UI's connection to the controller */
    private ControllerPTUI controller;

    /**
     * Construct the PTUI.  Create the lasers.lasers.model and initialize the view.
     * @param filename the safe file name
     * @throws FileNotFoundException if file not found
     */
    public LasersPTUI(String filename) throws FileNotFoundException  {
        try {
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
        this.controller = new ControllerPTUI(this.model);
    }

    /**
     * Accessor for the model the PTUI create.
     *
     * @return the model
     */
    public LasersModel getModel() { return this.model; }

    /**
     * Called by the model to update any changes to be made to the baord
     * @param model LasersModel
     * @param data optional data the server.model can send to the observer
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        if (data == null) {
            System.out.println("Invalid coordinates");
        } else {
            ModelData.Commands command = data.getCommand();
            int row = data.getRow();
            int col = data.getCol();
            if (command == ModelData.Commands.ADD) {
                System.out.println("Laser added at: (" + row + ", " + col + ")");
                System.out.println(this.getModel());
            } else if (command == ModelData.Commands.ERROR_ADD) {
                System.out.println("Error adding laser at: (" + row + ", " + col + ")");
                System.out.println(this.getModel());
            } else if (command == ModelData.Commands.REMOVE) {
                System.out.println("Laser removed at: (" + row + ", " + col + ")");
                System.out.println(this.getModel());
            } else if (command == ModelData.Commands.ERROR_REMOVE) {
                System.out.println("Error removing laser at: (" + row + ", " + col + ")");
                System.out.println(this.getModel());
            } else if (command == ModelData.Commands.VERIFY) {
                System.out.println("Safe is fully verified!");
                System.out.println(this.getModel());
            } else if (command == ModelData.Commands.ERROR_VERIFY) {
                System.out.println("Error verifying at: (" + row + ", " + col + ")");
                System.out.println(this.getModel());
            } else if (command == ModelData.Commands.DISPLAY) {
                System.out.println(this.getModel());
            } else if (command == ModelData.Commands.HELP) {
                System.out.println("a|add r c: Add laser to (r,c)");
                System.out.println("d|display: Display safe");
                System.out.println("h|help: Print this help message");
                System.out.println("q|quit: Exit program");
                System.out.println("r|remove r c: Remove laser from (r,c)");
                System.out.println("v|verify: Verify safe correctness");
            }
        }
    }
}
