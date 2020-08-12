package lasers.backtracking;

import lasers.model.LasersModel;
import lasers.model.ModelData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the lasers.model
 * package and/or incorporate it into another class.
 *
 * @author RIT CS
 * @author Gerald Galano
 * @author Quynh Duong
 *
 */
public class SafeConfig implements Configuration {
    /** number of rows of the board */
    private int row;
    /** number of columns of the board */
    private int col;
    /** game board */
    private ModelData[][] board;
    /** current row location of the cursor */
    private int cursorRow;
    /** current column location of the cursor */
    private int cursorCol;

    /**
     * Creates a safe configuration based off the filename
     * and creating initialized tiles at each coordinate
     * @param filename String for the name of the file
     * @throws FileNotFoundException if the file does not exist
     */
    public SafeConfig(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
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
                this.board = new ModelData[this.row][this.col];
            } else {
                for (int j = 0; j < col; j++) {
                    this.board[i][j] = new ModelData(i, j, line[j], ModelData.Commands.INITIALIZE);
                }
            }
            i++;
        }
        this.cursorRow = 0;
        this.cursorCol = -1;
    }

    /**
     * Creates a safe configuration based off another safe configuration
     * @param other SafeConfig representing the other configuration
     * @param row number of rows on the board
     * @param col number of columns on the board
     * @param add boolean whether or not a laser should be added
     */
    public SafeConfig(SafeConfig other, int row, int col, boolean add){
        this.row = other.row;
        this.col = other.col;
        this.cursorRow = row;
        this.cursorCol = col;
        this.board = new ModelData[other.row][other.col];
        for(int i = 0; i < this.row; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, this.col);
        }
        String[] a = {"a", String.valueOf(this.cursorRow), String.valueOf(this.cursorCol)};
        if (add) {
            add(a);
        }
    }


    /**
     * Creates two new configurations at a coordinate
     * @return a Collection of all the successors
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new LinkedList<>();
        if (cursorCol >= this.col-1) {
            cursorCol = 0;
            cursorRow++;
        } else {
            cursorCol++;
        }
        if ((cursorCol < this.col && cursorRow < this.row)) {
            SafeConfig childL = new SafeConfig(this, cursorRow, cursorCol, true);
            successors.add(childL);
            SafeConfig childN = new SafeConfig(this, cursorRow, cursorCol, false);
            successors.add(childN);
        }
        return successors;
    }

    /**
     * Command to add a laser at a specified coordinate
     * Adds beams in four cardinal directions within regulations
     * @param a String[] representing add command
     */
    public void add(String[] a) {
        int row = Integer.parseInt(a[1]);
        int col = Integer.parseInt(a[2]);
        String value = this.board[row][col].getVal();
        if (value.equals("*") || value.equals(".") ){
            this.board[row][col] = new ModelData(row, col, "L", ModelData.Commands.ADD);
            addBeamUp(row, col);
            addBeamRight(row, col);
            addBeamDown(row, col);
            addBeamLeft(row, col);
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
            String t = this.board[i-1][col].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.board[i-1][col] = new ModelData(row, col, "*", ModelData.Commands.ADD);
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
            String t = this.board[row][i+1].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.board[row][i+1] = new ModelData(row, col, "*", ModelData.Commands.ADD);
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
            String t = this.board[i+1][col].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.board[i+1][col] = new ModelData(row, col, "*", ModelData.Commands.ADD);
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
            String t = this.board[row][i-1].getVal();
            if (t.equals(".") || t.equals("*")) {
                this.board[row][i-1] = new ModelData(row, col, "*", ModelData.Commands.ADD);
            } else {
                break;
            }
            i--;
        }
    }

    /**
     * Called by the backtracker to check whether or not a certain configuration is valid
     * @return true if the configuration is valid
     */
    @Override
    public boolean isValid() {
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                String t = this.board[i][j].getVal();
                if (t.equals("L")) {
                    boolean cU = checkUp(i, j);
                    boolean cD = checkDown(i, j);
                    boolean cL = checkLeft(i, j);
                    boolean cR = checkRight(i, j);
                    if (!cU || !cD || !cL || !cR) {
                        return false;
                    }
                } else if (t.equals("0") || t.equals("1") || t.equals("2") || t.equals("3") || t.equals("4")) {
                    int pillar = Integer.parseInt(t);
                    int cP = checkPillar(i, j);
                    if (cP > pillar) {
                        return false;
                    }
                    else if (cP < pillar) {
                        int visitCount = 0;
                        int spaceCount = 0;
                        if( cursorRow >= i && i > 0){
                            visitCount++;
                            if(cursorRow == i){
                                if(cursorCol >= j-1 && j>0){
                                    visitCount++;
                                }
                                if(cursorCol >= j+1 && j+1< this.col ){
                                    visitCount++;
                                }
                            }
                            else if (cursorRow == i+1 && i+1< this.row ){
                                if(cursorCol >= j){
                                    visitCount+=3;
                                }
                            }
                            else{
                                visitCount+=3;
                            }
                        }
                        if (j>0) { spaceCount++; }
                        if (i>0) { spaceCount++; }
                        if (j<this.col-1) { spaceCount++; }
                        if (i<this.row-1) { spaceCount++; }
                        int possible = spaceCount - visitCount + cP;
                        if (possible < pillar) {
                            return false;
                        }
                    }
                }
                else if(t.equals(".")){
                    boolean vD = verifyDown(i, j);
                    boolean vU = verifyUp(i, j);
                    boolean vR = verifyRight(i, j);
                    boolean vL = verifyLeft(i, j);
                    if (vD && vU && vR && vL) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Verifies that there can still be a beam at a specified point from below it
     * @param row row of the specified point
     * @param col column of the specified point
     * @return true if the possible points below it have been visited
     */
    public boolean verifyDown(int row, int col){
        boolean noWall = true;
        int i = 1;
        while(noWall && row + i < this.row){
            String val = this.board[row+i][col].getVal();
            if (val.equals("*") || val.equals(".")){
                i++;
            } else {
                noWall = false;
            }
        }
        if (cursorRow > row + i) {
            return true;
        } else if (cursorRow == row + i) {
            return cursorCol >= col;
        } else {
            return false;
        }
    }

    /**
     * Verifies that there can still be a beam at a specified point from above it
     * @param row row of the specified point
     * @param col column of the specified point
     * @return true if the possible points above it have been visited
     */
    public boolean verifyUp(int row, int col){
        boolean noWall = true;
        int i = 1;
        while(noWall && row - i >= 0){
            String val = this.board[row-i][col].getVal();
            if (val.equals("*") || val.equals(".")){
                i++;
            } else {
                noWall = false;
            }
        }
        if (cursorRow > row - i) {
            return true;
        } else if (cursorRow == row - i) {
            return cursorCol >= col;
        } else {
            return false;
        }
    }

    /**
     * Verifies that there can still be a beam at a specified point from the right
     * @param row row of the specified point
     * @param col column of the specified point
     * @return true if the possible points to the right of it have been visited
     */
    public boolean verifyRight(int row, int col){
        boolean noWall = true;
        int i = 1;
        while(noWall && col + i < this.col){
            String val = this.board[row][col+1].getVal();
            if (val.equals("*") || val.equals(".")){
                i++;
            } else {
                noWall = false;
            }
        }
        if (cursorCol > col + i) {
            return true;
        } else if (cursorCol == row + i) {
            return cursorRow >= row;
        } else {
            return false;
        }
    }

    /**
     * Verifies that there can still be a beam at a specified point from the left
     * @param row row of the specified point
     * @param col column of the specified point
     * @return true if the possible points to the left of it it have been visited
     */
    public boolean verifyLeft(int row, int col){
        boolean noWall = true;
        int i = 1;
        while(noWall && col - i > 0){
            String val = this.board[row][col-1].getVal();
            if (val.equals("*") || val.equals(".")){
                i++;
            } else {
                noWall = false;
            }
        }
        if (cursorCol > col - i) {
            return true;
        } else if (cursorCol == col - i) {
            return cursorRow >= row;
        } else {
            return false;
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
            String t = this.board[row][i-1].getVal();
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
            String t = this.board[row][i+1].getVal();
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
            String t = this.board[i-1][col].getVal();
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
            String t = this.board[i+1][col].getVal();
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
     * Checks to see if the configuration is a valid solution
     * @return true if the configuration is the solution
     */
    @Override
    public boolean isGoal() {
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                String t = this.board[i][j].getVal();
                if (t.equals(".")) {
                    this.board[i][j] = new ModelData(i, j, t, ModelData.Commands.ERROR_VERIFY);
                    return false;
                } else if (t.equals("L")) {
                    boolean cU = checkUp(i, j);
                    boolean cD = checkDown(i, j);
                    boolean cL = checkLeft(i, j);
                    boolean cR = checkRight(i, j);
                    if (!(cU && cD && cR && cL)) {
                        this.board[i][j] = new ModelData(i, j, t, ModelData.Commands.ERROR_VERIFY);
                        return false;
                    }
                } else if (t.equals("0") || t.equals("1") || t.equals("2") || t.equals("3") || t.equals("4")) {
                    int pillar = Integer.parseInt(t);
                    int cP = checkPillar(i, j);
                    if (!(pillar == cP)) {
                        this.board[i][j] = new ModelData(i, j, t, ModelData.Commands.ERROR_VERIFY);
                        return false;
                    }
                }
            }
        }
        return true;
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
            if (this.board[row-1][col].getVal().equals("L")) {
                count++;
            }
        }
        if (row + 1 < this.row) {
            if (this.board[row + 1][col].getVal().equals("L")) {
                count++;
            }
        }
        if (col - 1 >= 0) {
            if (this.board[row][col-1].getVal().equals("L")) {
                count++;
            }
        }
        if (col + 1 < this.col) {
            if (this.board[row ][col+1].getVal().equals("L")) {
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
        StringBuilder result = new StringBuilder();
        for (int row=0; row<this.row; ++row) {
            result.append("\n");
            for (int col=0; col<this.col; ++col) {
                result.append(this.board[row][col].getVal());
                result.append(" ");
            }
        }
        return result.toString();
    }
}
