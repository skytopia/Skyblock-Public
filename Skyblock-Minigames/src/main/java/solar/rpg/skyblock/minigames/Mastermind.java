package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.LeastAttemptsMinigameTask;
import solar.rpg.skyblock.island.minigames.task.Minigame;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Mastermind extends Minigame implements FlawlessEnabled, BoardGame, NewbieFriendly {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MasterRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Mastermind";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.CAULDRON_ITEM);
    }

    public String[] getDescription() {
        return new String[]{"Guessing right is the best feeling IN THE WORLD!",
                ChatColor.ITALIC + "Break the random 4-digit generated code!",
                ChatColor.ITALIC + "There's 6 colors to choose from, choose wisely!",
                ChatColor.ITALIC + "You only get 12 turns, so cross your fingers."};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public String getSummary() {
        return "Break the 4-digit code!";
    }

    public String getObjectiveWord() {
        return "guesses made";
    }

    public int getDuration() {
        return 240;
    }

    public int getGold() {
        return 8;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getFlawless() {
        return 12;
    }

    public int getMaxReward() {
        return 1500;
    }

    private class MasterRun extends LeastAttemptsMinigameTask implements Listener {

        private HashMap<Block, Integer> clickable;

        private Location gen;
        private int turn;

        private Integer[] code;
        private Integer[] currentPick;
        private Boolean[] solved;

        MasterRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1, 12);
        }

        public void onStart() {
            clickable = new HashMap<>();
            code = new Integer[]{main.main().rng().nextInt(6) + 1, main.main().rng().nextInt(6) + 1, main.main().rng().nextInt(6) + 1, main.main().rng().nextInt(6) + 1};
            solved = new Boolean[]{false, false, false, false};

            gen = generateLocation(100, 20, 140, true, false);
            for (int x = 0; x <= 17; x++)
                for (int y = 0; y <= 3; y++)
                    for (int z = 0; z <= 10; z++)
                        if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z).getType() != Material.AIR) {
                            error();
                            return;
                        }

            //Generate mastermind game board
            for (int x = 0; x <= 16; x++)
                for (int z = 0; z <= 6; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

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

            for (int i = 1; i <= 6; i++) {
                Location color = genColorLoc(i, gen);
                color.getBlock().setType(Material.CONCRETE);
                color.getBlock().setData(translateColor(i));
                clickable.put(color.getBlock(), i);
            }

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));
            System.out.println("[Anvil] Mastermind started with solution: " + code[0] + "" + code[1] + code[2] + code[3] + "");
            selectPlayer();
        }

        @Override
        public void selectPlayer() {
            super.selectPlayer();
            currentPick = new Integer[]{solved[0] ? code[0] : 0, solved[1] ? code[1] : 0, solved[1] ? code[1] : 0, solved[1] ? code[1] : 0};
            turn = solved[0] ? solved[1] ? solved[2] ? 4 : 3 : 2 : 1;
        }

        private void reveal(Block block) {
            if (!clickable.containsKey(block)) return;
            Integer ID = clickable.get(block);

            // If they still have more numbers to pick..
            Location loc = genColumnLoc(turn, gen, points);
            loc.getBlock().setType(Material.CONCRETE);
            loc.getBlock().setData(translateColor(ID));
            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);
            cooldown = System.currentTimeMillis() + 250;
            currentPick[turn - 1] = ID;
            turn++;
            while (turn <= 4 && solved[turn - 1])
                turn++;

            if (turn >= 5) {
                canMove = false;
                decide();
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
         * Check if the guess was correct or not.
         */
        private void decide() {
            for (int i = 0; i < 4; i++) {
                if (!solved[i])
                    if (Objects.equals(currentPick[i], code[i])) {
                        main.soundAll(getParticipants(), Sound.BLOCK_ANVIL_BREAK, 2.5F);
                        solved[i] = true;

                        // Set the rest of the columns to glass so they know that they do not have to guess there.
                        for (int x = 0; x < 13; x++) {
                            Block at = genColumnLoc(i + 1, gen, x).getBlock();
                            if (at.getType() == Material.BEDROCK)
                                at.setType(Material.GLASS);
                        }

                        Block ind = genColumnLoc(i + 1, gen, 13).getBlock();
                        ind.setType(Material.CONCRETE);
                        ind.setData((byte) 13);
                    } else
                        genColumnLoc(i + 1, gen, 13).getBlock().setType(Material.NETHER_BRICK);
            }
        }

        /**
         * Translates an integer from 1 to 6 to a color button.
         *
         * @param allocation The integer allocation from 1-6.
         * @param original   The bottom left of the board.
         * @return The grid location.
         */
        private Location genColorLoc(int allocation, Location original) {
            Location result = original.clone().add(2, 0, 8);
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
         * Translates an integer from 1 to 4 to a column.
         *
         * @param allocation The integer allocation from 1-4
         * @param original   The bottom left of the board.
         * @return The grid location.
         */
        private Location genColumnLoc(int allocation, Location original, int x) {
            Location result = original.clone().add(15, 0, 1);
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
         * Translates color code button/guess into a byte.
         *
         * @param code The "color" of this part of the code.
         * @return Corresponding dye color.
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
            }
            return 0;
        }

        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
            code = null;
            solved = null;
        }

        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() != Material.CONCRETE) return;
            if (canMove(event.getPlayer()))
                reveal(event.getClickedBlock());
        }
    }
}
