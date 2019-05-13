package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.minigames.tasks.DefaultMinigameTask;
import solar.rpg.skyblock.stored.Settings;

import java.util.List;
import java.util.UUID;

public class TheAbominable extends Minigame implements NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TheAbominableTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "The Abominable";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.SNOW_BALL, 1);
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Yeah, we're serious right now.",
                "He'll always come back to say hi!",
                ChatColor.ITALIC + "Track down and kill as many",
                ChatColor.ITALIC + "snowmen on your island as",
                ChatColor.ITALIC + "you can within 3 minutes!"};
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Kill the hidden snowman!";
    }

    @Override
    public String getObjectiveWord() {
        return "snowmen killed";
    }

    @Override
    public int getDuration() {
        return 180;
    }

    @Override
    public int getGold() {
        return 30;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getMaxReward() {
        return 9000;
    }

    private class TheAbominableTask extends DefaultMinigameTask {

        /* Currently alive and tracked snowman. */
        private Snowman tracked;

        /* Previously alive snowman, tracked as it despawns. */
        private Snowman dead;

        TheAbominableTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
        }

        @EventHandler
        public void onForm(EntityBlockFormEvent event) {
            if (tracked == null && dead == null) return;
            if (event.getEntity().equals(tracked) || event.getEntity().equals(dead))
                event.setCancelled(true);
        }

        @EventHandler
        public void onDamage(EntityDamageEvent event) {
            if (tracked == null) return;
            if (!event.getEntity().equals(tracked)) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.MELTING)
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onDamage(EntityDamageByEntityEvent event) {
            if (!event.getEntity().equals(tracked)) return;
            if (!(event.getDamager() instanceof Player)) return;
            if (!isValidParticipant(event.getDamager().getUniqueId())) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            if (!owner.isInside(event.getDamager().getLocation())) return;
            if (((LivingEntity) event.getEntity()).getHealth() <= event.getDamage()) {
                ((LivingEntity) event.getEntity()).setHealth(0);
                titleParticipants("", ((Player) event.getDamager()).getDisplayName() + ChatColor.RED + " killed the snowman!");
                scorePoints((Player) event.getDamager(), true, 1);
                new BukkitRunnable() {
                    public void run() {
                        dead = null;
                    }
                }.runTaskLater(main.main().plugin(), 30L);
            } else {
                main.main().messages().sendSpamMessage(event.getDamager(), ChatColor.RED + "Kill me in one hit, sissy!");
            }
        }

        @EventHandler
        public void onDeath(final EntityDeathEvent event) {
            if (tracked == null) return;
            if (!event.getEntity().equals(tracked)) return;
            event.getDrops().clear();
            dead = tracked;
            tracked = null;
            if (event.getEntity().getKiller() == null)
                titleParticipants("", ChatColor.GRAY + "The snowman died!");
            scorePoints(event.getEntity().getKiller(), true, 1);
            new BukkitRunnable() {
                public void run() {
                    dead = null;
                }
            }.runTaskLater(main.main().plugin(), 30L);
        }

        @Override
        public void onTick() {
            if (tracked != null) return;
            Player random = Bukkit.getPlayer(getParticipants().get(main.main().rng().nextInt(getParticipants().size())));
            // Respawn the snowman once it's dead.
            tracked = (Snowman) Bukkit.getWorld(Settings.ADMIN_WORLD_ID).spawnEntity(generateLocation(30, 10, random.getLocation().getBlockY(), true, true), EntityType.SNOWMAN);
        }

        @Override
        public void onFinish() {
            // Kill the snowman and don't track anything else.
            if (tracked != null)
                tracked.setHealth(0);
            tracked = null;
            dead = null;
        }
    }
}
