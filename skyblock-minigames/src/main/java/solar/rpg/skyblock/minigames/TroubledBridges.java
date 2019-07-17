package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.Playstyle;
import solar.rpg.skyblock.minigames.tasks.TimeCountdownMinigameTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TroubledBridges extends Minigame {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TroubledBridgesTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Troubled Bridges";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BEETROOT_SEEDS);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Make it back to the center of your island!",
                ChatColor.ITALIC + "Follow the crystal's beam to get home!",
                ChatColor.ITALIC + "All blocks turn to glass or stone brick.",
                ChatColor.ITALIC + "Placed blocks will fall every 30 seconds!",
                ChatColor.UNDERLINE + "Stone bricks are safe spots to stand."
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return Difficulty.values();
    }

    @Override
    public String getSummary() {
        return "Make it to your island spawnpoint!";
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
        return 245;
    }

    @Override
    public int getMaxReward() {
        return 4000;
    }

    @Override
    public Playstyle getPlaystyle() {
        return Playstyle.COMPETITIVE;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    private class TroubledBridgesTask extends TimeCountdownMinigameTask {

        /* The end crystal pointing towards home. */
        private EnderCrystal crystal;

        /* Task that destroys glass every 30 seconds. */
        private BukkitTask fall;

        TroubledBridgesTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
            rules.put("commands", false);
            rules.put("breaking", false);
        }

        @Override
        protected boolean isNoScoreIfOutOfTime() {
            // Finished users should keep their score, and unfinished users will always have a score of 0 if time is up.
            return false;
        }

        @Override
        public void onStart() {
            crystal = (EnderCrystal) Bukkit.getPlayer(participants.get(0)).getWorld().spawnEntity(generateLocation(20, 150, -1, true, false), EntityType.ENDER_CRYSTAL);
            crystal.setShowingBottom(true);
            crystal.setBeamTarget(main.main().islands().getHomeOrSpawnpoint(owner));

            genCyl(crystal.getLocation().clone().subtract(0, 1, 0), Material.BEDROCK);

            // Teleport players to the platform.
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(crystal.getLocation().clone().add(0, 1, 0));

            // Make placed glass fall every 30 seconds.
            fall = Bukkit.getScheduler().runTaskTimer(main.main().plugin(), () -> {
                ((ArrayList<Block>) placed.clone()).stream().filter(next -> next.getType() != Material.STONE_BRICKS).forEachOrdered(next -> {
                    placed.remove(next);
                    next.setType(Material.AIR);
                    next.getWorld().spawnFallingBlock(next.getLocation().add(0.5, 0.5, 0.5), Material.GLASS, (byte) 0);
                });
                main.soundAll(getParticipants(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2F);
                main.soundAll(getParticipants(), Sound.ENTITY_VEX_DEATH, 2F);
            }, 600L, 600L);
        }

        @Override
        public void onFinish() {
            genCyl(crystal.getLocation().clone().subtract(0, 1, 0), Material.AIR);
            if (crystal != null) {
                crystal.remove();
                crystal = null;
            }
            if (fall != null)
                fall.cancel();
            fall = null;
        }

        /**
         * Generates a cylinder with a radius of 4.
         *
         * @param loc The location to generate the cylinder at.
         * @param mat The material to construct it with.
         */
        private void genCyl(Location loc, Material mat) {
            int radiusSquared = 4 * 4;
            for (int x = -4; x <= 4; x++)
                for (int z = -4; z <= 4; z++)
                    if ((x * x) + (z * z) <= radiusSquared) loc.clone().add(x, 0, z).getBlock().setType(mat);
        }

        @EventHandler
        public void onTo(EntityChangeBlockEvent event) {
            if (event.getEntity() instanceof FallingBlock)
                if (owner.isInside(event.getEntity().getLocation()))
                    if (event.getTo() == Material.GLASS)
                        event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlace(BlockPlaceEvent event) {
            if (!participants.contains(event.getPlayer().getUniqueId())) return;
            if (disqualified.contains(event.getPlayer().getUniqueId())) return;
            if (!owner.isInside(event.getBlockPlaced().getLocation())) return;
            ItemStack itemUsed = getItemInHand(event.getPlayer(), event.getHand());
            if (main.main().rng().nextInt(10) == 7) {
                if (difficulty == Difficulty.HARDER && main.main().rng().nextBoolean())
                    event.getBlockPlaced().getLocation().getBlock().setType(Material.GLASS);
                else
                    event.getBlockPlaced().getLocation().getBlock().setType(Material.STONE_BRICKS);
            } else
                event.getBlockPlaced().getLocation().getBlock().setType(Material.GLASS);
            placed.add(event.getBlock());
            main.main().listener().bypass.add(event.getPlayer().getUniqueId());
            itemUsed.setAmount(2);
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                itemUsed.setAmount(1);
                event.getPlayer().updateInventory();
            }, 1L);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onDamage(EntityDamageEvent event) {
            if (event.isCancelled()) return;
            if (event.getEntity().equals(crystal))
                event.setCancelled(true);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (!finished.contains(event.getPlayer().getUniqueId())) {
                if (main.main().islands().getHomeOrSpawnpoint(owner).distanceSquared(event.getTo()) >= 225) return;

                finished.add(event.getPlayer().getUniqueId());
                titleParticipants(ChatColor.GOLD + "Finished!", event.getPlayer().getDisplayName() + ChatColor.RED + " made it back safe!");
                main.soundAll(getParticipants(), Sound.ENTITY_PLAYER_LEVELUP, 2F);
                main.soundAll(getParticipants(), Sound.ENTITY_BLAZE_AMBIENT, 3F);

                // Stop minigame once all participants have either finished or been disqualified.
                if (finished.size() + disqualified.size() == participants.size())
                    Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 60L);
            }
        }
    }
}
