package lasers.model;

/**
 * Details for each tile in the game board
 *
 * @author Gerald Galano
 * @author Quynh Duong
 */
public class Tile {

    /** All possible states of the tiles */
    public enum Commands {
        ADD,
        ERROR_ADD,
        REMOVE,
        ERROR_REMOVE,
        VERIFY,
        ERROR_VERIFY,
        INITIALIZE
    }

    /** row coordinate of the tile */
    private int row;
    /** column coordinate of the tile */
    private int col;
    /** the value of the tile */
    private String val;
    /** state from the Commands enum */
    private Commands command;

    /** Creates a new instance of a tile */
    public Tile(int row, int col, String val, Commands command) {
        this.row = row;
        this.col = col;
        this.val = val;
        this.command = command;
    }

    /**
     * Gets the value of the tile
     * @return tile value string
     */
    public String getVal() {
        return this.val;
    }

    /**
     * Gets the row of the tile
     * @return tile row integer
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Gets the column of the tile
     * @return tile column integer
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Gets the command of the tile
     * @return tile command Commands
     */
    public Commands getCommand() {
        return this.command;
    }
}
