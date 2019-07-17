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
import solar.rpg.skyblock.island.minigames.*;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.minigames.tasks.TimedTurnBasedMinigameTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TileSwap extends Minigame implements FlawlessEnabled, BoardGame {

    /* Supported tile swap pictures. Warning: long! */
    private static final Picture[] PICTURES = new Picture[]{new Picture(new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.DIRT, (byte) 0), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.DIRT, (byte) 0), new ItemStack(Material.DIRT, (byte) 0), new ItemStack(Material.DIRT, (byte) 0), new ItemStack(Material.LIGHT_GRAY_WOOL), new ItemStack(Material.DIRT, (byte) 0), new ItemStack(Material.LIGHT_GRAY_WOOL), new ItemStack(Material.LIGHT_GRAY_WOOL), new ItemStack(Material.LIGHT_GRAY_WOOL)), new Picture(new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.LIME_WOOL)), new Picture(new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL)), new Picture(new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL)), new Picture(new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.LIME_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.ORANGE_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.LIME_WOOL)), new Picture(new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.QUARTZ_BLOCK, (byte) 0), new ItemStack(Material.QUARTZ_BLOCK, (byte) 0), new ItemStack(Material.QUARTZ_BLOCK, (byte) 0), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.COAL_BLOCK, (byte) 0), new ItemStack(Material.COAL_BLOCK, (byte) 0)), new Picture(new ItemStack(Material.PINK_WOOL), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.QUARTZ_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.PINK_WOOL)), new Picture(new ItemStack(Material.STONE, (byte) 6), new ItemStack(Material.RED_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.STONE, (byte) 6), new ItemStack(Material.RED_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.BLACK_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.BLACK_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.BLACK_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.BLACK_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.STONE, (byte) 6), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL)), new Picture(new ItemStack(Material.CLAY, (byte) 0), new ItemStack(Material.CLAY, (byte) 0), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.CLAY, (byte) 0), new ItemStack(Material.CLAY, (byte) 0), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.CLAY, (byte) 0), new ItemStack(Material.CLAY, (byte) 0), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.CLAY, (byte) 0), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL), new ItemStack(Material.BLUE_WOOL)), new Picture(new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.PINK_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.SPONGE, (byte) 0), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.SPONGE, (byte) 0), new ItemStack(Material.SPONGE, (byte) 0), new ItemStack(Material.SPONGE, (byte) 0), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.SPONGE, (byte) 0), new ItemStack(Material.WHITE_WOOL)), new Picture(new ItemStack(Material.CYAN_WOOL), new ItemStack(Material.CYAN_WOOL), new ItemStack(Material.CYAN_WOOL), new ItemStack(Material.CYAN_WOOL), new ItemStack(Material.CYAN_WOOL), new ItemStack(Material.CYAN_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.CYAN_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.GLASS, (byte) 0), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.GLASS, (byte) 0), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL)), new Picture(new ItemStack(Material.CRAFTING_TABLE), new ItemStack(Material.BRICK, (byte) 0), new ItemStack(Material.BRICK, (byte) 0), new ItemStack(Material.BRICK, (byte) 0), new ItemStack(Material.BRICK, (byte) 0), new ItemStack(Material.FURNACE, (byte) 0), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.CHEST, (byte) 0), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.FURNACE, (byte) 0), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.MAGENTA_WOOL), new ItemStack(Material.CRAFTING_TABLE), new ItemStack(Material.BRICK, (byte) 0), new ItemStack(Material.BRICK, (byte) 0), new ItemStack(Material.BRICK, (byte) 0)), new Picture(new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.OAK_LEAVES), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.OAK_LEAVES), new ItemStack(Material.OAK_LEAVES), new ItemStack(Material.OAK_LEAVES), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.OAK_LEAVES), new ItemStack(Material.JUNGLE_LOG), new ItemStack(Material.OAK_LEAVES), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.JUNGLE_LOG), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.LIGHT_BLUE_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.JUNGLE_LOG), new ItemStack(Material.GREEN_WOOL)), new Picture(new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.IRON_BARS), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.GLASS, (byte) 0), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.GRAY_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.GRAY_WOOL)), new Picture(new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.EMERALD_BLOCK, (byte) 0), new ItemStack(Material.DIAMOND_BLOCK, (byte) 0), new ItemStack(Material.GOLD_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.GOLD_BLOCK, (byte) 0), new ItemStack(Material.IRON_BLOCK, (byte) 0), new ItemStack(Material.LAPIS_BLOCK, (byte) 0), new ItemStack(Material.IRON_BLOCK, (byte) 0), new ItemStack(Material.EMERALD_BLOCK, (byte) 0), new ItemStack(Material.DIAMOND_BLOCK, (byte) 0), new ItemStack(Material.LAPIS_BLOCK, (byte) 0), new ItemStack(Material.IRON_BLOCK, (byte) 0), new ItemStack(Material.LAPIS_BLOCK, (byte) 0), new ItemStack(Material.DIAMOND_BLOCK, (byte) 0), new ItemStack(Material.EMERALD_BLOCK, (byte) 0), new ItemStack(Material.IRON_BLOCK, (byte) 0), new ItemStack(Material.LAPIS_BLOCK, (byte) 0), new ItemStack(Material.IRON_BLOCK, (byte) 0), new ItemStack(Material.GOLD_BLOCK, (byte) 0), new ItemStack(Material.REDSTONE_BLOCK, (byte) 0), new ItemStack(Material.GOLD_BLOCK, (byte) 0), new ItemStack(Material.DIAMOND_BLOCK, (byte) 0), new ItemStack(Material.EMERALD_BLOCK, (byte) 0)), new Picture(new ItemStack(Material.DARK_OAK_WOOD), new ItemStack(Material.ACACIA_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.ACACIA_WOOD), new ItemStack(Material.DARK_OAK_WOOD), new ItemStack(Material.ACACIA_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.SPRUCE_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.ACACIA_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.SPRUCE_WOOD), new ItemStack(Material.OAK_WOOD), new ItemStack(Material.SPRUCE_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.ACACIA_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.SPRUCE_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.ACACIA_WOOD), new ItemStack(Material.DARK_OAK_WOOD), new ItemStack(Material.ACACIA_WOOD), new ItemStack(Material.BIRCH_WOOD), new ItemStack(Material.ACACIA_WOOD)),};

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
        return new ItemStack(Material.SMOOTH_STONE_SLAB);
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
    public int getMinimumPlayers() {
        return 1;
    }

    @Override
    public boolean enforceMinimum() {
        return false;
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

    /**
     * Represents a 2D square of block data.
     */
    private static class Picture {
        final ItemStack[] data;

        Picture(ItemStack... data) {
            this.data = data;
        }
    }

    private class TileRun extends TimedTurnBasedMinigameTask {

        /* Holds all blocks which are considered game tiles. */
        private HashMap<Block, Short> clickable;

        TileRun(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 16);
            rules.put("breaking", false);
            rules.put("placing", false);
        }

        @Override
        protected boolean isNoScoreIfOutOfTime() {
            // Either way, the players have no score if time runs out.
            return true;
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

            makePlatform(gen, 9, 9, Material.STONE_BRICKS);

            // Generate solution wall.
            for (int y = 1; y < 10; y++)
                for (int x = 0; x < 9; x++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + 9);
                    bl.setType(Material.STONE_BRICKS);
                    placed.add(bl);
                }

            // Generate picture on solution wall..
            Picture picture = PICTURES[main.main().rng().nextInt(PICTURES.length)];
            for (short i = 1; i <= 24; i++) {
                Block found = generateWallLocation(i).getBlock();
                found.setType(picture.data[i - 1].getType());
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
                ItemStack data = picture.data[ind];
                ind++;
                Block found = generateGridLocation(picked).getBlock();
                if (found.getType() == data.getType() && main.main().rng().nextBoolean()) {
                    ind--;
                    continue;
                }
                space.add(picked);
                found.setType(data.getType());
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
            ItemStack data = new ItemStack(block.getType(), block.getData());
            block.setType(Material.BEDROCK);
            bedrock.setType(data.getType());
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
