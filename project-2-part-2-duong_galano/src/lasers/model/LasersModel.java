package lasers.model;

import javafx.scene.control.Button;
import lasers.backtracking.Backtracker;
import lasers.backtracking.Configuration;
import lasers.backtracking.SafeConfig;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The model of the lasers safe.  You are free to change this class however
 * you wish, but it should still follow the MVC architecture.
 *
 * @author RIT CS
 * @author Gerald Galano
 * @author Quynh Duong
 */
public class LasersModel {

    /** number of rows of the board */
    private int row;
    /** number of columns of the board */
    private int col;
    /** game board */
    private ModelData[][] model;
    /** the observers who are registered with this model */
    private List<Observer<LasersModel, ModelData>> observers;

    /**
     * Creates a new LaserModel instance and sets the observers
     * @param filename String for the name of the file
     * @throws FileNotFoundException if the file is not found
     */
    public LasersModel(String filename) throws FileNotFoundException {
        this.observers = new LinkedList<>();
        createModel(filename);
    }

    /**
     * Gets the number of rows of the board
     * @return int for the number of rows
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Gets the number of columns of the board
     * @return int for the number of columns
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Creates a game board by reading in a file
     * and setting all their commands to initialized
     * @param fileName String for the name of the file
     * @throws FileNotFoundException if the file does not exist
     */
    public void createModel(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        int i = -1;
        while(scanner.hasNextLine()) {
            String l = scanner.nextLine();
            if(l.equals("")){
                break;
            }
            String[] line = l.split(" ");
            if (i == -1){
                this.row = Integer.parseInt(line[0]);
                this.col = Integer.parseInt(line[1]);
                this.model = new ModelData[row][col];
            } else {
                for (int j = 0; j < col; j++) {
                    this.model[i][j] = new ModelData(i, j, line[j], ModelData.Commands.INITIALIZE);
                }
            }
            i++;
        }
    }

    /**
     * Add a new observer.
     *
     * @param observer the new observer
     */
    public void addObserver(Observer<LasersModel, ModelData > observer) {
        this.observers.add(observer);
    }

    /**
     * Notify observers the model has changed.
     *
     * @param data optional data the model can send to the view
     */
    private void notifyObservers(ModelData data){
        for (Observer<LasersModel, ModelData> observer: observers) {
            observer.update(this, data);
        }
    }

    /**
     * Checks to see if the dimensions of the user are within the bounds of the board
     * @param row integer of the user row input
     * @param col integer of the user column input
     * @return boolean whether or not the coordinate is valid
     */
    public boolean isCoordValid(int row, int col) {
        return (row >= 0) && (row < this.row) && (col >= 0) && (col < this.col);
    }

    /**
     * Gets the instance of the safe
     * @return Safe
     */
    public ModelData[][] getModel() {
        return this.model;
    }

    /**
     * Method called when the user clicks on a tile
     * Determines whether a laser should be added or removed
     * @param row row of the tile the user clicked
     * @param col column of the tile the user clicked
     */
    public void click(int row, int col) {
        String value = model[row][col].getVal();
        if (value.equals("L")) {
            String[] com = {"r", String.valueOf(row), String.valueOf(col)};
            command(com);
        } else {
            String[] com = {"a", String.valueOf(row), String.valueOf(col)};
            command(com);
        }
    }

    /**
     * Method called when the user clicks on the check button
     */
    public void check(){
        String [] com = {"v"};
        command(com);
    }

    /**
     * Method called when the user clicks on the load button
     * Overwrites the model with a new model
     * @param filename String for the name of the file
     * @throws FileNotFoundException if the file is not found
     */
    public void load(String filename) throws FileNotFoundException {
        createModel(filename);
        String load = this.model[0][0].getVal();
        this.model[0][0] = new ModelData(0, 0, load, ModelData.Commands.LOAD);
        notifyObservers(this.model[0][0]);
    }

    /**
     * Method called when the user clicks on the reset button
     * Overwrites the model with an empty version of the model
     * @param filename String for the name of the file
     * @throws FileNotFoundException if the file is not found
     */
    public void restart(String filename) throws FileNotFoundException {
        createModel(filename);
        String reset = this.model[0][0].getVal();
        this.model[0][0] = new ModelData(0, 0, reset, ModelData.Commands.RESET);
        notifyObservers(this.model[0][0]);
    }

