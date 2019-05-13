package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.util.ItemUtility;

/**
 * High-velocity arrow that teleports the player to its location of impact.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class EnderArrow extends ArrowGadget {

    @Override
    public String getName() {
        return ChatColor.GREEN + "Ender Arrow";
    }

    @Override
    public int getPrice() {
        return 2500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{"High velocity arrow imbued with End aura"};
    }

    @Override
    public ItemStack getIcon() {
        return ItemUtility.changeSize(ItemUtility.createTippedArrow(PotionEffectType.POISON, 20 * 120, 0), 10);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        arrowCheck(event.getEntity(), () -> {
            Projectile proj = event.getEntity();
            proj.setVelocity(proj.getVelocity().multiply(1.5));
            proj.setMetadata("ender", new FixedMetadataValue(main().plugin(), ""));
        });
    }

    @EventHandler
    public void onProjHit(ProjectileHitEvent event) {
        arrowMetaCheck(event.getEntity(), "ender", () -> {
            Projectile proj = event.getEntity();
            Location loc = proj.getLocation();

            proj.removeMetadata("ender", main().plugin());
            proj.remove();
            loc.setYaw(((Player) event.getEntity().getShooter()).getLocation().getYaw());
            loc.setPitch(((Player) event.getEntity().getShooter()).getLocation().getPitch());
            ((Player) event.getEntity().getShooter()).teleport(loc);
            loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMEN_TELEPORT, 1.5F, 1.5F);
            loc.getWorld().spawnParticle(Particle.END_ROD, loc, 30);
        });
    }
}
