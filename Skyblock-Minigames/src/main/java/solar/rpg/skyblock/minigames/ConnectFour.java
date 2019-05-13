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
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.minigames.extra.connectfour.ConnectFourAI;
import solar.rpg.skyblock.minigames.extra.connectfour.ConnectFourBoard;
import solar.rpg.skyblock.minigames.tasks.AttemptsMinigameTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConnectFour extends Minigame implements BoardGame, NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new ConnectFourTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Connect Four";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BIRCH_DOOR_ITEM);
    }

    @Override
    public String[] getDescription() {
        return new String[]{"For those with a big heart and a good hustle!",
                ChatColor.ITALIC + "Beat an AI in a match of Connect Four!",
                ChatColor.ITALIC + "Try to get a four-in-a-row on the board!",
                ChatColor.ITALIC + "Difficulty of AI varies each turn."};
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Outsmart the Connect Four AI!";
    }

    @Override
    public String getObjectiveWord() {
        return "points awarded";
    }

    @Override
    public int getDuration() {
        return 600;
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
        return 5000;
    }

    /**
     * @see ConnectFourBoard
     * @see ConnectFourAI
     */
    private class ConnectFourTask extends AttemptsMinigameTask implements Listener {

        /* Connect Four implementation classes. */
        private ConnectFourBoard board;
        private ConnectFourAI comp;

        /* Holds all blocks where moves can be made. */
        private HashMap<Block, Integer> clickable;

        ConnectFourTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        @Override
        public void onStart() {
            clickable = new HashMap<>();
            board = new ConnectFourBoard();

            /* The generated location where the minigame will play. */
            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, 10, 9, 11)) {
                error();
                return;
            }

            //Generate platform.
            makePlatform(gen, 10, 11, Material.SMOOTH_BRICK);

            // Generate iron fence backdrop.
            for (int x = 0; x <= 10; x++)
                for (int y = 0; y <= 5; y++) {
                    if (x != 0 && x != 10) continue;
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y + 3, gen.getBlockZ() + 11);
                    bl.setType(Material.IRON_FENCE);
                    placed.add(bl);
                }

            // Generate stone backdrop.
            for (int x = 0; x <= 8; x++)
                for (int y = 0; y <= 7; y++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x + 1, gen.getBlockY() + y + 2, gen.getBlockZ() + 11);
                    if (y == 7)
                        bl.setType(Material.STEP);
                    else
                        bl.setType(Material.STONE);
                    placed.add(bl);
                }

            // Generate wool backdrop.
            for (int x = 0; x <= 6; x++)
                for (int y = 0; y <= 5; y++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x + 2, gen.getBlockY() + y + 3, gen.getBlockZ() + 11);
                    bl.setType(Material.WOOL);
                    placed.add(bl);
                }

            // Generate stone legs.
            for (int x = 0; x <= 10; x++)
                if (x == 0 || x == 10) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + 1, gen.getBlockZ() + 11);
                    bl.setType(Material.STONE);
                    placed.add(bl);
                }

            for (int i = 1; i <= 7; i++)
                registerClicks(generateGridLocation(i), i - 1);

            // Teleports players on to the platform.
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));

            // Whoever moves first on connect four is mathematically guaranteed to win if
            // they play absolutely perfectly. The AI is not perfect, however.
            // Normal difficulty: Player moves first.
            // Harder difficulty: Computer moves first.
            if (difficulty.equals(Difficulty.HARDER)) computerMove();
            else selectPlayer();
        }

        /**
         * Adds points after the winner has been decided.
         * <p>
         * Player wins: 3 points (gold)
         * Player tied: 2 points (silver)
         * Player lost: 1 point (bronze)
         * Player lost in less than 10 turns: 0 points (none)
         */
        private void addPoints() {
            if (board.full()) {
                points += 2;
                main.messageAll(getParticipants(), ChatColor.GRAY + "The match ended in a draw.");
            } else {
                if (board.getPlayer() == 1) {
                    points += board.totalMoves < 10 ? 0 : 1;
                    main.messageAll(getParticipants(), ChatColor.GRAY + "The computer won this match.");
                } else {
                    points += 3;
                    main.messageAll(getParticipants(), ChatColor.GRAY + "Congratulations! You won!");
                }
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

            int col = clickable.get(block);
            int row = board.drop(col);

            // Check that the column isn't full.
            if (row == -1) {
                player.sendMessage(ChatColor.RED + "You can't move here! Try again.");
                return;
            }

            // Places the player's move.
            Block toChange = generateGameLocation(row, col).getBlock();
            toChange.setType(Material.WOOL);
            toChange.setData((byte) 11);

            // If the player won or the board is full, the game is over.
            if (board.win() || board.full())
                addPoints();

            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);

            computerMove();
        }

        /**
         * The computer makes a move.
         */
        private void computerMove() {
            // No additional moves can be made while the computer is thinking.
            canMove = false;

            Bukkit.getScheduler().runTaskLaterAsynchronously(main.main().plugin(), () -> {
                if (points > 0)
                    Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 39L);
                else {
                    // Create a new computer with latest board information.
                    comp = new ConnectFourAI(board.getGrid(), 5 + main.main().rng().nextInt(2));

                    // Let computer make move, then grab the row that was selected.
                    int compCol = comp.calcValue();
                    int compRow = board.drop(compCol);

                    // After we do the calculation, run the rest of the shit on the main thread to avoid concurrency issues.
                    Bukkit.getScheduler().runTask(main.main().plugin(), () -> {
                        // Place the computer's move.
                        Block toChange1 = generateGameLocation(compRow, compCol).getBlock();
                        toChange1.setType(Material.WOOL);
                        toChange1.setData((byte) 14);
                        main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_PRIMED, 2F);

                        // If the player won or the board is full, the game is over.
                        if (board.win() || board.full()) {
                            addPoints();
                            Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 39L);
                        } else {
                            // Otherwise, move again!
                            canMove = true;
                            selectPlayer();
                        }
                    });
                }
            }, 19L);
        }

        /**
         * Registers locations for move selection so clicks can be listened for.
         *
         * @param loc        Location of a clickable block.
         * @param allocation The board column index that this location corresponds to.
         */
        private void registerClicks(Location loc, int allocation) {
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Takes a board column index number and returns the appropriate
         * spot to place the clickable block so the player can make moves.
         *
         * @param allocation The index allocation from 1-9.
         * @return The location of the clickable block.
         */
        private Location generateGridLocation(int allocation) {
            return gen.clone().add(allocation - 1 + 2, 0, 9);
        }

        /**
         * Takes a row and column from the board model and
         * returns the appropriate block on the board.
         *
         * @param row Row. (y)
         * @param col Column. (x)
         * @return TicTacToeBoard location.
         */
        private Location generateGameLocation(int row, int col) {
            return gen.clone().add(2 + col, 8 - row, 11);
        }

        @Override
        public void onFinish() {
            returnParticipants();
            board = null;
            comp = null;
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
            if (canMove(event.getPlayer()))
                move(event.getClickedBlock(), event.getPlayer());
        }
    }
}