    /**
     * Method called when the user clicks on the solve button
     * Overwrites the model with a solved model
     * Calls the backtracker to solve the puzzle
     * @param filename String for the name of the file
     * @throws FileNotFoundException if the file is not found
     */
    public void solve(String filename) throws FileNotFoundException {
        createModel(filename);
        Configuration init = new SafeConfig(filename);
        Backtracker bt = new Backtracker(true);
        Optional<Configuration> sol = bt.solve(init);
        String newModel = sol.toString();
        String[] line = newModel.split(" ");
        int i = 0;
        for (int r = 0; r < this.row; r++) {
            for (int c = 0; c < this.col; c++) {
                if (i==0) {
                    String[] one = line[i].split("");
                    int len = one.length;
                    String val = one[len-1];
                    this.model[r][c] = new ModelData(r, c, val, ModelData.Commands.SOLVE);
                } else {
                    String val = line[i];
                    String[] vals = val.split("");
                    if (vals[0].equals("\n")) {
                        this.model[r][c] = new ModelData(r, c, vals[1], ModelData.Commands.SOLVE);
                    } else {
                        this.model[r][c] = new ModelData(r, c, val, ModelData.Commands.SOLVE);
                    }
                }
                i++;
            }
        }
        notifyObservers(this.model[0][0]);
    }

    /**
     * Called by the view to handle adding, removing, and verifying commands
     * Checks if the coordinates are valid
     * @param command String[] representing the user input
     */
    public void command(String[] command) {
        boolean flag = false;
        String[] a = command[0].split("");
        if (command.length != 3 && command.length != 1) {
            notifyObservers(null);
        } else if (command.length == 3) {
            int r = Integer.parseInt(command[1]);
            int c = Integer.parseInt(command[2]);
            boolean valid = isCoordValid(r, c);
            if (!valid) {
                notifyObservers(null);
                flag = true;
            }
            if (!flag) {
                switch (a[0]) {
                    case "a":
                        add(command);
                        break;
                    case "r":
                        remove(command);
                        break;
                }
            }
        } else {
            switch (a[0]) {
                case "v":
                    verify();
                    break;
                case "h":
                    String help = this.model[0][0].getVal();
                    this.model[0][0] = new ModelData(0, 0, help, ModelData.Commands.HELP);
                    notifyObservers(this.model[0][0]);
                    break;
                case "d":
                    String display = this.model[0][0].getVal();
                    this.model[0][0] = new ModelData(0, 0, display, ModelData.Commands.DISPLAY);
                    notifyObservers(this.model[0][0]);
                    break;
            }
        }
    }

    /**
     * Command to add a laser at a specified coordinate
     * Adds beams in four cardinal directions within regulations
     * @param a String[] representing add command
     */
    public void add(String[] a) {
        int row = Integer.parseInt(a[1]);
        int col = Integer.parseInt(a[2]);
        String value = this.model[row][col].getVal();
        if (value.equals("*") || value.equals(".") ){
            this.model[row][col] = new ModelData(row, col, "L", ModelData.Commands.ADD);
            addBeamUp(row, col);
            addBeamRight(row, col);
            addBeamDown(row, col);
            addBeamLeft(row, col);
            notifyObservers(this.model[row][col]);
        }
        else{
            this.model[row][col] = new ModelData(row, col, value, ModelData.Commands.ERROR_ADD);
            notifyObservers(this.model[row][col]);
        }
    }

