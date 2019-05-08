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
import solar.rpg.skyblock.minigames.extra.connectfour.ConnectFourAI;
import solar.rpg.skyblock.minigames.extra.connectfour.ConnectFourBoard;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConnectFour extends Minigame implements BoardGame, NewbieFriendly {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new ConnectRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Connect Four";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BIRCH_DOOR_ITEM);
    }

    public String[] getDescription() {
        return new String[]{"For those with a big heart and a good hustle!",
                ChatColor.ITALIC + "Beat an AI in a match of Connect Four!",
                ChatColor.ITALIC + "Try to get a four-in-a-row on the board!",
                ChatColor.ITALIC + "Difficulty of AI varies each turn."};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    public String getSummary() {
        return "Outsmart the Connect Four AI!";
    }

    public String getObjectiveWord() {
        return "points awarded";
    }

    public int getDuration() {
        return 600;
    }

    public int getGold() {
        return 3;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getMaxReward() {
        return 5000;
    }

    private class ConnectRun extends AttemptsMinigameTask implements Listener {

        ConnectFourBoard board;
        ConnectFourAI comp;
        private HashMap<Block, Integer> clickable;
        private Location gen;

        ConnectRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        public void onStart() {
            clickable = new HashMap<>();
            board = new ConnectFourBoard();

            gen = generateLocation(100, 20, 140, true, false);
            for (int x = 0; x <= 10; x++)
                for (int y = 0; y <= 9; y++)
                    for (int z = 0; z <= 11; z++)
                        if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + z).getType() != Material.AIR) {
                            stop();
                            main.messageAll(getParticipants(), ChatColor.RED + "There was an error loading this minigame. Please consult Sky.");
                            return;
                        }

            //Generate floor.
            for (int x = 0; x <= 10; x++)
                for (int z = 0; z <= 11; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

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
                registerClicks(generateGridLocation(i, gen), i - 1);

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));
            selectPlayer();
        }

        /**
         * Player wins: 3 points
         * Player tied: 2 points
         * Player lost: 1 point
         * Player lost in less than 10 turns: 0 points
         */
        void addPoints() {
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
         * Called after a player clicks a square.
         *
         * @param block       What block was clicked.
         * @param responsible Who clicked it.
         */
        void reveal(Block block, Player responsible) {
            if (!clickable.containsKey(block)) return;

            int col = clickable.get(block);
            int row = board.drop(col);

            if (row == -1) {
                responsible.sendMessage(ChatColor.RED + "You can't move here! Try again.");
                return;
            }

            Block toChange = generateGameLocation(row, col, gen).getBlock();
            toChange.setType(Material.WOOL);
            toChange.setData((byte) 11);

            if (board.win() || board.full())
                addPoints();

            canMove = false;
            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);

            // Run the computer's move async to not lag the server.
            Bukkit.getScheduler().runTaskLaterAsynchronously(main.main().plugin(), () -> {
                if (points > 0) {
                    Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 39L);
                } else {
                    comp = new ConnectFourAI(board.getGrid(), 5 + main.main().rng().nextInt(2));

                    final int col1 = comp.calcValue();
                    final int row1 = board.drop(col1);

                    // After we do the calculation, run the rest of the shit sync to not crash.
                    Bukkit.getScheduler().runTask(main.main().plugin(), () -> {
                        Block toChange1 = generateGameLocation(row1, col1, gen).getBlock();
                        toChange1.setType(Material.WOOL);
                        toChange1.setData((byte) 14);
                        main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_PRIMED, 2F);

                        if (board.win() || board.full()) {
                            addPoints();
                            Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 39L);
                        } else {
                            canMove = true;
                            selectPlayer();
                        }
                    });
                }
            }, 19L);
        }

        /**
         * Registers clickable squares.
         *
         * @param loc        The bottom left origination location.
         * @param allocation The allocation from 1-9 given.
         */
        void registerClicks(Location loc, int allocation) {
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Translates an integer from 1 to 9 to a space on the board.
         *
         * @param allocation The integer allocation from 1-9.
         * @param original   The bottom left of the board.
         * @return The grid location.
         */
        Location generateGridLocation(int allocation, Location original) {
            return original.clone().add(allocation - 1 + 2, 0, 9);
        }

        /**
         * Translates an x,y coordinate to a board space.
         *
         * @param row      Row. (y)
         * @param col      Column. (x)
         * @param original Bottom left of the minigame board.
         * @return TicTacToeBoard location.
         */
        Location generateGameLocation(int row, int col, Location original) {
            return original.clone().add(2 + col, 8 - row, 11);
        }

        public void onFinish() {
            returnParticipants();
            board = null;
            comp = null;
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
            if (canMove(event.getPlayer()))
                reveal(event.getClickedBlock(), event.getPlayer());
        }
    }
}
