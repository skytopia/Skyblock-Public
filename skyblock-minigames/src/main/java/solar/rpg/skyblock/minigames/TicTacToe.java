package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.minigames.extra.tictactoe.AIPlayerMinimax;
import solar.rpg.skyblock.minigames.extra.tictactoe.TicTacToeBoard;
import solar.rpg.skyblock.minigames.tasks.AttemptsMinigameTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TicTacToe extends Minigame implements BoardGame, NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TicTacToeTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Tic Tac Toe";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.STRUCTURE_VOID);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Beat an AI in of Tic Tac Toe!",
                ChatColor.ITALIC + "Round 1: Players move first",
                ChatColor.ITALIC + "Round 2: AI moves first",
                "\"First they take our noughts..",
                "and then our crosses!\"",};
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Outsmart the Tic Tac Toe AI!";
    }

    @Override
    public String getObjectiveWord() {
        return "points awarded";
    }

    @Override
    public int getDuration() {
        return 240;
    }

    @Override
    public int getGold() {
        return 3;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getMaxReward() {
        return 2500;
    }

    private class TicTacToeTask extends AttemptsMinigameTask {

        /* Tic Tac Toe implementation classes. */
        private TicTacToeBoard board;
        private TicTacToeBoard.GameState currentState;
        private TicTacToeBoard.Seed playerSeed;

        /* True when the second round is playing. */
        private boolean round2 = false;

        /* Holds all blocks where moves can be made. */
        private HashMap<Block, Short> clickable;

        TicTacToeTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        @Override
        public void onStart() {
            clickable = new HashMap<>();

            board = new TicTacToeBoard();
            currentState = TicTacToeBoard.GameState.PLAYING;
            playerSeed = TicTacToeBoard.Seed.CROSS;
            canMove = true;

            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, 11, 6, 13)) {
                error();
                return;
            }

            makePlatform(gen, 11, 13, Material.STONE_BRICKS);

            for (short i = 1; i <= 9; i++)
                registerClicks(generateGridLocation(i), i);

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));

            selectPlayer();
        }

        /**
         * Checks if there is a winner or a tie.
         * Notifies participants if there is.
         *
         * @param theSeed Who we're checking for the win.
         */
        private void checkWinner(TicTacToeBoard.Seed theSeed) {
            if (board.hasWon(theSeed)) {  // check for win
                currentState = (theSeed == TicTacToeBoard.Seed.CROSS) ? TicTacToeBoard.GameState.CROSS_WON : TicTacToeBoard.GameState.NOUGHT_WON;
                main.messageAll(getParticipants(), ChatColor.GRAY + theSeed.name + " has won this round!");
                main.soundAll(getParticipants(), Sound.ENTITY_PARROT_HURT, 2F);
            } else if (board.isDraw()) {  // check for draw
                currentState = TicTacToeBoard.GameState.DRAW;
                main.messageAll(getParticipants(), ChatColor.GRAY + "This round was a draw!");
                main.soundAll(getParticipants(), Sound.BLOCK_ANVIL_BREAK, 2F);
            }
        }

        /**
         * Performs a move.
         *
         * @param row  The row number. (x)
         * @param col  The column number. (y)
         * @param seed The player who made the move.
         * @return True if the move was valid.
         */
        private boolean playerMove(int row, int col, TicTacToeBoard.Seed seed) {
            if (row >= 0 && row < TicTacToeBoard.ROWS && col >= 0 && col < TicTacToeBoard.COLS
                    && board.cells[row][col].content == TicTacToeBoard.Seed.EMPTY) {
                board.cells[row][col].content = seed;
                board.currentRow = row;
                board.currentCol = col;
                return true;
            } else return false;
        }

        /**
         * When playing, there are two rounds.
         * Once the first round is complete, the AI will move first.
         */
        private void nextRound() {
            addPoints();
            if (round2) {
                stop();
                return;
            } else
                round2 = true;

            board.init();
            playerSeed = TicTacToeBoard.Seed.NOUGHT;
            currentState = TicTacToeBoard.GameState.PLAYING;
            for (short i = 1; i <= 9; i++)
                resetTile(i);

            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                AIPlayerMinimax ai = new AIPlayerMinimax(board); // Create an AI to decide the move for this turn.
                ai.setSeed(TicTacToeBoard.Seed.CROSS); // Decide their seed.
                int[] move = ai.move(); // Calculate the move.

                playerMove(move[0], move[1], ai.getSeed());
                colorSquare(gridToAlloc(move), ai.getSeed()); // Color the square.
                checkWinner(ai.getSeed()); // Update the game.
                main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_PRIMED, 2F);
                selectPlayer(); // Select the next player.
                canMove = true;
            }, 20L);
        }

        /**
         * Adds points after the winner has been decided.
         * <p>
         * Player wins: 2 points
         * Player tied: 1 point
         * Player lost: no points
         */
        private void addPoints() {
            switch (currentState) {
                case DRAW:
                    points += 1;
                    break;
                case NOUGHT_WON:
                    if (playerSeed == TicTacToeBoard.Seed.NOUGHT)
                        points += 2;
                    break;
                case CROSS_WON:
                    if (playerSeed == TicTacToeBoard.Seed.CROSS)
                        points += 2;
                    break;
            }
        }

        /**
         * Makes a move on the board.
         *
         * @param block  What block was clicked.
         * @param player Who clicked it.
         */
        private void move(Block block, Player player) {
            if (!clickable.containsKey(block)) return;
            short ID = clickable.get(block);
            int[] grid = allocToGrid(ID);

            if (!playerMove(grid[0], grid[1], playerSeed)) {
                player.sendMessage(ChatColor.RED + "This move was invalid. Try again.");
                return;
            }

            canMove = false;

            colorSquare(ID, playerSeed);
            checkWinner(playerSeed);

            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                if (currentState != TicTacToeBoard.GameState.PLAYING) {
                    Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::nextRound, 39L);
                } else {
                    AIPlayerMinimax ai = new AIPlayerMinimax(board); // Create an AI to decide the move for this turn.
                    ai.setSeed(playerSeed == TicTacToeBoard.Seed.CROSS ? TicTacToeBoard.Seed.NOUGHT : TicTacToeBoard.Seed.CROSS); // Decide their seed.
                    int[] move;
                    if (main.main().rng().nextInt(10) == 7) {
                        // 1 out of 10 times, the AI will just select a random move.
                        // Randomly generate moves until a valid one is played.
                        move = randomMove();
                        while (!playerMove(move[0], move[1], ai.getSeed()))
                            move = randomMove();
                    } else {
                        // Calculate move and place it on the board.
                        move = ai.move();
                        playerMove(move[0], move[1], ai.getSeed());
                    }

                    main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_PRIMED, 2F);
                    colorSquare(gridToAlloc(move), ai.getSeed()); // Color the square.
                    checkWinner(ai.getSeed()); // Update the game.

                    if (currentState != TicTacToeBoard.GameState.PLAYING)
                        Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::nextRound, 39L);
                    else {
                        selectPlayer();
                        canMove = true;
                    }
                }
            }, 19L);
            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);
        }

        /**
         * @return A random move. Not checked.
         */
        private int[] randomMove() {
            return new int[]{main.main().rng().nextInt(3), main.main().rng().nextInt(3)};
        }

        /**
         * Registers locations for move selection so clicks can be listened for.
         *
         * @param loc        Location of a clickable tile block.
         * @param allocation The board column index that this location corresponds to.
         */
        void registerClicks(Location loc, short allocation) {
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);

            loc.add(1, 0, 0);
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);

            loc.add(0, 0, 1);
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);

            loc.subtract(1, 0, 0);
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Colors a tile with the player's color.
         *
         * @param allocation Square ID 1-9.
         */
        private void colorSquare(short allocation, TicTacToeBoard.Seed seed) {
            Location loc = generateGridLocation(allocation);

            loc.getBlock().setType(seed.wool);
            loc.getBlock().getState().update(true);

            loc.add(1, 0, 0);
            loc.getBlock().setType(seed.wool);
            loc.getBlock().getState().update(true);

            loc.add(0, 0, 1);
            loc.getBlock().setType(seed.wool);
            loc.getBlock().getState().update(true);

            loc.subtract(1, 0, 0);
            loc.getBlock().setType(seed.wool);
            loc.getBlock().getState().update(true);
        }

        /**
         * Sets a tile back to bedrock.
         *
         * @param allocation Square ID 1-9.
         */
        private void resetTile(short allocation) {
            Location loc = generateGridLocation(allocation);

            loc.getBlock().setType(Material.BEDROCK);
            loc.add(1, 0, 0);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(0, 0, 1);
            loc.getBlock().setType(Material.BEDROCK);
            loc.subtract(1, 0, 0);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Takes a board index number and returns the appropriate
         * spot to place the clickable block so the player can make moves.
         *
         * @param allocation The integer allocation from 1-9.
         * @return The grid location.
         */
        Location generateGridLocation(short allocation) {
            Location result = gen.clone().add(2, 0, 4);
            switch (allocation) {
                case 2:
                    return result.add(3, 0, 0);
                case 3:
                    return result.add(6, 0, 0);
                case 4:
                    return result.add(0, 0, 3);
                case 5:
                    return result.add(3, 0, 3);
                case 6:
                    return result.add(6, 0, 3);
                case 7:
                    return result.add(0, 0, 6);
                case 8:
                    return result.add(3, 0, 6);
                case 9:
                    return result.add(6, 0, 6);
                default:
                    return result;
            }
        }

        /**
         * Takes a board index number and returns its
         * x and y position in the board array.
         *
         * @param allocation The integer allocation from 1-9.
         * @return The grid location.
         */
        private int[] allocToGrid(short allocation) {
            switch (allocation) {
                case 1:
                    return new int[]{0, 0};
                case 2:
                    return new int[]{1, 0};
                case 3:
                    return new int[]{2, 0};
                case 4:
                    return new int[]{0, 1};
                case 5:
                    return new int[]{1, 1};
                case 6:
                    return new int[]{2, 1};
                case 7:
                    return new int[]{0, 2};
                case 8:
                    return new int[]{1, 2};
                case 9:
                    return new int[]{2, 2};
                default:
                    return new int[2];
            }
        }

        /**
         * Takes a grid location and returns its index
         * allocation number.
         *
         * @param grid Grid location.
         * @return Board index number.
         */
        private short gridToAlloc(int[] grid) {
            switch (grid[0]) {
                case 0:
                    if (grid[1] == 0)
                        return 1;
                    else if (grid[1] == 1)
                        return 4;
                    else if (grid[1] == 2)
                        return 7;
                    break;
                case 1:
                    if (grid[1] == 0)
                        return 2;
                    else if (grid[1] == 1)
                        return 5;
                    else if (grid[1] == 2)
                        return 8;
                    break;
                case 2:
                    if (grid[1] == 0)
                        return 3;
                    else if (grid[1] == 1)
                        return 6;
                    else if (grid[1] == 2)
                        return 9;
                    break;
            }
            return 0;
        }

        @Override
        public void onFinish() {
            returnParticipants();
            board = null;
            currentState = null;
            playerSeed = null;
            clickable.clear();
            clickable = null;
        }

        @Override
        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() != Material.BEDROCK) return;
            if (canMove && canMove(event.getPlayer()))
                move(event.getClickedBlock(), event.getPlayer());
        }
    }
}
