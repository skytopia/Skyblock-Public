package solar.rpg.skyblock.gadgets;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.Main;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.util.Title;

/**
 * On contact with an entity, it raises it into the air.
 * After a few moments, the entity is killed instantly.
 * Multiple lightning bolts are struck and a gold block appears.
 * If the arrow does not connect, the player is attacked instead.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class ThorArrow extends ArrowGadget {

    @Override
    public String getName() {
        return ChatColor.GOLD + "" + ChatColor.UNDERLINE + ChatColor.ITALIC + ChatColor.BOLD + "Thor Arrow";
    }

    @Override
    public int getPrice() {
        return 25000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{"Point and shoot at whatever you want gone", "Has huge miss recoil"};
    }

    @Override
    public ItemStack getIcon() {
        return ItemUtility.changeSize(ItemUtility.createTippedArrow(PotionEffectType.GLOWING, 20 * 120, 0), 2);
    }

    @EventHandler
    public void onClick(ProjectileLaunchEvent event) {
        arrowCheck(event.getEntity(), () -> {
            Projectile proj = event.getEntity();
            proj.setVelocity(proj.getVelocity().multiply(1.5));
            proj.setMetadata("thor", new FixedMetadataValue(main().plugin(), ""));
        });
    }

    @EventHandler
    public void onProjHit(ProjectileHitEvent event) {
        arrowMetaCheck(event.getEntity(), "thor", () -> {
            Projectile proj = event.getEntity();
            Player shooter = (Player) event.getEntity().getShooter();
            Location loc = proj.getLocation();
            Bukkit.getScheduler().runTaskLater(main().plugin(), () -> {
                if (proj.hasMetadata("thor")) {
                    proj.removeMetadata("thor", main().plugin());
                    proj.remove();
                    Title.showTitle(shooter, "", ChatColor.DARK_RED + "** MISS **", 5, 20, 5);
                    shooter.getWorld().strikeLightningEffect(shooter.getLocation());
                    shooter.setFireTicks(100);
                    loc.getWorld().spawnParticle(Particle.CRIT, loc, 30);
                }
            }, 1L);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.getEntity() instanceof Player) return;

        arrowMetaCheck(event.getDamager(), "thor", () -> {
            Projectile proj = (Projectile) event.getDamager();
            Player shooter = (Player) ((Projectile) event.getDamager()).getShooter();
            proj.removeMetadata("thor", main().plugin());
            proj.remove();
            Title.showTitle(shooter, "", ChatColor.GOLD + "** RELINQUISHED **", 5, 20, 5);
            event.getEntity().setVelocity(new Vector(0, 1, 0));
            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 20, 0));

            // Kill enemy 3 seconds later.
            Bukkit.getScheduler().runTaskLater(main().plugin(), new Runnable() {
                @Override
                public void run() {
                    if (event.getEntity().isDead()) return;
                    Location loc = event.getEntity().getLocation();
                    ((LivingEntity) event.getEntity()).setHealth(0);
                    event.getEntity().getWorld().spawnFallingBlock(loc, new MaterialData(Material.GOLD_BLOCK)).setVelocity(new Vector(0, 0.25, 0));
                    new BukkitRunnable() {
                        int count = 16;

                        @Override
                        public void run() {
                            count--;
                            loc.getWorld().strikeLightning(loc);
                            loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 5);
                            if (count == 0) this.cancel();
                        }
                    }.runTaskTimer(main().plugin(), 0L, 1L);
                }
            }, 60L);
        });
    }
}
