package solar.rpg.skyblock.gadgets;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.util.Utility;


/**
 * Upon impact, this arrow splits apart into 9 arrows
 * which spread out from the initial contact point.
 * Any enemies within the vicinity of one of these
 * arrows upon second contact are launched into the
 * air and violently slammed to the ground.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class CrashArrow extends ArrowGadget {

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Crash Arrow";
    }

    @Override
    public int getPrice() {
        return 5000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Splits apart on impact",
                "Launches nearby monsters up into",
                "the air, then slams them down"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.SPECTRAL_ARROW, 3);
    }

    @EventHandler
    public void onClick(ProjectileLaunchEvent event) {
        arrowCheck(event.getEntity(), () -> event.getEntity().setMetadata("crash", new FixedMetadataValue(main().plugin(), "")));
    }

    @EventHandler
    public void onProjHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        Location loc = proj.getLocation();

        arrowMetaCheck(event.getEntity(), "crash", () -> {
            // Split apart on first contact.
            proj.removeMetadata("crash", main().plugin());
            proj.remove();
            proj.getWorld().playSound(proj.getLocation(), Sound.ENTITY_SHULKER_BULLET_HIT, 2F, 2F);
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 40);
            spawnCrash(proj.getShooter(), loc, 0.25, -0.25);
            spawnCrash(proj.getShooter(), loc, 0.25, 0);
            spawnCrash(proj.getShooter(), loc, 0.25, 0.25);

            spawnCrash(proj.getShooter(), loc, -0.25, -0.25);
            spawnCrash(proj.getShooter(), loc, -0.25, 0);
            spawnCrash(proj.getShooter(), loc, -0.25, 0.25);

            spawnCrash(proj.getShooter(), loc, 0, -0.25);
            spawnCrash(proj.getShooter(), loc, 0, 0.25);
        });

        arrowMetaCheck(event.getEntity(), "crash2", () -> {
            // Launch up and slam down nearby entities on second contact.
            proj.removeMetadata("crash2", main().plugin());
            proj.remove();
            loc.getWorld().createExplosion(loc, 0F);
            loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 20);
            for (Entity nearby : proj.getNearbyEntities(2.5, 2.5, 2.5))
                if (Utility.isHostile(nearby) && nearby.getVelocity().getY() < 0.15) {
                    nearby.setVelocity(new Vector(Math.random(), 2, Math.random()));
                    nearby.getWorld().spawnParticle(Particle.PORTAL, nearby.getLocation(), 8);
                    Bukkit.getScheduler().runTaskLater(main().plugin(), () -> {
                        if (!nearby.isDead() && nearby.getVelocity().getY() >= 0) {
                            if (main().rng().nextInt(100) == 69)
                                // Strike lightning 1 in 100 times.
                                nearby.getWorld().strikeLightning(nearby.getLocation());
                            else {
                                nearby.getWorld().createExplosion(nearby.getLocation(), 0F);
                                nearby.getWorld().spawnParticle(Particle.LAVA, nearby.getLocation(), 3);
                                nearby.setVelocity(nearby.getVelocity().subtract(new Vector(0, 2.25, 0)));
                            }
                        }
                    }, 18L);
                }
        });
    }

    /**
     * Spawns a "first contact" arrow.
     *
     * @param source The original shooter.
     * @param loc    The location to spawn the arrow.
     * @param x1     X velocity.
     * @param z1     Z velocity.
     */
    private void spawnCrash(ProjectileSource source, Location loc, double x1, double z1) {
        SpectralArrow arrow = loc.getWorld().spawn(loc, SpectralArrow.class);
        arrow.setShooter(source);
        arrow.setMetadata("crash2", new FixedMetadataValue(main().plugin(), ""));
        arrow.setVelocity(new Vector(x1, 0.85, z1));
    }
}
