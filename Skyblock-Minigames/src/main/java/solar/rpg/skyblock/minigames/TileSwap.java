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
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimedTurnBasedMinigameTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TileSwap extends Minigame implements FlawlessEnabled, BoardGame {

    private final Picture[] pictures = new Picture[]{
            new Picture(new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 13), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 13), new Pair(Material.WOOL, (byte) 13), new Pair(Material.WOOL, (byte) 13), new Pair(Material.DIRT, (byte) 0), new Pair(Material.WOOL, (byte) 13), new Pair(Material.DIRT, (byte) 0), new Pair(Material.DIRT, (byte) 0), new Pair(Material.DIRT, (byte) 0), new Pair(Material.WOOL, (byte) 8), new Pair(Material.DIRT, (byte) 0), new Pair(Material.WOOL, (byte) 8), new Pair(Material.WOOL, (byte) 8), new Pair(Material.WOOL, (byte) 8)),
            new Picture(new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 5)),
            new Picture(new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0)),
            new Picture(new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0)),
            new Picture(new Pair(Material.WOOL, (byte) 13), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 5), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 1), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 5)),
            new Picture(new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.QUARTZ_BLOCK, (byte) 0), new Pair(Material.QUARTZ_BLOCK, (byte) 0), new Pair(Material.QUARTZ_BLOCK, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0), new Pair(Material.COAL_BLOCK, (byte) 0)),
            new Picture(new Pair(Material.WOOL, (byte) 6), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.WOOL, (byte) 6), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.WOOL, (byte) 6), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.QUARTZ_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.WOOL, (byte) 6), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.WOOL, (byte) 6)),
            //Broken new Picture(new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.QUARTZ_BLOCK, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.QUARTZ_BLOCK, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 8), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 12), new Pair(Material.STAINED_CLAY, (byte) 12), new Pair(Material.STAINED_CLAY, (byte) 12), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0), new Pair(Material.STAINED_CLAY, (byte) 0)),
            //Broken new Picture(new Pair(Material.STAINED_GLASS, (byte) 9), new Pair(Material.STAINED_GLASS, (byte) 9), new Pair(Material.STAINED_GLASS, (byte) 0), new Pair(Material.STAINED_GLASS, (byte) 13), new Pair(Material.STAINED_GLASS, (byte) 1), new Pair(Material.STAINED_GLASS, (byte) 2), new Pair(Material.STAINED_GLASS, (byte) 1), new Pair(Material.STAINED_GLASS, (byte) 4), new Pair(Material.STAINED_GLASS, (byte) 1), new Pair(Material.STAINED_GLASS, (byte) 13), new Pair(Material.STAINED_GLASS, (byte) 0), new Pair(Material.STAINED_GLASS, (byte) 8), new Pair(Material.STAINED_GLASS, (byte) 13), new Pair(Material.STAINED_GLASS, (byte) 14), new Pair(Material.STAINED_GLASS, (byte) 8), new Pair(Material.STAINED_GLASS, (byte) 14), new Pair(Material.STAINED_GLASS, (byte) 4), new Pair(Material.STAINED_GLASS, (byte) 2), new Pair(Material.STAINED_GLASS, (byte) 9), new Pair(Material.STAINED_GLASS, (byte) 4), new Pair(Material.STAINED_GLASS, (byte) 2), new Pair(Material.STAINED_GLASS, (byte) 14), new Pair(Material.STAINED_GLASS, (byte) 8)),
            new Picture(new Pair(Material.STONE, (byte) 6), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 14), new Pair(Material.STONE, (byte) 6), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 15), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 15), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 15), new Pair(Material.WOOL, (byte) 14), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 15), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.STONE, (byte) 6), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0)),
            new Picture(new Pair(Material.CLAY, (byte) 0), new Pair(Material.CLAY, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.CLAY, (byte) 0), new Pair(Material.CLAY, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.CLAY, (byte) 0), new Pair(Material.CLAY, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 3), new Pair(Material.CLAY, (byte) 0), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11), new Pair(Material.WOOL, (byte) 11)),
            new Picture(new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 6), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 7), new Pair(Material.SPONGE, (byte) 0), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.SPONGE, (byte) 0), new Pair(Material.SPONGE, (byte) 0), new Pair(Material.SPONGE, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.SPONGE, (byte) 0), new Pair(Material.WOOL, (byte) 0)),
            new Picture(new Pair(Material.WOOL, (byte) 9), new Pair(Material.WOOL, (byte) 9), new Pair(Material.WOOL, (byte) 9), new Pair(Material.WOOL, (byte) 9), new Pair(Material.WOOL, (byte) 9), new Pair(Material.WOOL, (byte) 9), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 9), new Pair(Material.WOOL, (byte) 4), new Pair(Material.GLASS, (byte) 0), new Pair(Material.WOOL, (byte) 4), new Pair(Material.GLASS, (byte) 0), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 4)),
            new Picture(new Pair(Material.WORKBENCH, (byte) 0), new Pair(Material.BRICK, (byte) 0), new Pair(Material.BRICK, (byte) 0), new Pair(Material.BRICK, (byte) 0), new Pair(Material.BRICK, (byte) 0), new Pair(Material.FURNACE, (byte) 0), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.CHEST, (byte) 0), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.FURNACE, (byte) 0), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WOOL, (byte) 2), new Pair(Material.WORKBENCH, (byte) 0), new Pair(Material.BRICK, (byte) 0), new Pair(Material.BRICK, (byte) 0), new Pair(Material.BRICK, (byte) 0)),
            new Picture(new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.LEAVES, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.LEAVES, (byte) 0), new Pair(Material.LEAVES, (byte) 0), new Pair(Material.LEAVES, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.LEAVES, (byte) 0), new Pair(Material.LOG, (byte) 3), new Pair(Material.LEAVES, (byte) 0), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.LOG, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 3), new Pair(Material.WOOL, (byte) 13), new Pair(Material.WOOL, (byte) 13), new Pair(Material.LOG, (byte) 3), new Pair(Material.WOOL, (byte) 13)),
            new Picture(new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.IRON_FENCE, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 7), new Pair(Material.GLASS, (byte) 0), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 4), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 7), new Pair(Material.WOOL, (byte) 0), new Pair(Material.WOOL, (byte) 7)),
            new Picture(new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.EMERALD_BLOCK, (byte) 0), new Pair(Material.DIAMOND_BLOCK, (byte) 0), new Pair(Material.GOLD_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.GOLD_BLOCK, (byte) 0), new Pair(Material.IRON_BLOCK, (byte) 0), new Pair(Material.LAPIS_BLOCK, (byte) 0), new Pair(Material.IRON_BLOCK, (byte) 0), new Pair(Material.EMERALD_BLOCK, (byte) 0), new Pair(Material.DIAMOND_BLOCK, (byte) 0), new Pair(Material.LAPIS_BLOCK, (byte) 0), new Pair(Material.IRON_BLOCK, (byte) 0), new Pair(Material.LAPIS_BLOCK, (byte) 0), new Pair(Material.DIAMOND_BLOCK, (byte) 0), new Pair(Material.EMERALD_BLOCK, (byte) 0), new Pair(Material.IRON_BLOCK, (byte) 0), new Pair(Material.LAPIS_BLOCK, (byte) 0), new Pair(Material.IRON_BLOCK, (byte) 0), new Pair(Material.GOLD_BLOCK, (byte) 0), new Pair(Material.REDSTONE_BLOCK, (byte) 0), new Pair(Material.GOLD_BLOCK, (byte) 0), new Pair(Material.DIAMOND_BLOCK, (byte) 0), new Pair(Material.EMERALD_BLOCK, (byte) 0)),
            new Picture(new Pair(Material.WOOD, (byte) 5), new Pair(Material.WOOD, (byte) 4), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 4), new Pair(Material.WOOD, (byte) 5), new Pair(Material.WOOD, (byte) 4), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 1), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 4), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 1), new Pair(Material.WOOD, (byte) 0), new Pair(Material.WOOD, (byte) 1), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 4), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 1), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 4), new Pair(Material.WOOD, (byte) 5), new Pair(Material.WOOD, (byte) 4), new Pair(Material.WOOD, (byte) 2), new Pair(Material.WOOD, (byte) 4)),
    };

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TileRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Tile Swap";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.STEP);
    }

    public String[] getDescription() {
        return new String[]{"Smooth moves, blocky tiles.",
                ChatColor.ITALIC + "Slide blocks around to create a picture!",
                ChatColor.ITALIC + "Fewer moves gives a higher score!",
                ChatColor.ITALIC + "Difficult pictures have more moves!"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    public String getSummary() {
        return "Switch tiles to match the pictures!";
    }

    public String getObjectiveWord() {
        return "seconds remaining";
    }

    public int getDuration() {
        return 420;
    }

    public int getGold() {
        return 220;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getFlawless() {
        return 300;
    }

    public int getMaxReward() {
        return 5000;
    }

    private class TileRun extends TimedTurnBasedMinigameTask implements Listener {

        private Picture picture;
        private HashMap<Block, Short> clickable;

        private Location gen;

        TileRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 16);
        }

        public void onStart() {
            clickable = new HashMap<>();
            cooldown = System.currentTimeMillis();

            gen = generateLocation(100, 20, 140, true, false);
            for (int x = 0; x < 9; x++)
                for (int y = 1; y <= 9; y++)
                    for (int z = 0; z < 9; z++)
                        if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + z).getType() != Material.AIR) {
                            error();
                            return;
                        }

            //Generate board.
            for (int x = 0; x < 9; x++)
                for (int z = 0; z < 9; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            //Generate wall.
            for (int y = 1; y < 10; y++)
                for (int x = 0; x < 9; x++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + 9);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            //Generate picture.
            picture = pictures[main.main().rng().nextInt(pictures.length)];
            for (short i = 1; i <= 24; i++) {
                Pair pair = picture.pairs[i - 1];
                Block found = generateWallLocation(i, gen).getBlock();
                found.setType(pair.material);
                found.setData(pair.data);
            }
            generateWallLocation((short) 25, gen).getBlock().setType(Material.BEDROCK);
            ArrayList<Short> space = new ArrayList<>();
            for (short i = 1; i <= 25; i++)
                registerClicks(generateGridLocation(i, gen), i);
            int ind = 0;
            while (space.size() < 24) {
                short picked = (short) (main.main().rng().nextInt(25) + 1);
                if (space.contains(picked)) continue;
                Pair pair = picture.pairs[ind];
                ind++;
                Block found = generateGridLocation(picked, gen).getBlock();
                if (found.getType() == pair.material && found.getData() == pair.data && main.main().rng().nextBoolean()) {
                    ind--;
                    continue;
                }
                space.add(picked);
                found.setType(pair.material);
                found.setData(pair.data);
            }
            for (short i = 1; i <= 25; i++)
                if (!space.contains(i))
                    generateGridLocation(i, gen).getBlock().setType(Material.BEDROCK);
            space.clear();
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));
            selectPlayer();
        }

        private void swap(Block block) {
            if (!clickable.containsKey(block)) return;
            Block bedrock = null;
            for (Block adjacent : getSimpleRelatives(block))
                if (adjacent.getType() == Material.BEDROCK)
                    bedrock = adjacent;
            if (bedrock == null) return;
            Pair pair = new Pair(block.getType(), block.getData());
            block.setType(Material.BEDROCK);
            bedrock.setType(pair.material);
            bedrock.setData(pair.data);
            cooldown = System.currentTimeMillis() + 400;
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                if (checkSolved())
                    stop();
                else
                    selectPlayer();
            }, 5L);
            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);
        }

        boolean checkSolved() {
            for (short i = 1; i <= 25; i++) {
                Block wall = generateWallLocation(i, gen).getBlock();
                Block floor = generateGridLocation(i, gen).getBlock();
                if (wall.getType() != floor.getType()) return false;
                if (wall.getData() != floor.getData()) return false;
            }
            return true;
        }

        /**
         * Returns the block relatives by 90 degrees.
         */
        private Block[] getSimpleRelatives(Block block) {
            return new Block[]{block.getRelative(BlockFace.NORTH),
                    block.getRelative(BlockFace.EAST),
                    block.getRelative(BlockFace.SOUTH),
                    block.getRelative(BlockFace.WEST)};
        }

        /**
         * Registers clickable squares. (and hides them)
         */
        private void registerClicks(Location loc, short allocation) {
            clickable.put(loc.getBlock(), allocation);
        }

        /**
         * Translates an integer from 1 to 25 to a space on the board.
         */
        private Location generateGridLocation(short allocation, Location original) {
            Location result = original.clone().add(2, 0, 2);
            if (allocation >= 21) {
                result.add(0, 0, 0);
            } else if (allocation >= 16) {
                result.add(0, 0, 1);
            } else if (allocation >= 11) {
                result.add(0, 0, 2);
            } else if (allocation >= 6) {
                result.add(0, 0, 3);
            } else {
                result.add(0, 0, 4);
            }
            while (allocation > 5)
                allocation -= 5;
            return result.add(5 - allocation, 0, 0);
        }

        /**
         * Translates an integer from 1 to 25 to a space on the wall.
         */
        private Location generateWallLocation(short allocation, Location original) {
            Location result = original.clone().add(2, 7, 9);
            if (allocation >= 21) {
                result.subtract(0, 4, 0);
            } else if (allocation >= 16) {
                result.subtract(0, 3, 0);
            } else if (allocation >= 11) {
                result.subtract(0, 2, 0);
            } else if (allocation >= 6) {
                result.subtract(0, 1, 0);
            } else {
                result.subtract(0, 0, 0);
            }
            while (allocation > 5)
                allocation -= 5;
            return result.add(5 - allocation, 0, 0);
        }

        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
        }

        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (canMove(event.getPlayer()))
                swap(event.getClickedBlock());
        }
    }

    private class Picture {
        final Pair[] pairs;

        Picture(Pair... pairs) {
            this.pairs = pairs;
        }
    }

    private class Pair {
        public final Material material;
        public final byte data;

        Pair(Material material, byte data) {
            this.material = material;
            this.data = data;
        }
    }
}
