package lasers.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

import java.io.FileNotFoundException;

import lasers.model.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the lasers.lasers.model
 * and receives updates from it.
 *
 * @author RIT CS
 * @author Gerald Galano
 * @author Quynh Duong
 */
public class LasersGUI extends Application implements Observer<LasersModel, ModelData> {
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;
    /** The name of the filename for the board */
    private String filename;
    /** BorderPane that represents the entire view of the board */
    BorderPane borderPane;
    /** The center of the borderPane, represents the safe tiles */
    GridPane board;

    /**
     * Initialized the game board by reading in the filename and creating a model based off of it
     * Adds this as an observer for the model
     */
    @Override
    public void init() {
        try {
            Parameters params = getParameters();
            this.filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * Creates the gridpane based on the dimensions from the model
     * and sets a button at each coordinate
     * @return GridPane
     */
    private GridPane makeGridPane() {
        int row = model.getRow();
        int col = model.getCol();
        GridPane gridPane = new GridPane();
        for (int r=0; r<row; ++r) {
            for (int c=0; c<col; ++c) {
                Button button = new Button();
                ModelData[][] md = this.model.getModel();
                String val = md[r][c].getVal();
                int finalR = r;
                int finalC = c;
                button.setOnAction(event -> this.model.click(finalR, finalC));
                switch (val) {
                    case ".":
                        ImageView empty = new ImageView(new Image(getClass().getResourceAsStream("resources/white.png")));
                        button.setGraphic(empty);
                        setButtonBackground(button, "yellow.png");
                        break;
                    case "X":
                        ImageView X = new ImageView(new Image(getClass().getResourceAsStream("resources/pillarX.png")));
                        button.setGraphic(X);
                        setButtonBackground(button, "white.png");
                        break;
                    case "0":
                        ImageView zero = new ImageView(new Image(getClass().getResourceAsStream("resources/pillar0.png")));
                        button.setGraphic(zero);
                        setButtonBackground(button, "white.png");
                        break;
                    case "1":
                        ImageView one = new ImageView(new Image(getClass().getResourceAsStream("resources/pillar1.png")));
                        button.setGraphic(one);
                        setButtonBackground(button, "white.png");
                        break;
                    case "2":
                        ImageView two = new ImageView(new Image(getClass().getResourceAsStream("resources/pillar2.png")));
                        button.setGraphic(two);
                        setButtonBackground(button, "white.png");
                        break;
                    case "3":
                        ImageView three = new ImageView(new Image(getClass().getResourceAsStream("resources/pillar3.png")));
                        button.setGraphic(three);
                        setButtonBackground(button, "white.png");
                        break;
                    case "4":
                        ImageView four = new ImageView(new Image(getClass().getResourceAsStream("resources/pillar4.png")));
                        button.setGraphic(four);
                        setButtonBackground(button, "white.png");
                        break;
                }
                gridPane.add(button, c, r);
            }
        }
        return gridPane;
    }

    /**
     * Load command that opens up a filechooser and then creates a new model based off that
     * @throws FileNotFoundException if the file is not fount
     */
    private void load() throws FileNotFoundException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
            this.filename = selectedFile.toString();
            this.model.load(this.filename);
        }
    }

