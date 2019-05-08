package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimeCountdownMinigameTask;

import java.util.*;

public class TroubledBridges extends Minigame {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TroubledRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Troubled Bridges";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BEETROOT_SEEDS);
    }

    public String[] getDescription() {
        return new String[]{"Where the hell are we..?",
                ChatColor.ITALIC + "Make it back to the center of your island!",
                ChatColor.ITALIC + "Follow the crystal's beam to get home!",
                ChatColor.ITALIC + "Stone bricks are safe spots to stand."};
    }

    public Difficulty[] getDifficulties() {
        return Difficulty.values();
    }

    public String getSummary() {
        return "Make it to your island spawnpoint!";
    }

    public String getObjectiveWord() {
        return "seconds remaining";
    }

    public int getDuration() {
        return 420;
    }

    public int getGold() {
        return 245;
    }

    public int getMaxReward() {
        return 6000;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    private class TroubledRun extends TimeCountdownMinigameTask implements Listener {

        private EnderCrystal crystal1;
        private Set<UUID> safe;
        private BukkitTask fall;

        TroubledRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
            rules.put("modules", false);
            rules.put("breaking", false);
        }

        public void onStart() {
            safe = new HashSet<>();

            crystal1 = (EnderCrystal) Bukkit.getPlayer(participants.get(0)).getWorld().spawnEntity(generateLocation(20, 150, -1, true, false), EntityType.ENDER_CRYSTAL);
            crystal1.setShowingBottom(true);
            crystal1.setBeamTarget(main.main().islands().getHomeOrSpawnpoint(owner));
            genCyl(crystal1.getLocation().clone().subtract(0, 1, 0), Material.BEDROCK);

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(crystal1.getLocation().clone().add(0, 1, 0));

            fall = Bukkit.getScheduler().runTaskTimer(main.main().plugin(),
                    () -> {
                        //noinspection unchecked
                        for (Block next : (ArrayList<Block>) placed.clone()) {
                            if (next.getType() != Material.SMOOTH_BRICK) {
                                placed.remove(next);
                                next.setType(Material.AIR);
                                next.getWorld().spawnFallingBlock(next.getLocation().add(0.5, 0.5, 0.5), Material.GLASS, (byte) 0);
                            }
                        }
                        main.soundAll(getParticipants(), Sound.ENTITY_ENDERDRAGON_GROWL, 2F);
                        main.soundAll(getParticipants(), Sound.ENTITY_VEX_DEATH, 2F);
                    }, 600L, 600L);
        }

        public void onFinish() {
            safe.clear();
            safe = null;
            genCyl(crystal1.getLocation().clone().subtract(0, 1, 0), Material.AIR);
            if (crystal1 != null) {
                crystal1.remove();
                crystal1 = null;
            }
            if (fall != null)
                fall.cancel();
            fall = null;
        }

        void genCyl(Location loc, Material mat) {
            int radiusSquared = 4 * 4;
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if ((x * x) + (z * z) <= radiusSquared) {
                        loc.clone().add(x, 0, z).getBlock().setType(mat);
                    }
                }
            }
        }

        @EventHandler
        public void onTo(EntityChangeBlockEvent event) {
            if (event.getEntity() instanceof FallingBlock)
                if (owner.isInside(event.getEntity().getLocation()))
                    if (event.getTo() == Material.GLASS)
                        event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlace(final BlockPlaceEvent event) {
            if (!participants.contains(event.getPlayer().getUniqueId())) return;
            if (disqualified.contains(event.getPlayer().getUniqueId())) return;
            if (!owner.isInside(event.getBlockPlaced().getLocation())) return;
            if (main.main().rng().nextInt(10) == 7) {
                if (difficulty == Difficulty.HARDER && main.main().rng().nextBoolean())
                    event.getBlockPlaced().getLocation().getBlock().setType(Material.GLASS);
                else
                    event.getBlockPlaced().getLocation().getBlock().setType(Material.SMOOTH_BRICK);
            } else
                event.getBlockPlaced().getLocation().getBlock().setType(Material.GLASS);
            placed.add(event.getBlock());
            main.main().listener().bypass.add(event.getPlayer().getUniqueId());
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() + 1);
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1), 1L);
        }

        @Override
        public void disqualify(Player pl) {
            if (disqualified.size() == getParticipants().size()) return;
            checkWin();
            super.disqualify(pl);
        }

        void checkWin() {
            if (safe.size() >= getParticipants().size() - disqualified.size())
                stop();
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onDamage(EntityDamageEvent event) {
            if (event.isCancelled()) return;
            if (event.getEntity().equals(crystal1))
                event.setCancelled(true);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (!participants.contains(event.getPlayer().getUniqueId())) return;
            if (disqualified.contains(event.getPlayer().getUniqueId())) return;
            if (!safe.contains(event.getPlayer().getUniqueId())) {
                if (main.main().islands().getHomeOrSpawnpoint(owner).distanceSquared(event.getTo()) >= 225)
                    return;
                safe.add(event.getPlayer().getUniqueId());
                main.messageAll(getParticipants(), event.getPlayer().getDisplayName() + ChatColor.GOLD + " made it back safe! (" + safe.size() + "/" + (getParticipants().size() - disqualified.size()) + ")");
                main.soundAll(getParticipants(), Sound.ENTITY_PLAYER_LEVELUP, 2F);
                main.soundAll(getParticipants(), Sound.ENTITY_BLAZE_AMBIENT, 3F);
            }
            checkWin();
        }
    }
}
