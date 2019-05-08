package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.AttemptsMinigameTask;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Minesweeper extends Minigame implements FlawlessEnabled, BoardGame {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new SweepRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Minesweeper";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.FIREWORK_CHARGE);
    }

    public String[] getDescription() {
        return new String[]{"Wow! Wow! These punk mines are back again!",
                ChatColor.ITALIC + "Solve an 8x8 minesweeper grid! Don't blow up!",
                ChatColor.ITALIC + "Wool indicates the level of danger!",
                ChatColor.ITALIC + "Revealing a bomb results in an instant game over!"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public String getSummary() {
        return "Solve the Minesweeper grid!";
    }

    public String getObjectiveWord() {
        return "squares solved";
    }

    public int getDuration() {
        return 600;
    }

    public int getGold() {
        return 50;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getFlawless() {
        return 64;
    }

    public int getMaxReward() {
        return 5000;
    }

    private class SweepRun extends AttemptsMinigameTask implements Listener {

        // Game constants
        private final int boardX = difficulty == Difficulty.HARDER ? 19 : 11;
        private final int boardZ = difficulty == Difficulty.HARDER ? 21 : 13;
        private final int squares = difficulty == Difficulty.HARDER ? 256 : 64;
        private final int mines = difficulty == Difficulty.HARDER ? 56 : 14;
        int temp = 4;
        private HashMap<Block, Short> clickable;
        private ArrayList<Short> bombs;
        private Location gen;
        private ArrayList<Short> solved;

        SweepRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        public void onStart() {
            clickable = new HashMap<>();
            bombs = new ArrayList<>();
            solved = new ArrayList<>();
            gen = generateLocation(100, 20, 140, true, false);

            for (int x = 0; x <= boardX; x++)
                for (int z = 0; z <= boardZ; z++)
                    if (Material.AIR != gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z).getType()) {
                        error();
                        return;
                    }

            for (int x = 0; x <= boardX; x++)
                for (int z = 0; boardZ >= z; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            // Register clickable area.
            for (short i = 1; i <= squares; i++)
                registerClicks(generateGridLocation(i, gen), i);

            // Register bombs.
            while (bombs.size() < mines) {
                short ID = (short) (main.main().rng().nextInt(squares) + 1);
                if (!bombs.contains(ID))
                    bombs.add(ID);
            }
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));
            selectPlayer();
        }

        private void retryAndSelect() {
            bombs.clear();
            clickable.clear();
            solved.clear();
            bronze = false;
            silver = false;
            gold = false;
            canMove = true;
            points = 0;
            temp = 4;
            resetTurns();

            // Re-register clickable area.
            for (short i = 1; i <= squares; i++)
                registerClicks(generateGridLocation(i, gen), i);

            // Re-register mine locations to new random spots.
            while (bombs.size() < mines) {
                short ID = (short) (main.main().rng().nextInt(squares) + 1);
                if (!bombs.contains(ID))
                    bombs.add(ID);
            }
            selectPlayer();
        }

        private void reveal(Action action, Block block, Player responsible) {
            if (!clickable.containsKey(block)) return;
            short ID = clickable.get(block);
            boolean isBomb = bombs.contains(ID);
            boolean lost = isBomb ? action == Action.RIGHT_CLICK_BLOCK : action == Action.LEFT_CLICK_BLOCK;

            boolean retry = false;
            if (lost) {
                if (isBomb) {
                    Location loc = generateGridLocation(ID, gen);
                    loc.getBlock().setType(Material.REDSTONE_BLOCK);
                    main.messageAll(getParticipants(), ChatColor.RED + "You've revealed a mine. Game over!");
                } else
                    main.messageAll(getParticipants(), ChatColor.RED + "You've flagged a safe square. Game over!");
                for (Map.Entry<Block, Short> entry : clickable.entrySet()) {
                    if (bombs.contains(entry.getValue()))
                        entry.getKey().setType(Material.REDSTONE_BLOCK);
                }
                if (getTurns() <= 3) {
                    retry = true;
                    main.messageAll(getParticipants(), ChatColor.RED + "Retrying as minigame did not go long enough.");
                    canMove = false;
                    main.soundAll(getParticipants(), Sound.ENTITY_IRONGOLEM_DEATH, 3F);
                }
            } else {
                if (!isBomb) {
                    int nearby = getNearbyBombs(block);
                    if (nearby == 0) {
                        revealNearbyEmptySpaces(block, responsible);
                    } else
                        reveal(ID, (short) nearby, responsible);
                } else reveal(ID, (short) 0, responsible);
            }

            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);

            cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1);
            boolean finalRetry = retry;
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                if (lost && !finalRetry || solved.size() >= getFlawless() * (difficulty == Difficulty.HARDER ? 4 : 1))
                    stop();
                else if (finalRetry)
                    retryAndSelect();
                else
                    selectPlayer();
            }, retry ? 100L : 19L);
        }

        /**
         * Reveals all empty spaces.
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
         * There must be a more efficient way to do this surely.
         *
         * @param allEmpty Empty spaces calculated.
         */
        private void revealEmptyNeighbors(List<Block> allEmpty, Player responsible) {
            for (Block empty : allEmpty)
                for (Block relative : getRelatives(empty)) {
                    if (relative.getType() == Material.BEDROCK)
                        reveal(clickable.get(relative), (short) getNearbyBombs(relative), responsible);
                }
        }

        /**
         * Based on the current block, it will scan and reveal other empty blocks.
         */
        private void scanEmpty(Block current, List<Block> baseList) {
            for (Block relative : getRelatives(current)) {
                if (relative.getType() == Material.BEDROCK) {
                    if (getNearbyBombs(relative) == 0) {
                        if (!baseList.contains(relative)) {
                            baseList.add(relative);
                            scanEmpty(relative, baseList);
                        }
                    }
                }
            }
        }

        /**
         * Reveals a space on the board.
         */
        private void reveal(short ID, short size, Player responsible) {
            Location loc = generateGridLocation(ID, gen);
            if (bombs.contains(ID))
                loc.getBlock().setType(Material.REDSTONE_BLOCK);
            else {
                loc.getBlock().setType(Material.WOOL);
                loc.getBlock().setData(translateDyeColor(size));
            }
            solved.add(ID);
            temp--;
            if (temp == 0) {
                scorePoint(responsible, true, 1);
                temp = 4;
            }
        }

        /**
         * Returns number of adjacent bombs.
         */
        private int getNearbyBombs(Block block) {
            int found = 0;
            for (Block relative : getRelatives(block))
                if (bombs.contains(clickable.get(relative)))
                    found++;
            return found;
        }

        /**
         * Returns all adjacent positions.
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
         * Registers clickable squares. (and hides them)
         */
        private void registerClicks(Location loc, short allocation) {
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Translates an integer from 1 to 64 to a space on the board.
         */
        private Location generateGridLocation(short allocation, Location original) {
            Location result = original.clone().add(2, 0, 4);
            if (difficulty == Difficulty.NORMAL) {
                if (allocation >= 57) result.add(0, 0, 7);
                else if (allocation >= 49) result.add(0, 0, 6);
                else if (allocation >= 41) result.add(0, 0, 5);
                else if (allocation >= 33) result.add(0, 0, 4);
                else if (allocation >= 25) result.add(0, 0, 3);
                else if (allocation >= 17) result.add(0, 0, 2);
                else if (allocation >= 9) result.add(0, 0, 1);
                return generateGridLocationPhase2(allocation, result);
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
                return generateGridLocationPhase2(allocation, result);
            }
        }

        /**
         * After finding the Z axis, find the X axis.
         */
        private Location generateGridLocationPhase2(short allocation, Location result) {
            if (difficulty == Difficulty.NORMAL)
                while (allocation > 8)
                    allocation -= 8;
            else
                while (allocation > 16)
                    allocation -= 16;
            return result.add(allocation - 1, 0, 0);
        }

        /**
         * Translates danger (bombs nearby) to a dye color.
         */
        private byte translateDyeColor(short neighbors) {
            if (neighbors == 1)
                return 5;
            else if (neighbors == 2)
                return 4;
            else if (neighbors == 3)
                return 1;
            else if (neighbors == 4)
                return 14;
            else if (neighbors == 5)
                return 10;
            else if (neighbors >= 6)
                return 15;
            else return 0;
        }

        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
            bombs.clear();
            bombs = null;
        }

        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() != Material.BEDROCK) return;
            if (canMove(event.getPlayer()))
                reveal(event.getAction(), event.getClickedBlock(), event.getPlayer());
        }
    }
}
