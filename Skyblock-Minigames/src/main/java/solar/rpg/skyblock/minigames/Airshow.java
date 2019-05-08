package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.DefaultMinigameTask;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.stored.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Airshow extends Minigame {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new RingRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Airshow";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ELYTRA);
    }

    public String[] getDescription() {
        return new String[]{"Is it a bird? Is it a plane?",
                "No, it's a pair of Super Elytra!",
                ChatColor.ITALIC + "Soar through rings in the sky!",
                ChatColor.ITALIC + "(press sneak to gain height!)",
                ChatColor.ITALIC + "(smaller rings = more points)",
                ChatColor.UNDERLINE + "(make sure you have elytra!!))"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    public String getSummary() {
        return "Fly through rings with Elytra!";
    }

    public String getObjectiveWord() {
        return "points scored";
    }

    public int getDuration() {
        return 180;
    }

    public int getGold() {
        return 75;
    }

    public int getMaxReward() {
        return 7500;
    }

    public boolean isScoreDivisible() {
        return true;
    }

    private class RingRun extends DefaultMinigameTask implements Listener {

        private World world;

        /*
         * [0][1][2] XYZ
         * [3] X or Z? (0 = Z or 1 = X)
         * [4] Size/Radius (0, 1, or 2)
         */
        private ArrayList<Integer[]> data;

        RingRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
        }

        public void onStart() {
            // Initialize data and world variable.
            data = new ArrayList<>();
            world = Bukkit.getWorld(Settings.ADMIN_WORLD_ID);
        }

        public void onTick() {
            // Re-create rings as needed.
            int toRecreate = 10 - data.size();
            while (toRecreate > 0) {
                toRecreate--;
                generateRing();
            }
        }

        public void onFinish() {
            for (Integer[] entry : data)
                removeRing(entry);
            data.clear();
            data = null;
        }

        /**
         * Remember, you idiot.
         *
         * @param data Ring data.
         */
        private void removeRing(Integer[] data) {
            if (data[3] == 0) {
                for (int z = data[2] - 5; z < data[2] + 5; z++)
                    for (int y = data[1] - 5; y < data[1] + 5; y++) {
                        Block found = world.getBlockAt(data[0], y, z);
                        if (found.getType() == Material.WOOL)
                            found.setType(Material.AIR);
                    }
            } else {
                for (int x = data[0] - 5; x < data[0] + 5; x++)
                    for (int y = data[1] - 5; y < data[1] + 5; y++) {
                        Block found = world.getBlockAt(x, y, data[2]);
                        if (found.getType() == Material.WOOL)
                            found.setType(Material.AIR);
                    }
            }
        }

        /**
         * Creates a random ring of a random size and orientation.
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
                }
            }
            data.add(new Integer[]{random.getBlockX(), random.getBlockY(), random.getBlockZ(), xz, size});
        }

        /**
         * Converts size into a dye color for the ring.
         */
        private byte getColor(int size) {
            if (size == 2)
                return 13;
            else if (size == 1)
                return 4;
            else if (size == 0)
                return 14;
            else return 15;
        }

        /**
         * Converts size and orientation into an array of locations. (a ring)
         */
        private Location[] ringBlocks(Location center, int size, int xz) {
            if (size == 0) return xz == 0 ? smallZ(center) : smallX(center);
            else if (size == 1) return xz == 0 ? mediumZ(center) : mediumX(center);
            else return xz == 0 ? largeZ(center) : largeX(center);
        }

        private Location[] smallX(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 2, 0).clone(),
                    gen.add(1, 0, 0).clone(),
                    gen.add(1, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.subtract(1, -1, 0).clone(),
                    gen.subtract(1, 0, 0).clone(),
                    gen.subtract(1, 0, 0).clone(),
                    gen.subtract(1, 1, 0).clone(),
                    gen.subtract(0, 1, 0).clone(),
                    gen.subtract(0, 1, 0).clone(),
                    gen.subtract(-1, 1, 0).clone()};
        }

        private Location[] smallZ(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 2, 0).clone(),
                    gen.add(0, 0, 1).clone(),
                    gen.add(0, 1, 1).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.subtract(0, -1, 1).clone(),
                    gen.subtract(0, 0, 1).clone(),
                    gen.subtract(0, 0, 1).clone(),
                    gen.subtract(0, 1, 1).clone(),
                    gen.subtract(0, 1, 0).clone(),
                    gen.subtract(0, 1, 0).clone(),
                    gen.subtract(0, 1, -1).clone()};
        }

        private Location[] mediumX(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 3, 0).clone(),
                    gen.add(1, 0, 0).clone(),
                    gen.add(1, 1, 0).clone(),
                    gen.add(1, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(-1, 1, 0).clone(),
                    gen.add(-1, 1, 0).clone(),
                    gen.add(-1, 0, 0).clone(),
                    gen.add(-1, 0, 0).clone(),
                    gen.add(-1, -1, 0).clone(),
                    gen.add(-1, -1, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(1, -1, 0).clone(),
                    gen.add(1, -1, 0).clone()};
        }

        private Location[] mediumZ(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 3, 0).clone(),
                    gen.add(0, 0, 1).clone(),
                    gen.add(0, 1, 1).clone(),
                    gen.add(0, 1, 1).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, -1).clone(),
                    gen.add(0, 1, -1).clone(),
                    gen.add(0, 0, -1).clone(),
                    gen.add(0, 0, -1).clone(),
                    gen.add(0, -1, -1).clone(),
                    gen.add(0, -1, -1).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, -1, 1).clone(),
                    gen.add(0, -1, 1).clone()};
        }

        private Location[] largeX(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 4, 0).clone(),
                    gen.add(1, 0, 0).clone(),
                    gen.add(1, 1, 0).clone(),
                    gen.add(1, 0, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(1, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(-1, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(-1, 0, 0).clone(),
                    gen.add(-1, 1, 0).clone(),
                    gen.add(-1, 0, 0).clone(),
                    gen.add(-1, 0, 0).clone(),
                    gen.add(-1, -1, 0).clone(),
                    gen.add(-1, 0, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(-1, -1, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(1, -1, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(1, 0, 0).clone(),
                    gen.add(1, -1, 0).clone()};
        }

        private Location[] largeZ(Location center) {
            Location gen = center.clone();
            return new Location[]{gen.subtract(0, 4, 0).clone(),
                    gen.add(0, 0, 1).clone(),
                    gen.add(0, 1, 1).clone(),
                    gen.add(0, 0, 1).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, 1).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 1, -1).clone(),
                    gen.add(0, 1, 0).clone(),
                    gen.add(0, 0, -1).clone(),
                    gen.add(0, 1, -1).clone(),
                    gen.add(0, 0, -1).clone(),
                    gen.add(0, 0, -1).clone(),
                    gen.add(0, -1, -1).clone(),
                    gen.add(0, 0, -1).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, -1, -1).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, -1, 1).clone(),
                    gen.add(0, -1, 0).clone(),
                    gen.add(0, 0, 1).clone(),
                    gen.add(0, -1, 1).clone()};
        }

        /**
         * Check that a ring was flown in to.
         */
        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (event.getPlayer().isGliding()) {
                Vector vel = event.getPlayer().getVelocity();
                Integer[] toRemove = null;
                for (Integer[] datum : data) {
                    if ((datum[3] == 0)) {
                        if (vel.getZ() < 0) {
                            if (vel.getZ() > vel.getX()) return;
                        } else if (vel.getZ() < vel.getX()) return;
                    } else if ((datum[3] == 1))
                        if (vel.getX() < 0) {
                            if (vel.getX() > vel.getZ()) return;
                        } else if (vel.getX() < vel.getZ()) return;
                    if (new Location(event.getPlayer().getWorld(), datum[0], datum[1], datum[2]).distanceSquared(event.getPlayer().getLocation()) <= 16) {
                        removeRing(datum);
                        toRemove = datum;
                        scorePoint(event.getPlayer(), true, Math.abs(datum[4] - 3));
                        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5F, 1F);
                    }
                }
                if (toRemove != null)
                    data.remove(toRemove);
            }
        }

        /**
         * Tap sneak for additional gliding height.
         */
        @EventHandler
        public void onElytra(PlayerToggleSneakEvent event) {
            if (!event.getPlayer().isGliding()) return;
            if (event.getPlayer().isSneaking()) return;
            Vector velocity = event.getPlayer().getVelocity();
            if (velocity.getY() >= 2) return;
            if (participants.contains(event.getPlayer().getUniqueId()))
                event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1.05, 0)));
        }
    }
}
