package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimeCountdownMinigameTask;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RFW extends Minigame implements FlawlessEnabled {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new WoolRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "RFW";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BANNER, 1, (short) 15);
    }

    public String[] getDescription() {
        return new String[]{"It's a race for the wool!",
                "feat. One Wise Ender Crystal",
                ChatColor.ITALIC + "Flags are scattered around your island!",
                ChatColor.ITALIC + "Collect them in the correct order to win!",
                ChatColor.ITALIC + "All blocks you place turn to glass."};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public String getSummary() {
        return "Capture all the flags!";
    }

    public String getObjectiveWord() {
        return "seconds remaining";
    }

    public int getDuration() {
        return 600;
    }

    public int getFlawless() {
        return 520;
    }

    public int getGold() {
        return 300;
    }

    public int getMaxReward() {
        return 7500;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    private class WoolRun extends TimeCountdownMinigameTask implements Listener {

        private EnderCrystal crystal;
        private Location[] flags;
        private int flag = 0;
        private UUID flagHolder = null;

        WoolRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
        }

        public void onStart() {
            Player random = Bukkit.getPlayer(participants.get(new Random().nextInt(participants.size())));

            crystal = (EnderCrystal) random.getWorld().spawnEntity(main.main().islands().getHomeOrSpawnpoint(owner), EntityType.ENDER_CRYSTAL);
            crystal.setShowingBottom(true);
            flags = new Location[4];

            // Generate 4 random flag block locations.
            for (int i = 0; i < (difficulty == Difficulty.NORMAL ? 3 : 5); i++) {
                Location gen = generateLocation(70, 20, crystal.getLocation().getBlockY(), true, false);
                gen.getBlock().getRelative(BlockFace.DOWN).setType(Material.BEDROCK);
                gen.getBlock().setType(Material.WOOL);
                if (i == 0)
                    gen.getBlock().setData((byte) 14);
                else if (i == 1)
                    gen.getBlock().setData((byte) 5);
                else if (i == 2)
                    gen.getBlock().setData((byte) 11);
                else if (i == 3)
                    gen.getBlock().setData((byte) 4);
                flags[i] = gen;
            }
            crystal.setBeamTarget(flags[0]);
        }

        public void onFinish() {
            if (crystal != null) {
                crystal.remove();
                crystal = null;
            }
            for (Location flag : flags) {
                flag.getBlock().setType(Material.AIR);
                flag.getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
            }
        }

        @Override
        public void disqualify(Player pl) {
            if (flagHolder != null)
                if (flagHolder.equals(pl.getUniqueId())) {
                    main.messageAll(getParticipants(), pl.getDisplayName() + ChatColor.GOLD + " lost the flag!");
                    main.soundAll(getParticipants(), Sound.ENTITY_IRONGOLEM_DEATH, 1F);
                    flagHolder = null;
                }
            super.disqualify(pl);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlace(final BlockPlaceEvent event) {
            if (!participants.contains(event.getPlayer().getUniqueId())) return;
            if (disqualified.contains(event.getPlayer().getUniqueId())) return;
            if (!owner.isInside(event.getBlockPlaced().getLocation())) return;
            placed.add(event.getBlock());
            event.getBlockPlaced().getLocation().getBlock().setType(Material.GLASS);
            main.main().listener().bypass.add(event.getPlayer().getUniqueId());
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() + 1);
            Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1), 1L);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onBreak(BlockBreakEvent event) {
            if (!participants.contains(event.getPlayer().getUniqueId())) return;
            if (disqualified.contains(event.getPlayer().getUniqueId())) return;
            if (!owner.isInside(event.getBlock().getLocation())) return;
            if (flagHolder == null)
                if (flags[flag].equals(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    flagHolder = event.getPlayer().getUniqueId();
                    main.messageAll(getParticipants(), event.getPlayer().getDisplayName() + ChatColor.GOLD + " took the flag!");
                    event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Punch the ender crystal to capture!!");
                    main.soundAll(getParticipants(), Sound.ENTITY_BLAZE_SHOOT, 2F);
                } else if (event.getBlock().getType() == Material.WOOL) {
                    event.getPlayer().sendMessage(ChatColor.RED + "This is not the correct flag!");
                    event.setCancelled(true);
                }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onDamage(EntityDamageByEntityEvent event) {
            if (!participants.contains(event.getDamager().getUniqueId())) return;
            if (disqualified.contains(event.getDamager().getUniqueId())) return;
            if (!event.getEntity().equals(crystal)) return;
            if (!(event.getDamager() instanceof Player)) return;
            event.setCancelled(true);
            if (flagHolder == null) return;
            if (flagHolder.equals(event.getDamager().getUniqueId())) {
                flagHolder = null;
                flag++;
                if (flag == 4)
                    stop();
                else {
                    main.messageAll(getParticipants(), ((Player) event.getDamager()).getDisplayName() + ChatColor.GOLD + " captured a flag! (" + flag + "/4)");
                    main.soundAll(getParticipants(), Sound.ENTITY_PLAYER_LEVELUP, 2F);
                    main.soundAll(getParticipants(), Sound.ENTITY_BLAZE_AMBIENT, 3F);
                    crystal.setBeamTarget(flags[flag]);
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onDamage(EntityDamageEvent event) {
            if (event.isCancelled()) return;
            if (!event.getEntity().equals(crystal)) return;
            event.setCancelled(true);
        }
    }
}
