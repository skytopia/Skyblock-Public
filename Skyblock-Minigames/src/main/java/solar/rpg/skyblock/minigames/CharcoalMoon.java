package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimeCountdownMinigameTask;
import solar.rpg.skyblock.stored.Settings;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CharcoalMoon extends Minigame {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new WitherRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Charcoal Moon";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LAVA_BUCKET);
    }

    public String[] getDescription() {
        return new String[]{"You're not gonna have a good time..",
                ChatColor.ITALIC + "Wither Skeletons are invading!",
                ChatColor.ITALIC + "Kill 50 within the time limit to win!"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public String getSummary() {
        return "Kill 50 Wither Skeletons!";
    }

    public String getObjectiveWord() {
        return "seconds remaining";
    }

    public int getDuration() {
        return 300;
    }

    public int getMaxReward() {
        return 7500;
    }

    public int getGold() {
        return 220;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    private class WitherRun extends TimeCountdownMinigameTask implements Listener {

        private Set<UUID> skels;

        WitherRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
        }

        public void onStart() {
            skels = new LinkedHashSet<>();
            for (UUID part : getParticipants())
                if (Bukkit.getPlayer(part) != null)
                    Bukkit.getPlayer(part).setPlayerTime(19000, false);

        }

        public void onTick() {
            // Only respawn skeletons 50% of the time every second.
            if (main.main().rng().nextBoolean()) return;
            if (getParticipants() == null) return;
            if (skels.size() > 15 * getParticipants().size()) return;
            for (UUID part : getParticipants()) {
                Player random = Bukkit.getPlayer(part);
                if (random == null) continue;
                WitherSkeleton skel = (WitherSkeleton) Bukkit.getWorld(Settings.ADMIN_WORLD_ID).spawnEntity(generateLocation(12, 5, random.getLocation().getBlockY(), false, true), EntityType.WITHER_SKELETON);
                if (main.main().rng().nextBoolean())
                    skel.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
                skel.setTarget(random);
                skels.add(skel.getUniqueId());
            }
        }

        public void onFinish() {
            // Clear and remove skeleton list.
            skels.clear();
            skels = null;

            // Kill all wither skeletons on the island.
            for (WitherSkeleton skel : Bukkit.getWorld(Settings.ADMIN_WORLD_ID).getEntitiesByClass(WitherSkeleton.class))
                if (owner.isInside(skel.getLocation()))
                    skel.setHealth(0);

            // Set hidden night time back to default.
            for (UUID part : getParticipants())
                if (Bukkit.getPlayer(part) != null)
                    Bukkit.getPlayer(part).resetPlayerTime();
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            if (event.getEntity() instanceof Skeleton) {
                if (skels != null)
                    if (skels.contains(event.getEntity().getUniqueId())) {
                        event.getDrops().clear();
                        skels.remove(event.getEntity().getUniqueId());
                        points++;
                        main.soundAll(getParticipants(), Sound.BLOCK_LEVER_CLICK, 2F);
                        if (points == 50)
                            stop();
                        else if (points % 10 == 0)
                            titleParticipants(ChatColor.GOLD + "Progress!", ChatColor.RED + "" + points + "/50 skeletons killed!");
                    }
            }
        }

        @EventHandler
        public void onTarget(EntityTargetLivingEntityEvent event) {
            if (event.getEntity() instanceof WitherSkeleton)
                if (skels.contains(event.getEntity().getUniqueId()))
                    if (!isValidParticipant(event.getTarget().getUniqueId()))
                        event.setCancelled(true);
        }
    }
}
