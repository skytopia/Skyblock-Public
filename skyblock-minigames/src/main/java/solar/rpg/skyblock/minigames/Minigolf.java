package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.minigames.tasks.AttemptsMinigameTask;
import solar.rpg.skyblock.util.Utility;

import java.util.List;
import java.util.UUID;

public class Minigolf extends Minigame implements FlawlessEnabled, NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MinigolfTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Minigolf";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_EYE);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Classic minecraft 9-hole minigolf!",
                ChatColor.ITALIC + "Use any item as your ball.",
                ChatColor.ITALIC + "Make your first throw on the green.",
                "\"You know how golf works, right?\""
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Throw your ball into the hole!";
    }

    @Override
    public String getObjectiveWord() {
        return "points earnt";
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getGold() {
        return 28;
    }

    @Override
    public boolean isScoreDivisible() {
        return true;
    }

    @Override
    public int getFlawless() {
        return 30;
    }

    @Override
    public int getMaxReward() {
        return 8000;
    }

    /**
     * Denotes all the different defined courses.
     * Contains par & schematic information.
     */
    private enum HoleData {
        COURSE_1("course1.schematic", 2),
        COURSE_2("course2.schematic", 2),
        COURSE_3("course3.schematic", 3),
        COURSE_4("course4.schematic", 3),
        COURSE_5("course5.schematic", 3),
        COURSE_6("course6.schematic", 6),
        COURSE_7("course7.schematic", 4),
        COURSE_8("course8.schematic", 3),
        COURSE_9("course9.schematic", 3),
        COURSE_10("hcourse1.schematic", 2),
        COURSE_11("hcourse2.schematic", 2),
        COURSE_12("hcourse3.schematic", 3),
        COURSE_13("hcourse4.schematic", 3),
        COURSE_14("hcourse5.schematic", 4),
        COURSE_15("hcourse6.schematic", 6),
        COURSE_16("hcourse7.schematic", 5),
        COURSE_17("hcourse8.schematic", 5),
        COURSE_18("hcourse9.schematic", 1);

        final String schem;
        final int par;

        HoleData(String schem, int par) {
            this.schem = schem;
            this.par = par;
        }

        public HoleData next() {
            switch (this) {
                case COURSE_1:
                    return COURSE_2;
                case COURSE_2:
                    return COURSE_3;
                case COURSE_3:
                    return COURSE_4;
                case COURSE_4:
                    return COURSE_5;
                case COURSE_5:
                    return COURSE_6;
                case COURSE_6:
                    return COURSE_7;
                case COURSE_7:
                    return COURSE_8;
                case COURSE_8:
                    return COURSE_9;
                case COURSE_10:
                    return COURSE_11;
                case COURSE_11:
                    return COURSE_12;
                case COURSE_12:
                    return COURSE_13;
                case COURSE_13:
                    return COURSE_14;
                case COURSE_14:
                    return COURSE_15;
                case COURSE_15:
                    return COURSE_16;
                case COURSE_16:
                    return COURSE_17;
                case COURSE_17:
                    return COURSE_18;
                default:
                    return null;
            }
        }
    }

    private class MinigolfTask extends AttemptsMinigameTask {

        /* The current select player's golf ball drop. */
        private Item ball;

        /* Information on the current hole. */
        private HoleData data;

        /* Amount of attempts to get the ball into the hole. */
        private int tries;

        /* Amount of players yet to complete the hole. */
        private int playersRemaining;

        /* True if this player's throw is their first. */
        private boolean firstThrow;

        /* True when the selected player is making a throw and can't move. */
        private boolean frozen;

        MinigolfTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        @Override
        public boolean ascendingTimer() {
            return true;
        }

        public void onStart() {
            canMove = false;

            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, 9, 16, 12)) {
                error();
                return;
            }

            doNextCourse();
        }

        /**
         * Runs routine to bring players to the next course.
         */
        private void doNextCourse() {
            // Get next course.
            // Normal: Holes 1-9.
            // Harder: Holes 10-19.
            if (data == null)
                data = difficulty == Difficulty.NORMAL ? HoleData.COURSE_1 : HoleData.COURSE_10;
            else data = data.next();

            if (data == null) {
                stop();
                return;
            }

            canMove = false;
            playersRemaining = getParticipants().size() - disqualified.size();
            titleParticipants(ChatColor.GOLD + "Next course!", "");
            main.soundAll(getParticipants(), Sound.ENTITY_WOLF_GROWL, 2F);

            // Set all blocks to air before pasting schematic.
            for (Block placed : placed)
                if (placed.getType() != Material.AIR)
                    placed.setType(Material.AIR);

            // Past next hole's schematic.
            Utility.pasteSchematic(main.main(), data.schem, gen, false, false);

            // Teleport all players to the spawn location of the next hole.
            for (UUID in : getParticipants())
                if (isValidParticipant(in))
                    Bukkit.getPlayer(in).teleport(gen);

            // Send a message, then let everyone have a go!
            main.messageAll(getParticipants(), ChatColor.GOLD + "Hole " + data.toString().charAt(difficulty == Difficulty.HARDER ? 8 : 7) + ": " + ChatColor.RED + "Par is " + data.par);
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::doNextPlayer, 50L);
        }

        /**
         * When a player has gotten the ball in the hole, select the next player.
         */
        private void doNextPlayer() {
            if (playersRemaining <= 0) {
                doNextCourse();
                return;
            }
            firstThrow = true;
            frozen = false;
            tries = 0;
            selectPlayer();
            canMove = true;
        }

        @Override
        public void selectPlayer() {
            super.selectPlayer();
            playersRemaining--;
        }

        @Override
        public void disqualify(Player pl) {
            if (ball != null)
                ball.remove();

            // Have to Override TurnBasedMinigameTask disqualification.
            // If all players get disqualified, there is no reward.
            if (!isValidParticipant(pl.getUniqueId())) return;
            disqualified.add(pl.getUniqueId());
            if (disqualified != null)
                if (disqualified.size() == participants.size()) {
                    if (!ascendingTimer()) {
                        points = 0;
                        clock = 0;
                    }
                    stop();
                }

            if (getSelected() == null || getSelected().equals(pl.getUniqueId()))
                doNextPlayer();
        }

        @Override
        public void onFinish() {
            returnParticipants();
        }

        @Override
        public void onTick() {
        }

        @EventHandler
        public void onDrop(PlayerDropItemEvent event) {
            if (owner.isInside(event.getItemDrop().getLocation()))
                if (isValidParticipant(event.getPlayer().getUniqueId()) && isSelected(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    if (ball != null || !canMove) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot do that right now.");
                        return;
                    }
                    Material feet = event.getPlayer().getLocation().clone().subtract(0, 0.1, 0).getBlock().getType();

                    // First throw must be on the green stained clay.
                    if (firstThrow) {
                        if (feet != Material.GREEN_TERRACOTTA) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Stand on the green and throw!");
                            return;
                        }
                        firstThrow = false;
                    } else if (feet != Material.PACKED_ICE && feet != Material.SAND) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't jump and throw the ball!");
                        return;
                    }

                    tries++;
                    Player thrower = event.getPlayer();
                    ball = event.getItemDrop().getWorld().dropItem(event.getItemDrop().getLocation(), new ItemStack(Material.SNOWBALL));
                    ball.setVelocity(event.getItemDrop().getVelocity());
                    ball.setPickupDelay(999999999);
                    canMove = false;

                    // After throwing the ball, watch where it goes.
                    new BukkitRunnable() {
                        Location lastLoc = ball.getLocation();

                        public void run() {
                            try {
                                if (!isValidParticipant(thrower.getUniqueId()))
                                    this.cancel();
                                else if (lastLoc.getY() == ball.getLocation().getY() && lastLoc.distanceSquared(ball.getLocation()) <= 0.02) {
                                    // Wait until the ball moves at a slow enough speed to be considered stopped without taking forever.
                                    Block feet = ball.getLocation().clone().subtract(0, 0.1, 0).getBlock();
                                    while (feet.getType() == Material.AIR)
                                        feet = feet.getRelative(BlockFace.DOWN);
                                    if (feet.getType() == Material.PURPUR_BLOCK) {
                                        // Purpur block is the hole.
                                        frozen = false;
                                        calcTriesForPoints(thrower);
                                        Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> doNextPlayer(), 60L);
                                    } else if (feet.getType() == Material.PACKED_ICE || feet.getType() == Material.SAND) {
                                        // Ice and sand are regular blocks, continue throw from wherever it stops.
                                        main.soundAll(getParticipants(), Sound.ENTITY_CAT_PURREOW, 2F);
                                        titleParticipants("", ChatColor.GOLD + "Throw #" + (tries + 1) + "!");
                                        Location tele = ball.getLocation();
                                        tele.setX(tele.getBlockX() + 0.5);
                                        tele.setZ(tele.getBlockZ() + 0.5);
                                        tele.setYaw(thrower.getLocation().getYaw());
                                        tele.setPitch(thrower.getLocation().getPitch());
                                        thrower.teleport(tele);
                                        frozen = true;
                                        canMove = true;
                                    } else {
                                        // Out of bounds. Whoops.
                                        frozen = false;
                                        titleParticipants(ChatColor.GOLD + "Whoops!", ChatColor.RED + "Ball is out of bounds.");
                                        Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> doNextPlayer(), 60L);
                                    }
                                    ball.remove();
                                    ball = null;
                                    this.cancel();
                                } else lastLoc = ball.getLocation();
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(main.main().plugin(), 0L, 5L);
                } else event.setCancelled(true);
        }

        /**
         * Calculates golf terms of the score they got.
         *
         * @param scorer The scoring player.
         */
        private void calcTriesForPoints(Player scorer) {
            int points = data.par - tries + 3;
            if (points < 0) points = -1;
            String type;
            switch (points) {
                case 0:
                    type = "Triple Bogey!";
                    break;
                case 1:
                    type = "Double Bogey!";
                    break;
                case 2:
                    type = "Bogey!";
                    break;
                case 3:
                    type = "Par!";
                    break;
                case 4:
                    type = "Birdie!";
                    break;
                case 5:
                    type = "Eagle!";
                    break;
                default:
                    // Technically the player can get an albatross (-3) and the game would say "you suck"!
                    type = "Wow, you suck!";
                    break;
            }
            titleParticipants(ChatColor.GOLD + type, ChatColor.RED + "+" + points + " Points!");
            scorePoints(scorer, true, data.par - tries + 3);
        }

        @EventHandler
        public void onDrop(EntityPickupItemEvent event) {
            // As of v3.1.1, all items except the ball can be picked up.
            if (event.getItem().equals(ball))
                event.setCancelled(true);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (frozen && ball == null && isSelected(event.getPlayer().getUniqueId()))
                if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())
                    event.setCancelled(true);
        }
    }
}
