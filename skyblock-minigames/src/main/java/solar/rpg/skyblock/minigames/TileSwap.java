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
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.minigames.tasks.TimedTurnBasedMinigameTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TileSwap extends Minigame implements FlawlessEnabled, BoardGame {

    /* Supported tile swap pictures. Warning: long! */
    private static final Picture[] PICTURES = new Picture[]{new Picture(new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 13), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 13), new Data(Material.WOOL, (byte) 13), new Data(Material.WOOL, (byte) 13), new Data(Material.DIRT, (byte) 0), new Data(Material.WOOL, (byte) 13), new Data(Material.DIRT, (byte) 0), new Data(Material.DIRT, (byte) 0), new Data(Material.DIRT, (byte) 0), new Data(Material.WOOL, (byte) 8), new Data(Material.DIRT, (byte) 0), new Data(Material.WOOL, (byte) 8), new Data(Material.WOOL, (byte) 8), new Data(Material.WOOL, (byte) 8)), new Picture(new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 5)), new Picture(new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0)), new Picture(new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0)), new Picture(new Data(Material.WOOL, (byte) 13), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 5), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 1), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 5)), new Picture(new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.STAINED_CLAY, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.QUARTZ_BLOCK, (byte) 0), new Data(Material.QUARTZ_BLOCK, (byte) 0), new Data(Material.QUARTZ_BLOCK, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0), new Data(Material.COAL_BLOCK, (byte) 0)), new Picture(new Data(Material.WOOL, (byte) 6), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.WOOL, (byte) 6), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.WOOL, (byte) 6), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.QUARTZ_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.WOOL, (byte) 6), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.WOOL, (byte) 6)), new Picture(new Data(Material.STONE, (byte) 6), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 14), new Data(Material.STONE, (byte) 6), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 15), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 15), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 15), new Data(Material.WOOL, (byte) 14), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 15), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.STONE, (byte) 6), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0)), new Picture(new Data(Material.CLAY, (byte) 0), new Data(Material.CLAY, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.CLAY, (byte) 0), new Data(Material.CLAY, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.CLAY, (byte) 0), new Data(Material.CLAY, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 3), new Data(Material.CLAY, (byte) 0), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11), new Data(Material.WOOL, (byte) 11)), new Picture(new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 6), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 7), new Data(Material.SPONGE, (byte) 0), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.SPONGE, (byte) 0), new Data(Material.SPONGE, (byte) 0), new Data(Material.SPONGE, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.SPONGE, (byte) 0), new Data(Material.WOOL, (byte) 0)), new Picture(new Data(Material.WOOL, (byte) 9), new Data(Material.WOOL, (byte) 9), new Data(Material.WOOL, (byte) 9), new Data(Material.WOOL, (byte) 9), new Data(Material.WOOL, (byte) 9), new Data(Material.WOOL, (byte) 9), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 9), new Data(Material.WOOL, (byte) 4), new Data(Material.GLASS, (byte) 0), new Data(Material.WOOL, (byte) 4), new Data(Material.GLASS, (byte) 0), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 4)), new Picture(new Data(Material.WORKBENCH, (byte) 0), new Data(Material.BRICK, (byte) 0), new Data(Material.BRICK, (byte) 0), new Data(Material.BRICK, (byte) 0), new Data(Material.BRICK, (byte) 0), new Data(Material.FURNACE, (byte) 0), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.CHEST, (byte) 0), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.FURNACE, (byte) 0), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.WOOL, (byte) 2), new Data(Material.WORKBENCH, (byte) 0), new Data(Material.BRICK, (byte) 0), new Data(Material.BRICK, (byte) 0), new Data(Material.BRICK, (byte) 0)), new Picture(new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.LEAVES, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.LEAVES, (byte) 0), new Data(Material.LEAVES, (byte) 0), new Data(Material.LEAVES, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.LEAVES, (byte) 0), new Data(Material.LOG, (byte) 3), new Data(Material.LEAVES, (byte) 0), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.LOG, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 3), new Data(Material.WOOL, (byte) 13), new Data(Material.WOOL, (byte) 13), new Data(Material.LOG, (byte) 3), new Data(Material.WOOL, (byte) 13)), new Picture(new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.IRON_FENCE, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 7), new Data(Material.GLASS, (byte) 0), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 4), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 7), new Data(Material.WOOL, (byte) 0), new Data(Material.WOOL, (byte) 7)), new Picture(new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.EMERALD_BLOCK, (byte) 0), new Data(Material.DIAMOND_BLOCK, (byte) 0), new Data(Material.GOLD_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.GOLD_BLOCK, (byte) 0), new Data(Material.IRON_BLOCK, (byte) 0), new Data(Material.LAPIS_BLOCK, (byte) 0), new Data(Material.IRON_BLOCK, (byte) 0), new Data(Material.EMERALD_BLOCK, (byte) 0), new Data(Material.DIAMOND_BLOCK, (byte) 0), new Data(Material.LAPIS_BLOCK, (byte) 0), new Data(Material.IRON_BLOCK, (byte) 0), new Data(Material.LAPIS_BLOCK, (byte) 0), new Data(Material.DIAMOND_BLOCK, (byte) 0), new Data(Material.EMERALD_BLOCK, (byte) 0), new Data(Material.IRON_BLOCK, (byte) 0), new Data(Material.LAPIS_BLOCK, (byte) 0), new Data(Material.IRON_BLOCK, (byte) 0), new Data(Material.GOLD_BLOCK, (byte) 0), new Data(Material.REDSTONE_BLOCK, (byte) 0), new Data(Material.GOLD_BLOCK, (byte) 0), new Data(Material.DIAMOND_BLOCK, (byte) 0), new Data(Material.EMERALD_BLOCK, (byte) 0)), new Picture(new Data(Material.WOOD, (byte) 5), new Data(Material.WOOD, (byte) 4), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 4), new Data(Material.WOOD, (byte) 5), new Data(Material.WOOD, (byte) 4), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 1), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 4), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 1), new Data(Material.WOOD, (byte) 0), new Data(Material.WOOD, (byte) 1), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 4), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 1), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 4), new Data(Material.WOOD, (byte) 5), new Data(Material.WOOD, (byte) 4), new Data(Material.WOOD, (byte) 2), new Data(Material.WOOD, (byte) 4)),};

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TileRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Tile Swap";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.STEP);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Slide blocks around to match the picture.",
                ChatColor.ITALIC + "Do it as quickly as possible!",
                ChatColor.UNDERLINE + "((the bedrock block is the slider))"
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Switch tiles to match the pictures!";
    }

    @Override
    public String getObjectiveWord() {
        return "seconds remaining";
    }

    @Override
    public int getDuration() {
        return 420;
    }

    @Override
    public int getGold() {
        return 220;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getFlawless() {
        return 300;
    }

    @Override
    public int getMaxReward() {
        return 5000;
    }

    /**
     * Represents a 2D square of block data.
     */
    private static class Picture {
        final Data[] data;

        Picture(Data... data) {
            this.data = data;
        }
    }

    /**
     * Represents the data of a single block.
     */
    private static class Data {
        public final Material material;
        public final byte data;

        Data(Material material, byte data) {
            this.material = material;
            this.data = data;
        }
    }

    private class TileRun extends TimedTurnBasedMinigameTask {

        /* Holds all blocks which are considered game tiles. */
        private HashMap<Block, Short> clickable;

        TileRun(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 16);
        }

        @Override
        public void onStart() {
            clickable = new HashMap<>();
            cooldown = System.currentTimeMillis();

            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, 9, 6, 9)) {
                error();
                return;
            }

            makePlatform(gen, 9, 9, Material.SMOOTH_BRICK);

            // Generate solution wall.
            for (int y = 1; y < 10; y++)
                for (int x = 0; x < 9; x++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + 9);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            // Generate picture on solution wall..
            Picture picture = PICTURES[main.main().rng().nextInt(PICTURES.length)];
            for (short i = 1; i <= 24; i++) {
                Data data = picture.data[i - 1];
                Block found = generateWallLocation(i).getBlock();
                found.setType(data.material);
                found.setData(data.data);
            }

            // Place the bedrock slider.
            generateWallLocation((short) 25).getBlock().setType(Material.BEDROCK);

            for (short i = 1; i <= 25; i++)
                registerClicks(generateGridLocation(i), i);

            // Randomize positions of tiles on the floor.
            ArrayList<Short> space = new ArrayList<>();
            int ind = 0;
            while (space.size() < 24) {
                short picked = (short) (main.main().rng().nextInt(25) + 1);
                if (space.contains(picked)) continue;
                Data data = picture.data[ind];
                ind++;
                Block found = generateGridLocation(picked).getBlock();
                if (found.getType() == data.material && found.getData() == data.data && main.main().rng().nextBoolean()) {
                    ind--;
                    continue;
                }
                space.add(picked);
                found.setType(data.material);
                found.setData(data.data);
            }
            for (short i = 1; i <= 25; i++)
                if (!space.contains(i))
                    generateGridLocation(i).getBlock().setType(Material.BEDROCK);
            space.clear();

            // Teleport participants to the platform.
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 5));

            selectPlayer();
        }

        /**
         * Swaps the bedrock slider with a block adjacent to it.
         *
         * @param block The block adjacent to the slider.
         */
        private void swap(Block block) {
            if (!clickable.containsKey(block)) return;

            // Check that the bedrock slider is adjacent.
            Block bedrock = null;
            Block[] simpleRelatives = getSimpleRelatives(block);
            for (Block adjacent : simpleRelatives)
                if (adjacent.getType() == Material.BEDROCK)
                    bedrock = adjacent;
            if (bedrock == null) return;

            // Swap the two blocks.
            Data data = new Data(block.getType(), block.getData());
            block.setType(Material.BEDROCK);
            bedrock.setType(data.material);
            bedrock.setData(data.data);
            cooldown = System.currentTimeMillis() + 400;
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                if (checkSolved())
                    stop();
                else
                    selectPlayer();
            }, 5L);
            main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);
        }

        /**
         * @return True if the picture on the floor matches the picture on the wall.
         */
        private boolean checkSolved() {
            for (short i = 1; i <= 25; i++) {
                Block wall = generateWallLocation(i).getBlock();
                Block floor = generateGridLocation(i).getBlock();
                if (wall.getType() != floor.getType()) return false;
                if (wall.getData() != floor.getData()) return false;
            }
            return true;
        }

        /**
         * @return Block relatives in X,Z directions.
         */
        private Block[] getSimpleRelatives(Block block) {
            return new Block[]{block.getRelative(BlockFace.NORTH),
                    block.getRelative(BlockFace.EAST),
                    block.getRelative(BlockFace.SOUTH),
                    block.getRelative(BlockFace.WEST)};
        }

        /**
         * Registers clickable tiles on the floor.
         */
        private void registerClicks(Location loc, short allocation) {
            clickable.put(loc.getBlock(), allocation);
        }

        /**
         * Takes a board index allocation and returns a tile
         * on the floor which the player can interact with.
         *
         * @param allocation The board index allocation.
         */
        private Location generateGridLocation(short allocation) {
            Location result = gen.clone().add(2, 0, 2);
            if (allocation >= 21) result.add(0, 0, 0);
            else if (allocation >= 16) result.add(0, 0, 1);
            else if (allocation >= 11) result.add(0, 0, 2);
            else if (allocation >= 6) result.add(0, 0, 3);
            else result.add(0, 0, 4);
            while (allocation > 5)
                allocation -= 5;
            return result.add(5 - allocation, 0, 0);
        }

        /**
         * Takes a board index allocation and returns a tile
         * on the wall which the player can view.
         *
         * @param allocation The board index allocation.
         */
        private Location generateWallLocation(short allocation) {
            Location result = gen.clone().add(2, 7, 9);
            if (allocation >= 21) result.subtract(0, 4, 0);
            else if (allocation >= 16) result.subtract(0, 3, 0);
            else if (allocation >= 11) result.subtract(0, 2, 0);
            else if (allocation >= 6) result.subtract(0, 1, 0);
            else result.subtract(0, 0, 0);
            while (allocation > 5)
                allocation -= 5;
            return result.add(5 - allocation, 0, 0);
        }

        @Override
        public void onFinish() {
            returnParticipants();
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
            if (canMove(event.getPlayer()))
                swap(event.getClickedBlock());
        }
    }
}