    /**
     * Representation of the buttons at the bottom of the borderPane
     * @return HBox for all of the buttons
     */
    private HBox buttons(){
        HBox buttons = new HBox();
        Button check = new Button("Check");
        check.setOnAction(event -> this.model.check());
        Button hint = new Button("Hint");
        Button solve = new Button("Solve");
        solve.setOnAction(event -> {
            try {
                this.model.solve(this.filename);
            } catch (FileNotFoundException e) {
                e.getMessage();
            }
        });
        Button restart = new Button("Restart");
        restart.setOnAction(event -> {
            try {
                this.model.restart(this.filename);
            } catch (FileNotFoundException e) {
                e.getMessage();
            }
        });
        Button load = new Button("Load");
        load.setOnAction(event -> {
            try {
                load();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        buttons.getChildren().addAll(check, hint, solve, restart, load);
        return buttons;
    }

    /**
     * The initialization of all GUI component happens here.
     *
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {
        this.borderPane = new BorderPane();
        Label label = new Label(this.filename + " loaded" );
        borderPane.setTop(label);
        BorderPane.setAlignment(label, Pos.TOP_CENTER);
        this.board = makeGridPane();
        borderPane.setCenter(board);
        BorderPane.setAlignment(board, Pos.CENTER);
        board.setHgap(10);
        board.setVgap(10);
        HBox buttons = buttons();
        borderPane.setBottom(buttons);
        BorderPane.setAlignment(buttons, Pos.BOTTOM_CENTER);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
    }

    /**
     * Starts the stage
     * @param stage Stage
     */
    @Override
    public void start(Stage stage) {
        init(stage);
        stage.setTitle("Lasers GUI");
        stage.show();
    }

    /**
     * Updates the button at a tile
     * @param res Button to be updated
     * @param r row of the button
     * @param c column of the button
     */
    public void updateTile(Button res, int r, int c) {
        ModelData[][] md = model.getModel();
        String value = md[r][c].getVal();
        switch (value) {
            case "L":
                res.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/laser.png"))));
                break;
            case "*":
                res.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/beam.png"))));
                break;
            case ".":
                res.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/white.png"))));
                break;
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
                setButtonBackground(res, "white.png");
                break;

        }
    }

    /**
     * Updates the entire board based on the updated model
     * Displays it to the user
     */
    public void updateView() {
        int row = model.getRow();
        int col = model.getCol();
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                ObservableList<Node> children = board.getChildren();
                Button result = null;
                for (Node node : children) {
                    if (GridPane.getRowIndex(node) == r && GridPane.getColumnIndex(node) == c) {
                        result = (Button) node;
                    }
                }
                if (result != null) {
                    Button finalResult = result;
                    int finalR = r;
                    int finalC = c;
                    Platform.runLater(() -> updateTile(finalResult, finalR, finalC));
                }
            }
        }
    }

    /**
     * When there is an error in verifying, this method sets the tile to have a red background
     * @param rowVerify row of the unverified button
     * @param colVerify column of the unverified button
     */
    public void updateVerify(int rowVerify, int colVerify) {
        int row = model.getRow();
        int col = model.getCol();
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                ObservableList<Node> children = board.getChildren();
                Button result = null;
                for (Node node : children) {
                    if (GridPane.getRowIndex(node) == rowVerify && GridPane.getColumnIndex(node) == colVerify) {
                        result = (Button) node;
                    }
                }
                if (result != null) {
                    ModelData[][] md = model.getModel();
                    String value = md[rowVerify][colVerify].getVal();
                    Button finalResult = result;
                    if (value.equals(".")) {
                        Platform.runLater(() -> finalResult.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/red.png")))));
                    } else {
                        Platform.runLater(() -> setButtonBackground(finalResult, "red.png"));
                    }
                }
            }
        }
    }

    /**
     * Called by the model to update any changes to be made to the baord
     * @param model LasersModel
     * @param data optional data the server.model can send to the observer
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        ModelData.Commands command = data.getCommand();
        int row = data.getRow();
        int col = data.getCol();
        if (command == ModelData.Commands.ADD) {
            Label add = new Label("Laser added at: (" + row + ", " + col + ")");
            borderPane.setTop(add);
            BorderPane.setAlignment(add, Pos.CENTER);
            updateView();
        } else if (command == ModelData.Commands.ERROR_ADD) {
            Label errorAdd = new Label("Error adding laser at: (" + row + ", " + col + ")");
            borderPane.setTop(errorAdd);
            BorderPane.setAlignment(errorAdd, Pos.CENTER);
        } else if (command == ModelData.Commands.REMOVE) {
            Label remove = new Label("Laser removed at: (" + row + ", " + col + ")");
            borderPane.setTop(remove);
            BorderPane.setAlignment(remove, Pos.CENTER);
            updateView();
        } else if (command == ModelData.Commands.ERROR_REMOVE) {
            Label errorRemove = new Label("Error removing laser at: (" + row + ", " + col + ")");
            borderPane.setTop(errorRemove);
            BorderPane.setAlignment(errorRemove, Pos.CENTER);
        } else if (command == ModelData.Commands.VERIFY) {
            Label verify = new Label("Safe is fully verified!");
            borderPane.setTop(verify);
            BorderPane.setAlignment(verify, Pos.CENTER);
        } else if (command == ModelData.Commands.ERROR_VERIFY) {
            Label errorVerify = new Label("Error verifying at: (" + row + ", " + col + ")");
            borderPane.setTop(errorVerify);
            BorderPane.setAlignment(errorVerify, Pos.CENTER);
            updateVerify(row, col);
        } else if (command == ModelData.Commands.RESET) {
            Label reset = new Label(this.filename + " has been reset");
            borderPane.setTop(reset);
            BorderPane.setAlignment(reset, Pos.CENTER);
            updateView();
        } else if (command == ModelData.Commands.LOAD) {
            this.board = makeGridPane();
            borderPane.setCenter(board);
            BorderPane.setAlignment(board, Pos.CENTER);
            Label load = new Label(this.filename + " loaded!");
            borderPane.setTop(load);
            BorderPane.setAlignment(load, Pos.CENTER);
            updateView();
        } else if (command == ModelData.Commands.SOLVE) {
            Label solve = new Label(this.filename + " solved!");
            borderPane.setTop(solve);
            BorderPane.setAlignment(solve, Pos.CENTER);
            updateView();
        }
    }
}
