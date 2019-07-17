package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.island.minigames.Playstyle;
import solar.rpg.skyblock.minigames.tasks.TimeCountupMinigameTask;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

//FIXME: Finish this minigame, make it competitive.
public class Volleyball extends Minigame implements NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new VolleyballTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Volleyball";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.MAGMA_CREAM);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Jump up and hit the volleyball to return it!",
                ChatColor.ITALIC + "Look out for spikes, though!",
                "\"A classic beach game!\""
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Return the volleyball!";
    }

    @Override
    public String getObjectiveWord() {
        return " rallies";
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
        return 0;
    }

    @Override
    public int getGold() {
        return 100;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getMaxReward() {
        return 0;
    }

    @Override
    public Playstyle getPlaystyle() {
        return Playstyle.COOPERATIVE;
    }

    private class VolleyballTask extends TimeCountupMinigameTask {

        /* The volleyball and its state. */
        private FallingBlock ball;
        private BallState state = BallState.WAITING_TO_THROW;

        /* Amount of misses remaining before a game over. */
        private int chances;

        /* Keeps track of combo */
        private float combo;

        /* Spots where the ball can be launched and land. */
        private List<Block> playerBlocks, compBlocks, centreBlocks;

        /* The volleyball watchdog task. */
        private BukkitTask volleyTask;

        VolleyballTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("breaking", false);
            rules.put("placing", false);
            rules.put("flying", false);
        }

        @Override
        public void onStart() {
            playerBlocks = new LinkedList<>();
            compBlocks = new LinkedList<>();
            centreBlocks = new LinkedList<>();
            chances = 5;
            combo = 0.5F;

            /* The generated location where the minigame will play. */
            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, 14, 20, 13)) {
                error();
                return;
            }

            // Base platform.
            for (int x = 0; x <= 14; x++)
                for (int z = 0; z <= 13; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    if (z == 0 || x == 0 || z == 13 || x == 14)
                        bl.setType(Material.BEDROCK);
                    else if (x == 7) {
                        bl.setType(Material.CHISELED_STONE_BRICKS);
                    } else {
                        bl.setType(Material.SMOOTH_SANDSTONE);
                    }

                    if (z != 0 && z != 13)
                        if (x > 7) {
                            // Don't serve from the front row as a computer because it just hits the net.
                            if (x != 8)
                                compBlocks.add(bl);
                        } else if (x < 7) playerBlocks.add(bl);
                        else centreBlocks.add(bl);

                    if (x == 7) {
                        Block net = bl.getRelative(BlockFace.UP, 3);
                        net.setType(Material.WHITE_STAINED_GLASS_PANE);
                        placed.add(net);

                        // Net pillars
                        if (z == 0 || z == 13) {
                            Block pillar = bl.getRelative(BlockFace.UP);
                            pillar.setType(Material.COBBLESTONE_WALL);
                            placed.add(pillar);
                            pillar = pillar.getRelative(BlockFace.UP);
                            pillar.setType(Material.COBBLESTONE_WALL);
                            placed.add(pillar);
                        }
                    }
                    placed.add(bl);
                }

            // Teleports players on to the platform.
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(3, 2, 7));

            volleyTask = new BukkitRunnable() {

                @Override
                public void run() {
                    switch (state) {
                        case WAITING_TO_THROW: {
                            Location serveSpot = compBlocks.get(main.main().rng().nextInt(compBlocks.size())).getLocation().add(0, 1, 0);
                            Location landSpot = playerBlocks.get(main.main().rng().nextInt(playerBlocks.size())).getLocation();
                            Location centreSpot = centreBlocks.get(main.main().rng().nextInt(centreBlocks.size())).getLocation();

                            ball = gen.getWorld().spawnFallingBlock(serveSpot.add(0, 1, 0), new MaterialData(Material.SLIME_BLOCK));
                            ball.setDropItem(false);

                            if (getActualResult(null) > 0 && (main.main().rng().nextInt(getActualResult(null)) > 45 ||
                                    getActualResult(null) < 45 && main.main().rng().nextInt(50) < 5)) {
                                // Spike the ball every now and then during the start.
                                // Spike the ball more often as the amount of rallies increases.
                                Vector vel = centreSpot.toVector().subtract(serveSpot.toVector()).multiply(0.0285).setY(1.525);
                                ball.setVelocity(vel);
                                state = BallState.SPIKING;
                            } else {
                                Vector vel = landSpot.toVector().subtract(serveSpot.toVector()).multiply(0.0285).setY(1.215);
                                ball.setVelocity(vel);
                                state = BallState.THROWN;
                            }
                            break;
                        }
                        case SPIKING: {
                            if (ball.getVelocity().getY() <= 0.01) {
                                gen.getWorld().playSound(ball.getLocation(), Sound.BLOCK_ANVIL_PLACE, 3F, 1.75F);
                                gen.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, ball.getLocation(), 1);
                                Location landSpot = playerBlocks.get(main.main().rng().nextInt(playerBlocks.size())).getLocation();
                                Vector vel = landSpot.toVector().subtract(ball.getLocation().toVector());
                                double y = vel.getY() * 0.0295D;
                                vel.multiply(.0295).setY(y);
                                ball.setVelocity(vel);
                                state = BallState.THROWN;
                            }
                            break;
                        }
                        case THROWN: {
                            for (UUID part : getParticipants()) {
                                Player pl = Bukkit.getPlayer(part);
                                if (pl.getVelocity().getY() > 0)
                                    if (pl.getEyeLocation().distance(ball.getLocation()) <= 2.25) {
                                        gen.getWorld().spawnParticle(Particle.SLIME, ball.getLocation(), 3);
                                        gen.getWorld().playSound(ball.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1F, 1.5F);
                                        gen.getWorld().playSound(ball.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, combo);
                                        Location landLoc = pl.getLocation().clone().add(7, 0, 0);
                                        Vector vel = landLoc.toVector().subtract(ball.getLocation().toVector()).multiply(0.0285).setY(1.255);
                                        scorePoints(pl, false, true, 1);
                                        ball.setVelocity(vel);
                                        state = BallState.RETURNING;
                                        combo += 0.1F;
                                    }
                            }
                            break;
                        }
                        case RETURNING: {
                            if (!ball.isValid())
                                state = BallState.WAITING_TO_THROW;
                            break;
                        }
                    }
                }
            }.runTaskTimer(main.main().plugin(), 100L, 2L);
        }

        /**
         * Called when the participants fail to return the ball.
         */
        private void miss() {
            chances--;
            main.soundAll(getParticipants(), Sound.ENTITY_ILLUSIONER_DEATH, 1.5F);
            state = BallState.LOST;
            combo = 0.5F;
            if (chances == 0) {
                main.messageAll(getParticipants(), ChatColor.RED + "You failed to return the ball! Game over.");
                volleyTask.cancel();
                ball.remove();
                Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 60L);
            } else {
                main.messageAll(getParticipants(), String.format(ChatColor.RED + "You failed to return the ball! You have %s chance%s remaining.", chances, chances == 1 ? "" : "s"));
                Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> state = BallState.WAITING_TO_THROW, 60L);
            }
        }

        @EventHandler
        public void onLand(EntityChangeBlockEvent event) {
            if (event.getEntity().equals(ball)) {
                event.setCancelled(true);
                switch (state) {
                    case SPIKING:
                    case RETURNING: {
                        state = BallState.WAITING_TO_THROW;
                        break;
                    }
                    case THROWN:
                        miss();
                        break;
                    case LOST:
                    case WAITING_TO_THROW:
                        throw new IllegalStateException("Ball landing in illegal state?");
                }
            }
        }

        @Override
        public void onFinish() {
            returnParticipants();
            if (ball != null)
                ball.remove();
            ball = null;
            if (volleyTask != null)
                volleyTask.cancel();
            volleyTask = null;
        }

        @Override
        public void onTick() {
        }
    }

    private enum BallState {
        WAITING_TO_THROW,
        LOST,
        THROWN,
        SPIKING,
        RETURNING
    }
}
