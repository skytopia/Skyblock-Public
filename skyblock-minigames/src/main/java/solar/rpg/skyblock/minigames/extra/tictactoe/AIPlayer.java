package solar.rpg.skyblock.minigames.extra.tictactoe;


public abstract class AIPlayer {
    final int ROWS = 3;  // number of rows
    final int COLS = 3;  // number of columns

    final TicTacToeBoard.Cell[][] cells; // the board's ROWS-by-COLS array of Cells
    TicTacToeBoard.Seed mySeed;    // computer's seed
    TicTacToeBoard.Seed oppSeed;   // opponent's seed

    /**
     * Constructor with reference to game board
     */
    AIPlayer(TicTacToeBoard board) {
        cells = board.cells;
    }

    public TicTacToeBoard.Seed getSeed() {
        return mySeed;
    }

    /**
     * Set/change the seed used by computer and opponent
     */
    public void setSeed(TicTacToeBoard.Seed seed) {
        this.mySeed = seed;
        oppSeed = (mySeed == TicTacToeBoard.Seed.CROSS) ? TicTacToeBoard.Seed.NOUGHT : TicTacToeBoard.Seed.CROSS;
    }

    /**
     * Abstract method to get next move. Return int[2] of {row, col}
     */
    abstract int[] move();  // to be implemented by subclasses
}