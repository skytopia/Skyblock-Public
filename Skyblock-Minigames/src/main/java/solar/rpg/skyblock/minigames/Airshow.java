package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.minigames.tasks.DefaultMinigameTask;
import solar.rpg.skyblock.stored.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Airshow extends Minigame {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new AirshowTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Airshow";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.ELYTRA);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Soar through rings in the sky!",
                ChatColor.ITALIC + "(tap sneak to glide high!)",
                ChatColor.ITALIC + "(smaller rings = more points)",
                ChatColor.UNDERLINE + "((elytra are required))",
                "\"Is it a bird? Is it a plane?\"",
                "\"No, it's the super glider squad!\"",
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Fly through rings with Elytra!";
    }

    @Override
    public String getObjectiveWord() {
        return "points scored";
    }

    @Override
    public int getDuration() {
        return 180;
    }

    @Override
    public int getGold() {
        return 75;
    }

    @Override
    public int getMaxReward() {
        return 7500;
    }

    @Override
    public boolean isScoreDivisible() {
        return true;
    }

    private class AirshowTask extends DefaultMinigameTask {

        /* Keep an instance of the world where the minigame is running. */
        private World world;

        /*
         * Keeps an instance of each ring's data.
         *
         * Ring data:
         * [0-2] XYZ coordinates of center of ring.
         * [3] 0 = drawn on z axis, 1 = drawn on x axis.
         * [4] Ring size, small (0), medium (1) , large (2).
         */
        private ArrayList<Integer[]> data;

        //TODO: Make ring data into its own object.

        AirshowTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
        }

        @Override
        public void onStart() {
            // Initialize data and world variable.
            data = new ArrayList<>();
            world = Bukkit.getWorld(Settings.ADMIN_WORLD_ID);
        }

        @Override
        public void onTick() {
            // Re-generate rings as they are flown through.
            for (int i = 10 - data.size(); i > 0; i--)
                generateRing();
        }

        @Override
        public void onFinish() {
            // Rings are removed automatically. Just clear the data.
            data.clear();
            data = null;
        }

        /**
         * Removes a ring from the world.
         *
         * @param data Ring data.
         */
        private void removeRing(Integer[] data) {
            if (data[3] == 0) {
                // Remove from z-axis.
                for (int z = data[2] - 5; z < data[2] + 5; z++)
                    for (int y = data[1] - 5; y < data[1] + 5; y++) {
                        Block found = world.getBlockAt(data[0], y, z);
                        if (found.getType() == Material.WOOL)
                            found.setType(Material.AIR);
                    }
            } else {
                // Remove from x axis.
                for (int x = data[0] - 5; x < data[0] + 5; x++)
                    for (int y = data[1] - 5; y < data[1] + 5; y++) {
                        Block found = world.getBlockAt(x, y, data[2]);
                        if (found.getType() == Material.WOOL)
                            found.setType(Material.AIR);
                    }
            }
        }

        /**
         * Creates a random ring of a random size and axis.
         */
        private void generateRing() {
            Location random = generateLocation(70, 20, 128, true, false);
            int xz = main.main().rng().nextInt(2);
            int size = main.main().rng().nextInt(3);
            byte color = getColor(size);
            for (Location loc : ringBlocks(random, size, xz)) {
                if (loc.getBlock().getType() == Material.AIR) {
                    loc.getBlock().setType(Material.WOOL);
                    loc.getBlock().setData(color);

                    // Add this block to the 'placed' array so it gets removed automatically.
                    placed.add(loc.getBlock());
                }
            }
            data.add(new Integer[]{random.getBlockX(), random.getBlockY(), random.getBlockZ(), xz, size});
        }

        /**
         * Converts size into a dye color for the ring.
         */
        private byte getColor(int size) {
            switch (size) {
                case 2:
                    return 13;
                case 1:
                    return 4;
                case 0:
                    return 14;
                default:
                    return 15;
            }
        }

        /**
         * Gets all appropriate locations for the requested ring structure.
         */
        private Location[] ringBlocks(Location center, int size, int xz) {
            if (size == 0) return xz == 0 ? smallZ(center) : smallX(center);
            else if (size == 1) return xz == 0 ? mediumZ(center) : mediumX(center);
            else return xz == 0 ? largeZ(center) : largeX(center);
        }

        /**
         * @param center The center of the ring.
         * @return Small ring on X axis.
         */
        private Location[] smallX(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 2, 0).clone(), gen.add(1, 0, 0).clone(), gen.add(1, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.subtract(1, -1, 0).clone(), gen.subtract(1, 0, 0).clone(), gen.subtract(1, 0, 0).clone(), gen.subtract(1, 1, 0).clone(), gen.subtract(0, 1, 0).clone(), gen.subtract(0, 1, 0).clone(), gen.subtract(-1, 1, 0).clone()};
        }

        /**
         * @param center The center of the ring.
         * @return Small ring on Z axis.
         */
        private Location[] smallZ(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 2, 0).clone(), gen.add(0, 0, 1).clone(), gen.add(0, 1, 1).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.subtract(0, -1, 1).clone(), gen.subtract(0, 0, 1).clone(), gen.subtract(0, 0, 1).clone(), gen.subtract(0, 1, 1).clone(), gen.subtract(0, 1, 0).clone(), gen.subtract(0, 1, 0).clone(), gen.subtract(0, 1, -1).clone()};
        }

        /**
         * @param center The center of the ring.
         * @return Medium ring on X axis.
         */
        private Location[] mediumX(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 3, 0).clone(), gen.add(1, 0, 0).clone(), gen.add(1, 1, 0).clone(), gen.add(1, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(-1, 1, 0).clone(), gen.add(-1, 1, 0).clone(), gen.add(-1, 0, 0).clone(), gen.add(-1, 0, 0).clone(), gen.add(-1, -1, 0).clone(), gen.add(-1, -1, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(1, -1, 0).clone(), gen.add(1, -1, 0).clone()};
        }

        /**
         * @param center The center of the ring.
         * @return Medium ring on Z axis.
         */
        private Location[] mediumZ(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 3, 0).clone(), gen.add(0, 0, 1).clone(), gen.add(0, 1, 1).clone(), gen.add(0, 1, 1).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, -1).clone(), gen.add(0, 1, -1).clone(), gen.add(0, 0, -1).clone(), gen.add(0, 0, -1).clone(), gen.add(0, -1, -1).clone(), gen.add(0, -1, -1).clone(), gen.add(0, -1, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(0, -1, 1).clone(), gen.add(0, -1, 1).clone()};
        }

        /**
         * @param center The center of the ring.
         * @return Large ring on X axis.
         */
        private Location[] largeX(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 4, 0).clone(), gen.add(1, 0, 0).clone(), gen.add(1, 1, 0).clone(), gen.add(1, 0, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(1, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(-1, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(-1, 0, 0).clone(), gen.add(-1, 1, 0).clone(), gen.add(-1, 0, 0).clone(), gen.add(-1, 0, 0).clone(), gen.add(-1, -1, 0).clone(), gen.add(-1, 0, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(-1, -1, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(1, -1, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(1, 0, 0).clone(), gen.add(1, -1, 0).clone()};
        }

        /**
         * @param center The center of the ring.
         * @return Large ring on Z axis.
         */
        private Location[] largeZ(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 4, 0).clone(), gen.add(0, 0, 1).clone(), gen.add(0, 1, 1).clone(), gen.add(0, 0, 1).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, 1).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 1, -1).clone(), gen.add(0, 1, 0).clone(), gen.add(0, 0, -1).clone(), gen.add(0, 1, -1).clone(), gen.add(0, 0, -1).clone(), gen.add(0, 0, -1).clone(), gen.add(0, -1, -1).clone(), gen.add(0, 0, -1).clone(), gen.add(0, -1, 0).clone(), gen.add(0, -1, -1).clone(), gen.add(0, -1, 0).clone(), gen.add(0, -1, 0).clone(), gen.add(0, -1, 1).clone(), gen.add(0, -1, 0).clone(), gen.add(0, 0, 1).clone(), gen.add(0, -1, 1).clone()};
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (event.getPlayer().isGliding()) {
                Vector vel = event.getPlayer().getVelocity();
                Integer[] toRemove = null;
                for (Integer[] datum : data) {
                    // Check that they are flying perpendicular to this ring.
                    if ((datum[3] == 0)) {
                        // Ring was drawn on Z axis.
                        if (Math.abs(vel.getZ()) < 0) {
                            if (vel.getZ() > vel.getX()) return;
                            else if (vel.getZ() < vel.getX()) return;
                        }
                    } else if ((datum[3] == 1))
                        // Ring was drawn on X axis.
                        if (Math.abs(vel.getX()) < 0) {
                            if (vel.getX() > vel.getZ()) return;
                            else if (vel.getX() < vel.getZ()) return;
                        }

                    // If they are, check if they are nearby.
                    if (new Location(event.getPlayer().getWorld(), datum[0], datum[1], datum[2]).distanceSquared(event.getPlayer().getLocation()) <= 16) {
                        removeRing(datum);
                        toRemove = datum;
                        // Smaller ring = more points.
                        scorePoints(event.getPlayer(), true, 3 - datum[4]);
                        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5F, 1F);
                    }
                }
                if (toRemove != null)
                    data.remove(toRemove);
            }
        }

        @EventHandler
        public void onElytra(PlayerToggleSneakEvent event) {
            if (!event.getPlayer().isGliding()) return;
            if (event.getPlayer().isSneaking()) return;
            Vector velocity = event.getPlayer().getVelocity();
            if (velocity.getY() >= 2) return;
            if (participants.contains(event.getPlayer().getUniqueId()))
                event.getPlayer().setVelocity(new Vector(velocity.getX() * 1.05, velocity.getY() + 0.5, velocity.getZ() * 1.05));
        }
    }
}
