package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimeCountdownMinigameTask;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WitherBusters extends Minigame {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new WitherRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Wither Busters";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
    }

    public String[] getDescription() {
        return new String[]{"Time attack, TIME ATTACK!!",
                "Become an impromptu Wither Buster!",
                ChatColor.ITALIC + "Kill the wither quickly!",
                ChatColor.ITALIC + "(fly mode is disabled)",
                ChatColor.ITALIC + ""};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public String getSummary() {
        return "Kill the wither quickly!";
    }

    public String getObjectiveWord() {
        return "seconds remaining";
    }

    public int getDuration() {
        return 180;
    }

    public int getGold() {
        return 155;
    }

    public int getMaxReward() {
        return 3000;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    private class WitherRun extends TimeCountdownMinigameTask implements Listener {

        private Wither tracked;

        WitherRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
        }

        public void onStart() {
            // Spawn a wither above a random participant.
            Player random = Bukkit.getPlayer(getParticipants().get(new Random().nextInt(participants.size())));
            tracked = (Wither) random.getWorld().spawnEntity(random.getLocation().add(0, 10, 0), EntityType.WITHER);
            if (difficulty.equals(Difficulty.HARDER))
                tracked.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 999999 * 20, 2));
        }

        public void onFinish() {
            if (tracked != null) {
                tracked.setHealth(0);
                tracked = null;
            }
        }

        @EventHandler
        public void onGrief(EntityChangeBlockEvent event) {
            if (tracked == null) return;
            if (event.getEntity().equals(tracked))
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onDamage(EntityDamageByEntityEvent event) {
            if (event.isCancelled()) return;
            if (!event.getEntity().equals(tracked)) return;
            if (event.getDamager() instanceof Projectile) {
                if (!isValidParticipant(((Player) ((Projectile) event.getDamager()).getShooter()).getUniqueId()))
                    event.setCancelled(true);
            } else if (event.getDamage() >= 250)
                event.setDamage(0);
            else if (!isValidParticipant(event.getDamager().getUniqueId()))
                event.setCancelled(true);
        }

        @EventHandler
        public void onExplode(EntityExplodeEvent event) {
            if (tracked == null) return;
            if (event.getEntity() instanceof WitherSkull)
                if (((WitherSkull) event.getEntity()).getShooter().equals(tracked))
                    event.blockList().clear();
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            if (tracked == null) onFinish();
            if (!event.getEntity().equals(tracked)) return;
            event.getDrops().clear();
            tracked = null;
            stop();
        }
    }
}