    /**
     * Adds a beam above the specified laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void addBeamUp(int row, int col) {
        int i = row;
        while (i - 1 >= 0) {
            String t = this.model[i-1][col].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.model[i-1][col] = new ModelData(row, col, "*", ModelData.Commands.ADD);
            } else {
                break;
            }
            i--;
        }
    }

    /**
     * Adds a beam to the right of the specified laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void addBeamRight(int row, int col) {
        int i = col;
        while (i + 1 < this.col) {
            String t = this.model[row][i+1].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.model[row][i+1] = new ModelData(row, col, "*", ModelData.Commands.ADD);
            } else {
                break;
            }
            i++;
        }
    }

    /**
     * Adds a beam below the specified laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void addBeamDown(int row, int col) {
        int i = row;
        while (i + 1 < this.row) {
            String t = this.model[i+1][col].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.model[i+1][col] = new ModelData(row, col, "*", ModelData.Commands.ADD);
            } else {
                break;
            }
            i++;
        }
    }

    /**
     * Adds a beam to the left of the specified laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void addBeamLeft(int row, int col) {
        int i = col;
        while (i - 1 >= 0) {
            String t = this.model[row][i-1].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.model[row][i-1] = new ModelData(row, col, "*", ModelData.Commands.ADD);
            } else {
                break;
            }
            i--;
        }
    }

    /**
     * Command to remove a laser at a specified coordinate
     * Removes beams in four cardinal directions within regulations
     * @param a String[] representing remove command
     */
    public void remove(String[] a) {
        int row = Integer.parseInt(a[1]);
        int col = Integer.parseInt(a[2]);
        String value = this.model[row][col].getVal();
        if (value.equals("L")) {
            this.model[row][col] = new ModelData(row, col, ".", ModelData.Commands.REMOVE);
            removeBeamUp(row, col);
            removeBeamDown(row, col);
            removeBeamRight(row, col);
            removeBeamLeft(row, col);
            notifyObservers(this.model[row][col]);
        } else {
            this.model[row][col] = new ModelData(row, col, value, ModelData.Commands.ERROR_REMOVE);
            notifyObservers(this.model[row][col]);
        }
    }

    /**
     * Removes a beam above the specified laser if not acted on by another laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void removeBeamUp(int row, int col) {
        int i = row;
        while (i - 1 >= 0) {
            String t = this.model[i-1][col].getVal();
            if (t.equals("*")) {
                boolean cL = checkLeft(i-1, col);
                boolean cR = checkRight(i-1, col);
                if (cL && cR) {
                    this.model[i - 1][col] = new ModelData(row, col, ".", ModelData.Commands.REMOVE);
                }
            } else {
                break;
            }
            i--;
        }
    }

    /**
     * Removes a beam below the specified laser if not acted on by another laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void removeBeamDown(int row, int col){
        int i = row;
        while (i + 1 < this.row) {
            String t = this.model[i+1][col].getVal();
            if (t.equals("*")) {
                boolean cL = checkLeft(i+1, col);
                boolean cR = checkRight(i+1, col);
                if (cL && cR) {
                    this.model[i + 1][col] = new ModelData(row, col, ".", ModelData.Commands.REMOVE);
                }
            } else {
                break;
            }
            i++;
        }
    }

    /**
     * Removes a beam to the right of the specified laser if not acted on by another laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void removeBeamRight(int row, int col){
        int i = col;
        while (i + 1 < this.col) {
            String t = this.model[row][i+1].getVal();
            if (t.equals("*")) {
                boolean cU = checkUp(row, i+1);
                boolean cD = checkDown(row, i+1);
                if (cU && cD) {
                    this.model[row][i+1] = new ModelData(row, col, ".", ModelData.Commands.REMOVE);
                }
            } else {
                break;
            }
            i++;
        }
    }

    /**
     * Removes a beam to the left of the specified laser if not acted on by another laser
     * @param row int for the laser row coordinate
     * @param col int for the laser col coordinate
     */
    public void removeBeamLeft(int row, int col){
        int i = col;
        while (i - 1 >= 0) {
            String t = this.model[row][i-1].getVal();
            if (t.equals("*")) {
                boolean cU = checkUp(row, i-1);
                boolean cD = checkDown(row, i-1);
                if (cU && cD) {
                    this.model[row][i-1] = new ModelData(row, col, ".", ModelData.Commands.REMOVE);
                }
            } else {
                break;
            }
            i--;
        }
    }

