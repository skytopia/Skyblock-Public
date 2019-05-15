package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.minigames.tasks.TimeCountupMinigameTask;
import solar.rpg.skyblock.util.Title;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Earthquake extends Minigame {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new EarthquakeTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Earthquake!";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.STONE, 1, (short) 6);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "How long can you survive without",
                ChatColor.ITALIC + "being thrown off of your island?",
                "\"A minigame of a whole different magnitude!\""
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public String getSummary() {
        return "Don't fall off your island!";
    }

    @Override
    public String getObjectiveWord() {
        return "seconds survived";
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getGold() {
        return 300;
    }

    @Override
    public int getMaxReward() {
        return 8500;
    }

    private class EarthquakeTask extends TimeCountupMinigameTask {

        /* Current difficulty (strength and speed) of the earthquakes. */
        private int magnitude;

        /* Amount of earthquakes that have struck. */
        private int runs;

        /* Amount of seconds since the last earthquake struck. */
        private int sinceLastQuake;

        EarthquakeTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
            rules.put("modules", false);
            rules.put("placing", false);
        }

        @Override
        public void onTick() {
            // Do not continue game while players are falling or being thrown up.
            // This ensures that players who have fallen off their island die.
            for (UUID uuid : participants) {
                if (!isValidParticipant(uuid)) continue;
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null) continue;
                if (pl.getVelocity().getY() < -0.85) return;
                if (pl.getVelocity().getY() > 0.5) return;
            }

            // After a quakeTime, a timeout is selected based on the current difficulty.
            if (sinceLastQuake < timeout()) {
                sinceLastQuake++;
                return;
            } else
                sinceLastQuake = 0;

            // After timeout, do the next earthquake.
            runs++;
            if (runs % 5 == 0 && runs != 0 && magnitude < 10) {
                magnitude++;
                titleParticipants(ChatColor.RED + "Magnitude Increased!", "");
            }

            targetPlayers();
        }

        /**
         * Calculates how many seconds of grace there are between earthquakes.
         * This decreases with difficulty, and is always the lowest value on harder.
         */
        private int timeout() {
            if (difficulty.equals(Difficulty.HARDER)) return 2;
            else if (magnitude <= 3) return 9;
            else if (magnitude <= 6) return 6;
            else if (magnitude <= 8) return 3;
            else return 2;
        }

        /**
         * Calculates quakeTime time data based on difficulty.
         * Quake time data:
         * [0] Delay in ticks before decrementing timer.
         * [1] Amount of times the timer will be decremented.
         *
         * @return Quake time data.
         */
        private Integer[] quakeTime() {
            // Harder difficulty: Delay time is always between 5-8 ticks. Timer time is always between 1 and 4.
            if (difficulty.equals(Difficulty.HARDER))
                return new Integer[]{main.main().rng().nextInt(4) + 5, main.main().rng().nextInt(4) + 1};
            else if (magnitude <= 2) return new Integer[]{20, 5};
            else if (magnitude <= 4) return new Integer[]{8, 5};
            else if (magnitude <= 7) return new Integer[]{18, 3};
            else return new Integer[]{8, 3};
        }

        /**
         * Targets players to be victims of the next earthquake.
         */
        private void targetPlayers() {
            Set<UUID> targets = new HashSet<>();
            for (UUID uuid : getParticipants()) {
                if (!isValidParticipant(uuid)) continue;
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null) continue;
                pl.teleport(generateLocation(165, 0, pl.getLocation().getBlockY(), false, true));
                targets.add(uuid);
            }

            // Run the earthquake task.
            Integer[] data = quakeTime();
            new QuakeTask(targets, data[1], randomVel()).runTaskTimer(main.main().plugin(), 0L, data[0]);
        }

        /**
         * Creates a vector of a random x, y, and z velocity.
         * This acts as the strike of the earthquake.
         */
        private Vector randomVel() {
            // Harder: maximum velocity modifiers are slightly larger. Increases throwing distance.
            double maxHorizontalModifier = difficulty.equals(Difficulty.HARDER) ? 1.25 : 1.15;
            double maxVerticalModifier = difficulty.equals(Difficulty.HARDER) ? 0.9 : 0.7;

            double x = (Math.random() * maxHorizontalModifier) * (main.main().rng().nextInt(2) + 1);
            double z = (Math.random() * maxHorizontalModifier) * (main.main().rng().nextInt(2) + 1);
            double y = (Math.random() * (main.main().rng().nextInt(2) + 1)) * maxVerticalModifier;
            if (main.main().rng().nextBoolean()) x = -x;
            if (main.main().rng().nextBoolean()) z = -z;
            return new Vector(x, y, z);
        }

        /**
         * A timer that starts after a specified delay.
         * Once targets and throw direction have been chosen, do the countdown.
         * Countdown the specified number of seconds, then throw all targets.
         */
        private class QuakeTask extends BukkitRunnable {

            private final Set<UUID> targets;
            private final Vector quakeDir;
            private int count;

            QuakeTask(Set<UUID> targets, int count, Vector quakeDir) {
                this.targets = targets;
                this.count = count;
                this.quakeDir = quakeDir;
            }

            public void run() {
                for (UUID target : targets) {
                    if (Bukkit.getPlayer(target) == null) continue;
                    if (disqualified == null) break;
                    Player tar = Bukkit.getPlayer(target);
                    if (count > 0 && isValidParticipant(target)) {
                        tar.playSound(tar.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 2F, 2F);
                        Title.showTitle(tar, ChatColor.RED + "" + count + "!", ChatColor.GOLD + "Get to cover!", 0, 20, 0);
                    } else {
                        tar.getWorld().playSound(tar.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
                        tar.getWorld().playSound(tar.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1F, 1F);
                        tar.getWorld().playSound(tar.getLocation(), Sound.AMBIENT_CAVE, 3F, 3F);
                        tar.setVelocity(quakeDir);
                    }
                }
                if (count <= 0) this.cancel();
                count--;
            }
        }
    }
}
