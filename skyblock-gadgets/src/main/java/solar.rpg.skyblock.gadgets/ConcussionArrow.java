package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.util.Utility;

/**
 * Knocks nearby mobs away from the impact location.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class ConcussionArrow extends ArrowGadget {

    @Override
    public String getName() {
        return ChatColor.DARK_RED + "Concussion Arrow";
    }

    @Override
    public int getPrice() {
        return 7500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "High velocity explosive arrow",
                "Clears the area of impact",
                "Stuns affected targets"
        };
    }

    @Override
    public ItemStack getIcon() {
        return ItemUtility.changeSize(ItemUtility.createTippedArrow(PotionEffectType.DAMAGE_RESISTANCE, 20 * 120, 0), 6);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        arrowCheck(event.getEntity(), () -> {
            Projectile proj = event.getEntity();
            proj.setVelocity(proj.getVelocity().multiply(1.5));
            proj.setMetadata("concussion", new FixedMetadataValue(main().plugin(), ""));
        });
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        arrowMetaCheck(event.getEntity(), "concussion", () -> {
            Projectile proj = event.getEntity();
            Location loc = proj.getLocation();

            Player shooter = (Player) event.getEntity().getShooter();
            // Only affect monsters if in the vicinity of someone else's island.
            boolean restricted = false;
            if (strictCheck(loc.getWorld()))
                restricted = !main().islands().getIslandAt(loc).members().isMember(shooter.getUniqueId());

            proj.removeMetadata("concussion", main().plugin());
            proj.remove();
            loc.getWorld().createExplosion(loc, 0F);
            loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 20);
            loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
            loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2.5F, 3F);
            for (Entity nearby : proj.getNearbyEntities(5, 5, 5))
                if (nearby instanceof LivingEntity)
                    if (!(nearby instanceof Player))
                        if (!restricted || Utility.isHostile(nearby)) {
                            Location targetLoc = nearby.getLocation();
                            nearby.setVelocity(targetLoc.toVector().subtract(event.getEntity().getLocation().toVector()).setY(1).multiply(1.5));
                            ((LivingEntity) nearby).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, main().rng().nextInt(5) * 20 + 5 * 20, 100));
                        }
        });
    }
}