    /**
     * checks whether there is a laser to the left of the a specified coordinate
     * prior to hitting a boundary or pillar
     * @param row integer for the row coordinate
     * @param col integer for the column coordinate
     * @return true if there is no laser
     */
    public boolean checkLeft(int row, int col) {
        int i = col;
        while (i - 1 >= 0) {
            String t = this.model[row][i-1].getVal();
            switch (t) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "X":
                    return true;
                case "L":
                    return false;
            }
            i--;
        }
        return true;
    }

    /**
     * checks whether there is a laser to the right of the a specified coordinate
     * prior to hitting a boundary or pillar
     * @param row integer for the row coordinate
     * @param col integer for the column coordinate
     * @return true if there is no laser
     */
    public boolean checkRight(int row, int col) {
        int i = col;
        while (i + 1 < this.col) {
            String t = this.model[row][i+1].getVal();
            switch (t) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "X":
                    return true;
                case "L":
                    return false;
            }
            i++;
        }
        return true;
    }

    /**
     * checks whether there is a laser above a specified coordinate
     * prior to hitting a boundary or pillar
     * @param row integer for the row coordinate
     * @param col integer for the column coordinate
     * @return true if there is no laser
     */
    public boolean checkUp(int row, int col){
        int i = row;
        while (i - 1 >= 0) {
            String t = this.model[i-1][col].getVal();
            switch (t) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "X":
                    return true;
                case "L":
                    return false;
            }
            i--;
        }
        return true;
    }

    /**
     * checks whether there is a laser below a specified coordinate
     * prior to hitting a boundary or pillar
     * @param row integer for the row coordinate
     * @param col integer for the column coordinate
     * @return true if there is no laser
     */
    public boolean checkDown(int row, int col){
        int i = row;
        while (i + 1 < this.row) {
            String t = this.model[i+1][col].getVal();
            switch (t) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "X":
                    return true;
                case "L":
                    return false;
            }
            i++;
        }
        return true;
    }

    /**
     * Command to verify the game board and all the Tile components
     * to ensure that all rules have been met
     */
    public void verify() {
        boolean flag = false;
        for(int i = 0; i < this.row; i++){
            if(flag){
                break;
            }
            for(int j = 0; j < this.col; j++){
                String t = this.model[i][j].getVal();
                if(t.equals(".")){
                    this.model[i][j] = new ModelData(i, j, t, ModelData.Commands.ERROR_VERIFY);
                    notifyObservers(this.model[i][j]);
                    flag = true;
                    break;
                }
                else if(t.equals("L")){
                    boolean cU = checkUp(i,j);
                    boolean cD = checkDown(i,j);
                    boolean cL = checkLeft(i,j);
                    boolean cR = checkRight(i,j);
                    if(!(cU && cD && cR && cL)){
                        this.model[i][j] = new ModelData(i, j, t, ModelData.Commands.ERROR_VERIFY);
                        notifyObservers(this.model[i][j]);
                        flag = true;
                        break;
                    }
                }
                else if(t.equals("0") || t.equals("1") ||t.equals("2") ||t.equals("3") ||t.equals("4") ){
                    int pillar = Integer.parseInt(t);
                    int cP = checkPillar(i, j);
                    if (!(pillar == cP)) {
                        this.model[i][j] = new ModelData(i, j, t, ModelData.Commands.ERROR_VERIFY);
                        notifyObservers(this.model[i][j]);
                        flag = true;
                        break;
                    }
                }
            }
        }
        if (!flag) {
            String value = this.model[this.row-1][this.col-1].getVal();
            this.model[this.row-1][this.col-1] = new ModelData(this.row-1, this.col-1, value, ModelData.Commands.VERIFY);
            notifyObservers(this.model[this.row-1][this.col-1]);
        }
    }

    /**
     * Counts the amount of lasers in the spaces right next to the pillar
     * @param row integer for the row coordinate
     * @param col integer for the column coordinate
     * @return int for the amount of lasers
     */
    public int checkPillar(int row, int col) {
        int count = 0;
        if (row - 1 >= 0) {
            if (this.model[row-1][col].getVal().equals("L")) {
                count++;
            }
        }
        if (row + 1 < this.row) {
            if (this.model[row + 1][col].getVal().equals("L")) {
                count++;
            }
        }
        if (col - 1 >= 0) {
            if (this.model[row][col-1].getVal().equals("L")) {
                count++;
            }
        }
        if (col + 1 < this.col) {
            if (this.model[row ][col+1].getVal().equals("L")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Represents the game board in an appealing manner
     * @return the board string
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // build the top row of indices
        str.append("  ");
        for (int col=0; col<this.col; ++col) {
            str.append(col);
        }
        str.append("\n");
        // build each row of the actual board
        for (int row=0; row<this.row; ++row) {
            str.append(row).append("|");
            // build the columns of the board
            for (int col=0; col<this.col; ++col) {
                str.append(this.model[row][col].getVal());
            }
            str.append("\n");
        }
        return str.toString();
    }
}
