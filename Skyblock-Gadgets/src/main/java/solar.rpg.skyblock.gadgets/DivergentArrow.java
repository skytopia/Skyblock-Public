package solar.rpg.skyblock.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.util.Title;

/**
 * An arrow that splits apart when it comes into
 * contact with, and kills a monster.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class DivergentArrow extends ArrowGadget {

    @Override
    public String getName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + ChatColor.ITALIC + ChatColor.BOLD + "Divergent Arrow";
    }

    @Override
    public int getPrice() {
        return 5000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{"Affects monsters only, splits apart on contact kills"};
    }

    @Override
    public ItemStack getIcon() {
        return ItemUtility.changeSize(ItemUtility.createTippedArrow(PotionEffectType.FAST_DIGGING, 20 * 120, 0), 8);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        arrowCheck(event.getEntity(), () -> {
            Projectile proj = event.getEntity();
            proj.setVelocity(proj.getVelocity().multiply(1.5));
            proj.setMetadata("diverge", new FixedMetadataValue(main().plugin(), ""));
        });
    }

    @EventHandler
    public void onProjHit(ProjectileHitEvent event) {
        arrowMetaCheck(event.getEntity(), "diverge", () ->
                Bukkit.getScheduler().runTaskLater(main().plugin(), () -> {
                    Projectile proj = event.getEntity();
                    if (proj.hasMetadata("diverge")) {
                        proj.removeMetadata("diverge", main().plugin());
                        proj.remove();
                    }
                }, 1L));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        arrowMetaCheck(event.getEntity(), "diverge", () -> {
            Projectile proj = (Projectile) event.getDamager();
            Player shooter = (Player) ((Projectile) event.getDamager()).getShooter();
            if (proj.hasMetadata("diverge")) {
                proj.removeMetadata("diverge", main().plugin());
                proj.remove();
                if (event.getFinalDamage() >= ((LivingEntity) event.getEntity()).getHealth()) {
                    Title.showTitle(shooter, "", ChatColor.GOLD + "** SPLIT **", 5, 20, 5);
                    proj.getWorld().playSound(proj.getLocation(), Sound.ENTITY_ENDERMEN_SCREAM, 2F, 2F);
                    launchAdditional(proj);
                }
            }
        });
    }

    /**
     * Launches another additional divergent arrow.
     * Only a maximum of 3 arrows can split off one arrow.
     *
     * @param proj The original projectile.
     */
    private void launchAdditional(Projectile proj) {
        int limit = 3;
        for (Entity nearby : proj.getNearbyEntities(20, 20, 20)) {
            if (limit == 0) break;
            limit--;
            if (nearby instanceof Monster) {
                TippedArrow newProj = proj.getWorld().spawnArrow(proj.getLocation(), ((Monster) nearby).getEyeLocation().toVector().subtract(proj.getLocation().toVector()).multiply(0.3), 3F, 0, TippedArrow.class);
                newProj.setShooter(proj.getShooter());
                newProj.setCritical(true);
                newProj.setMetadata("diverge", new FixedMetadataValue(main().plugin(), ""));
                newProj.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 120, 0), true);
            }
        }
    }
}
