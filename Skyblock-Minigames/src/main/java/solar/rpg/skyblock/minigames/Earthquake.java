package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.cb.Title;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimeCountupMinigameTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Earthquake extends Minigame {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new QuakeRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Earthquake!";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.STONE, 1, (short) 6);
    }

    public String[] getDescription() {
        return new String[]{"The ground is shaking!",
                "Be sure to watch your step.",
                ChatColor.ITALIC + "How long can you survive without",
                ChatColor.ITALIC + "being thrown off of your island?"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public String getSummary() {
        return "Don't fall off your island!";
    }

    public String getObjectiveWord() {
        return "seconds survived";
    }

    public int getDuration() {
        return 0;
    }

    public int getGold() {
        return 300;
    }

    public int getMaxReward() {
        return 8500;
    }

    private class QuakeRun extends TimeCountupMinigameTask {

        private int difficulty;
        private int runs;
        private int sinceLastQuake;

        QuakeRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
            rules.put("modules", false);
            rules.put("placing", false);
        }

        public void onTick() {
            for (UUID uuid : participants) {
                if (!isValidParticipant(uuid)) continue;
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null) continue;
                if (pl.getVelocity().getY() < -0.85) return;
                if (pl.getVelocity().getY() > 0.5) return;
            }
            if (sinceLastQuake < timeout(difficulty)) {
                sinceLastQuake++;
                return;
            } else
                sinceLastQuake = 0;
            runs++;
            if (runs % 5 == 0 && runs != 0 && difficulty < 10) {
                difficulty++;
                titleParticipants(ChatColor.RED + "Difficulty Increased!", "");
            }
            quake();
        }

        /**
         * Decreases wait time between earthquakes as difficulty increases.
         */
        private int timeout(int difficulty) {
            if (difficulty <= 3)
                return 9;
            else if (difficulty <= 6)
                return 6;
            else if (difficulty <= 8)
                return 3;
            else
                return 2;
        }

        /**
         * [0] Delay in ticks before decrementing timer.
         * [1] Amount of times the timer will be decremented.
         */
        private Integer[] quake(int difficulty) {
            if (difficulty <= 2)
                return new Integer[]{20, 5};
            else if (difficulty <= 4)
                return new Integer[]{8, 5};
            else if (difficulty <= 7)
                return new Integer[]{18, 3};
            else
                return new Integer[]{8, 3};
        }

        void quake() {
            Set<UUID> targets = new HashSet<>();
            for (UUID uuid : getParticipants()) {
                if (!isValidParticipant(uuid)) continue;
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null) continue;
                pl.teleport(generateLocation(165, 0, pl.getLocation().getBlockY(), false, true));
                targets.add(uuid);
            }
            Integer[] data = quake(difficulty);
            new QuakeTask(data[1], targets).runTaskTimer(main.main().plugin(), 0L, data[0]);
        }

        /**
         * Creates a vector of a random x, y, and z velocity.
         */
        private Vector randomVel() {
            double x = (Math.random() * 1.15) * (main.main().rng().nextInt(2) + 1);
            double z = (Math.random() * 1.15) * (main.main().rng().nextInt(2) + 1);
            double y = (Math.random() * (main.main().rng().nextInt(2) + 1)) * 0.7;
            if (main.main().rng().nextBoolean()) x = -x;
            if (main.main().rng().nextBoolean()) z = -z;
            return new Vector(x, y, z);
        }

        class QuakeTask extends BukkitRunnable {

            final Set<UUID> targets;
            int count;

            QuakeTask(int count, Set<UUID> targets) {
                this.count = count;
                this.targets = targets;
            }

            public void run() {
                for (UUID target : targets) {
                    if (Bukkit.getPlayer(target) == null) continue;
                    if (disqualified == null) break;
                    Player tar = Bukkit.getPlayer(target);
                    if (count > 0 && isValidParticipant(target)) {
                        tar.playSound(tar.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 2F, 2F);
                        Title.showTitle(tar, ChatColor.RED + "" + count + "!", "", 0, 20, 0);
                    } else {
                        tar.getWorld().playSound(tar.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
                        tar.getWorld().playSound(tar.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1F, 1F);
                        tar.getWorld().playSound(tar.getLocation(), Sound.AMBIENT_CAVE, 3F, 3F);
                        tar.setVelocity(randomVel());
                    }
                }
                if (count <= 0) this.cancel();
                count--;
            }
        }
    }
}
