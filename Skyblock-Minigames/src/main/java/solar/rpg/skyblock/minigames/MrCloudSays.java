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
import solar.rpg.skyblock.cb.Title;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimeCountupMinigameTask;

import java.util.*;

public class MrCloudSays extends Minigame implements NewbieFriendly {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new CloudRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Mr. Cloud Says";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.RECORD_9);
    }

    public String[] getDescription() {
        return new String[]{"Do you like being bossed around?",
                "You don't? How unfortunate..",
                ChatColor.ITALIC + "Follow Mr. Cloud's commands!",
                ChatColor.ITALIC + "(hint: stand in an open area!)"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.SIMPLE, Difficulty.NORMAL, Difficulty.HARDER};
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public String getSummary() {
        return "Follow Mr Cloud's commands!";
    }

    public String getObjectiveWord() {
        return "time elapsed";
    }

    public int getDuration() {
        return 0;
    }

    public int getGold() {
        return 215;
    }

    public int getMaxReward() {
        return 9000;
    }

    enum Cloud {
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

        Cloud(String desc, int id) {
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

    private class CloudRun extends TimeCountupMinigameTask {

        private ArrayList<UUID> done;
        private Cloud command;

        private int duration;
        private int timeout;
        private int speed;
        private int runs;
        private int wait;

        CloudRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
        }

        public void onStart() {
            done = new ArrayList<>();
            duration = 5;
            timeout = 5;
            speed = 12;
            runs = 0;
            wait = 0;
        }

        public void onTick() {
            if (command != null) return;
            if (wait > 0) {
                wait--;
                return;
            }
            runs++;
            if (runs % 9 == 0 && duration > 1)
                duration--;
            if (runs % 9 == 0 && timeout > 1)
                timeout--;
            if (runs % 10 == 0 && speed > 1)
                speed--;
            command();
        }

        public void onFinish() {
            done.clear();
            done = null;
            command = null;
        }

        void done(Player pl) {
            if (done.contains(pl.getUniqueId())) return;
            done.add(pl.getUniqueId());
            pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1.5F, 1.5F);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (done.contains(event.getPlayer().getUniqueId())) return;
            if (command == Cloud.STILL)
                disqualify(event.getPlayer());
            else if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getZ() != event.getTo().getZ()) {
                if (event.getFrom().getYaw() == event.getTo().getYaw()) {
                    if (command == Cloud.FORWARD) {
                        if (getMoveResult(event.getFrom(), event.getTo())[2])
                            done(event.getPlayer());
                        else
                            disqualify(event.getPlayer());
                    } else if (command == Cloud.BACKWARD) {
                        if (getMoveResult(event.getFrom(), event.getTo())[3])
                            done(event.getPlayer());
                        else
                            disqualify(event.getPlayer());
                    } else if (command == Cloud.LEFT) {
                        if (getMoveResult(event.getFrom(), event.getTo())[0])
                            done(event.getPlayer());
                        else
                            disqualify(event.getPlayer());
                    } else if (command == Cloud.RIGHT) {
                        if (getMoveResult(event.getFrom(), event.getTo())[1])
                            done(event.getPlayer());
                        else
                            disqualify(event.getPlayer());
                    }
                }
            } else if (command == Cloud.JUMP) {
                if (event.getPlayer().getVelocity().getY() > 0)
                    done(event.getPlayer());
            } else if (command == Cloud.JUMP_SNEAK) {
                if (event.getPlayer().getVelocity().getY() > 0)
                    if (event.getPlayer().isSneaking())
                        done(event.getPlayer());
            } else if (command == Cloud.LOOK_UP) {
                if (event.getFrom().getPitch() > event.getTo().getPitch())
                    done(event.getPlayer());
                else if (event.getFrom().getPitch() < event.getTo().getPitch())
                    disqualify(event.getPlayer());
            } else if (command == Cloud.LOOK_DOWN) {
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
            if (command == Cloud.PUNCH_LEFT) {
                if (event.getAction().equals(Action.LEFT_CLICK_BLOCK))
                    done(event.getPlayer());
                else
                    disqualify(event.getPlayer());
            } else if (command == Cloud.RIGHT_CLICK) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    done(event.getPlayer());
                else
                    disqualify(event.getPlayer());
            }
        }

        /**
         * Returns what directions the player moved in. (left,right,up,down)
         */
        Boolean[] getMoveResult(Location from, Location to) {
            Float yaw = Math.abs(to.getYaw());
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
        public void onSneak(PlayerToggleSneakEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (done.contains(event.getPlayer().getUniqueId())) return;
            if (command == Cloud.SNEAK)
                done(event.getPlayer());
        }

        public void command() {
            int num = main.main().rng().nextInt(12);
            for (Cloud cmd : Cloud.values())
                if (cmd.getId() == num) {
                    command = cmd;
                    break;
                }
            new CloudTask().runTaskTimer(main.main().plugin(), 0L, speed);
        }

        class CloudTask extends BukkitRunnable {

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
                        tar.playSound(tar.getLocation(), Sound.ENTITY_COW_MILK, 2F, 2F);
                        Title.showTitle(tar, ChatColor.GOLD + "Mr Cloud Says..", ChatColor.RED + command.getDesc() + ChatColor.GRAY + " (" + count + "!)", 0, 20, 0);
                    } else {
                        if (command == Cloud.STILL) {
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
