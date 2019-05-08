package solar.rpg.skyblock.minigames.extra.tictactoe;

import org.bukkit.DyeColor;

public class TicTacToeBoard {
    public static final int ROWS = 3;
    public static final int COLS = 3;

    // package access
    public final Cell[][] cells;  // a board composes of ROWS-by-COLS Cell instances
    public int currentRow;
    public int currentCol;  // the current seed's row and column

    /**
     * Constructor to initialize the game board
     */
    public TicTacToeBoard() {
        cells = new Cell[ROWS][COLS];  // allocate the array
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col); // allocate element of the array
            }
        }
    }

    /**
     * Initialize (or re-initialize) the contents of the game board
     */
    public void init() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].clear();  // clear the cell content
            }
        }
    }

    /**
     * Return true if it is a draw (i.e., no more EMPTY cell)
     */
    public boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.EMPTY) {
                    return false; // an empty seed found, not a draw, exit
                }
            }
        }
        return true; // no empty cell, it's a draw
    }

    /**
     * Return true if the player with "theSeed" has won after placing at
     * (currentRow, currentCol)
     */
    public boolean hasWon(Seed theSeed) {
        return (cells[currentRow][0].content == theSeed         // 3-in-the-row
                && cells[currentRow][1].content == theSeed
                && cells[currentRow][2].content == theSeed
                || cells[0][currentCol].content == theSeed      // 3-in-the-column
                && cells[1][currentCol].content == theSeed
                && cells[2][currentCol].content == theSeed
                || currentRow == currentCol            // 3-in-the-diagonal
                && cells[0][0].content == theSeed
                && cells[1][1].content == theSeed
                && cells[2][2].content == theSeed
                || currentRow + currentCol == 2    // 3-in-the-opposite-diagonal
                && cells[0][2].content == theSeed
                && cells[1][1].content == theSeed
                && cells[2][0].content == theSeed);
    }

    public class Cell {

        private final int row;
        private final int col;
        public Seed content;

        /**
         * Constructor to initialize this cell
         */
        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            clear();  // clear content
        }

        /**
         * Clear the cell content to EMPTY
         */
        public void clear() {
            content = Seed.EMPTY;
        }
    }

    /**
     * Store the seed for a player.
     */
    public enum Seed {

        EMPTY(DyeColor.WHITE, "No One"),
        CROSS(DyeColor.RED, "Crosses"),
        NOUGHT(DyeColor.BLUE, "Noughts");

        public final DyeColor color;
        public final String name;

        Seed(DyeColor color, String name) {
            this.color = color;
            this.name = name;
        }
    }

    /**
     * States of the game.
     */
    public enum GameState {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }
}