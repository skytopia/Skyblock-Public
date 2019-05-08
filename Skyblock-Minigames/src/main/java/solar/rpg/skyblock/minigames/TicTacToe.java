package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.island.minigames.task.AttemptsMinigameTask;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.minigames.extra.tictactoe.AIPlayerMinimax;
import solar.rpg.skyblock.minigames.extra.tictactoe.TicTacToeBoard;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TicTacToe extends Minigame implements BoardGame, NewbieFriendly {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TicTacRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Tic Tac Toe";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.STRUCTURE_VOID);
    }

    public String[] getDescription() {
        return new String[]{"First they take our noughts.. and then our crosses!",
                ChatColor.ITALIC + "Beat an AI in a two-round match of Tic Tac Toe!",
                ChatColor.ITALIC + "Round 1: Players move first",
                ChatColor.ITALIC + "Round 2: AI moves first"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    public String getSummary() {
        return "Outsmart the Tic Tac Toe AI!";
    }

    public String getObjectiveWord() {
        return "points awarded";
    }

    public int getDuration() {
        return 240;
    }

    public int getGold() {
        return 3;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getMaxReward() {
        return 2500;
    }

    private class TicTacRun extends AttemptsMinigameTask implements Listener {

        TicTacToeBoard board;
        TicTacToeBoard.GameState currentState;
        TicTacToeBoard.Seed currentPlayer;
        boolean round2 = false;
        private HashMap<Block, Short> clickable;
        private Location gen;
        private boolean canMove;

        TicTacRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        public void onStart() {
            clickable = new HashMap<>();

            board = new TicTacToeBoard();
            currentState = TicTacToeBoard.GameState.PLAYING;
            currentPlayer = TicTacToeBoard.Seed.CROSS;
            canMove = true;

            gen = generateLocation(100, 20, 140, true, false);
            for (int x = 0; x <= 11; x++)
                for (int z = 0; z <= 13; z++)
                    if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z).getType() != Material.AIR) {
                        error();
                        return;
                    }

            for (int x = 0; x <= 11; x++)
                for (int z = 0; z <= 13; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            for (short i = 1; i <= 9; i++)
                registerClicks(generateGridLocation(i, gen), i);

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));

            selectPlayer();
        }

        /**
         * Updates the state of the game based on the state of the board.
         *
         * @param theSeed Who we're checking for the win.
         */
        void updateGame(TicTacToeBoard.Seed theSeed) {
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
         * @param row     The row number. (x)
         * @param col     The column number. (y)
         * @param theSeed The player.
         * @return Whether or not it was a valid move.
         */
        boolean playerMove(int row, int col, TicTacToeBoard.Seed theSeed) {
            if (row >= 0 && row < TicTacToeBoard.ROWS && col >= 0 && col < TicTacToeBoard.COLS
                    && board.cells[row][col].content == TicTacToeBoard.Seed.EMPTY) {
                board.cells[row][col].content = theSeed;
                board.currentRow = row;
                board.currentCol = col;
                return true;
            } else return false;
        }

        /**
         * Moves onto round 2.
         * CPU moves first.
         */
        void nextRound() {
            addPoints();
            if (round2) {
                stop();
                return;
            } else
                round2 = true;


            board.init();
            currentPlayer = TicTacToeBoard.Seed.NOUGHT;
            currentState = TicTacToeBoard.GameState.PLAYING;
            for (short i = 1; i <= 9; i++)
                clearSquare(i);

            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                AIPlayerMinimax ai = new AIPlayerMinimax(board); // Create an AI to decide the move for this turn.
                ai.setSeed(TicTacToeBoard.Seed.CROSS); // Decide their seed.
                int[] move = ai.move(); // Calculate the move.

                playerMove(move[0], move[1], ai.getSeed());
                colorSquare(gridToAlloc(move), ai.getSeed()); // Color the square.
                updateGame(ai.getSeed()); // Update the game.
                main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_PRIMED, 2F);
                selectPlayer(); // Select the next player.
                canMove = true;
            }, 20L);
        }

        /**
         * Player won: 2 points
         * Player tied/died: 1 point
         * Player lost: no points
         */
        void addPoints() {
            switch (currentState) {
                case DRAW:
                    points += 1;
                    break;
                case NOUGHT_WON:
                    if (currentPlayer == TicTacToeBoard.Seed.NOUGHT)
                        points += 2;
                    break;
                case CROSS_WON:
                    if (currentPlayer == TicTacToeBoard.Seed.CROSS)
                        points += 2;
                    break;
            }
        }

        /**
         * Called after a player clicks a square.
         *
         * @param block       What block was clicked.
         * @param responsible Who clicked it.
         */
        void reveal(Block block, Player responsible) {
            if (!clickable.containsKey(block)) return;
            short ID = clickable.get(block);
            int[] grid = allocToGrid(ID);

            if (!playerMove(grid[0], grid[1], currentPlayer)) {
                responsible.sendMessage(ChatColor.RED + "This move was invalid. Try again.");
                return;
            }

            canMove = false;

            colorSquare(ID, currentPlayer);
            updateGame(currentPlayer);

            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                if (currentState != TicTacToeBoard.GameState.PLAYING) {
                    Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::nextRound, 39L);
                } else {
                    AIPlayerMinimax ai = new AIPlayerMinimax(board); // Create an AI to decide the move for this turn.
                    ai.setSeed(currentPlayer == TicTacToeBoard.Seed.CROSS ? TicTacToeBoard.Seed.NOUGHT : TicTacToeBoard.Seed.CROSS); // Decide their seed.
                    int[] move = ai.move(); // Calculate the move.

                    main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_PRIMED, 2F);
                    playerMove(move[0], move[1], ai.getSeed()); // Make the move on the board.

                    colorSquare(gridToAlloc(move), ai.getSeed()); // Color the square.
                    updateGame(ai.getSeed()); // Update the game.

                    if (currentState != TicTacToeBoard.GameState.PLAYING) {
                        Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::nextRound, 39L);
                    } else {
                        selectPlayer();
                        canMove = true;
                    }
                }
            }, 19L);
            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);
        }

        /**
         * Registers clickable squares.
         *
         * @param loc        The bottom left origination location.
         * @param allocation The allocation from 1-9 given.
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
         * Colors a square.
         *
         * @param allocation Square ID 1-9.
         */
        void colorSquare(short allocation, TicTacToeBoard.Seed seed) {
            Location loc = generateGridLocation(allocation, gen);

            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(seed.color.getWoolData());
            loc.getBlock().getState().update(true);

            loc.add(1, 0, 0);
            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(seed.color.getWoolData());
            loc.getBlock().getState().update(true);

            loc.add(0, 0, 1);
            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(seed.color.getWoolData());
            loc.getBlock().getState().update(true);

            loc.subtract(1, 0, 0);
            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(seed.color.getWoolData());
            loc.getBlock().getState().update(true);
        }

        /**
         * Clears a square.
         *
         * @param allocation Square ID 1-9.
         */
        void clearSquare(short allocation) {
            Location loc = generateGridLocation(allocation, gen);

            loc.getBlock().setType(Material.BEDROCK);
            loc.add(1, 0, 0);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(0, 0, 1);
            loc.getBlock().setType(Material.BEDROCK);
            loc.subtract(1, 0, 0);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Translates an integer from 1 to 9 to a space on the board.
         *
         * @param allocation The integer allocation from 1-9.
         * @param original   The bottom left of the board.
         * @return The grid location.
         */
        Location generateGridLocation(short allocation, Location original) {
            Location result = original.clone().add(2, 0, 4);
            switch (allocation) {
                case 1:
                    return result;
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

        int[] allocToGrid(short allocation) {
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
            }
            return new int[2];
        }

        short gridToAlloc(int[] grid) {
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

        public void onFinish() {
            returnParticipants();
            board = null;
            currentState = null;
            currentPlayer = null;
            clickable.clear();
            clickable = null;
        }

        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() != Material.BEDROCK) return;
            if (canMove && canMove(event.getPlayer()))
                reveal(event.getClickedBlock(), event.getPlayer());
        }
    }
}
