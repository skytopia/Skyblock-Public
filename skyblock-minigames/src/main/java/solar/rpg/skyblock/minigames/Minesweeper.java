package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.*;
import solar.rpg.skyblock.minigames.tasks.AttemptsMinigameTask;
import solar.rpg.skyblock.util.StringUtility;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Minesweeper extends Minigame implements FlawlessEnabled, BoardGame {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MinesweeperTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Minesweeper";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FIREWORK_STAR);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Solve a classic minesweeper grid!",
                ChatColor.ITALIC + "Wool indicates the level of danger!",
                ChatColor.UNDERLINE + "((right click to reveal safe tiles))",
                ChatColor.UNDERLINE + "((left click to reveal bomb tiles))",
                "\"（╯°□°）╯ ︵(\\ .o.)\\\""
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.SIMPLE, Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Solve the Minesweeper grid!";
    }

    @Override
    public String getObjectiveWord() {
        return "squares solved";
    }

    @Override
    public int getMinimumPlayers() {
        return 1;
    }

    @Override
    public boolean enforceMinimum() {
        return false;
    }

    @Override
    public int getDuration() {
        return 600;
    }

    @Override
    public int getGold() {
        return 50;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getFlawless() {
        return 64;
    }

    @Override
    public int getFlawlessPlayerMinimum() {
        return 1;
    }

    @Override
    public int getMaxReward() {
        return 5000;
    }

    @Override
    public Playstyle getPlaystyle() {
        return Playstyle.COOPERATIVE;
    }

    private class MinesweeperTask extends AttemptsMinigameTask {

        /*
         * Game constants.
         *
         * Simple: Board is the regular 8x8 with 14 mines. Mines do not cause a game over.
         * Normal: Board is the regular 8x8 size with 14 mines.
         * Harder: Board size is increased to 16x16 with 56 mines.
         */
        private final int boardX = difficulty == Difficulty.HARDER ? 19 : 11;
        private final int boardZ = difficulty == Difficulty.HARDER ? 21 : 13;
        private final int squares = difficulty == Difficulty.HARDER ? 256 : 64;
        private final int mines = difficulty == Difficulty.HARDER ? 56 : 14;
        private final int scoreDelay = difficulty == Difficulty.HARDER ? 4 : 1;

        /*
         * Number of correct moves remaining before a point is awarded.
         * Normal: every tile revealed is one point.
         * Harder: every 4 tiles revealed is one point.
         */
        private int delay = scoreDelay;

        /* Maps clickable blocks to tiles on the board. */
        private HashMap<Block, Short> clickable;

        /* Grid indexes that contain bombs. */
        private ArrayList<Short> bombs;

        /* Grid indexes that have been solved. */
        private ArrayList<Short> solved;

        /* Keep track of mistakes in simple mode. */
        private int mistakes;

        MinesweeperTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        @Override
        protected boolean isNoScoreIfOutOfTime() {
            // If time runs out, no points should be awarded.
            return true;
        }

        @Override
        public void onStart() {
            clickable = new HashMap<>();
            bombs = new ArrayList<>();
            solved = new ArrayList<>();

            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, boardX, 6, boardZ)) {
                error();
                return;
            }

            makePlatform(gen, boardX, boardZ, Material.STONE_BRICKS);

            // Register clickable blocks.
            for (short i = 1; i <= squares; i++)
                registerClicks(generateGridLocation(i), i);

            // Randomize bombs.
            while (bombs.size() < mines) {
                short ID = (short) (main.main().rng().nextInt(squares) + 1);
                if (!bombs.contains(ID))
                    bombs.add(ID);
            }

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));
            selectPlayer();
        }

        /**
         * Minesweeper can be quite frustrating, especially the first few
         * turns which rely completely on chance. If the player causes a
         * game over in the first three turns, the minigame will not end.
         * It will re-generate the board and reset this grace period.
         */
        private void retryAndSelect() {
            bombs.clear();
            clickable.clear();
            solved.clear();
            medal = Medal.NONE;
            canMove = true;
            setStartingPoints(0);
            delay = scoreDelay;
            resetTurns();

            // Re-register clickable blocks.
            for (short i = 1; i <= squares; i++)
                registerClicks(generateGridLocation(i), i);

            // Re-randomize mine locations.
            while (bombs.size() < mines) {
                short ID = (short) (main.main().rng().nextInt(squares) + 1);
                if (!bombs.contains(ID))
                    bombs.add(ID);
            }

            selectPlayer();
        }

        /**
         * Makes a move on the board.
         * Left clicking flags a square as a mine.
         * Right clicking reveals a safe square.
         * Performing a wrong move results in a game over.
         *
         * @param action      The action performed.
         * @param block       The block clicked.
         * @param responsible The player who made the move.
         */
        private void move(Action action, Block block, Player responsible) {
            if (!clickable.containsKey(block)) return;
            short ID = clickable.get(block);
            boolean isBomb = bombs.contains(ID);
            boolean lost = isBomb ? action == Action.RIGHT_CLICK_BLOCK : action == Action.LEFT_CLICK_BLOCK;

            boolean retry = false;
            if (lost) {
                mistakes++;
                if (!difficulty.equals(Difficulty.SIMPLE)) {
                    if (isBomb)
                        // The user right clicked on a mine. Game over!
                        main.messageAll(getParticipants(), ChatColor.RED + "You've revealed a mine. Game over!");
                    else
                        // The user left clicked on a safe square. Game over!
                        main.messageAll(getParticipants(), ChatColor.RED + "You've flagged a safe square. Game over!");
                    for (Map.Entry<Block, Short> entry : clickable.entrySet()) {
                        if (bombs.contains(entry.getValue()))
                            entry.getKey().setType(Material.REDSTONE_BLOCK);
                    }
                    // Are they eligible for grace period?
                    if (getTurns() <= 3) {
                        retry = true;
                        main.messageAll(getParticipants(), ChatColor.RED + "Retrying as minigame did not go long enough.");
                        canMove = false;
                        main.soundAll(getParticipants(), Sound.ENTITY_IRON_GOLEM_DEATH, 3F);
                    }
                } else {
                    if (isBomb) main.messageAll(getParticipants(), ChatColor.RED + "You've revealed a mine!");
                    else main.messageAll(getParticipants(), ChatColor.RED + "You've flagged a safe square!");
                    main.messageAll(getParticipants(), String.format(ChatColor.RED + "This is your %s mistake!", StringUtility.ordinal(mistakes)));
                    lost = false;
                }
            } else {
                // Reveal all nearby safe squares where applicable.
                if (!isBomb) {
                    int nearby = getNearbyMines(block);
                    if (nearby == 0) {
                        revealNearbyEmptySpaces(block, responsible);
                    } else
                        reveal(ID, (short) nearby, responsible);
                } else reveal(ID, (short) 0, responsible);
            }

            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);

            cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1);
            boolean finalRetry = retry;
            boolean finalLost = lost;
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                if (finalLost && !finalRetry || solved.size() >= getFlawless() * (difficulty == Difficulty.HARDER ? 4 : 1))
                    stop();
                else if (finalRetry)
                    retryAndSelect();
                else
                    selectPlayer();
            }, retry ? 50L : 19L);
        }

        /**
         * When revealing a square, the code will recursively scan
         * nearby blocks if there are no adjacent mines. This is what
         * you commonly see when making a first move in minesweeper.
         *
         * @param current     The original block that was revealed.
         * @param responsible The player who revealed the block.
         */
        private void revealNearbyEmptySpaces(Block current, Player responsible) {
            List<Block> allEmpty = new ArrayList<>();
            allEmpty.add(current);
            scanEmpty(current, allEmpty);
            for (Block empty : allEmpty)
                reveal(clickable.get(empty), (short) 0, responsible);
            revealEmptyNeighbors(allEmpty, responsible);
        }

        /**
         * Based on the current block, it will scan and move other empty blocks.
         */
        private void scanEmpty(Block current, List<Block> baseList) {
            for (Block relative : getRelatives(current))
                if (relative.getType() == Material.BEDROCK)
                    if (getNearbyMines(relative) == 0)
                        if (!baseList.contains(relative)) {
                            baseList.add(relative);
                            scanEmpty(relative, baseList);
                        }
        }

        /**
         * Reveals all detected safe blocks.
         *
         * @see #reveal(short, short, Player)
         */
        private void revealEmptyNeighbors(List<Block> allEmpty, Player responsible) {
            for (Block empty : allEmpty)
                for (Block relative : getRelatives(empty))
                    if (relative.getType() == Material.BEDROCK)
                        reveal(clickable.get(relative), (short) getNearbyMines(relative), responsible);
        }

        /**
         * Reveals a single space on the board.
         *
         * @param index       The grid index of the tile.
         * @param nearby      Number of mines adjacent to this tile.
         * @param responsible The player who made the original move.
         */
        private void reveal(short index, short nearby, Player responsible) {
            Location loc = generateGridLocation(index);
            if (bombs.contains(index))
                loc.getBlock().setType(Material.REDSTONE_BLOCK);
            else {
                loc.getBlock().setType(translateDyeColor(nearby));
            }
            solved.add(index);
            // Don't award points on simple difficulty.
            if (difficulty.equals(Difficulty.SIMPLE)) return;
            delay--;
            if (delay == 0) {
                scorePoints(responsible, false, true, 1);
                delay = scoreDelay;
            }
        }

        /**
         * @param block Supplied tile.
         * @return Number of adjacent mines to this tile.
         */
        private int getNearbyMines(Block block) {
            int found = 0;
            for (Block relative : getRelatives(block))
                if (bombs.contains(clickable.get(relative)))
                    found++;
            return found;
        }

        /**
         * @param block Supplied tile.
         * @return All possible adjacent positions to a tile.
         */
        private Block[] getRelatives(Block block) {
            return new Block[]{block.getRelative(BlockFace.NORTH),
                    block.getRelative(BlockFace.NORTH_EAST),
                    block.getRelative(BlockFace.EAST),
                    block.getRelative(BlockFace.SOUTH_EAST),
                    block.getRelative(BlockFace.SOUTH),
                    block.getRelative(BlockFace.SOUTH_WEST),
                    block.getRelative(BlockFace.WEST),
                    block.getRelative(BlockFace.NORTH_WEST)};
        }

        /**
         * Registers clickable block locations (and hides them).
         */
        private void registerClicks(Location loc, short allocation) {
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Takes a grid index and returns the appropriate board location.
         *
         * @param allocation Grid index.
         * @return Tile location for this grid index.
         */
        private Location generateGridLocation(short allocation) {
            Location result = gen.clone().add(2, 0, 4);

            //TODO: Improve this with while loop?

            // Find X axis.
            if (difficulty != Difficulty.HARDER) {
                if (allocation >= 57) result.add(0, 0, 7);
                else if (allocation >= 49) result.add(0, 0, 6);
                else if (allocation >= 41) result.add(0, 0, 5);
                else if (allocation >= 33) result.add(0, 0, 4);
                else if (allocation >= 25) result.add(0, 0, 3);
                else if (allocation >= 17) result.add(0, 0, 2);
                else if (allocation >= 9) result.add(0, 0, 1);
            } else {
                if (allocation >= 241) result.add(0, 0, 15);
                else if (allocation >= 225) result.add(0, 0, 14);
                else if (allocation >= 209) result.add(0, 0, 13);
                else if (allocation >= 193) result.add(0, 0, 12);
                else if (allocation >= 177) result.add(0, 0, 11);
                else if (allocation >= 161) result.add(0, 0, 10);
                else if (allocation >= 145) result.add(0, 0, 9);
                else if (allocation >= 129) result.add(0, 0, 8);
                else if (allocation >= 113) result.add(0, 0, 7);
                else if (allocation >= 97) result.add(0, 0, 6);
                else if (allocation >= 81) result.add(0, 0, 5);
                else if (allocation >= 65) result.add(0, 0, 4);
                else if (allocation >= 49) result.add(0, 0, 3);
                else if (allocation >= 33) result.add(0, 0, 2);
                else if (allocation >= 17) result.add(0, 0, 1);
            }

            // Find Z axis.
            if (difficulty != Difficulty.HARDER)
                while (allocation > 8)
                    allocation -= 8;
            else
                while (allocation > 16)
                    allocation -= 16;

            result.add(allocation - 1, 0, 0);
            return result;
        }

        /**
         * Translates number of bombs nearby to a concrete color.
         */
        private Material translateDyeColor(short neighbors) {
            switch (neighbors) {
                case 1:
                    return Material.LIME_CONCRETE;
                case 2:
                    return Material.YELLOW_CONCRETE;
                case 3:
                    return Material.ORANGE_CONCRETE;
                case 4:
                    return Material.RED_CONCRETE;
                case 5:
                    return Material.PURPLE_CONCRETE;
                case 6:
                case 7:
                case 8:
                    return Material.BLACK_CONCRETE;
                default:
                    return Material.WHITE_CONCRETE;
            }
        }

        @Override
        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
            bombs.clear();
            bombs = null;
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
                move(event.getAction(), event.getClickedBlock(), event.getPlayer());
        }
    }
}
