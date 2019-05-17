package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.minigames.tasks.TimeCountdownMinigameTask;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CaptureTheWool extends Minigame implements FlawlessEnabled {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new CaptureTheWoolTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Capture The Wool";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BANNER, 1, (short) 15);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Flags are scattered around your island!",
                ChatColor.ITALIC + "Collect them in the correct order to win!",
                ChatColor.ITALIC + "All blocks you place turn to glass."
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Capture all the flags!";
    }

    @Override
    public String getObjectiveWord() {
        return "seconds remaining";
    }

    @Override
    public int getDuration() {
        return 600;
    }

    @Override
    public int getFlawless() {
        return 520;
    }

    @Override
    public int getGold() {
        return 300;
    }

    @Override
    public int getMaxReward() {
        return 7500;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    private class CaptureTheWoolTask extends TimeCountdownMinigameTask {

        /*
         * Sets the appropriate number of flags based on difficulty.
         * Normal: 4 flags to capture.
         * Harder: 6 flags to capture.
         */
        private final int noOfFlags = (difficulty.equals(Difficulty.HARDER) ? 6 : 4);
        /* The ender crystal entity. */
        private EnderCrystal crystal;
        /* Location of the wool flags. */
        private Location[] flags;
        /* The current flag to be captured.*/
        private int flag = 0;
        /* The holder of the current flag. */
        private UUID flagHolder = null;

        CaptureTheWoolTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            rules.put("gliding", false);
        }

        @Override
        public void onStart() {
            Player random = Bukkit.getPlayer(participants.get(new Random().nextInt(participants.size())));

            crystal = (EnderCrystal) random.getWorld().spawnEntity(main.main().islands().getHomeOrSpawnpoint(owner), EntityType.ENDER_CRYSTAL);
            crystal.setShowingBottom(true);
            flags = new Location[noOfFlags];

            // Randomly generated defined flag locations.
            // Normal: 4 flags to capture.
            // Harder: 6 flags to capture.
            for (int i = 0; i < noOfFlags; i++) {
                Location gen = generateLocation(70, 20, crystal.getLocation().getBlockY(), true, false);
                gen.getBlock().getRelative(BlockFace.DOWN).setType(Material.BEDROCK);
                gen.getBlock().setType(Material.WOOL);
                switch (i) {
                    case 0:
                        gen.getBlock().setData((byte) 14);
                        break;
                    case 1:
                        gen.getBlock().setData((byte) 5);
                        break;
                    case 2:
                        gen.getBlock().setData((byte) 11);
                        break;
                    case 3:
                        gen.getBlock().setData((byte) 4);
                        break;
                    case 4:
                        gen.getBlock().setData((byte) 1);
                        break;
                    default:
                        gen.getBlock().setData((byte) 7);
                        break;
                }
                flags[i] = gen;
            }
            crystal.setBeamTarget(flags[0]);
        }

        @Override
        public void onTick() {
            if (crystal.isDead()) {
                main.messageAll(participants, ChatColor.RED + "The ender crystal somehow exploded.. Whoops!");
                stop();
            }
        }

        @Override
        public void onFinish() {
            if (crystal != null) {
                crystal.remove();
                crystal = null;
            }

            // Remove flag blocks.
            for (Location flag : flags) {
                flag.getBlock().setType(Material.AIR);
                flag.getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
            }
        }

        @Override
        public void disqualify(Player pl) {
            // Disqualified players will drop the flag if they are holding it.
            if (flagHolder != null)
                if (flagHolder.equals(pl.getUniqueId())) {
                    main.messageAll(getParticipants(), pl.getDisplayName() + ChatColor.GOLD + " lost the flag!");
                    main.soundAll(getParticipants(), Sound.ENTITY_IRONGOLEM_DEATH, 1F);
                    flagHolder = null;
                }
            super.disqualify(pl);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlace(BlockPlaceEvent event) {
            if (!participants.contains(event.getPlayer().getUniqueId())) return;
            if (disqualified.contains(event.getPlayer().getUniqueId())) return;
            if (!owner.isInside(event.getBlockPlaced().getLocation())) return;
            // Turn all placed blocks into glass. Refund the block used to place the glass.
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
            if (flagHolder == null) {
                if (flags[flag].equals(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    flagHolder = event.getPlayer().getUniqueId();
                    main.messageAll(getParticipants(), event.getPlayer().getDisplayName() + ChatColor.GOLD + " took the flag!");
                    event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Punch the ender crystal to capture!!");
                    main.soundAll(getParticipants(), Sound.ENTITY_BLAZE_SHOOT, 2F);
                }
            } else if (event.getBlock().getType() == Material.WOOL) {
                // Only the current flag can be taken.
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
                if (flag == noOfFlags)
                    stop();
                else {
                    main.messageAll(getParticipants(), ((Player) event.getDamager()).getDisplayName() + ChatColor.GOLD + " captured a flag! (" + flag + "/" + noOfFlags + ")");
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
            // Prevent crystal from exploding.
            event.setCancelled(true);
        }
    }
}
