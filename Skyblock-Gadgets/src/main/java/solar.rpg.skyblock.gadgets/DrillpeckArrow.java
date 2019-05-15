package solar.rpg.skyblock.gadgets;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.util.Title;

/**
 * On contact with a monster, this arrow makes them erupt in ores.
 * Damage is dealt after the "drilling" is done.
 * If the arrow does not connect, the player loses half their health.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class DrillpeckArrow extends ArrowGadget {

    @Override
    public String getName() {
        return ChatColor.AQUA + "" + ChatColor.BOLD + "Drillpeck Arrow";
    }

    @Override
    public int getPrice() {
        return 11500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Violently drills into monsters",
                "Extracts ores from your victim",
                ChatColor.RED + "Halves your health if you miss"
        };
    }

    @Override
    public ItemStack getIcon() {
        return ItemUtility.changeSize(ItemUtility.createTippedArrow(PotionEffectType.SPEED, 20 * 120, 0), 16);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        arrowCheck(event.getEntity(), () -> {
            Projectile proj = event.getEntity();
            proj.setVelocity(proj.getVelocity().multiply(1.5));
            proj.setMetadata("drill", new FixedMetadataValue(main().plugin(), ""));
        });
    }

    @EventHandler
    public void onProjHit(ProjectileHitEvent event) {
        arrowMetaCheck(event.getEntity(), "drill", () -> {
            Projectile proj = event.getEntity();
            Player shooter = (Player) event.getEntity().getShooter();
            Location loc = proj.getLocation();
            Bukkit.getScheduler().runTaskLater(main().plugin(), () -> {
                if (proj.hasMetadata("drill")) {
                    proj.removeMetadata("drill", main().plugin());
                    proj.remove();
                    Title.showTitle(shooter, "", ChatColor.DARK_RED + "** MISS **", 5, 20, 5);
                    shooter.damage(0.1);
                    shooter.setHealth(shooter.getHealth() / 2);
                    loc.getWorld().spawnParticle(Particle.CRIT, loc, 30);
                }
            }, 1L);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Monster)) return;
        arrowMetaCheck(event.getDamager(), "drill", () -> {
            Projectile proj = (Projectile) event.getDamager();
            Player shooter = (Player) proj.getShooter();
            proj.removeMetadata("drill", main().plugin());
            proj.remove();
            double damage = event.getDamage();
            event.setDamage(0);
            Title.showTitle(shooter, "", ChatColor.GOLD + "** EXTRACTION **", 5, 20, 5);
            event.getEntity().setVelocity(new Vector(0, 1, 0));

            if (event.getEntity().isDead()) return;
            Location loc = event.getEntity().getLocation();
            new BukkitRunnable() {
                int count = 15;

                @Override
                public void run() {
                    count--;
                    event.getEntity().getWorld().spawnFallingBlock(loc, new MaterialData(main().ore().genOre(main().rng().nextInt(100))))
                            .setVelocity(new Vector((main().rng().nextBoolean() ? Math.random() : -Math.random()) / 5, 0.55 + Math.random() / 3, (main().rng().nextBoolean() ? Math.random() : -Math.random()) / 5));
                    loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 5);
                    loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_PLACE, 3F, 1.75F);
                    if (count == 0) {
                        ((LivingEntity) event.getEntity()).damage(damage);
                        loc.getWorld().playSound(loc, Sound.ENTITY_GHAST_SCREAM, 3F, 1F);
                        this.cancel();
                    }
                }
            }.runTaskTimer(main().plugin(), 0L, 1L);
        });
    }
}
