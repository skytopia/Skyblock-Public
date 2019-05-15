package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.minigames.tasks.TimeCountupMinigameTask;
import solar.rpg.skyblock.util.Title;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MrCloudSays extends Minigame implements NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MrCloudSaysTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Mr. Cloud Says";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.RECORD_9);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Follow Mr. Cloud's commands!",
                ChatColor.ITALIC + "(hint: stand in an open area!)",
                "\"My god, all these kids have A.D.D.!\""
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
        return "Follow Mr Cloud's commands!";
    }

    @Override
    public String getObjectiveWord() {
        return "seconds elapsed";
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getGold() {
        return 215;
    }

    @Override
    public int getMaxReward() {
        return 9000;
    }

    /**
     * Denotes all the different prompts that can be displayed.
     */
    enum Prompt {
        STILL("Stand still!", 0),
        LEFT("Move left!", 1),
        RIGHT("Move right!", 2),
        FORWARD("Move forward!", 3),
        BACKWARD("Move backward!", 4),
        JUMP("Jump up!", 5),
        SNEAK("Sneak!", 6),
        JUMP_SNEAK("Sneak and jump!", 7),
        PUNCH_LEFT("Punch a block!", 8),
        RIGHT_CLICK("Right click a block!", 9),
        LOOK_UP("Look up!", 10),
        LOOK_DOWN("Look down!", 11);

        private final String desc;
        private final int id;

        Prompt(String desc, int id) {
            this.desc = desc;
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public int getId() {
            return id;
        }
    }

    private class MrCloudSaysTask extends TimeCountupMinigameTask {

        /* Players who have completed the most recent command. */
        private Set<UUID> done;

        /* What Mr Cloud has said to do. */
        private Prompt command;

        /* How much time the user has to perform the command. */
        private int duration;

        /* The timeout period between commands. */
        private int timeout;

        /* How fast the time counts down. */
        private int speed;

        /* How many commands have been made. */
        private int runs;
        private int wait;

        MrCloudSaysTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
        }

        @Override
        public void onStart() {
            done = new HashSet<>();
            duration = 5;
            // On harder, the speed is twice as fast to start off with.
            timeout = difficulty.equals(Difficulty.HARDER) ? 2 : 5;
            speed = difficulty.equals(Difficulty.HARDER) ? 6 : 12;
            runs = 0;
            wait = 0;
        }

        @Override
        public void onTick() {
            if (command != null) return;
            if (wait > 0) {
                wait--;
                return;
            }
            runs++;
            if (runs % 9 == 0 && duration > 2)
                duration--;
            if (runs % 9 == 0 && timeout > 1)
                timeout--;
            if (runs % 10 == 0 && speed > 1)
                speed--;
            command();
        }

        @Override
        public void onFinish() {
            done.clear();
            done = null;
            command = null;
        }

        /**
         * Called when a player successfully completes the prompt.
         *
         * @param pl The player who completed the prompt.
         */
        private void done(Player pl) {
            if (done.contains(pl.getUniqueId())) return;
            done.add(pl.getUniqueId());
            pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1.5F, 1.5F);
        }

        /**
         * This method returns an array of 4 boolean for 4 directions.
         * [0 - left, 1 - right, 3 - forward, 4 - backward].
         * If the player is moving in a direction, it is set to true in the array.
         * <em>This takes into account the player's head rotation!</em>
         */
        private Boolean[] getMoveDirs(Location from, Location to) {
            float yaw = Math.abs(to.getYaw());
            while (yaw > 360)
                yaw -= 360;
            boolean left = false, right = false, up = false, down = false;

            if (yaw >= 135 && yaw <= 224) {
                if (from.getX() > to.getX())
                    left = true;
                else if (from.getX() < to.getX())
                    right = true;
                if (from.getZ() < to.getZ())
                    down = true;
                else if (to.getZ() < from.getZ())
                    up = true;
            } else if (yaw >= 245 && yaw <= 314) {
                if (to.getX() > from.getX())
                    up = true;
                else if (to.getX() < from.getX())
                    down = true;
                if (to.getZ() < from.getZ())
                    left = true;
                else if (to.getZ() > from.getZ())
                    right = true;
            } else if (yaw >= 314 || yaw <= 44) {
                if (to.getX() > from.getX())
                    left = true;
                else if (to.getX() < from.getX())
                    right = true;
                if (to.getZ() > from.getZ())
                    up = true;
                else if (to.getZ() < from.getZ())
                    down = true;
            } else if (yaw >= 45 && yaw <= 134) {
                if (to.getX() > from.getX())
                    down = true;
                else if (to.getX() < from.getX())
                    up = true;
                if (to.getZ() > from.getZ())
                    left = true;
                else if (to.getZ() < from.getZ())
                    right = true;
            }
            return new Boolean[]{left, right, up, down};
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (!done.contains(event.getPlayer().getUniqueId()))
                if (event.getFrom().getX() != event.getTo().getX() ||
                        event.getFrom().getZ() != event.getTo().getZ()) {
                    if (command == Prompt.STILL)
                        // As of v3.1.1, players can move their heads while standing still.
                        disqualify(event.getPlayer());
                    if (event.getFrom().getYaw() == event.getTo().getYaw()) {
                        switch (command) {
                            case FORWARD:
                                if (getMoveDirs(event.getFrom(), event.getTo())[2])
                                    done(event.getPlayer());
                                else
                                    disqualify(event.getPlayer());
                                break;
                            case BACKWARD:
                                if (getMoveDirs(event.getFrom(), event.getTo())[3])
                                    done(event.getPlayer());
                                else
                                    disqualify(event.getPlayer());
                                break;
                            case LEFT:
                                if (getMoveDirs(event.getFrom(), event.getTo())[0])
                                    done(event.getPlayer());
                                else
                                    disqualify(event.getPlayer());
                                break;
                            case RIGHT:
                                if (getMoveDirs(event.getFrom(), event.getTo())[1])
                                    done(event.getPlayer());
                                else
                                    disqualify(event.getPlayer());
                                break;
                        }
                    }
                } else if (command == Prompt.JUMP) {
                    if (event.getPlayer().getVelocity().getY() > 0)
                        done(event.getPlayer());
                } else if (command == Prompt.JUMP_SNEAK) {
                    if (event.getPlayer().getVelocity().getY() > 0)
                        if (event.getPlayer().isSneaking())
                            done(event.getPlayer());
                } else if (command == Prompt.LOOK_UP) {
                    if (event.getFrom().getPitch() > event.getTo().getPitch())
                        done(event.getPlayer());
                    else if (event.getFrom().getPitch() < event.getTo().getPitch())
                        disqualify(event.getPlayer());
                } else if (command == Prompt.LOOK_DOWN) {
                    if (event.getFrom().getPitch() < event.getTo().getPitch())
                        done(event.getPlayer());
                    else if (event.getFrom().getPitch() > event.getTo().getPitch())
                        disqualify(event.getPlayer());
                }
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (done.contains(event.getPlayer().getUniqueId())) return;
            if (command == Prompt.PUNCH_LEFT) {
                if (event.getAction().equals(Action.LEFT_CLICK_BLOCK))
                    done(event.getPlayer());
                else
                    disqualify(event.getPlayer());
            } else if (command == Prompt.RIGHT_CLICK) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    done(event.getPlayer());
                else
                    disqualify(event.getPlayer());
            }
        }

        @EventHandler
        public void onSneak(PlayerToggleSneakEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (done.contains(event.getPlayer().getUniqueId())) return;
            if (command == Prompt.SNEAK)
                done(event.getPlayer());
        }

        /**
         * Prompts another command to participants.
         */
        private void command() {
            int num = main.main().rng().nextInt(12);
            for (Prompt cmd : Prompt.values())
                if (cmd.getId() == num) {
                    command = cmd;
                    break;
                }
            new CloudTask().runTaskTimer(main.main().plugin(), 0L, speed);
        }

        /**
         * Produces a countdown of the specified duration and speed.
         * Targets must perform the specified action before the task ends.
         * Those who do not perform it in time are disqualified.
         * Those who do an incorrect action are also disqualified.
         */
        private class CloudTask extends BukkitRunnable {

            final Set<UUID> targets;
            int count;

            CloudTask() {
                count = duration;
                targets = new HashSet<>();
                for (UUID target : participants) {
                    if (disqualified.contains(target)) continue;
                    targets.add(target);
                }
            }

            public void run() {
                for (UUID target : targets) {
                    if (Bukkit.getPlayer(target) == null) continue;
                    if (done == null) break;
                    Player tar = Bukkit.getPlayer(target);
                    if (count > 0 && !disqualified.contains(target)) {
                        // Show them the prompt if they haven't completed it yet.
                        tar.playSound(tar.getLocation(), Sound.ENTITY_COW_MILK, 2F, 2F);
                        Title.showTitle(tar, ChatColor.GOLD + "Mr Cloud Says..", ChatColor.RED + command.getDesc() + ChatColor.GRAY + " (" + count + "!)", 0, 20, 0);
                    } else {
                        if (command == Prompt.STILL) {
                            if (disqualified.contains(target)) continue;
                            done.add(target);
                        }
                        if (done.contains(target)) {
                            Title.showTitle(tar, ChatColor.GOLD + "Good job!", ChatColor.RED + "Have a rest, and stand still!", 20, 60, 20);
                        } else {
                            if (!disqualified.contains(target))
                                if (Bukkit.getPlayer(target) != null)
                                    disqualify(Bukkit.getPlayer(target));
                        }
                    }
                }
                if (count <= 0) {
                    command = null;
                    wait = timeout;
                    if (done != null)
                        done.clear();
                    this.cancel();
                }
                count--;
            }
        }
    }
}
