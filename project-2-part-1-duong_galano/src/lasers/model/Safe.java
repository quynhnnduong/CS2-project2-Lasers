package lasers.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * The model that represents the safe for the lasers game.
 * Called by the PTUI to update the state of the game
 * Notifies its observers when modified
 *
 * @author Gerald Galano
 * @author Quynh Duong
 */
public class Safe {

    /** number of rows of the board */
    private int row;
    /** number of columns of the board */
    private int col;
    /** game board */
    private Tile[][] safe;
    /** the observers */
    private List<Observer<Safe, Tile>> observers;

    /** Construct the board by reading in a file and creates a list for the observers */
    public Safe(String fileName) throws FileNotFoundException {
        createSafe(fileName);
        this.observers = new LinkedList<>();
    }

    /**
     * Creates a game board by reading in a file
     * and setting all their commands to initialized
     * @param fileName String for the name of the file
     * @throws FileNotFoundException if the file does not exist
     */
    public void createSafe(String fileName) throws FileNotFoundException {
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
                this.safe = new Tile[row][col];
            } else {
                for (int j = 0; j < col; j++) {
                    this.safe[i][j] = new Tile(i, j, line[j], Tile.Commands.INITIALIZE);
                }
            }
            i++;
        }
    }

    /**
     * Adds a new observer
     * @param observer the new observer
     */
    public void addObserver(Observer<Safe, Tile > observer) {
        this.observers.add(observer);
    }

    /**
     * Notifies the observers when changes have been made to the game
     * @param tile which tile is being updated
     */
    private void notifyObservers(Tile tile){
        for (Observer<Safe, Tile> observer: observers) {
            // we pass the game status as client data
            observer.update(this, tile);
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
    public Tile[][] getSafe() {
        return this.safe;
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
            verify();
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
        String value = this.safe[row][col].getVal();
        if (value.equals("*") || value.equals(".") ){
            this.safe[row][col] = new Tile(row, col, "L", Tile.Commands.ADD);
            addBeamUp(row, col);
            addBeamRight(row, col);
            addBeamDown(row, col);
            addBeamLeft(row, col);
            notifyObservers(this.safe[row][col]);
        }
        else{
            this.safe[row][col] = new Tile(row, col, value, Tile.Commands.ERROR_ADD);
            notifyObservers(this.safe[row][col]);
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
            String t = this.safe[i-1][col].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.safe[i-1][col] = new Tile(row, col, "*", Tile.Commands.ADD);
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
            String t = this.safe[row][i+1].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.safe[row][i+1] = new Tile(row, col, "*", Tile.Commands.ADD);
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
            String t = this.safe[i+1][col].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.safe[i+1][col] = new Tile(row, col, "*", Tile.Commands.ADD);
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
            String t = this.safe[row][i-1].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.safe[row][i-1] = new Tile(row, col, "*", Tile.Commands.ADD);
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
        String value = this.safe[row][col].getVal();
        if (value.equals("L")) {
            this.safe[row][col] = new Tile(row, col, ".", Tile.Commands.REMOVE);
            removeBeamUp(row, col);
            removeBeamDown(row, col);
            removeBeamRight(row, col);
            removeBeamLeft(row, col);
            notifyObservers(this.safe[row][col]);
        } else {
            this.safe[row][col] = new Tile(row, col, value, Tile.Commands.ERROR_REMOVE);
            notifyObservers(this.safe[row][col]);
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
            String t = this.safe[i-1][col].getVal();
            if (t.equals("*")) {
                boolean cL = checkLeft(i-1, col);
                boolean cR = checkRight(i-1, col);
                if (cL && cR) {
                    this.safe[i - 1][col] = new Tile(row, col, ".", Tile.Commands.REMOVE);
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
            String t = this.safe[i+1][col].getVal();
            if (t.equals("*")) {
                boolean cL = checkLeft(i+1, col);
                boolean cR = checkRight(i+1, col);
                if (cL && cR) {
                    this.safe[i + 1][col] = new Tile(row, col, ".", Tile.Commands.REMOVE);
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
            String t = this.safe[row][i+1].getVal();
            if (t.equals("*")) {
                boolean cU = checkUp(row, i+1);
                boolean cD = checkDown(row, i+1);
                if (cU && cD) {
                    this.safe[row][i+1] = new Tile(row, col, ".", Tile.Commands.REMOVE);
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
            String t = this.safe[row][i-1].getVal();
            if (t.equals("*")) {
                boolean cU = checkUp(row, i-1);
                boolean cD = checkDown(row, i-1);
                if (cU && cD) {
                    this.safe[row][i-1] = new Tile(row, col, ".", Tile.Commands.REMOVE);
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
            String t = this.safe[row][i-1].getVal();
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
            String t = this.safe[row][i+1].getVal();
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
            String t = this.safe[i-1][col].getVal();
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
            String t = this.safe[i+1][col].getVal();
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
                String t = this.safe[i][j].getVal();
                if(t.equals(".")){
                    this.safe[i][j] = new Tile(i, j, t, Tile.Commands.ERROR_VERIFY);
                    notifyObservers(this.safe[i][j]);
                    flag = true;
                    break;
                }
                else if(t.equals("L")){
                    boolean cU = checkUp(i,j);
                    boolean cD = checkDown(i,j);
                    boolean cL = checkLeft(i,j);
                    boolean cR = checkRight(i,j);
                    if(!(cU && cD && cR && cL)){
                        this.safe[i][j] = new Tile(i, j, t, Tile.Commands.ERROR_VERIFY);
                        notifyObservers(this.safe[i][j]);
                        flag = true;
                        break;
                    }
                }
                else if(t.equals("0") || t.equals("1") ||t.equals("2") ||t.equals("3") ||t.equals("4") ){
                    int pillar = Integer.parseInt(t);
                    int cP = checkPillar(i, j);
                    if (!(pillar == cP)) {
                        this.safe[i][j] = new Tile(i, j, t, Tile.Commands.ERROR_VERIFY);
                        notifyObservers(this.safe[i][j]);
                        flag = true;
                        break;
                    }
                }
            }
        }
        if (!flag) {
            String value = this.safe[this.row-1][this.col-1].getVal();
            this.safe[this.row-1][this.col-1] = new Tile(this.row-1, this.col-1, value, Tile.Commands.VERIFY);
            notifyObservers(this.safe[this.row-1][this.col-1]);
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
            if (this.safe[row-1][col].getVal().equals("L")) {
                count++;
            }
        }
        if (row + 1 < this.row) {
            if (this.safe[row + 1][col].getVal().equals("L")) {
                count++;
            }
        }
        if (col - 1 >= 0) {
            if (this.safe[row][col-1].getVal().equals("L")) {
                count++;
            }
        }
        if (col + 1 < this.col) {
            if (this.safe[row ][col+1].getVal().equals("L")) {
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
                str.append(this.safe[row][col].getVal());
            }
            str.append("\n");
        }
        return str.toString();
    }
}
