package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.*;
import solar.rpg.skyblock.minigames.tasks.LeastAttemptsMinigameTask;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Mastermind extends Minigame implements FlawlessEnabled, BoardGame, NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MastermindTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Mastermind";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.CAULDRON_ITEM);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Break the random 4-digit generated code!",
                ChatColor.ITALIC + "There's 6 colors to choose from, choose wisely!",
                ChatColor.ITALIC + "You only get 12 turns, so cross your fingers."
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Break the 4-digit code!";
    }

    @Override
    public String getObjectiveWord() {
        return "guesses made";
    }

    @Override
    public int getDuration() {
        return 240;
    }

    @Override
    public int getGold() {
        return 8;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getFlawless() {
        return 12;
    }

    @Override
    public int getMaxReward() {
        return 1500;
    }

    private class MastermindTask extends LeastAttemptsMinigameTask {

        /* Holds all blocks where moves can be made. */
        private HashMap<Block, Integer> clickable;

        /* Amount of columns to guess before it's the next player's turn. */
        private int turn;

        /* The current four-digit code. */
        private Integer[] code;

        /* The current guess made by the participants. */
        private Integer[] currentPick;

        /* Indexes that have been solved (matched). */
        private Boolean[] solved;

        MastermindTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1, 12);
        }

        @Override
        public void onStart() {
            clickable = new HashMap<>();

            // Generate code.
            code = new Integer[]{main.main().rng().nextInt(6) + 1, main.main().rng().nextInt(6) + 1, main.main().rng().nextInt(6) + 1, main.main().rng().nextInt(6) + 1};
            solved = new Boolean[]{false, false, false, false};

            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, 17, 6, 10)) {
                error();
                return;
            }

            makePlatform(gen, 16, 6, Material.SMOOTH_BRICK);

            //Generate step thingy
            for (int x = 0; x <= 12; x++)
                for (int z = 0; z <= 4; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + 2 + x, gen.getBlockY(), gen.getBlockZ() + 1 + z);
                    bl.setType(Material.STEP);
                    placed.add(bl);
                }

            //Generate correction thingy
            for (int x = 0; x <= 11; x++)
                for (int z = 0; z <= 3; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + 3 + x, gen.getBlockY(), gen.getBlockZ() + 1 + z);
                    bl.setType(Material.BEDROCK);
                    placed.add(bl);
                }

            //Generate upper thingy
            for (int z = 0; z <= 3; z++) {
                Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + 15, gen.getBlockY() + 1, gen.getBlockZ() + 1 + z);
                bl.setType(Material.BEDROCK);
                bl.getRelative(BlockFace.UP).setType(Material.STEP);
                placed.add(bl);
                placed.add(bl.getRelative(BlockFace.UP));
            }

            //Generate color board
            for (int x = 0; x <= 7; x++)
                for (int z = 0; z <= 3; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z + 7);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            // Generate useless slabs lol
            for (int z = 1; z <= 2; z++) {
                Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + 5, gen.getBlockY() + 1, gen.getBlockZ() + z + 7);
                bl.setType(Material.STEP);
                placed.add(bl);
            }

            // Generate clickable colour selection squares.
            for (int i = 1; i <= 6; i++) {
                Location color = genColorLoc(i);
                color.getBlock().setType(Material.CONCRETE);
                color.getBlock().setData(translateColor(i));
                clickable.put(color.getBlock(), i);
            }

            // Teleports players on to the platform.
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));

            selectPlayer();
        }

        @Override
        public void selectPlayer() {
            super.selectPlayer();
            // Calculate how many columns need to be solved, as this is how many moves they get.
            currentPick = new Integer[]{solved[0] ? code[0] : 0, solved[1] ? code[1] : 0, solved[1] ? code[1] : 0, solved[1] ? code[1] : 0};
            turn = solved[0] ? solved[1] ? solved[2] ? 4 : 3 : 2 : 1;
        }

        /**
         * Guesses a color for the next available column.
         *
         * @param block The colour that was guessed.
         */
        private void move(Block block) {
            if (!clickable.containsKey(block)) return;
            Integer ID = clickable.get(block);

            // Place guess on the board.
            Location loc = genColumnLoc(turn, points);
            loc.getBlock().setType(Material.CONCRETE);
            loc.getBlock().setData(translateColor(ID));
            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);
            cooldown = System.currentTimeMillis() + 250;

            currentPick[turn - 1] = ID;
            turn++;
            while (turn <= 4 && solved[turn - 1])
                turn++;

            // If they have guessed every column, check if it is solved, otherwise it's the next player's turn.
            if (turn >= 5) {
                canMove = false;
                checkWin();
                Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                    if (solved[0] && solved[1] && solved[2] && solved[3]) {
                        stop();
                    } else {
                        points--;
                        selectPlayer();
                        canMove = true;
                    }
                }, 10L);
            }
        }

        /**
         * Check if the combination has been solved.
         * If a column has been solved, it turns into glass.
         * Once all columns have been solved, game over!
         */
        private void checkWin() {
            for (int i = 0; i < 4; i++) {
                if (!solved[i])
                    if (Objects.equals(currentPick[i], code[i])) {
                        main.soundAll(getParticipants(), Sound.BLOCK_ANVIL_BREAK, 2.5F);
                        solved[i] = true;

                        // Set the rest of the columns to glass so they know that they do not have to guess there.
                        for (int x = 0; x < 13; x++) {
                            Block at = genColumnLoc(i + 1, x).getBlock();
                            if (at.getType() == Material.BEDROCK)
                                at.setType(Material.GLASS);
                        }

                        Block ind = genColumnLoc(i + 1, 13).getBlock();
                        ind.setType(Material.CONCRETE);
                        ind.setData((byte) 13);
                    } else
                        genColumnLoc(i + 1, 13).getBlock().setType(Material.NETHER_BRICK);
            }
        }

        /**
         * Takes a colour code allocation and returns a
         * colour selection block on the board.
         *
         * @param allocation The integer allocation from 1-6.
         * @return The location of the clickable block.
         */
        private Location genColorLoc(int allocation) {
            Location result = gen.clone().add(2, 0, 8);
            switch (allocation) {
                case 1:
                    result.add(0, 0, 0);
                    break;
                case 2:
                    result.add(0, 0, 1);
                    break;
                case 3:
                    result.add(1, 0, 0);
                    break;
                case 4:
                    result.add(1, 0, 1);
                    break;
                case 5:
                    result.add(2, 0, 0);
                    break;
                case 6:
                    result.add(2, 0, 1);
                    break;
            }
            return result;
        }

        /**
         * Takes a column index and returns the next available row.
         *
         * @param allocation The column index from 1-4.
         * @param x          X offset.
         * @return The grid location.
         */
        private Location genColumnLoc(int allocation, int x) {
            Location result = gen.clone().add(15, 0, 1);
            switch (allocation) {
                case 1:
                    result.add(0, 0, 0);
                    break;
                case 2:
                    result.add(0, 0, 1);
                    break;
                case 3:
                    result.add(0, 0, 2);
                    break;
                case 4:
                    result.add(0, 0, 3);
                    break;
            }
            return result.subtract(x, 0, 0);
        }

        /**
         * Translates color code index into a color.
         *
         * @param code The color code index.
         * @return Corresponding data color.
         */
        private byte translateColor(Integer code) {
            switch (code) {
                case 1:
                    return 14;
                case 2:
                    return 5;
                case 3:
                    return 11;
                case 4:
                    return 4;
                case 5:
                    return 2;
                case 6:
                    return 1;
                default:
                    return 0;
            }
        }

        @Override
        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
            code = null;
            solved = null;
        }

        @Override
        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() != Material.CONCRETE) return;
            if (canMove(event.getPlayer()))
                move(event.getClickedBlock());
        }
    }
}
